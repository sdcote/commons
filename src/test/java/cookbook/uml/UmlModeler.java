package cookbook.uml;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.uml.UmlClass;
import coyote.commons.uml.UmlModel;
import coyote.commons.uml.UmlNode;
import coyote.commons.uml.UmlPackage;
import coyote.commons.uml.UmlPort;
import coyote.commons.uml.UmlStereotype;
import coyote.commons.uml.marshal.Xmi11Marshaler;
import coyote.commons.uml.marshal.Xmi25Marshaler;
import coyote.commons.uml.marshal.XmiMarshaler;

/**
 * The goal of this class is to demonstrate how to create a UmlModel
 * and generate an XML file to import into a CASE tool
 */
public class UmlModeler {

    public static final UmlStereotype HOST_STEREOTYPE = new UmlStereotype("host");

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
        Xmi25Marshaler marshaler = new Xmi25Marshaler();
        marshaler.setName("MyMarshaler");
        marshaler.setVersion("1.0.42");
        String xml = marshaler.marshal(model, true);
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
     * @return
     */
    private UmlModel buildUmlModel() {

        // This is the root of the model. The name is not critical.
        UmlModel model = new UmlModel("MyModel");

        UmlPackage rootPackage = new UmlPackage("DeployDemo");
        model.addElement(rootPackage);

        // The package containing the deployment view
        UmlPackage deployViewPkg = new UmlPackage("Deployment View");
        rootPackage.addElement(deployViewPkg);

        // The package containing the Host Nodes
        UmlPackage hostPkg = new UmlPackage("Hosts");
        deployViewPkg.addElement(hostPkg);

        UmlNode node1 = new UmlNode("Node1");
        hostPkg.addElement(node1);
        node1.addFeature(new UmlPort("80")); // Ports are features not elements

        UmlNode node2 = new UmlNode("Node2");
        hostPkg.addElement(node2);
        node2.addFeature(new UmlPort("80"));
        node2.addFeature(new UmlPort("443"));

        UmlNode node3 = new UmlNode("Node3");
        hostPkg.addElement(node3);
        node3.addFeature(new UmlPort("80"));
        node3.addFeature(new UmlPort("443"));
        node3.addFeature(new UmlPort("5432"));

        UmlNode node4 = new UmlNode("Node4");
        hostPkg.addElement(node4);
        node4.addStereotype(HOST_STEREOTYPE);
        node4.addFeature(new UmlPort("80"));
        node4.addFeature(new UmlPort("443"));
        node4.addFeature(new UmlPort("5432"));
        node4.addFeature(new UmlPort("55290"));


        return model;
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
}
