package com.beeplay.bdis.server.protocol.model.bcache;

import com.beeplay.bdis.server.bcache.Bcache;
import com.beeplay.bdis.server.util.RedisMessageUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Method;
import java.util.List;


public class BcacheHandler extends ChannelDuplexHandler {

    public static Bcache bcache;
    public  void sendCommand(ChannelHandlerContext ctx, List<FullBulkStringRedisMessage> messages) throws Exception{
        int cmdSize=messages.size();
        if(cmdSize==0){
            RedisMessageUtil.unknownCommand(ctx);
            return;
        }
        String command=getMessage(messages,0).toLowerCase();
        Object obj=BcacheHandler.class.newInstance();
        if(cmdSize==1) {
            try {
                Method method = BcacheHandler.class.getDeclaredMethod(command, ChannelHandlerContext.class);
                method.invoke(obj,ctx);
            }catch (NoSuchMethodException e){
                RedisMessageUtil.unknownCommand(ctx,command);
            }
        }else if(cmdSize>1){
            try {
                Method method = BcacheHandler.class.getDeclaredMethod(command, ChannelHandlerContext.class,List.class);
                method.invoke(obj,ctx,messages);
            }catch (NoSuchMethodException e){
                RedisMessageUtil.unknownCommand(ctx,command);
            }
        }
    }
    public  String getMessage(List<FullBulkStringRedisMessage> messages,int count){
        return messages.get(count).content().toString(CharsetUtil.UTF_8);
    }
    public void get(ChannelHandlerContext ctx, List<FullBulkStringRedisMessage> messages){
        if(messages.size()>1) {
            String key = getMessage(messages,1);
            RedisMessageUtil.returnData(bcache.get(key), ctx);
            return;
        }
        RedisMessageUtil.unknownCommand(ctx);
    }
    public void set(ChannelHandlerContext ctx,List<FullBulkStringRedisMessage> messages){
        if(messages.size()==3) {
            String key =  getMessage(messages,1);
            String value = getMessage(messages,2);
            RedisMessageUtil.returnData(bcache.set(key, value), ctx);
            return;
        }
        RedisMessageUtil.unknownCommand(ctx);
    }
    public void del(ChannelHandlerContext ctx,List<FullBulkStringRedisMessage> messages){
        if(messages.size()>1) {
            String key = getMessage(messages,1);
            RedisMessageUtil.returnData(bcache.del(key), ctx);
            return;
        }
        RedisMessageUtil.unknownCommand(ctx);
    }
}
