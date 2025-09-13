package org.example.common.command;

import java.io.Serializable;
import org.example.common.data.Person;

public class AddCommand extends Command implements Serializable {
  private static final long serialVersionUID = 1L;
  private final Person person;

  public AddCommand(String arg, Person person) {
    super("add", arg);
    this.person = person;
  }

  public Person getPerson() {
    return person;
  }
}
