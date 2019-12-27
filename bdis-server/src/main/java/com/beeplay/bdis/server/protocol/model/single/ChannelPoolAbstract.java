package com.beeplay.bdis.server.protocol.model.single;
import com.beeplay.bdis.server.util.Pool;
import io.netty.channel.Channel;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * 通道池基类
 */
public class ChannelPoolAbstract extends Pool<Connection> {
    public ChannelPoolAbstract() {
        super();
    }

    public ChannelPoolAbstract(GenericObjectPoolConfig poolConfig, PooledObjectFactory<Connection> factory) {
        super(poolConfig, factory);
    }

    @Override
    protected void returnBrokenResource(Connection resource) {
        super.returnBrokenResource(resource);
    }

    @Override
    protected void returnResource(Connection resource) {
        super.returnResource(resource);
    }
}
