package com.beeplay.bdis.server.protocol.model.cluster;


import com.beeplay.bdis.server.command.BdisCommand;
import com.beeplay.bdis.server.util.GfJsonUtil;
import com.beeplay.bdis.server.util.RedisMessageUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.CharsetUtil;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ScanResult;

import java.util.List;

public abstract class ClusterAbstract extends ChannelDuplexHandler {

    public static JedisCluster jedisCluster;

    public void returnData(String out,ChannelHandlerContext ctx){
        RedisMessageUtil.returnData(out,ctx);
    }
    public void returnData(ScanResult<String> sr, ChannelHandlerContext ctx){
        RedisMessageUtil.returnData(sr,ctx);
    }
    public void returnData(Long outLong,ChannelHandlerContext ctx){
        RedisMessageUtil.returnData(outLong,ctx);
    }
    public void unknownCommand(ChannelHandlerContext ctx){
        RedisMessageUtil.unknownCommand(ctx);
    }
    public void empty(ChannelHandlerContext ctx){
        RedisMessageUtil.empty(ctx);
    }
    public void unknownCommand(ChannelHandlerContext ctx,String command){
        RedisMessageUtil.unknownCommand(ctx,command);
    }
    public String getMessage(List<FullBulkStringRedisMessage> messages,int count){
        return RedisMessageUtil.getMessage(messages,count);
    }
}
