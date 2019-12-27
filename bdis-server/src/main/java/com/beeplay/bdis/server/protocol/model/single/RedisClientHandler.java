package com.beeplay.bdis.server.protocol.model.single;

import com.beeplay.bdis.server.protocol.model.BdisClientPool;
import com.beeplay.bdis.server.util.GfJsonUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenlf
 * @date 2019/10/24
 */
public class RedisClientHandler extends ChannelDuplexHandler{
    private static Logger logger = LoggerFactory.getLogger(RedisClientHandler.class);
    private String chxId;

    public String getChxId() {
        return chxId;
    }

    public void setChxId(String chxId) {
        this.chxId = chxId;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        RedisMessage request = (ArrayRedisMessage)msg;
        ctx.write(request, promise);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
      BdisClientPool.bdisClients.get(chxId).writeAndFlush(msg);
      printAggregatedRedisResponseSys((RedisMessage)msg);
    }
    private static void printAggregatedRedisResponseSys(RedisMessage msg) {
        if (msg instanceof SimpleStringRedisMessage) {
            logger.info("return : "+((SimpleStringRedisMessage) msg).content());
        } else if (msg instanceof ErrorRedisMessage) {
            logger.info("return : "+((ErrorRedisMessage) msg).content());
        } else if (msg instanceof IntegerRedisMessage) {
            logger.info("return : "+String.valueOf(((IntegerRedisMessage) msg).value()));
        } else if (msg instanceof FullBulkStringRedisMessage) {
            logger.info("return : "+getString((FullBulkStringRedisMessage) msg));
        } else if (msg instanceof ArrayRedisMessage) {
            StringBuffer returnString=new StringBuffer("[");
            int a=0;
            for (RedisMessage child : ((ArrayRedisMessage) msg).children()) {
                a++;
                returnString=returnString.append(printAggregatedRedisResponse(child)).append(",");
                if(a>10){
                    break;
                }
            }
            logger.info("return : "+returnString.append(".....]"));
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }
    public static Object printAggregatedRedisResponse(RedisMessage msg) {
        if (msg instanceof SimpleStringRedisMessage) {
            return ((SimpleStringRedisMessage) msg).content();
        } else if (msg instanceof ErrorRedisMessage) {
            return ((ErrorRedisMessage) msg).content();
        } else if (msg instanceof IntegerRedisMessage) {
            return ((IntegerRedisMessage) msg).toString();
        } else if (msg instanceof FullBulkStringRedisMessage) {
            return getString((FullBulkStringRedisMessage) msg);
        }  else if (msg instanceof ArrayRedisMessage) {
            StringBuffer returnString=new StringBuffer("[");
            int a=0;
            for (RedisMessage child : ((ArrayRedisMessage) msg).children()) {
                a++;
                returnString=returnString.append(printAggregatedRedisResponse(child)).append(",");
                if(a>10){
                    break;
                }
            }

            return returnString.append("....]");
        }else {
            return "(null)";
        }
    }
    private static String getString(FullBulkStringRedisMessage msg) {
        if (msg.isNull()) {
            return "(null)";
        }
        return msg.content().toString(CharsetUtil.UTF_8);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
