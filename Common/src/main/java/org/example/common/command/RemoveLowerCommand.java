package org.example.common.command;

import java.io.Serializable;
import org.example.common.data.Person;


public class RemoveLowerCommand extends Command implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String arg;
  private final Person person;
  private final String username;

  public RemoveLowerCommand(String arg, Person person, String username) {
    super("add");
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
