package cookbook.uml;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.uml.UmlClass;
import coyote.commons.uml.UmlModel;
import coyote.commons.uml.UmlPackage;
import coyote.commons.uml.marshal.Xmi11Marshaler;

/**
 * The goal of this class is to demonstrate how to create a UmlModel
 * and generate an XML file to import into a CASE tool
 */
public class UmlModeler {

    /**
     * Main entry into the code.
     * 
     * @param args command line arguments - ignored in this example.
     */
    public static void main(String[] args) {
        UmlModeler modeler = new UmlModeler();
        UmlModel model = modeler.buildUmlModel();

        // Now that we have a UML Model in memory, we need to save it to disk
        // so it can be imported into your CASE tool of choice.
        // First we marshal it into an XMI format (XML). We have a generic XMI
        // marshaler that will generate XML from a UML Model
        Xmi11Marshaler.setName("MyMarshaler");
        Xmi11Marshaler.setVersion("1.0.42");
        String xml = Xmi11Marshaler.marshal(model, false);
        // There can be subtle differences in XMI compatibility so you might
        // want to create a tool-specific modeler to get all the features of
        // your CASE tool. For example, you might want to create a Sparx
        // marshaler to use all the feature of Sparx EA

        // Save the file to disk
        save(xml, "UmlModelXmi.xml", "UTF-8");

    }


    /**
     * This is where you create the UML Model.
     * 
     * <p>More examples will be added here over time.</p>
     * 
     * @return
     */
    private UmlModel buildUmlModel() {
        UmlModel model = new UmlModel("MyModel");

        UmlPackage rootPackage = new UmlPackage("Root");
        model.addElement(rootPackage);

        UmlClass clas = new UmlClass("Class1");
        rootPackage.addElement(clas);

        return model;
    }


    /**
     * This adds a preamble to the XML and saves it to disk.
     * 
     * <p>This uses the FileUtil class to do the heavy lifting for us.</p>
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
}
