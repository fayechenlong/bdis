package com.beeplay.bdis.server.protocol.model.cluster;

import com.beeplay.bdis.server.protocol.BdisClientPool;
import com.beeplay.bdis.server.util.LogExceptionStackTrace;
import com.beeplay.bdis.server.util.RedisMessageUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * @author chenlf
 * @date 2019/10/24
 */
public class BdisClusterHandler extends ClusterHandler {
    private static Logger logger = LoggerFactory.getLogger(BdisClusterHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  {

        RedisMessageUtil.printAggregatedRedisResponseCommand(((RedisMessage)msg));//日志打印

        List<RedisMessage> messages=((ArrayRedisMessage)msg).children();
        try {
            super.sendCommand(ctx,messages);
        }catch (Exception e){
            logger.error(LogExceptionStackTrace.erroStackTrace(e).toString());
            ctx.writeAndFlush(new SimpleStringRedisMessage(e.getMessage()));
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
    }
}
