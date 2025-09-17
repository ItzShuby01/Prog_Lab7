package org.example.common.util;

// A simple utility class for logging messages with different levels.
public class Logger {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";

  // Prints an informational message to the console.
  public static void info(String message) {
    System.out.println(ANSI_GREEN + "[INFO] " + message + ANSI_RESET);
  }

  // Prints a warning message to the console.
  public static void warn(String message) {
    System.out.println(ANSI_YELLOW + "[WARN] " + message + ANSI_RESET);
  }

  // Prints an error message to the console.
  public static void error(String message) {
    System.err.println(ANSI_RED + "[ERROR] " + message + ANSI_RESET);
  }
}
