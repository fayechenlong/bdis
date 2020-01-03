package com.beeplay.bdis.server.protocol.model.single;

import com.beeplay.bdis.server.protocol.BdisClientPool;
import com.beeplay.bdis.server.BdisServerStart;
import com.beeplay.bdis.server.util.RedisMessageUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenlf
 * @date 2019/10/24
 */
public class BdisSingleHandler extends ChannelDuplexHandler {
    private static Logger logger = LoggerFactory.getLogger(BdisSingleHandler.class);
    private Map<Object,Object> stringMap = new HashMap<>();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RedisMessageUtil.printAggregatedRedisResponseCommand((RedisMessage)msg);

        Connection connection= BdisServerStart.channelPool.getResource();
        connection.setChxId(ctx.channel().id().toString());
        connection.write(msg);
        BdisServerStart.channelPool.returnResource(connection);
        //返回值
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
