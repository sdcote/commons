package coyote;

import coyote.commons.StringUtil;
import coyote.commons.log.ConsoleAppender;
import coyote.commons.log.Log;

public class BootStrap  {
    static {
        // Set up a default uncaught exception handler to assist in diagnosing
        // silent thread death in any components.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                e.printStackTrace(new java.io.PrintWriter(out, true));
                String message = "UNCAUGHT THREAD EXCEPTION: " + t.getName() + ": " + e.getMessage() + "\n" + out.toString();
                Log.fatal(message);
                System.err.println(message);
            }
        });
    }

    /**
     * Return the value in either the named environment variable or system
     * property, returning the given default value if neither are found.
     *
     * <p>The system property takes precedence over the environment variable.</p>
     *
     * @param tag the name of the environment variable or system property to
     *            locate.
     * @param defaultValue The default value to return if neither ar found.
     *
     * @return The value found, or the default value.
     */
    private static String getEnvironmentOrProperty(String tag, String defaultValue) {
        String retval = defaultValue;
        String envVar = System.getenv(tag);
        String sysProp = System.getProperty(tag);
        if (StringUtil.isNotBlank(sysProp)) {
            retval = sysProp;
        } else if (StringUtil.isNotBlank(envVar)) {
            retval = envVar;
        }
        return retval;
    }


    public static void main(String[] args) {

        // set the default logger
        Log.addLogger(Log.DEFAULT_LOGGER_NAME, new ConsoleAppender(Log.NOTICE_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS));

        // Parse the command line arguments
        parseArgs(args);

        Log.info("Verbose logging is enabled");
        Log.debug("Debug logging is enabled");
        Log.debug("Trace logging is enabled");

        // configure the class

        // run the class


    }

    private static void parseArgs(String[] args) {
    }
}
