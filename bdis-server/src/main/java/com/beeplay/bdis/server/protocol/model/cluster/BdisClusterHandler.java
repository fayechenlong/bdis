package com.beeplay.bdis.server.protocol.model.cluster;

import com.beeplay.bdis.server.command.BdisCommand;
import com.beeplay.bdis.server.config.StartConfig;
import com.beeplay.bdis.server.protocol.model.BdisClientPool;
import com.beeplay.bdis.server.protocol.model.single.Connection;
import com.beeplay.bdis.server.protocol.model.single.RedisClientHandler;
import com.beeplay.bdis.server.start.BdisServer;
import com.beeplay.bdis.server.util.RedisMessageUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenlf
 * @date 2019/10/24
 */
public class BdisClusterHandler extends ChannelDuplexHandler {
    private static Logger logger = LoggerFactory.getLogger(BdisClusterHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RedisMessageUtil.printAggregatedRedisResponseCommand(((RedisMessage)msg));//日志打印

        List<RedisMessage> messages=((ArrayRedisMessage)msg).children();
        String type=((FullBulkStringRedisMessage)messages.get(0)).content().toString(CharsetUtil.UTF_8);
        String out="(null)";
        if(type.equalsIgnoreCase(BdisCommand.SET.cmd())){
            if(messages.size()!=3){
                ctx.writeAndFlush(new SimpleStringRedisMessage("ERR: error command '" + type + "'"));
                return;
            }
            out=BdisServer.jedisCluster.set(((FullBulkStringRedisMessage)messages.get(1)).content().toString(CharsetUtil.UTF_8),((FullBulkStringRedisMessage)messages.get(2)).content().toString(CharsetUtil.UTF_8));
            if(out!=null) {
                ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), out)));
            }else {
                ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), BdisCommand.NULL.cmd())));
            }
        }else if(type.equalsIgnoreCase(BdisCommand.DEL.cmd())){
            if(messages.size()!=2){
                ctx.writeAndFlush(new SimpleStringRedisMessage("ERR: error command '" + type + "'"));
                return;
            }
            Long outLong=BdisServer.jedisCluster.del(((FullBulkStringRedisMessage)messages.get(1)).content().toString(CharsetUtil.UTF_8));
            if(outLong!=null) {
                ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), String.valueOf(outLong))));
            }else {
                ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), BdisCommand.NULL.cmd())));
            }
        }else if(type.equalsIgnoreCase(BdisCommand.GET.cmd())){
            if(messages.size()!=2){
                ctx.writeAndFlush(new SimpleStringRedisMessage("ERR: error command '" + type + "'"));
                return;
            }
            out=BdisServer.jedisCluster.get(((FullBulkStringRedisMessage)messages.get(1)).content().toString(CharsetUtil.UTF_8));
            if(out!=null) {
                ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), out)));
            }else {
                ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), BdisCommand.NULL.cmd())));
            }
        }else if (type.equalsIgnoreCase(BdisCommand.PING.cmd())) {
            ctx.writeAndFlush(new SimpleStringRedisMessage(BdisCommand.PONG.cmd()));
        }else if (type.equalsIgnoreCase(BdisCommand.INFO.cmd())) {
            ctx.writeAndFlush(new SimpleStringRedisMessage(StartConfig.BDIS_INFO));
        }else {
            ctx.writeAndFlush(new SimpleStringRedisMessage("ERR unknown command '" + type + "'"));
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
        BdisServer.channelHandlerContext=ctx;
    }
}
