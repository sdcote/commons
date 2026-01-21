package cookbook.uml;

import coyote.commons.log.ConsoleAppender;
import coyote.commons.log.Log;
import coyote.commons.uml.*;

/**
 * The goal of this class is to demonstrate how to create a UmlModel
 * and generate an XML file to import into a CASE tool
 */
public class FindByTaggedValue {

    public static final UmlStereotype HOST_STEREOTYPE = new UmlStereotype("host");

    /**
     * Main entry into the code.
     *
     * @param args command line arguments - ignored in this example.
     */
    public static void main(String[] args) {
        Log.addLogger(Log.DEFAULT_LOGGER_NAME, new ConsoleAppender(Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS));

        FindByTaggedValue modeler = new FindByTaggedValue();
        UmlModel model = modeler.buildUmlModel();

        UmlElement found = model.getElementByTaggedValue("IP", "10.20.250.105");
        if (found != null) {
            Log.info("SUCCESS - " + found.getId());
        } else {
            Log.info("FAILURE!");
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
        node1.addComment(new UmlComment("This is a comment for Node1"));


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
        UmlNamedElement nodeFour = hostPkg.getElementByName("Node4"); // we can limit the search by choosing the closest element
        UmlNamedElement port5432 = nodeFour.getElementByName("80"); // find the port in that node
        UmlDependency dependency = new UmlDependency("DB Connection", nodeOne.getId(), port5432.getId());
        hostPkg.addElement(dependency); // add the dependency to the package common to the elements connected.

        // diagram example
        UmlDiagram diagram = new UmlDiagram("Simple Diagram");
        hostPkg.addElement(diagram);
        diagram.setDiagramType(DiagramType.DEPLOYMENT);

        // Add node1 to the diagram
        UmlShape element = new UmlShape(node1);
        element.setBounds(new DiagramBounds(100, 100, 120, 80));
        diagram.add(element);

        // Add node 4 to the diagram
        element = new UmlShape(node4);
        element.setBounds(new DiagramBounds(300, 100, 120, 80));
        diagram.add(element);

        // Place the port on the edge of Node4
        UmlNamedElement modelElement = node4.getElementByName("5432");
        element = new UmlShape(modelElement);
        element.setBounds(new DiagramBounds(420, 110, 10, 10));
        diagram.add(element);

        return model;
    }
}
