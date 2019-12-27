package com.beeplay.bdis.server.protocol.model.single;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.LoggerFactory;

public class ChannelFactory implements PooledObjectFactory<Connection> {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ChannelFactory.class);
    private String host;
    private int port;

    ChannelFactory(String host,int port){
        this.host=host;
        this.port=port;
    }
    @Override
    public void activateObject(PooledObject<Connection> pooledBdis) throws Exception {

    }
    @Override
    public void destroyObject(PooledObject<Connection> pooledBdis) throws Exception{

    }
    @Override
    public PooledObject<Connection> makeObject() throws Exception {
        Connection conn=new Connection(host,port);
        logger.info("new  redis Connection Connect!");
        return new DefaultPooledObject<Connection>(conn);
    }
    @Override
    public void passivateObject(PooledObject<Connection> pooledBdis) throws Exception {
        // TODO maybe should select db 0? Not sure right now.
    }
    @Override
    public boolean validateObject(PooledObject<Connection> pooledBdis) {
        return true;
    }

}
