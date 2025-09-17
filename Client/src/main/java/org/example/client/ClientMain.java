package org.example.client;

import java.io.IOException;
import java.util.Scanner;
import org.example.client.util.ConsoleIOService;
import org.example.client.util.IOService;
import org.example.common.command.LoginCommand;
import org.example.common.command.RegisterCommand;
import org.example.common.response.Response;

public class ClientMain {
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Usage: java -jar Client.jar <server_address> <port>");
      return;
    }

    String serverAddress = args[0];
    int serverPort = Integer.parseInt(args[1]);

    try (IOService ioService = new ConsoleIOService(new Scanner(System.in))) {
      ClientRunner clientRunner = new ClientRunner(serverAddress, serverPort, ioService);
      authenticateUser(clientRunner, ioService);
      clientRunner.run();
    } catch (IOException e) {
      System.err.println("Error connecting to server: " + e.getMessage());
    }
  }

  private static void authenticateUser(ClientRunner clientRunner, IOService ioService) {
    while (true) {
      ioService.print("\n--- Authentication ---");
      ioService.print("1. Login");
      ioService.print("2. Register");
      ioService.print("3. Exit");
      String choice = ioService.readLine("Enter Option number: ").trim();
      if ("3".equalsIgnoreCase(choice)) {
        ioService.print("Exiting client.");
        System.exit(0);
      }

      String username = ioService.readLine("Enter username: ").trim();
      String password = new String(System.console().readPassword("Enter password: "));

      try {
        if ("1".equalsIgnoreCase(choice)) {
          Response response = clientRunner.executeLogin(new LoginCommand(username, password));
          if (response.isSuccess()) {
            ioService.print("Login successful!");
            clientRunner.setUsername(username);
            clientRunner.setPassword(password);
            return;
          } else {
            ioService.print("Login failed: " + response.getMessage());
          }
        } else if ("2".equalsIgnoreCase(choice)) {
          Response response = clientRunner.executeRegister(new RegisterCommand(username, password));
          if (response.isSuccess()) {
            ioService.print("Registration successful. You can now log in.");
          } else {
            ioService.print("Registration failed: " + response.getMessage());
          }
        } else {
          ioService.print("Invalid choice. Please try again.");
        }
      } catch (IOException e) {
        ioService.print("Connection error during authentication: " + e.getMessage());
        System.exit(1);
      }
    }
  }
}
