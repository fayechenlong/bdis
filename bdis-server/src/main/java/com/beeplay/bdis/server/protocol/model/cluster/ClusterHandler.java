package com.beeplay.bdis.server.protocol.model.cluster;

import com.beeplay.bdis.server.command.BdisCommand;
import com.beeplay.bdis.server.config.StartConfig;
import com.beeplay.bdis.server.BdisServerAbstract;
import com.beeplay.bdis.server.protocol.model.bcache.BcacheHandler;
import com.beeplay.bdis.server.util.RedisMessageUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.CharsetUtil;
import redis.clients.jedis.ScanParams;

import java.lang.reflect.Method;
import java.util.List;


public  class ClusterHandler extends ClusterAbstract {

    public  void sendCommand(ChannelHandlerContext ctx, List<FullBulkStringRedisMessage> messages) throws Exception{
        int cmdSize=messages.size();
        if(cmdSize==0){
            RedisMessageUtil.unknownCommand(ctx);
            return;
        }
        String command=getMessage(messages,0).toLowerCase();
        Object obj=ClusterHandler.class.newInstance();
        if(cmdSize==1) {
            try {
                Method method = ClusterHandler.class.getDeclaredMethod(command, ChannelHandlerContext.class);
                method.invoke(obj,ctx);
            }catch (NoSuchMethodException e){
                RedisMessageUtil.unknownCommand(ctx,command);
            }
        }else if(cmdSize>1){
            try {
                Method method = ClusterHandler.class.getDeclaredMethod(command, ChannelHandlerContext.class,List.class);
                method.invoke(obj,ctx,messages);
            }catch (NoSuchMethodException e){
                RedisMessageUtil.unknownCommand(ctx,command);
            }
        }
    }
    public void ping(ChannelHandlerContext ctx){
        ctx.writeAndFlush(new SimpleStringRedisMessage(BdisCommand.PONG.cmd()));
    }
    public void info(ChannelHandlerContext ctx){
        ctx.writeAndFlush(new SimpleStringRedisMessage(StartConfig.BDIS_INFO));
    }
    public void get(ChannelHandlerContext ctx,List<FullBulkStringRedisMessage> messages){
        if(messages.size()>1) {
            String key = RedisMessageUtil.getMessage(messages,1);
            super.returnData(jedisCluster.get(key), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    public void type(ChannelHandlerContext ctx,List<FullBulkStringRedisMessage> messages){
        if(messages.size()>1) {
            String key = getMessage(messages,1);
            super.returnData(jedisCluster.type(key), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    public void ttl(ChannelHandlerContext ctx,List<FullBulkStringRedisMessage> messages){
        if(messages.size()>1) {
            String key = getMessage(messages,1);
            super.returnData(jedisCluster.ttl(key), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    public void del(ChannelHandlerContext ctx,List<FullBulkStringRedisMessage> messages){
        if(messages.size()>1) {
            String key = getMessage(messages,1);
            super.returnData(jedisCluster.del(key), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    public void scard(ChannelHandlerContext ctx,List<FullBulkStringRedisMessage> messages){
        if(messages.size()>1) {
            String key = getMessage(messages,1);
            super.returnData(jedisCluster.scard(key), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    public void incr(ChannelHandlerContext ctx,List<FullBulkStringRedisMessage> messages){
        if(messages.size()>1) {
            String key = getMessage(messages,1);
            super.returnData(jedisCluster.incr(key), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    public void exists(ChannelHandlerContext ctx,List<FullBulkStringRedisMessage> messages){
        if(messages.size()>1) {
            String key = getMessage(messages,1);
            Long outLong = 0L;
            if (jedisCluster.exists(key)) {
                outLong = 1L;
            }
            super.returnData(outLong, ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    public void set(ChannelHandlerContext ctx,List<FullBulkStringRedisMessage> messages){
        if(messages.size()==3) {
            String key =  getMessage(messages,1);
            String value = getMessage(messages,2);
            super.returnData(jedisCluster.set(key, value), ctx);
            return;
        }else if(messages.size()==5){
            String key = getMessage(messages,1);
            String value = getMessage(messages,2);
            String command =getMessage(messages,3);
            if(command.toLowerCase().equals("ex")){
            Integer seconds = Integer.parseInt(getMessage(messages,4));
            super.returnData(jedisCluster.setex(key,seconds,value), ctx);
            }
            return;
        }
        super.unknownCommand(ctx);
    }
    public void setex(ChannelHandlerContext ctx,List<FullBulkStringRedisMessage> messages){
        if(messages.size()==4){
            String key = getMessage(messages,1);
            Integer seconds = Integer.parseInt(getMessage(messages,2));
            String value =  getMessage(messages,3);
            super.returnData(jedisCluster.setex(key,seconds,value), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    public void scan(ChannelHandlerContext ctx,List<FullBulkStringRedisMessage> messages){
        if(messages.size()>1) {
            String sursor = getMessage(messages,1);
            ScanParams sp=new ScanParams();
            if(messages.size()>3) {
                String match = getMessage(messages,2);
                if (match.toLowerCase().equals("match")){
                    String matchValue = getMessage(messages,3);
                    sp.match(matchValue);
                 }
            }
            if(messages.size()>5) {
                String count = getMessage(messages,4);
                if(count.toLowerCase().equals("count")) {
                    Integer countValue = Integer.parseInt(getMessage(messages,5));
                    sp.count(countValue);
                }
            }
            super.returnData(jedisCluster.scan(sursor,sp), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
}
