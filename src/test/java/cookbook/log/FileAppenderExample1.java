package cookbook.log;

import coyote.commons.log.FileAppender;
import coyote.commons.log.Log;

import java.io.File;

public class FileAppenderExample1 {

    public static void main(String[] args) {
        File target = new File("FileExample1.log");
        System.out.println("target: " + target.getAbsolutePath());

        // Use FileAppender to create new logger
        FileAppender myLogger = new FileAppender(target);
        Log.addLogger("MyLogger", myLogger);

        //Start logging these categories
        myLogger.startLogging(Log.INFO);
        myLogger.startLogging(Log.FATAL);
        myLogger.startLogging(Log.ERROR);
        Log.info("This is an info log message!");
        Log.debug("This is a debug log message!");
        Log.warn("This is a warn log message!");
        Log.error("This is an error log message!");
        Log.fatal("This is a fatal log message!");

    }

}