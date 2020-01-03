package com.beeplay.bdis.server.command;

/**
 * @author chenlf
 * @date 2019/10/24
 */
public enum BdisCommand {
    PONG("PONG"),
    NULL("(null)");
    private final String cmd;
    BdisCommand(String cmd){
             this.cmd=cmd;
    }
    public String cmd() {
        return this.cmd;
    }
}
