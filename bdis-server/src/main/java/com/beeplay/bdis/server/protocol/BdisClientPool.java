package com.beeplay.bdis.server.protocol;

import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * bdis客户端连接管理
 */
public class BdisClientPool {
    public static ConcurrentMap<String,ChannelHandlerContext> bdisClients=new ConcurrentHashMap<>();
}
