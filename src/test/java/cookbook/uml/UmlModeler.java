package cookbook.uml;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.uml.UmlClass;
import coyote.commons.uml.UmlDependency;
import coyote.commons.uml.UmlElement;
import coyote.commons.uml.UmlModel;
import coyote.commons.uml.UmlNamedElement;
import coyote.commons.uml.UmlNode;
import coyote.commons.uml.UmlPackage;
import coyote.commons.uml.UmlPort;
import coyote.commons.uml.UmlStereotype;
import coyote.commons.uml.marshal.Xmi11Marshaler;
import coyote.commons.uml.marshal.Xmi25Marshaler;
import coyote.commons.uml.marshal.XmiMarshaler;
import coyote.commons.log.ConsoleAppender;
import coyote.commons.log.Log;

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
        Log.addLogger(Log.DEFAULT_LOGGER_NAME, new ConsoleAppender(Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS));

        UmlModeler modeler = new UmlModeler();
        UmlModel model = modeler.buildUmlModel();

        // Now that we have a UML Model in memory, we need to save it to disk
        // so it can be imported into your CASE tool of choice.
        // First we marshal it into an XMI format (XML). We have a generic XMI
        // marshaler that will generate XML from a UML Model
        Xmi25Marshaler marshaler = new Xmi25Marshaler();
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

        // This is the root of the model.
        UmlModel model = new UmlModel("MyModel");

        // The package containing the deployment view
        UmlPackage cmpntViewPkg = new UmlPackage("Component View");
        model.addElement(cmpntViewPkg);

        // The package containing the deployment view
        UmlPackage deployViewPkg = new UmlPackage("Deployment View");
        model.addElement(deployViewPkg);

        // The package containing the Host Nodes
        UmlPackage hostPkg = new UmlPackage("Hosts");
        deployViewPkg.addElement(hostPkg);

        UmlNode node1 = new UmlNode("Node1");
        hostPkg.addElement(node1);
        node1.addElement(new UmlPort("80"));
        node1.setTaggedValue("RAM", "8GB");
        node1.addStereotype(HOST_STEREOTYPE); // as of XMI 2.5.1 tagged values require a stereotype

        UmlNode node2 = new UmlNode("Node2");
        hostPkg.addElement(node2);
        node2.addElement(new UmlPort("80"));
        node2.addElement(new UmlPort("443"));
        node2.setTaggedValue("RAM", "32GB"); 
        node2.addStereotype(HOST_STEREOTYPE); // as of XMI 2.5.1 tagged values require a stereotype

        UmlNode node3 = new UmlNode("Node3");
        hostPkg.addElement(node3);
        node3.addStereotype(HOST_STEREOTYPE);
        node3.addElement(new UmlPort("80"));
        node3.addElement(new UmlPort("443"));
        node3.addElement(new UmlPort("5432"));
        node3.setTaggedValue("IP", "10.20.250.105");

        UmlNode node4 = new UmlNode("Node4");
        hostPkg.addElement(node4);
        node4.addStereotype(HOST_STEREOTYPE);
        node4.addElement(new UmlPort("80"));
        node4.addElement(new UmlPort("443"));
        node4.addElement(new UmlPort("5432"));
        node4.addElement(new UmlPort("55290"));
        node4.setTaggedValue("IP", "10.20.250.106");

        // Create a dependency between Node1 and Node4's Port 5432
        UmlNamedElement nodeOne = model.getElementByName("Node1"); // find the element by its name - starting at the top of the model
        UmlNamedElement nodeFour = hostPkg.getElementByName("Node4"); // we can limit the search by chosing the closest element
        UmlNamedElement port5432 = nodeFour.getElementByName("80"); // find the port in that node
        UmlDependency dependency = new UmlDependency("DB Connection",nodeOne.getId(),port5432.getId());
        hostPkg.addElement(dependency); // add the dependency to the package common to the elements connected.

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
