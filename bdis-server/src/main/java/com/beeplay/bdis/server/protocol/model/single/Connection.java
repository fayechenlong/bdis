package com.beeplay.bdis.server.protocol.model.single;


import com.beeplay.bdis.server.util.LogExceptionStackTrace;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import org.slf4j.LoggerFactory;

public class Connection {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Connection.class);
    private String host;
    private int port;
    private String chxId;
    private RedisClientHandler redisClientHandler= new RedisClientHandler();
    public String getChxId() {
        return chxId;
    }

    public void setChxId(String chxId) {
        this.chxId = chxId;
    }

    Connection(String host, int port){
        try {
            this.host=host;
            this.port=port;
            connect();
        }catch (Exception e){
            logger.error("connect redis error! ",LogExceptionStackTrace.erroStackTrace(e));
        }
    }
    private Channel ch;

    public void connect() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast(new RedisDecoder());
                        p.addLast(new RedisBulkStringAggregator());
                        p.addLast(new RedisArrayAggregator());
                        p.addLast(new RedisEncoder());
                        p.addLast(redisClientHandler);
                    }
                });
        ch = b.connect(host, port).sync().channel();
    }
    public Object write(Object msg){
        ch.writeAndFlush(msg);
        redisClientHandler.setChxId(chxId);
        logger.info("channel id="+ch.id());
        return "";
    }

}
