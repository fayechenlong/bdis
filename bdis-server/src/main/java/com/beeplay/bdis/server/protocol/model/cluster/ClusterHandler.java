package com.beeplay.bdis.server.protocol.model.cluster;

import com.beeplay.bdis.server.command.BdisCommand;
import com.beeplay.bdis.server.config.StartConfig;
import com.beeplay.bdis.server.BdisServerStart;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.CharsetUtil;
import redis.clients.jedis.ScanParams;

import java.lang.reflect.Method;
import java.util.List;


public  class ClusterHandler extends ClusterAbstract {

    public void sendCommand(ChannelHandlerContext ctx, List<RedisMessage> messages) throws Exception{
        int cmdSize=messages.size();
        if(cmdSize==0){
            super.unknownCommand(ctx);
            return;
        }
        String command=((FullBulkStringRedisMessage)messages.get(0)).content().toString(CharsetUtil.UTF_8).toLowerCase();
        Object obj=ClusterHandler.class.newInstance();
        if(cmdSize==1) {
            try {
                Method method = ClusterHandler.class.getDeclaredMethod(command, ChannelHandlerContext.class);
                method.invoke(obj,ctx);
            }catch (NoSuchMethodException e){
                super.unknownCommand(ctx,command);
            }
        }else if(cmdSize>1){
            try {
                Method method = ClusterHandler.class.getDeclaredMethod(command, ChannelHandlerContext.class,List.class);
                method.invoke(obj,ctx,messages);
            }catch (NoSuchMethodException e){
                super.unknownCommand(ctx,command);
            }
        }
    }
    private void ping(ChannelHandlerContext ctx){
        ctx.writeAndFlush(new SimpleStringRedisMessage(BdisCommand.PONG.cmd()));
    }
    private void info(ChannelHandlerContext ctx){
        ctx.writeAndFlush(new SimpleStringRedisMessage(StartConfig.BDIS_INFO));
    }
    private void get(ChannelHandlerContext ctx,List<RedisMessage> messages){
        if(messages.size()>1) {
            String key = ((FullBulkStringRedisMessage) messages.get(1)).content().toString(CharsetUtil.UTF_8);
            super.returnData(BdisServerStart.jedisCluster.get(key), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    private void type(ChannelHandlerContext ctx,List<RedisMessage> messages){
        if(messages.size()>1) {
            String key = ((FullBulkStringRedisMessage) messages.get(1)).content().toString(CharsetUtil.UTF_8);
            super.returnData(BdisServerStart.jedisCluster.type(key), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    private void ttl(ChannelHandlerContext ctx,List<RedisMessage> messages){
        if(messages.size()>1) {
            String key = ((FullBulkStringRedisMessage) messages.get(1)).content().toString(CharsetUtil.UTF_8);
            super.returnData(BdisServerStart.jedisCluster.ttl(key), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    private void del(ChannelHandlerContext ctx,List<RedisMessage> messages){
        if(messages.size()>1) {
            String key = ((FullBulkStringRedisMessage) messages.get(1)).content().toString(CharsetUtil.UTF_8);
            super.returnData(BdisServerStart.jedisCluster.del(key), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    private void scard(ChannelHandlerContext ctx,List<RedisMessage> messages){
        if(messages.size()>1) {
            String key = ((FullBulkStringRedisMessage) messages.get(1)).content().toString(CharsetUtil.UTF_8);
            super.returnData(BdisServerStart.jedisCluster.scard(key), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    private void incr(ChannelHandlerContext ctx,List<RedisMessage> messages){
        if(messages.size()>1) {
            String key = ((FullBulkStringRedisMessage) messages.get(1)).content().toString(CharsetUtil.UTF_8);
            super.returnData(BdisServerStart.jedisCluster.incr(key), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    private void exists(ChannelHandlerContext ctx,List<RedisMessage> messages){
        if(messages.size()>1) {
            String key = ((FullBulkStringRedisMessage) messages.get(1)).content().toString(CharsetUtil.UTF_8);
            Long outLong = 0L;
            if (BdisServerStart.jedisCluster.exists(key)) {
                outLong = 1L;
            }
            super.returnData(outLong, ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    private void set(ChannelHandlerContext ctx,List<RedisMessage> messages){
        if(messages.size()==3) {
            String key = ((FullBulkStringRedisMessage) messages.get(1)).content().toString(CharsetUtil.UTF_8);
            String value = ((FullBulkStringRedisMessage) messages.get(2)).content().toString(CharsetUtil.UTF_8);
            super.returnData(BdisServerStart.jedisCluster.set(key, value), ctx);
            return;
        }else if(messages.size()==5){
            String key = ((FullBulkStringRedisMessage) messages.get(1)).content().toString(CharsetUtil.UTF_8);
            String value = ((FullBulkStringRedisMessage) messages.get(2)).content().toString(CharsetUtil.UTF_8);
            String command = ((FullBulkStringRedisMessage) messages.get(3)).content().toString(CharsetUtil.UTF_8);
            if(command.toLowerCase().equals("ex")){
            Integer seconds = Integer.parseInt(((FullBulkStringRedisMessage) messages.get(4)).content().toString(CharsetUtil.UTF_8));
            super.returnData(BdisServerStart.jedisCluster.setex(key,seconds,value), ctx);
            }
            return;
        }
        super.unknownCommand(ctx);
    }
    private void setex(ChannelHandlerContext ctx,List<RedisMessage> messages){
        if(messages.size()==4){
            String key = ((FullBulkStringRedisMessage) messages.get(1)).content().toString(CharsetUtil.UTF_8);
            Integer seconds = Integer.parseInt(((FullBulkStringRedisMessage) messages.get(2)).content().toString(CharsetUtil.UTF_8));
            String value = ((FullBulkStringRedisMessage) messages.get(3)).content().toString(CharsetUtil.UTF_8);
            super.returnData(BdisServerStart.jedisCluster.setex(key,seconds,value), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
    private void scan(ChannelHandlerContext ctx,List<RedisMessage> messages){
        if(messages.size()>1) {
            String sursor = ((FullBulkStringRedisMessage) messages.get(1)).content().toString(CharsetUtil.UTF_8);
            ScanParams sp=new ScanParams();
            if(messages.size()>3) {
                String match = ((FullBulkStringRedisMessage) messages.get(2)).content().toString(CharsetUtil.UTF_8);
                if (match.toLowerCase().equals("match")){
                    String matchValue = ((FullBulkStringRedisMessage) messages.get(3)).content().toString(CharsetUtil.UTF_8);
                    sp.match(matchValue);
                 }
            }
            if(messages.size()>5) {
                String count = ((FullBulkStringRedisMessage) messages.get(4)).content().toString(CharsetUtil.UTF_8);
                if(count.toLowerCase().equals("count")) {
                    Integer countValue = Integer.parseInt(((FullBulkStringRedisMessage) messages.get(5)).content().toString(CharsetUtil.UTF_8));
                    sp.count(countValue);
                }
            }
            super.returnData(BdisServerStart.jedisCluster.scan(sursor,sp), ctx);
            return;
        }
        super.unknownCommand(ctx);
    }
}
