package org.example.common.command;

import java.io.Serializable;


public class RemoveByIdCommand extends Command implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String arg;
  private final String username;

  public RemoveByIdCommand(String arg, String username) {
    super("remove_by_id");
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
