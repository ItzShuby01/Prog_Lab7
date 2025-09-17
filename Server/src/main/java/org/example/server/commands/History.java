package org.example.server.commands;

import org.example.common.command.HistoryCommand;
import org.example.common.response.Response;
import org.example.server.manager.CommandManager;

public class History implements ServerCommand {
  public static final String DESCRIPTION =
      "history: print the last 7 commands (without their arguments)";

  private final CommandManager commandManager;

  public History(CommandManager commandManager) {
    this.commandManager = commandManager;
  }

  public Response execute(HistoryCommand commandDto) {
    var commandHistory = commandManager.getCommandHistory();
    if (commandHistory.isEmpty()) {
      return new Response("No commands in history.", true);
    }
    // Return the history list in the data payload of the Response
    return new Response("Last " + commandHistory.size() + " commands:", true, commandHistory);
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}
