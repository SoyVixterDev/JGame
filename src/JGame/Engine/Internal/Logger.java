package JGame.Engine.Internal;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Function used for better debugging using simpler syntax
 */
public class Logger
{
    // ANSI escape codes for text colors
    public static final String RESET = "\033[0m";  // Reset to default color

    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String MAGENTA = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";

    /**
     * Log a message to the console
     * @param message
     * The message to be logged
     * @param color
     * The color for the message, use Logger.COLOR_NAME
     */
    public static void DebugLog(Object message, String color)
    {
        System.out.println(color + "[MESSAGE]: " + message + RESET);
    }

    /**
     * Logs a message to the console
     * @param message
     * The message to be logged
     */
    public static void DebugLog(Object message)
    {
        System.out.println("[MESSAGE]: " + message);
    }

    /**
     * Logs a yellow warning to the console
     * @param message
     * The message to be logged in the warning
     */
    public static void DebugWarning(Object message)
    {
        System.out.println(YELLOW + "[WARNING]: " + message + RESET);
    }

    /**
     * Logs an error to the console
     * @param message
     * The message to be sent as an error
     */
    public static void DebugError(Object message)
    {
        System.err.println(RED + "[ERROR]: " + message);
    }

    /**
     * Logs an error to the console, including the stack trace of an exception
     * @param message
     * The message to print
     * @param exception
     * The exception to extract the stack trace from
     */
    public static void DebugStackTraceError(String message, Exception exception)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        System.err.println(RED + "[ERROR]: " + message + "\nStack trace:\n" + sw + RESET);
    }
}
