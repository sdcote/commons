package cookbook.log;

import coyote.commons.log.ConsoleAppender;
import coyote.commons.log.Log;

public class ConsoleLogger {
    public static void main(String[] args) {
        // Nothing should be output as logging has not been initialized,
        // the default logger is a "NullLogger" that does nothing
        Log.info ("This is an info log message!" );
        Log.debug("This is a debug log message!" );
        Log.warn ("This is a warn log message!" );
        Log.error("This is an error log message!" );
        Log.fatal("This is a fatal log message!" );
        Log.trace("This is a trace log message!" );
        // This means it is safe to add log messages to all your code. Logging only happens if logging is configured.

        // Let's replace the default "Null" logger with a console appender.
        Log.addLogger(Log.DEFAULT_LOGGER_NAME, new ConsoleAppender(Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS));

        Log.info ("This is an info log message!" );
        Log.debug ("You should not see this message." );
        Log.warn ("This is a warn log message!" );
        Log.error("This is an error log message!" );
        Log.fatal("This is a fatal log message!" );
        Log.trace ("You should not see this message." );
        // Notice how Debug and Trace events were not displayed to the console.
        // When we added the Console logger, we did not enable those categories

    }
}
