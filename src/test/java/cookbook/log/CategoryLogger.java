package cookbook.log;

import coyote.commons.log.ConsoleAppender;
import coyote.commons.log.Log;

public class CategoryLogger {
    public static void main(String[] args) {
        // Let's add a ConsoleAppender in place of the default "NullAppender"
        Log.addLogger(Log.DEFAULT_LOGGER_NAME, new ConsoleAppender());

        // Because we are not using levels, we can enable and disable any category we wish
        // For example, Info is usually enabled if Warn events are enabled. What if we only want Warn events?
        Log.info("You should not see this message.");
        Log.debug("You should not see this message.");
        Log.warn("You should not see this message.");
        Log.error("You should not see this message.");
        Log.fatal("You should not see this message.");
        Log.trace("You should not see this message.");

        // Tell the logger to only log "WARN" events
        System.out.println("--------[ Only WARN Messages ]--------");
        Log.startLogging(Log.WARN);
        Log.info("This is an info log message!");
        Log.debug("This is a debug log message!");
        Log.warn("This is a warn log message!");
        Log.error("This is an error log message!");
        Log.fatal("This is a fatal log message!");
        Log.trace("This is a trace log message!");

        // This means we can create only the category of events we want to log
        System.out.println("--------[ Security Events Added ]--------");

        // Tell the system be also log "SECURITY" events
        Log.startLogging("SECURITY");

        //Because we don't know what custom categories you might want, we have to use the generic append method
        Log.append("SECURITY", "This is a security event");

        // You can use append for the standard categories (TRACE, DEBUG, INFO, WARN, ERROR, and FATAL)
        Log.append("WARN", "This is a warning event");

        Log.startLoggingAllCategories();
        System.out.println("--------[ All Possible Categories Added ]--------");
        Log.info("This is an info log message!");
        Log.debug("This is a debug log message!");
        Log.warn("This is a warn log message!");
        Log.error("This is an error log message!");
        Log.fatal("This is a fatal log message!");
        Log.trace("This is a trace log message!");
        Log.append("SECURITY", "This is a security message!");

    }
}
