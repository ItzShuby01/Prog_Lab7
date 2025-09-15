package org.example.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.example.client.util.IOService;
import org.example.client.util.PersonBuilder;
import org.example.client.util.PersonIOService;
import org.example.common.command.*;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.common.util.SerializationUtil;

public class ClientRunner {
  private static final int BUFFER_SIZE = 65535;

  private final String serverAddress;
  private final int serverPort;
  private final DatagramChannel clientChannel;
  private final InetSocketAddress serverSocketAddress;
  private String username;
  private String password;
  private final PersonIOService personIOService;
  private final IOService ioService;
  private final Set<String> executingScripts = new HashSet<>();

  public ClientRunner(String serverAddress, int serverPort, IOService ioService)
          throws IOException {
    this.serverAddress = serverAddress;
    this.serverPort = serverPort;
    this.ioService = ioService;
    this.clientChannel = DatagramChannel.open();
    this.clientChannel.configureBlocking(false);
    this.serverSocketAddress = new InetSocketAddress(this.serverAddress, this.serverPort);
    this.personIOService = new PersonIOService(ioService);
  }

  public void run() {
    System.out.println("Client is ready. Type 'help' for available commands.");

    while (true) {
      String input = ioService.readLine("> ");

      if (input == null || input.trim().isEmpty()) {
        continue;
      }

      String[] parts = input.trim().split("\\s+", 2);
      String commandName = parts[0].toLowerCase();
      String arg = parts.length > 1 ? parts[1] : null;

      try {
        if ("exit".equalsIgnoreCase(commandName)) {
          System.out.println("Exiting client.");
          break;
        } else if ("execute_script".equalsIgnoreCase(commandName)) {
          if (arg == null || arg.isEmpty()) {
            System.err.println("execute_script requires a file path.");
          } else {
            executeScript(arg);
          }
        } else {
          // For all other commands, process and send to server
          Command commandDto = processCommand(commandName, arg);
          if (commandDto != null) {
            Response response = sendCommand(commandDto);
            System.out.println("Server Response: " + response.getMessage());
          }
        }
      } catch (Exception e) {
        System.err.println("Error processing command: " + e.getMessage());
      }
    }
  }

  private void executeScript(String filePath) {
    if (executingScripts.contains(filePath)) {
      System.err.println("Error: Recursive script execution detected for: " + filePath);
      return;
    }

    if (!Files.exists(Path.of(filePath)) || !Files.isReadable(Path.of(filePath))) {
      System.err.println("Cannot read script file: " + filePath);
      return;
    }

    executingScripts.add(filePath);
    try {
      System.out.println("Executing script from file: " + filePath);
      List<String> scriptLines = Files.readAllLines(Path.of(filePath));
      int lineIndex = 0;

      while (lineIndex < scriptLines.size()) {
        String line = scriptLines.get(lineIndex++).trim();
        if (line.isEmpty() || line.startsWith("#")) {
          continue;
        }

        String[] parts = line.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : null;

        System.out.println("--- Executing from script: " + line);

        try {
          if ("exit".equalsIgnoreCase(commandName)) {
            System.out.println("Exiting client from script.");
            System.exit(0);
          } else if ("execute_script".equalsIgnoreCase(commandName)) {
            if (arg == null || arg.isEmpty()) {
              System.err.println("execute_script requires a file path.");
            } else {
              executeScript(arg);
            }
          } else {
            Command commandDto;
            switch (commandName) {
              case "add":
              case "add_if_max":
              case "update":
              case "remove_lower":
                // Check if enough lines are left for a full Person object
                if (lineIndex + 9 >= scriptLines.size()) {
                  throw new IllegalArgumentException(
                          "Script ended unexpectedly. Missing person details.");
                }
                List<String> personData = scriptLines.subList(lineIndex, lineIndex + 10);
                Person person = PersonBuilder.buildFromScript(personData);
                lineIndex += 10;

                switch (commandName) {
                  case "add":
                    commandDto = new AddCommand(arg, person, username);
                    break;
                  case "add_if_max":
                    commandDto = new AddIfMaxCommand(arg, person, username);
                    break;
                  case "update":
                    commandDto = new UpdateCommand(arg, person, username);
                    break;
                  case "remove_lower":
                    commandDto = new RemoveLowerCommand(arg, person, username);
                    break;
                  default:
                    throw new IllegalStateException("Unexpected command in script.");
                }
                break;
              default:
                commandDto = processCommand(commandName, arg);
                break;
            }

            Response response = sendCommand(commandDto);
            System.out.println("Server Response for '" + line + "': " + response.getMessage());
          }
        } catch (Exception e) {
          System.err.println("Error executing script command '" + line + "': " + e.getMessage());
        }
      }
    } catch (IOException e) {
      System.err.println("Error reading script file: " + e.getMessage());
    } finally {
      executingScripts.remove(filePath);
    }
  }

  private Command processCommand(String commandName, String arg) {
    switch (commandName) {
      case "help":
        return new HelpCommand(arg);
      case "info":
        return new InfoCommand(arg);
      case "show":
        return new ShowCommand(arg);
      case "add":
        ioService.print("--- Entering Person details for 'add' ---");
        Person person = personIOService.readPerson();
        return new AddCommand(arg, person, username);
      case "update":
        if (arg == null || arg.isEmpty()) {
          throw new IllegalArgumentException("Update command requires an ID.");
        }
        ioService.print("--- Entering new Person details for 'update' (ID: " + arg + ") ---");
        Person updatedPerson = personIOService.readPerson();
        return new UpdateCommand(arg, updatedPerson, username);
      case "remove_by_id":
        if (arg == null || arg.isEmpty()) {
          throw new IllegalArgumentException("remove_by_id requires an ID.");
        }
        return new RemoveByIdCommand(arg, username);
      case "clear":
        return new ClearCommand(arg, username);
      case "add_if_max":
        ioService.print("--- Entering Person details for 'add_if_max' ---");
        Person addIfMaxPerson = personIOService.readPerson();
        return new AddIfMaxCommand(arg, addIfMaxPerson, username);
      case "remove_lower":
        ioService.print("--- Entering Person details for 'remove_lower' ---");
        Person removeLowerPerson = personIOService.readPerson();
        return new RemoveLowerCommand(arg, removeLowerPerson, username);
      case "history":
        return new HistoryCommand(arg);
      case "max_by_id":
        return new MaxByIdCommand(arg);
      case "average_of_height":
        return new AverageOfHeightCommand(arg);
      case "count_by_location":
        if (arg == null || arg.isEmpty()) {
          throw new IllegalArgumentException("count_by_location requires a location argument.");
        }
        return new CountByLocationCommand(arg);
      default:
        throw new IllegalArgumentException("Unknown command: " + commandName);
    }
  }

  private Response sendCommand(Serializable serializable)
          throws IOException, ClassNotFoundException {
    byte[] commandBytes = SerializationUtil.serialize(serializable);
    ByteBuffer buffer = ByteBuffer.wrap(commandBytes);
    clientChannel.send(buffer, serverSocketAddress);

    // Wait/receive response
    ByteBuffer responseBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    InetSocketAddress senderAddress = null;
    while (senderAddress == null) {
      senderAddress = (InetSocketAddress) clientChannel.receive(responseBuffer);
    }

    responseBuffer.flip();
    byte[] responseBytes = new byte[responseBuffer.remaining()];
    responseBuffer.get(responseBytes);
    return (Response) SerializationUtil.deserialize(responseBytes);
  }

  public Response executeLogin(LoginCommand loginCommand) throws IOException {
    try {
      return sendCommand(loginCommand);
    } catch (ClassNotFoundException e) {
      throw new IOException("Error deserializing server response.", e);
    }
  }

  public Response executeRegister(RegisterCommand registerCommand) throws IOException {
    try {
      return sendCommand(registerCommand);
    } catch (ClassNotFoundException e) {
      throw new IOException("Error deserializing server response.", e);
    }
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
