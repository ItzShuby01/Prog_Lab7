package org.example.client;

import org.example.client.util.IOService;
import org.example.client.util.PersonIOService;
import org.example.common.command.*;
import org.example.common.data.Person;
import org.example.common.response.Response;
import org.example.common.util.SerializationUtil;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ClientRunner {
    private static final int BUFFER_SIZE = 65535;

    private final String serverAddress;
    private final int serverPort;
    private DatagramChannel clientChannel;
    private InetSocketAddress serverSocketAddress;
    private String username;
    private String password;
    private final PersonIOService personIOService;
    private final IOService ioService;

    public ClientRunner(String serverAddress, int serverPort, IOService ioService) throws IOException {
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
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting client.");
                break;
            }

            try {
                Response response = processCommand(input);
                if (response != null) {
                    System.out.println("Server Response: " + response.getMessage());
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error processing command: " + e.getMessage());
            }
        }
    }

    public Response processCommand(String input) throws IOException, ClassNotFoundException {
        // Parse the input to create the correct Command DTO
        String[] parts = input.trim().split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : null;

        Command commandDto;

        switch (commandName) {
            case "help":
                commandDto = new HelpCommand(arg);
                break;
            case "info":
                commandDto = new InfoCommand(arg);
                break;
            case "show":
                commandDto = new ShowCommand(arg);
                break;
            case "add":
                ioService.print("--- Entering Person details for 'add' ---");
                Person person = personIOService.readPerson();
                commandDto = new AddCommand(arg, person);
                break;
            case "update":
                if (arg == null || arg.isEmpty()) throw new IllegalArgumentException("Update command requires an ID.");
                ioService.print("--- Entering new Person details for 'update' (ID: " + arg + ") ---");
                Person updatedPerson = personIOService.readPerson();
                commandDto = new UpdateCommand(arg, updatedPerson);
                break;
            case "remove_by_id":
                if (arg == null || arg.isEmpty()) throw new IllegalArgumentException("remove_by_id requires an ID.");
                commandDto = new RemoveByIdCommand(arg);
                break;
            case "clear":
                commandDto = new ClearCommand(arg);
                break;
            case "add_if_max":
                ioService.print("--- Entering Person details for 'add_if_max' ---");
                Person addIfMaxPerson = personIOService.readPerson();
                commandDto = new AddIfMaxCommand(arg, addIfMaxPerson);
                break;
            case "remove_lower":
                ioService.print("--- Entering Person details for 'remove_lower' ---");
                Person removeLowerPerson = personIOService.readPerson();
                commandDto = new RemoveLowerCommand(arg, removeLowerPerson);
                break;
            case "history":
                commandDto = new HistoryCommand(arg);
                break;
            case "max_by_id":
                commandDto = new MaxByIdCommand(arg);
                break;
            case "average_of_height":
                commandDto = new AverageOfHeightCommand(arg);
                break;
            case "count_by_location":
                if (arg == null || arg.isEmpty()) throw new IllegalArgumentException("count_by_location requires a location argument.");
                commandDto = new CountByLocationCommand(arg);
                break;
            default:
                throw new IllegalArgumentException("Unknown command: " + commandName);
        }

        // Attach user credentials to the command
        commandDto.setUsername(username);
        commandDto.setPassword(password);

        // Send the command and receive the response
        return sendCommand(commandDto);
    }

    private Response sendCommand(Serializable serializable) throws IOException, ClassNotFoundException {
        byte[] commandBytes = SerializationUtil.serialize(serializable);
        ByteBuffer buffer = ByteBuffer.wrap(commandBytes);
        clientChannel.send(buffer, serverSocketAddress);

        // Wait/receive  response
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
