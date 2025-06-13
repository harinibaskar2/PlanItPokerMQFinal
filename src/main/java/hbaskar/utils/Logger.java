package hbaskar.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clean logging utility following software engineering best practices
 * Thread-safe, configurable, and performance-optimized
 * 
 * @author PlanItPoker Team
 * @version 1.0
 */
public class Logger {
    
    public enum LogLevel {
        DEBUG(0), INFO(1), WARN(2), ERROR(3);
        
        private final int priority;
        
        LogLevel(int priority) {
            this.priority = priority;
        }
        
        public int getPriority() {
            return priority;
        }
    }
    
    private static final String LOG_FORMAT = "[%s] %s - %s: %s";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static volatile LogLevel currentLogLevel = LogLevel.INFO;
    private static volatile boolean isEnabled = true;
    
    // Private constructor to prevent instantiation
    private Logger() {}
    
    /**
     * Set minimum log level (thread-safe)
     */
    public static void setLogLevel(LogLevel level) {
        currentLogLevel = level != null ? level : LogLevel.INFO;
    }
    
    /**
     * Enable or disable logging (thread-safe)
     */
    public static void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    
    /**
     * Log debug message
     */
    public static void debug(String message) {
        log(LogLevel.DEBUG, message, null);
    }
    
    /**
     * Log info message
     */
    public static void info(String message) {
        log(LogLevel.INFO, message, null);
    }
    
    /**
     * Log warning message
     */
    public static void warn(String message) {
        log(LogLevel.WARN, message, null);
    }
    
    /**
     * Log error message
     */
    public static void error(String message) {
        log(LogLevel.ERROR, message, null);
    }
    
    /**
     * Log error with exception
     */
    public static void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }
    
    /**
     * Log voting activity (domain-specific)
     */
    public static void logVote(String playerName, String storyTitle, String vote) {
        info(String.format("VOTE: %s voted '%s' for '%s'", 
                          sanitize(playerName), sanitize(vote), sanitize(storyTitle)));
    }
    
    /**
     * Log story selection (domain-specific)
     */
    public static void logStorySelection(String playerName, String storyTitle) {
        info(String.format("SELECTION: %s selected '%s'", 
                          sanitize(playerName), sanitize(storyTitle)));
    }
    
    /**
     * Log room activity (domain-specific)
     */
    public static void logRoomActivity(String activity) {
        info("ROOM: " + sanitize(activity));
    }
    
    /**
     * Log reveal activity (domain-specific)
     */
    public static void logReveal(String storyTitle, boolean isRevealed) {
        info(String.format("REVEAL: Cards %s for '%s'", 
                          isRevealed ? "revealed" : "hidden", sanitize(storyTitle)));
    }
    
    /**
     * Core logging method (thread-safe)
     */
    private static void log(LogLevel level, String message, Throwable throwable) {
        // Early exit if logging disabled or level too low
        if (!isEnabled || level.getPriority() < currentLogLevel.getPriority()) {
            return;
        }
        
        // Validate inputs
        if (message == null) {
            message = "null";
        }
        
        try {
            // Format log message
            String timestamp = LocalDateTime.now().format(TIME_FORMAT);
            String threadName = Thread.currentThread().getName();
            String logMessage = String.format(LOG_FORMAT, timestamp, threadName, level, message);
            
            // Output to console (could be extended to file, etc.)
            System.out.println(logMessage);
            
            // Print stack trace if exception provided
            if (throwable != null && level == LogLevel.ERROR) {
                if (currentLogLevel == LogLevel.DEBUG) {
                    throwable.printStackTrace();
                } else {
                    System.out.println("    Caused by: " + throwable.getClass().getSimpleName() + 
                                     ": " + throwable.getMessage());
                }
            }
            
        } catch (Exception e) {
            // Fallback logging to prevent log failures from crashing the app
            System.err.println("LOGGING ERROR: " + e.getMessage());
            System.err.println("Original message: " + message);
        }
    }
    
    /**
     * Sanitize input to prevent log injection attacks
     */
    private static String sanitize(String input) {
        if (input == null) {
            return "null";
        }
        
        // Remove/escape potentially dangerous characters
        return input.replaceAll("[\r\n\t]", "_")
                   .replaceAll("[\\p{Cntrl}]", "")
                   .trim();
    }
    
    /**
     * Get current log level
     */
    public static LogLevel getCurrentLogLevel() {
        return currentLogLevel;
    }
    
    /**
     * Check if logging is enabled
     */
    public static boolean isEnabled() {
        return isEnabled;
    }
    
    /**
     * Check if level would be logged
     */
    public static boolean isLevelEnabled(LogLevel level) {
        return isEnabled && level.getPriority() >= currentLogLevel.getPriority();
    }
}