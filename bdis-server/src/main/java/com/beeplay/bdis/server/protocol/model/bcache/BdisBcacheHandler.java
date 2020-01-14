package com.beeplay.bdis.server.protocol.model.bcache;

import com.beeplay.bdis.server.bcache.Bcache;
import com.beeplay.bdis.server.util.LogExceptionStackTrace;
import com.beeplay.bdis.server.util.RedisMessageUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class BdisBcacheHandler extends BcacheHandler {
    private static Logger logger = LoggerFactory.getLogger(BdisBcacheHandler.class);

    public BdisBcacheHandler(){
        init();
    }
    private void init(){
      bcache=new Bcache();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RedisMessageUtil.printAggregatedRedisResponseCommand(((RedisMessage)msg));//日志打印

        List<FullBulkStringRedisMessage> fullBulkStringRedisMessages=RedisMessageUtil.coverMessage(msg);
        try {
            sendCommand(ctx,fullBulkStringRedisMessages);
        }catch (Exception e){
            logger.error(LogExceptionStackTrace.erroStackTrace(e).toString());
            ctx.writeAndFlush(new SimpleStringRedisMessage(e.getMessage()));
        }
        ctx.flush();
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {


    }
    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.close(promise);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("client connect close!;address:" + ctx.channel().remoteAddress());
        ctx.close();
    }
}
