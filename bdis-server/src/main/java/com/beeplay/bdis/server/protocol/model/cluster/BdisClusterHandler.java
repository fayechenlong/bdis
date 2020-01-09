package com.beeplay.bdis.server.protocol.model.cluster;

import com.beeplay.bdis.server.bcache.Bcache;
import com.beeplay.bdis.server.config.StartConfig;
import com.beeplay.bdis.server.protocol.BdisClientPool;
import com.beeplay.bdis.server.util.LogExceptionStackTrace;
import com.beeplay.bdis.server.util.RedisMessageUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.*;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Expirations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author chenlf
 * @date 2019/10/24
 */
public class BdisClusterHandler extends ClusterHandler {
    private static Logger logger = LoggerFactory.getLogger(BdisClusterHandler.class);

    public BdisClusterHandler(){
        init();
    }
    private void init(){
        try {
            String [] clusterHosts= StartConfig.BDIS_CLUSTER_HOSTS.split(",");
            Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
            for(String hostAndPort:clusterHosts){
                String[] hap=hostAndPort.split(":");
                jedisClusterNodes.add(new HostAndPort(hap[0], Integer.parseInt(hap[1])));
            }
            jedisCluster = new JedisCluster(jedisClusterNodes);

            //提高读取性能增加本地缓存
            CacheManager cacheManager= CacheManagerBuilder.newCacheManagerBuilder().withCache("jedisCluster", CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(1000))
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofDays(1)))).build(true);
            localCache=cacheManager.getCache("jedisCluster",String.class,String.class);
        }catch (Exception e){
            logger.error("bdis started in jedisCluster model failed :",e.getMessage());
        }

    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  {

        RedisMessageUtil.printAggregatedRedisResponseCommand(((RedisMessage)msg));//日志打印

        List<FullBulkStringRedisMessage> fullBulkStringRedisMessages=RedisMessageUtil.coverMessage(msg);
        try {
            sendCommand(ctx,fullBulkStringRedisMessages);
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
