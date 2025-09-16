package org.example.common.command;

import java.io.Serializable;
import org.example.common.data.Person;


public class UpdateCommand extends Command implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String arg;
  private final Person person;
  private final String username;

  public UpdateCommand(String arg, Person person, String username) {
    super("update");
    this.arg = arg;
    this.person = person;
    this.username = username;
  }

  public String getArg() {
    return arg;
  }

  public Person getPerson() {
    return person;
  }

  public String getUsername() {
    return username;
  }
}
