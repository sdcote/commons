package cookbook.uml;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.log.ConsoleAppender;
import coyote.commons.log.Log;
import coyote.commons.uml.*;
import coyote.commons.uml.marshal.MarshalerExtension;
import coyote.commons.uml.marshal.SparxExtension;
import coyote.commons.uml.marshal.Xmi11Marshaler;
import coyote.commons.uml.marshal.Xmi25Marshaler;

/**
 * The goal of this class is to demonstrate how to create a UmlModel
 * and generate an XML file to import into a CASE tool
 */
public class UmlModeler11 {

    public static final UmlStereotype HOST_STEREOTYPE = new UmlStereotype("host");

    /**
     * Main entry into the code.
     *
     * @param args command line arguments - ignored in this example.
     */
    public static void main(String[] args) {
        Log.addLogger(Log.DEFAULT_LOGGER_NAME, new ConsoleAppender(Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS));

        UmlModeler11 modeler = new UmlModeler11();
        UmlModel model = modeler.buildUmlModel();

        // Now that we have a UML Model in memory, we need to save it to disk
        // so it can be imported into your CASE tool of choice.
        // First we marshal it into an XMI format (XML). We have a generic XMI
        // marshaler that will generate XML from a UML Model
        Xmi11Marshaler marshaler = new Xmi11Marshaler();
        MarshalerExtension sparx = new SparxExtension(); // add Sparx EA extensions
        marshaler.setExtension(sparx);
        String xml = marshaler.marshal(model, true);
        // There can be subtle differences in XMI compatibility so you might
        // want to create a tool-specific modeler to get all the features of
        // your CASE tool. For example, you might want to create a Sparx
        // marshaler to use all the feature of Sparx EA

        // Save the file to disk
        save(xml, "UmlModelXmi11.xml", "UTF-8");
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

        // This is the root of the model.
        UmlModel model = new UmlModel("MyModel");

        // The package containing the Host Nodes
        UmlPackage hostPkg = new UmlPackage("Hosts");
        model.addElement(hostPkg);

        UmlNode node1 = new UmlNode("Node1");
        hostPkg.addElement(node1);

        UmlNode node2 = new UmlNode("Node2");
        hostPkg.addElement(node2);

        UmlDependency dependency = new UmlDependency("DB Connection", node1.getId(), node2.getId());
        hostPkg.addElement(dependency); // add the dependency to the package common to the elements connected.

        return model;
    }
}
