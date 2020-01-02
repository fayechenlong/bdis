package com.beeplay.bdis.server.protocol.model.cluster;

import com.beeplay.bdis.server.command.BdisCommand;
import com.beeplay.bdis.server.config.StartConfig;
import com.beeplay.bdis.server.protocol.model.BdisClientPool;
import com.beeplay.bdis.server.start.BdisServer;
import com.beeplay.bdis.server.util.RedisMessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * @author chenlf
 * @date 2019/10/24
 */
public class BdisClusterHandler extends ChannelDuplexHandler {
    private static Logger logger = LoggerFactory.getLogger(BdisClusterHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RedisMessageUtil.printAggregatedRedisResponseCommand(((RedisMessage)msg));//日志打印

        List<RedisMessage> messages=((ArrayRedisMessage)msg).children();
        sendCommand(ctx,messages);

    }
    private void sendCommand(ChannelHandlerContext ctx, List<RedisMessage>  messages){
        int cmdSize=messages.size();
        if(cmdSize<=0){
            ctx.writeAndFlush(new SimpleStringRedisMessage("ERR unknown command"));
            return;
        }
        String command=((FullBulkStringRedisMessage)messages.get(0)).content().toString(CharsetUtil.UTF_8);
        String out="(null)";
        Long outLong=0L;
        if(cmdSize==1) {
            switch (command) {
                case "PING":
                    ctx.writeAndFlush(new SimpleStringRedisMessage(BdisCommand.PONG.cmd()));
                    break;
                case "INFO":
                    ctx.writeAndFlush(new SimpleStringRedisMessage(StartConfig.BDIS_INFO));
                    break;
                default:
                    ctx.writeAndFlush(new SimpleStringRedisMessage("ERR unknown command '" + command + "'"));
                    break;
            }
        }
        String message1=((FullBulkStringRedisMessage)messages.get(1)).content().toString(CharsetUtil.UTF_8);
        if(cmdSize==2) {
            switch (command) {
                case "GET":
                    out=BdisServer.jedisCluster.get(message1);
                    returnData(out,ctx);
                    break;
                case "TYPE":
                    out=BdisServer.jedisCluster.type(message1);
                    returnData(out,ctx);
                    break;
                case "TTL":
                    outLong=BdisServer.jedisCluster.ttl(message1);
                    returnData(outLong,ctx);
                    break;

                case "DEL":
                    outLong=BdisServer.jedisCluster.del(message1);
                    returnData(outLong,ctx);
                    break;
                case "SCARD":
                    outLong = BdisServer.jedisCluster.scard(message1);
                    returnData(outLong, ctx);
                    break;
                case "INCR" :
                    outLong=BdisServer.jedisCluster.incr(message1);
                    returnData(outLong,ctx);
                    break;
                case "EXISTS" :
                    if(BdisServer.jedisCluster.exists(message1)) {
                        outLong=1L;
                    };
                    returnData(outLong,ctx);
                    break;
                default:
                    ctx.writeAndFlush(new SimpleStringRedisMessage("ERR unknown command '" + command + "'"));
                    break;
            }
        }
        String message2=((FullBulkStringRedisMessage)messages.get(2)).content().toString(CharsetUtil.UTF_8);
        if(cmdSize==3) {
            switch (command) {
                case "SET":
                    out = BdisServer.jedisCluster.set(message1,message2);
                    returnData(out, ctx);
                    break;
                case "SADD":
                    outLong = BdisServer.jedisCluster.sadd(message1,message2);
                    returnData(outLong, ctx);
                    break;
                case "SREM":
                    outLong = BdisServer.jedisCluster.srem(message1,message2);
                    returnData(outLong, ctx);
                    break;
                case "INCRBY":
                    outLong=BdisServer.jedisCluster.incrBy(message1,Integer.parseInt(message2));
                    returnData(outLong,ctx);
                    break;
                case "EXPIRE" :
                    outLong=BdisServer.jedisCluster.expire(message1,Integer.parseInt(message2));
                    returnData(outLong,ctx);
                    break;
                default:
                    ctx.writeAndFlush(new SimpleStringRedisMessage("ERR unknown command '" + command + "'"));
                    break;
            }
        }
    }
    private void returnData(String out,ChannelHandlerContext ctx){
        if(out!=null) {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), out)));
        }else {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), BdisCommand.NULL.cmd())));
        }
    }
    private void returnData(Long outLong,ChannelHandlerContext ctx){
        if(outLong!=null) {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), String.valueOf(outLong))));
        }else {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), BdisCommand.NULL.cmd())));
        }
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String channelid=ctx.channel().id().toString();
        logger.info("client connect;address:" + ctx.channel().remoteAddress()+" id:"+channelid);
        super.channelActive(ctx);
        BdisClientPool.bdisClients.put(channelid,ctx);
        BdisServer.channelHandlerContext=ctx;
    }
}
