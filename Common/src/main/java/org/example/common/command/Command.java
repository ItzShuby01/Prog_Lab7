package org.example.common.command;

import java.io.Serializable;

 // This class is a Data Transfer Object (DTO) used to pass command information from the client to the server.
 // It contains the command name, arguments, and user authentication data
public class Command implements Serializable {
    private String name;
    private String arg;
    private Serializable payload;
    private String username;
    private String password;

    public Command(String name) {
        this.name = name;
    }

    public Command(String name, String arg) {
        this.name = name;
        this.arg = arg;
    }

    public Command(String name, String arg, Serializable payload) {
        this.name = name;
        this.arg = arg;
        this.payload = payload;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public Serializable getPayload() {
        return payload;
    }

    public void setPayload(Serializable payload) {
        this.payload = payload;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", arg='" + arg + '\'' +
                ", payload=" + payload +
                '}';
    }
}
