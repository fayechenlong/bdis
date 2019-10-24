package com.beeplay.bdis.server.start;

import com.beeplay.bdis.server.config.StartConfig;
import com.beeplay.bdis.server.protocol.BdisHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenlf
 * @date 2019/10/24
 */
public class BdisServer {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(BdisServer.class);
    private Integer port = StartConfig.BDIS_PORT;
    private static BdisServer nettyServer = new BdisServer();
    public static void main(String[] args) throws InterruptedException {
        nettyServer.run();
    }
    public  void run() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            serverBootstrap.group(group);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline p = socketChannel.pipeline();
                    p.addLast(new RedisDecoder());
                    p.addLast(new RedisBulkStringAggregator());
                    p.addLast(new RedisArrayAggregator());
                    p.addLast(new RedisEncoder());
                    p.addLast(new BdisHandler());
                }
            });
            serverBootstrap.channel(NioServerSocketChannel.class);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            logger.info("bdis start success！port:" + port);
            channelFuture.channel().closeFuture().sync();
        } catch(Exception e){
            logger.info("bdis start failed!");
            logger.info("error message:{}",e.getMessage());
        }finally {
            group.shutdownGracefully();
        }
    }
}
