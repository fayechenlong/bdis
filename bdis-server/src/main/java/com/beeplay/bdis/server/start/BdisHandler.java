package com.beeplay.bdis.server.start;

import com.beeplay.bdis.server.command.BdisCommand;
import com.beeplay.bdis.server.config.StartConfig;
import com.beeplay.bdis.server.protocol.model.BdisClientPool;
import com.beeplay.bdis.server.protocol.model.single.Connection;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenlf
 * @date 2019/10/24
 */
public class BdisHandler extends ChannelDuplexHandler {
    private static Logger logger = LoggerFactory.getLogger(BdisHandler.class);
    private Map<Object,Object> stringMap = new HashMap<>();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Connection connection=BdisServer.channelPool.getResource();
        connection.setChxId(ctx.channel().id().toString());
        connection.write(msg);
        BdisServer.channelPool.returnResource(connection);
        //返回值
    }
    private Object printAggregatedRedisResponseRequest(ChannelHandlerContext ctx,ArrayRedisMessage message) {
        String type = ((FullBulkStringRedisMessage) message.children().get(0)).content().toString(CharsetUtil.UTF_8);
        if (type.equalsIgnoreCase(BdisCommand.SET.cmd())) {
            stringMap.put( ((FullBulkStringRedisMessage) message.children().get(1)).content().toString(CharsetUtil.UTF_8),
                    ((FullBulkStringRedisMessage) message.children().get(2)).content().toString(CharsetUtil.UTF_8));
            return new SimpleStringRedisMessage(BdisCommand.OK.cmd());
        }
        if (type.equalsIgnoreCase(BdisCommand.DEL.cmd())) {
            stringMap.remove(((FullBulkStringRedisMessage) message.children().get(1)).content().toString(CharsetUtil.UTF_8));
            return new SimpleStringRedisMessage(BdisCommand.OK.cmd());
        }
        if (type.equalsIgnoreCase(BdisCommand.GET.cmd())) {
            Object o = stringMap.get(((FullBulkStringRedisMessage) message.children().get(1)).content().toString(CharsetUtil.UTF_8));
            if (o == null) {
                return new SimpleStringRedisMessage(BdisCommand.NULL.cmd());
            }else {
                return new  FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), (String) o));
            }
        }
        if (type.equalsIgnoreCase(BdisCommand.PING.cmd())) {
            return new SimpleStringRedisMessage(BdisCommand.PONG.cmd());
        }
        if (type.equalsIgnoreCase(BdisCommand.INFO.cmd())) {
            return new SimpleStringRedisMessage(StartConfig.BDIS_INFO);
        }
        return new SimpleStringRedisMessage("ERR unknown command '"+type+"'");
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
