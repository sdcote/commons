package cookbook.uml;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.log.ConsoleAppender;
import coyote.commons.log.Log;
import coyote.commons.uml.*;
import coyote.commons.uml.marshal.MarshalerExtension;
import coyote.commons.uml.marshal.SparxExtension;
import coyote.commons.uml.marshal.Xmi25Marshaler;

/**
 * The goal of this class is to demonstrate how to create a UmlModel
 * and generate an XML file to import into a CASE tool
 */
public class UmlCommentExample {

    public static final UmlStereotype HOST_STEREOTYPE = new UmlStereotype("host");

    /**
     * Main entry into the code.
     *
     * @param args command line arguments - ignored in this example.
     */
    public static void main(String[] args) {
        Log.addLogger(Log.DEFAULT_LOGGER_NAME, new ConsoleAppender(Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS));

        UmlCommentExample modeler = new UmlCommentExample();

        UmlModel model = modeler.buildUmlModel();

        Xmi25Marshaler marshaler = new Xmi25Marshaler();
        String xml = marshaler.marshal(model, true);

        save(xml, "UmlModelCommentXmi.xml", "UTF-8");
    }

    /**
     * This adds a preamble to the XML and saves it to disk.
     *
     * <p>
     * This uses the FileUtil class to do the heavy lifting for us.
     * </p>
     *
     * @param xml     The XML generated from a marshaller.
     * @param fname   The name of the file to write.
     * @param charset The characterset to use.
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

    /**
     * This is where you create the UML Model.
     *
     * @return
     */
    private UmlModel buildUmlModel() {
        UmlModel model = new UmlModel("MyModel");

        UmlPackage deployViewPkg = new UmlPackage("Deployment View");
        model.addElement(deployViewPkg);

        UmlNode node1 = new UmlNode("Node1");
        deployViewPkg.addElement(node1);
        node1.addElement(new UmlPort("80"));
        node1.addComment(new UmlComment("This is a comment for Node1"));

        return model;
    }
}
