package org.example.common.command;

import java.io.Serializable;

public class LoginCommand extends Command implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String username;
  private final String password;

  public LoginCommand(String username, String password) {
    super("login", "");
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
