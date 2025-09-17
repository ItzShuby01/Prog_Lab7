package org.example.common.command;

import java.io.Serializable;


public class ClearCommand extends Command implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String arg;
  private final String username;

  public ClearCommand(String arg, String username) {
    super("clear");
    this.arg = arg;
    this.username = username;
  }

  public String getArg() {
    return arg;
  }

  public String getUsername() {
    return username;
  }
}
