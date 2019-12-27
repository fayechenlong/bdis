package com.beeplay.bdis.server.protocol.model.single;
import com.beeplay.bdis.server.exception.BdisException;
import io.netty.channel.Channel;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * 通道池
 */
public class ChannelPool extends ChannelPoolAbstract{

    public ChannelPool() throws Exception {
        this(new GenericObjectPoolConfig(),Protocol.DEFAULT_HOST, Protocol.DEFAULT_PORT);
    }

    public ChannelPool(final GenericObjectPoolConfig poolConfig, final String host, final int port) {
        super(poolConfig,new ChannelFactory(host,port));
    }
    @Override
    public Connection getResource() {
        Connection connection = super.getResource();
        return connection;
    }
    @Override
    public void returnBrokenResource(final Connection resource) {
        if (resource != null) {
            returnBrokenResourceObject(resource);
        }
    }
    @Override
    public void returnResource(final Connection resource) {
        if (resource != null) {
            try {
                returnResourceObject(resource);
            } catch (Exception e) {
                returnBrokenResource(resource);
                throw new BdisException("Resource is returned to the pool as broken", e);
            }
        }
    }
    
}
