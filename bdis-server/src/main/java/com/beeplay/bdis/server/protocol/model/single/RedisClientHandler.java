package com.beeplay.bdis.server.protocol.model.single;

import com.beeplay.bdis.server.protocol.model.BdisClientPool;
import com.beeplay.bdis.server.util.GfJsonUtil;
import com.beeplay.bdis.server.util.RedisMessageUtil;
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
      RedisMessageUtil.printAggregatedRedisResponseReturn((RedisMessage)msg);
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
