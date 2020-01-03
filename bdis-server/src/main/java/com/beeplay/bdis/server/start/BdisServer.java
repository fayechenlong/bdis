package com.beeplay.bdis.server.start;


import com.beeplay.bdis.server.util.LogExceptionStackTrace;
import org.slf4j.LoggerFactory;

/**
 * @author chenlf
 * @date 2019/10/24
 */
public class BdisServer extends BdisServerAbstract {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(BdisServer.class);
    public static void main(String[] args){
        loadConfig();
        try {
            run();
        }catch (Exception e){
            logger.error("Bdis start failed!", LogExceptionStackTrace.erroStackTrace(e));
        }
    }


}
