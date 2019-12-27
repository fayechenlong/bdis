package com.beeplay.bdis.server.exception;

public class BdisException extends RuntimeException {
  private static final long serialVersionUID = -2946266495682282677L;

  public BdisException(String message) {
    super(message);
  }

  public BdisException(Throwable e) {
    super(e);
  }

  public BdisException(String message, Throwable cause) {
    super(message, cause);
  }
}
