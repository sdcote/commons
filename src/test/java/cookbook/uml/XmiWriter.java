package cookbook.uml;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.uml.UmlModel;
import coyote.commons.uml.marshal.Xmi11Marshaler;

public class XmiWriter {
    private static final boolean NO_FORMAT = false;

    /**
     *
     * @param xml
     * @param fname
     * @param charset
     * @return true if the file was saved, false if an error occurred.
     */
    public static boolean save(String xml, String fname, String charset) {
        if (StringUtil.checkCharacterSetName(charset)) {
            String b = "<?xml version=\"1.0\" encoding=\"" + charset +
                    "\"?>\r\n" +
                    xml;
            return FileUtil.stringToFile(b, fname, charset);
        } else {
            System.err.println("The character set of '" + charset + "' is not supported in this runtime");
            return false;
        }
    }

    public void write(UmlModel model) {

        if (model != null) {

            Xmi11Marshaler.setName("MyMarshaler");

            Xmi11Marshaler.setVersion("1.0.42");

            String xml = Xmi11Marshaler.marshal(model, NO_FORMAT);

            save(xml, "UmlModelXmi.xml", "UTF-8");

        }

    }
}
