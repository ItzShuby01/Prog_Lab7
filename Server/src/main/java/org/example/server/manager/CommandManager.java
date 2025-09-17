package org.example.server.manager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

// Manages the command history
public class CommandManager {
    private static final int HISTORY_SIZE = 7;
    private final Deque<String> commandHistory;

    public CommandManager() {
        this.commandHistory = new ArrayDeque<>(HISTORY_SIZE);
    }

    // Adds a command to the history.
    public void addCommandToHistory(String commandName) {
        if (commandHistory.size() == HISTORY_SIZE) {
            commandHistory.removeFirst();
        }
        commandHistory.addLast(commandName);
    }

    //Returns a list of the last commands in the history.
    public List<String> getCommandHistory() {
        return commandHistory.stream().collect(Collectors.toList());
    }
}
