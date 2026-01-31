package coyote.commons;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.FrameSet;
import coyote.commons.dataframe.marshal.CSVMarshaler;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import coyote.commons.dataframe.marshal.MarshalException;

public class FrameUtil {

    /**
     *
     * @param filename
     * @param frame
     */
    public static void save(String filename, DataFrame frame) {
        if (frame != null) {
            FileUtil.stringToFile(JSONMarshaler.marshal(frame), filename);
        }
    }

    /**
     *
     * @param filename
     * @param frame
     */
    public static void saveFormatted(String filename, DataFrame frame) {
        if (frame != null) {
            FileUtil.stringToFile(JSONMarshaler.toFormattedString(frame), filename);
        }
    }


    /**
     * Read an entire CSV file into a FrameSet.
     *
     * @param s
     * @return
     * @throws MarshalException
     */
    public static FrameSet readCsvFile(String s) throws MarshalException {
        return CSVMarshaler.read(s);
    }

    public static void writeCsvFile(FrameSet frameset, String filename) {
        CSVMarshaler.write(frameset, filename);
    }

}
