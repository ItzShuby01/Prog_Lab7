package org.example.common.command;

import java.io.Serializable;
import org.example.common.data.Person;

public class UpdateCommand extends Command implements Serializable {
  private static final long serialVersionUID = 1L;
  private final Person person; // The Person object with updated data

  public UpdateCommand(String arg, Person person) { // 'arg' will typically be the ID string
    super("update", arg);
    this.person = person;
  }

  public Person getPerson() {
    return person;
  }
}
