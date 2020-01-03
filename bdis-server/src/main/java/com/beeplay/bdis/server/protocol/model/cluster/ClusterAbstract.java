package com.beeplay.bdis.server.protocol.model.cluster;


import com.beeplay.bdis.server.command.BdisCommand;
import com.beeplay.bdis.server.util.GfJsonUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import redis.clients.jedis.ScanResult;

public abstract class ClusterAbstract extends ChannelDuplexHandler {

    public void returnData(String out,ChannelHandlerContext ctx){
        if(out!=null) {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), out)));
        }else {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), BdisCommand.NULL.cmd())));
        }
    }
    public void returnData(ScanResult<String> sr, ChannelHandlerContext ctx){
        ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), sr.getCursor())));
        if(sr.getResult().size()>0) {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), GfJsonUtil.toJSONString(sr.getResult()))));
        }else {
            empty(ctx);
        }
    }
    public void returnData(Long outLong,ChannelHandlerContext ctx){
        if(outLong!=null) {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), String.valueOf(outLong))));
        }else {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), BdisCommand.NULL.cmd())));
        }
    }
    public void unknownCommand(ChannelHandlerContext ctx){
        ctx.writeAndFlush(new SimpleStringRedisMessage("ERR : need more parms!"));
    }
    public void empty(ChannelHandlerContext ctx){
        ctx.writeAndFlush(new SimpleStringRedisMessage("(empty list or set)"));
    }
    public void unknownCommand(ChannelHandlerContext ctx,String command){
        ctx.writeAndFlush(new SimpleStringRedisMessage("ERR unknown command '" + command + "'"));
    }
}
