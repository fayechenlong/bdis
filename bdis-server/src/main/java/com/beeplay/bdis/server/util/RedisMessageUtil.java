package com.beeplay.bdis.server.util;

import com.beeplay.bdis.server.protocol.model.single.BdisSingleHandler;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RedisMessageUtil {
    private static Logger logger = LoggerFactory.getLogger(RedisMessageUtil.class);
    public static Object printAggregatedRedisResponse(RedisMessage msg) {
        if (msg instanceof SimpleStringRedisMessage) {
            return ((SimpleStringRedisMessage) msg).content();
        } else if (msg instanceof ErrorRedisMessage) {
            return ((ErrorRedisMessage) msg).content();
        } else if (msg instanceof IntegerRedisMessage) {
            return ((IntegerRedisMessage) msg).toString();
        } else if (msg instanceof FullBulkStringRedisMessage) {
            return getString((FullBulkStringRedisMessage) msg);
        }  else if (msg instanceof ArrayRedisMessage) {
            StringBuffer returnString=new StringBuffer("[");
            int a=0;
            for (RedisMessage child : ((ArrayRedisMessage) msg).children()) {
                a++;
                returnString=returnString.append(printAggregatedRedisResponse(child)).append(",");
                if(a>10){
                    break;
                }
            }
            return returnString.append("....]");
        }else {
            return "(null)";
        }
    }
    private static String getString(FullBulkStringRedisMessage msg) {
        if (msg.isNull()) {
            return "(null)";
        }
        return msg.content().toString(CharsetUtil.UTF_8);
    }
    public static void printAggregatedRedisResponseReturn(RedisMessage msg) {
        if (msg instanceof SimpleStringRedisMessage) {
            logger.info("return : "+((SimpleStringRedisMessage) msg).content());
        } else if (msg instanceof ErrorRedisMessage) {
            logger.info("return : "+((ErrorRedisMessage) msg).content());
        } else if (msg instanceof IntegerRedisMessage) {
            logger.info("return : "+String.valueOf(((IntegerRedisMessage) msg).value()));
        } else if (msg instanceof FullBulkStringRedisMessage) {
            logger.info("return : "+getString((FullBulkStringRedisMessage) msg));
        } else if (msg instanceof ArrayRedisMessage) {
            StringBuffer returnString=new StringBuffer("[");
            int a=0;
            for (RedisMessage child : ((ArrayRedisMessage) msg).children()) {
                a++;
                returnString=returnString.append(RedisMessageUtil.printAggregatedRedisResponse(child)).append(",");
                if(a>10){
                    break;
                }
            }
            logger.info("return : "+returnString.append(".....]"));
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }
    public static void printAggregatedRedisResponseCommand(RedisMessage msg) {
        if (msg instanceof SimpleStringRedisMessage) {
            logger.info("command : "+((SimpleStringRedisMessage) msg).content());
        } else if (msg instanceof ErrorRedisMessage) {
            logger.info("command : "+((ErrorRedisMessage) msg).content());
        } else if (msg instanceof IntegerRedisMessage) {
            logger.info("command : "+((IntegerRedisMessage) msg).toString());
        } else if (msg instanceof FullBulkStringRedisMessage) {
            logger.info("command : "+getString((FullBulkStringRedisMessage) msg));
        } else if (msg instanceof ArrayRedisMessage) {
            String returnString="";
            for (RedisMessage child : ((ArrayRedisMessage) msg).children()) {
                returnString=returnString+ RedisMessageUtil.printAggregatedRedisResponse(child)+" ";
            }
            logger.info("command : "+returnString);
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }
}
