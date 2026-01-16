package cookbook.dataframe;

import coyote.commons.dataframe.FrameSet;
import coyote.commons.dataframe.marshal.CSVMarshaler;
import coyote.commons.log.ConsoleAppender;
import coyote.commons.log.Log;

public class CSVFiles {
    static{
        Log.addLogger(Log.DEFAULT_LOGGER_NAME, new ConsoleAppender(Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS));
    }

    public static void main(String[] args) {
        FrameSet table = CSVMarshaler.read("C:\\Users\\scote\\Code\\Java\\ProcessInventory\\HostDetails.csv");
        Log.info("Loaded a table with "+table.size()+" rows");
    }
}
