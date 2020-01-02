package com.beeplay.bdis.server.command;

/**
 * @author chenlf
 * @date 2019/10/24
 */
public enum BdisCommand {
    SET("set"),
    GET("get"),
    DEL("del"),
    INFO("info"),
    SELECT("select"),
    PING("ping"),
    PONG("PONG"),
    OK("OK"),
    NULL("(null)"),
    INCR("INCR"),
    INCRBY("INCRBY");
    private final String cmd;
    BdisCommand(String cmd){
             this.cmd=cmd;
    }
    public String cmd() {
        return this.cmd;
    }
}
