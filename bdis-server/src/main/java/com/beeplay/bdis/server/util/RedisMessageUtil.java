package com.beeplay.bdis.server.util;

import com.beeplay.bdis.server.command.BdisCommand;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ScanResult;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class RedisMessageUtil {
    private static Logger logger = LoggerFactory.getLogger(RedisMessageUtil.class);
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
    public static void printAggregatedRedisResponseReturn(RedisMessage msg) {
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
                returnString=returnString.append(RedisMessageUtil.printAggregatedRedisResponse(child)).append(",");
                if(a>10){
                    break;
                }
            }
            logger.info("return : "+returnString.append(".....]"));
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }
    public static void printAggregatedRedisResponseCommand(RedisMessage msg) {
        if (msg instanceof SimpleStringRedisMessage) {
            logger.info("command : "+((SimpleStringRedisMessage) msg).content());
        } else if (msg instanceof ErrorRedisMessage) {
            logger.info("command : "+((ErrorRedisMessage) msg).content());
        } else if (msg instanceof IntegerRedisMessage) {
            logger.info("command : "+((IntegerRedisMessage) msg).toString());
        } else if (msg instanceof FullBulkStringRedisMessage) {
            logger.info("command : "+getString((FullBulkStringRedisMessage) msg));
        } else if (msg instanceof ArrayRedisMessage) {
            String returnString="";
            for (RedisMessage child : ((ArrayRedisMessage) msg).children()) {
                returnString=returnString+ RedisMessageUtil.printAggregatedRedisResponse(child)+" ";
            }
            logger.info("command : "+returnString);
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }
    public static void returnData(String out,ChannelHandlerContext ctx){
        if(out!=null) {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), out)));
        }else {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), BdisCommand.NULL.cmd())));
        }
    }
    public static void returnData(ScanResult<String> sr, ChannelHandlerContext ctx){
        ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), sr.getCursor())));
        if(sr.getResult().size()>0) {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), GfJsonUtil.toJSONString(sr.getResult()))));
        }else {
            empty(ctx);
        }
    }
    public static  void returnData(Long outLong,ChannelHandlerContext ctx){
        if(outLong!=null) {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), String.valueOf(outLong))));
        }else {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), BdisCommand.NULL.cmd())));
        }
    }
    public static void unknownCommand(ChannelHandlerContext ctx){
        ctx.writeAndFlush(new SimpleStringRedisMessage("ERR : need more parms!"));
    }
    public static void empty(ChannelHandlerContext ctx){
        ctx.writeAndFlush(new SimpleStringRedisMessage("(empty list or set)"));
    }
    public static void unknownCommand(ChannelHandlerContext ctx,String command){
        ctx.writeAndFlush(new SimpleStringRedisMessage("ERR unknown command '" + command + "'"));
    }
    public static  String getMessage(List<FullBulkStringRedisMessage> messages,int count){
        return messages.get(count).content().toString(CharsetUtil.UTF_8);
    }
    public static List<FullBulkStringRedisMessage>  coverMessage(Object msg){
        List<RedisMessage> messages=((ArrayRedisMessage)msg).children();
        List<FullBulkStringRedisMessage> fullBulkStringRedisMessages=new ArrayList<>();
        for(RedisMessage redisMessage:messages){
            fullBulkStringRedisMessages.add((FullBulkStringRedisMessage)redisMessage);
        }
        return  fullBulkStringRedisMessages;
    }
}
