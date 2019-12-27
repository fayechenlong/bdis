package com.beeplay.bdis.server.start;

import com.beeplay.bdis.server.config.StartConfig;
import com.beeplay.bdis.server.protocol.model.single.ChannelPool;
import com.beeplay.bdis.server.protocol.model.single.Protocol;
import com.beeplay.bdis.server.util.LogExceptionStackTrace;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author chenlf
 * @date 2019/10/24
 */
public class BdisServer {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(BdisServer.class);

    private Integer port = StartConfig.BDIS_PORT;
    private static BdisServer nettyServer = new BdisServer();
    public static ChannelPool channelPool;

    public static ChannelHandlerContext channelHandlerContext;

    public static void main(String[] args) throws Exception {
        nettyServer.run();
    }
    private void loadConfig(){
        try {
            Properties properties = new Properties();
            InputStream in = BdisServer.class.getClassLoader().getResourceAsStream("bdis.properties");
            properties.load(in);
            logger.info("config load success!");
            StartConfig.BDIS_PORT=Integer.parseInt(properties.getProperty("bdis.port"));
            Protocol.DEFAULT_HOST=properties.getProperty("bdis.single.redis.host");
            Protocol.DEFAULT_PORT=Integer.parseInt(properties.getProperty("bdis.single.redis.port"));

        }catch (Exception e){
            logger.error("config load error! ", LogExceptionStackTrace.erroStackTrace(e));
        }
    }
    public  void run() throws Exception {

        loadConfig();
        //根据配置加载启动模式

        channelPool = new ChannelPool();
        logger.info("Single redis conntion");

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
