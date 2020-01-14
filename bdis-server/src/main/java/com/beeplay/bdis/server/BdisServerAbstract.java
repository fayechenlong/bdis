package com.beeplay.bdis.server;


import com.beeplay.bdis.server.config.StartConfig;
import com.beeplay.bdis.server.protocol.model.bcache.BdisBcacheHandler;
import com.beeplay.bdis.server.protocol.model.cluster.BdisClusterHandler;
import com.beeplay.bdis.server.protocol.model.cluster.ClusterHandler;
import com.beeplay.bdis.server.protocol.model.single.BdisSingleHandler;
import com.beeplay.bdis.server.protocol.model.single.ChannelPool;
import com.beeplay.bdis.server.protocol.model.single.Protocol;
import com.beeplay.bdis.server.util.LogExceptionStackTrace;
import com.beeplay.bdis.server.util.RedisMessageUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class BdisServerAbstract {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(BdisServerAbstract.class);
    private static Integer port = StartConfig.BDIS_PORT;
    public static void loadConfig() {
        try {
            Properties properties = new Properties();
            InputStream in = BdisServerStart.class.getClassLoader().getResourceAsStream("bdis.properties");
            properties.load(in);
            StartConfig.BDIS_PORT = Integer.parseInt(properties.getProperty("bdis.port"));
            StartConfig.BDIS_MODEL = properties.getProperty("bdis.model");
            logger.info("bdis.model:{}",StartConfig.BDIS_MODEL);
            if(StartConfig.BDIS_MODEL.equals("single")) {
                Protocol.DEFAULT_HOST = properties.getProperty("bdis.single.redis.host");
                Protocol.DEFAULT_PORT = Integer.parseInt(properties.getProperty("bdis.single.redis.port"));
                logger.info("redis.host:{} redis.port:{}",Protocol.DEFAULT_PORT,Protocol.DEFAULT_PORT);
            }
            if (StartConfig.BDIS_MODEL.equals("cluster")) {
                StartConfig.BDIS_CLUSTER_HOSTS = properties.getProperty("bdis.cluster.redis.hosts");
                logger.info("cluster.redis.hosts:{}",StartConfig.BDIS_CLUSTER_HOSTS);
            }
            if (StartConfig.BDIS_MODEL.equals("bcache")) {
                StartConfig.heapSize =Long.valueOf(properties.getProperty("bdis.bcache.heapSize"));
                StartConfig.offheapSize = Long.valueOf(properties.getProperty("bdis.bcache.offheapSize"));
                logger.info("bcache memory: heapSize={}MB  offheapSize={}MB",StartConfig.heapSize,StartConfig.offheapSize);
            }
            logger.info("bdis config load success!");
        } catch (Exception e) {
            logger.error("config load error! {}", e.getMessage());
        }
    }
    public  static void run() throws Exception {
        startServer();
    }
    private static void startServer(){
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
                    if(StartConfig.BDIS_MODEL.equals("single")) {
                        p.addLast(new BdisSingleHandler());
                    }
                    if(StartConfig.BDIS_MODEL.equals("cluster")){
                        p.addLast(new BdisClusterHandler());
                    }
                    if(StartConfig.BDIS_MODEL.equals("bcache")){
                        p.addLast(new BdisBcacheHandler());
                    }
                }
            });
            serverBootstrap.channel(NioServerSocketChannel.class);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            if(StartConfig.BDIS_MODEL.equals("single")) {
                logger.info("bdis started in single model !");
            }
            if(StartConfig.BDIS_MODEL.equals("cluster")){
                logger.info("bdis started in jedisCluster model !");
            }
            if(StartConfig.BDIS_MODEL.equals("bcache")){
                logger.info("bdis started in bcache model !");
            }
            logger.info("bdis start success！port:{}",port);
            channelFuture.channel().closeFuture().sync();
        } catch(Exception e){
            logger.info("bdis start failed !");
            logger.info("error message:{}",e.getMessage());
        }finally {
            group.shutdownGracefully();
        }
    }

}
