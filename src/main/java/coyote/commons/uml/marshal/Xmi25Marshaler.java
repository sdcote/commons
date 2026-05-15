package coyote.commons.uml.marshal;

import coyote.commons.StringUtil;
import coyote.commons.log.Log;
import coyote.commons.uml.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Xmi25Marshaler extends AbstractMarshaler {
    private static final String NAMESPACE = "CoyoteUML";
    private static final String METADATA_STEREOTYPE = "ElementMetadata";

    private final List<UmlStereotypeApplication> stereotypeToApply = new ArrayList<>();

    private final List<UmlDiagram> diagramsToApply = new ArrayList<>();

    /**
     * Marshal the given model to an XMI string.
     *
     * @param model  The UML model to marshal
     * @param indent true to generate an indented XML string, false for compact
     * @return the XML string representing the model.
     */
    public String marshal(UmlModel model, boolean indent) {
        StringBuilder b = new StringBuilder();
        genXmi(b, model, indent ? 0 : -1);
        return b.toString();
    }

    /**
     * Top level of the document
     */
    protected void genXmi(StringBuilder b, UmlModel model, int level) {
        b.append("<xmi:XMI xmlns:xmi=\"http://www.omg.org/spec/XMI/20131001\"");
        b.append(" xmlns:uml=\"http://www.omg.org/spec/UML/20161101\"");
        b.append(" xmlns:umldi=\"http://www.omg.org/spec/UML/20161101/UMLDI\"");
        b.append(" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"");
        b.append(" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"");
        b.append(" xmlns:");
        b.append(NAMESPACE);
        b.append("=\"urn:coyote-uml:profile:host:1.0\" xmi:version=\"2.5.1\">");
        b.append(lineEnd(level));
        genDocumentation(b, (level > -1) ? level + 1 : level);
        genModel(b, model, (level > -1) ? level + 1 : level);
        genExtension(b, model, (level > -1) ? level + 1 : level);
        b.append("</xmi:XMI>");
    }

    /**
     * Allow any attached MarshalerExtension to add a {@code xmi:Extension}
     * block to the end of the standard XMI {@code uml:Model} block.
     *
     * @param b     the string builder to which all text should be appended.
     * @param model the UML model
     * @param level the current level of indentation. A value of -1 indicate no
     *              indentation or line feeds are to be used.
     */
    private void genExtension(StringBuilder b, UmlModel model, int level) {
        getExtension().generateExtensionBlock(b, model, level);
    }

    /**
     * create the model XML
     */
    private void genModel(StringBuilder b, UmlModel model, int level) {
        String pad = getPadding(level);
        b.append(pad);
        b.append("<uml:Model xmi:type=\"uml:Model\" xmi:id=\"");
        b.append(model.getId());
        b.append("\"");

        if (StringUtil.isNotBlank(model.getName())) {
            b.append(" name=\"");
            b.append(model.getName());
            b.append("\"");
        }

        b.append(" visibility=\"");
        b.append(model.getVisibility());
        b.append("\">");
        b.append(lineEnd(level));

        getExtension().generateModelMetaData(b, model, (level > -1) ? level + 1 : level);

        // Only package elements at the model level
        for (UmlNamedElement element : model.getOwnedElements()) {
            genPackagedElements(b, element, (level > -1) ? level + 1 : level);
        }
        for (UmlComment comment : model.getOwnedComments()) {
            genComment(b, comment, (level > -1) ? level + 1 : level);
        }

        // close up the model section
        b.append(pad);
        b.append("</uml:Model>");
        b.append(lineEnd(level));


        // All diagrams outside of the model
        for (UmlDiagram diagram : diagramsToApply) {
            genDiagram(b, diagram, level);
        }


        // This is applying stereotypes and as of UML 2.5.1, applying tagged values.
        for (UmlStereotypeApplication binding : stereotypeToApply) {
            b.append(pad);
            b.append("<");
            b.append(NAMESPACE);
            b.append(":");
            b.append(binding.getName());
            b.append(" xmi:id=\"");
            b.append(binding.getId());
            b.append("\" base_Node=\"");
            b.append(binding.getBaseNode());
            b.append("\"");

            UmlElement element = model.getElementById(binding.getBaseNode());
            if (element != null) {
                for (TaggedValue tvalue : element.getTaggedValues()) {
                    b.append(" ");
                    b.append(tvalue.getName());
                    b.append("=\"");
                    b.append(tvalue.getValue());
                    b.append("\"");
                }
            }

            b.append("/>");
            b.append(lineEnd(level));
        }

    }

    /**
     * If the given element has stereotypes, add it to the list of stereotypes
     * to apply later.
     *
     * <p>This also checks for tagged values and generates a stereotype for the
     * tagged values if necessary.</p>
     *
     * @param element the element to check
     */
    private void checkForStereotype(UmlNamedElement element) {
        if (element.hasStereotypes()) {
            for (String stereotype : element.getStereotypeNames()) {
                stereotypeToApply.add(new UmlStereotypeApplication(stereotype, element.getId()));
            }
        } else {
            // In strict adherence to the UML 2.5.1 specification, Tagged values
            // do not exist as independent entities. They are formally defined as
            // the attributes of a Stereotype. While many users think of "Tags"
            // as simple metadata buckets, the OMG standards treat them as a
            // mechanism for Extending the Metamodel. So if there are tagged
            // values, but no stereotype, we need to create a new stereotype to
            // handle those extensions.
            if (element.hasTaggedValues()) {
                stereotypeToApply.add(new UmlStereotypeApplication(METADATA_STEREOTYPE, element.getId()));
            }
        }
    }

    /**
     *
     */
    private void genElement(StringBuilder b, UmlNamedElement element, int level) {

        if (element instanceof UmlPort) {
            genPort(b, (UmlPort) element, level);
        } else if (element instanceof UmlDependency) {
            genDependency(b, (UmlDependency) element, level);
        } else if (element instanceof UmlAssociation) {
            genAssociation(b, (UmlAssociation) element, level);
        } else if (element instanceof UmlGeneralization) {
            genGeneralization(b, (UmlGeneralization) element, level);
        } else if (element instanceof UmlNode) {
            genPackagedElements(b, element, level);
        } else if (element instanceof UmlComponent) {
            genPackagedElements(b, element, level);
        } else if (element instanceof UmlDiagram) {
            diagramsToApply.add((UmlDiagram) element);
        } else {
            genPackagedElements(b, element, level);
// "Unsupported element encountered - " + element.getClass().getName() + " level: " + level + " parent: " + ((element.getParent() == null) ? "no parent" : element.getParent().getDisplayString()));
        }

        checkForStereotype(element);
    }

    private void genGeneralization(StringBuilder b, UmlGeneralization element, int level) {
        b.append(getPadding(level));
        b.append("<generalization xmi:type=\"uml:Generalization\" xmi:id=\"");
        b.append(element.getId());
        b.append("\" general=\"");
        b.append(element.getSuperType());
        b.append("\"/>");
        b.append(lineEnd(level));
    }

    private void genAssociation(StringBuilder b, UmlAssociation element, int level) {
        String pad = getPadding(level);
        b.append(pad);
        b.append("<packagedElement xmi:type=\"uml:Association\" xmi:id=\"");
        b.append(element.getId());
        b.append("\"");
        if (StringUtil.isNotEmpty(element.getName())) {
            b.append(" name=\"");
            b.append(element.getName());
            b.append("\"");
        }
        b.append(" visibility=\"");
        b.append(element.getVisibility().toString());
        b.append("\"");

        for (UmlAssociationEnd end : element.getEnds()) {
            b.append(" memberEnd=\"");
            b.append(end.getId());
            b.append("\"");
        }

        b.append(">");
        b.append(lineEnd(level));

        for (UmlAssociationEnd end : element.getEnds()) {
            genAssociationEnd(b, end, (level > -1) ? level + 1 : level);
        }

        b.append(pad);
        b.append("</packagedElement>");
        b.append(lineEnd(level));
    }

    private void genAssociationEnd(StringBuilder b, UmlAssociationEnd element, int level) {
        String pad = getPadding(level);
        b.append(pad);
        b.append("<ownedEnd xmi:type=\"uml:Property\" xmi:id=\"");
        b.append(element.getId());
        b.append("\"");
        if (StringUtil.isNotEmpty(element.getName())) {
            b.append(" name=\"");
            b.append(element.getName());
            b.append("\"");
        }
        b.append(" visibility=\"");
        b.append(element.getVisibility().toString());
        b.append("\" type=\"");
        b.append(element.getType());
        b.append("\" aggregation=\"");
        b.append(element.getAggregation().toString().toLowerCase());
        b.append("\" association=\"");
        b.append(((UmlNamedElement) element.getParent()).getId());
        b.append("\">");
        b.append(lineEnd(level));

        if (element.getMultiplicity() != null) {
            genMultiplicity(b, element.getMultiplicity(), (level > -1) ? level + 1 : level);
        }

        b.append(pad);
        b.append("</ownedEnd>");
        b.append(lineEnd(level));
    }

    private void genMultiplicity(StringBuilder b, UmlMultiplicity multiplicity, int level) {
        String pad = getPadding(level);
        // Lower bound
        b.append(pad);
        b.append("<lowerValue xmi:type=\"uml:LiteralInteger\" xmi:id=\"");
        b.append(UUID.randomUUID().toString());
        b.append("\" value=\"");
        b.append(multiplicity.getLowerBound());
        b.append("\"/>");
        b.append(lineEnd(level));

        // Upper bound
        b.append(pad);
        b.append("<upperValue xmi:type=\"uml:LiteralUnlimitedNatural\" xmi:id=\"");
        b.append(UUID.randomUUID().toString());
        b.append("\" value=\"");
        if (multiplicity.getUpperBound() == UmlMultiplicity.ANY) {
            b.append("*");
        } else {
            b.append(multiplicity.getUpperBound());
        }
        b.append("\"/>");
        b.append(lineEnd(level));
    }

    private void genDependency(StringBuilder b, UmlDependency element, int level) {
        b.append(getPadding(level));
        b.append("<packagedElement");
        b.append(" xmi:type=\"uml:");
        b.append(element.getClassifier().toString());
        b.append("\" xmi:id=\"");
        b.append(element.getId());
        b.append("\"");

        if (StringUtil.isNotEmpty(element.getName())) {
            b.append(" name=\"");
            b.append(element.getName());
            b.append("\"");
        }

        if (StringUtil.isNotEmpty(element.getClientId())) {
            b.append(" client=\"");
            b.append(element.getClientId());
            b.append("\"");
        }
        if (StringUtil.isNotEmpty(element.getSupplierId())) {
            b.append(" supplier=\"");
            b.append(element.getSupplierId());
            b.append("\"");
        }

        b.append("/>");
        b.append(lineEnd(level));

    }

    private void genPort(StringBuilder b, UmlPort element, int level) {
        b.append(getPadding(level));
        b.append("<ownedAttribute");
        b.append(" xmi:type=\"uml:");
        b.append(element.getClassifier().toString());
        b.append("\" xmi:id=\"");
        b.append(element.getId());
        b.append("\"");
        if (StringUtil.isNotEmpty(element.getName())) {
            b.append(" name=\"");
            b.append(element.getName());
            b.append("\"");
        }

        b.append(" isService=\"");
        b.append((element.isService()) ? "true" : "false");
        b.append("\"");

        b.append(" isStatic=\"");
        b.append(element.isStatic() ? "true" : "false");
        b.append("\"");

        b.append(" isReadOnly=\"");
        b.append(element.isReadOnly() ? "true" : "false");
        b.append("\"");

        b.append(" aggregation=\"");
        b.append(element.getAggregation().toString());
        b.append("\"");

        b.append(" visibility=\"");
        b.append(element.getVisibility().toString());
        b.append("\"");

        b.append("/>");
        b.append(lineEnd(level));

    }

    /**
     * Generate diagram specification
     *
     * @param b
     * @param diagram
     * @param level
     */
    private void genDiagram(StringBuilder b, UmlDiagram diagram, int level) {
        b.append(getPadding(level));
        b.append("<umldi:UMLDiagram xmi:type=\"umldi:UMLDiagram\" xmi:id=\"");
        b.append(diagram.getId());
        b.append("\"");

        if (StringUtil.isNotEmpty(diagram.getName())) {
            b.append(" name=\"");
            b.append(diagram.getName());
            b.append("\"");
        }
        b.append(" modelElement=\"");
        b.append(diagram.getParent().getId());
        b.append("\" visibility=\"public\">");
        b.append(lineEnd(level));

        for (UmlDiagramElement element : diagram.getDiagramElements()) {
            if (element instanceof UmlShape)
                genUmlShape(b, (UmlShape) element, (level > -1) ? level + 1 : level);
            else if (element instanceof UmlEdge)
                genUmlLine(b, (UmlEdge) element, (level > -1) ? level + 1 : level);
        }

        b.append(getPadding(level));
        b.append("</umldi:UMLDiagram>");
        b.append(lineEnd(level));


    }

    private void genUmlLine(StringBuilder b, UmlEdge element, int level) {
        String pad = getPadding(level);
        b.append(pad);
        b.append("<umldi:ownedUmlDiagramElement xmi:type=\"umldi:UMLEdge\" xmi:id=\"");
        b.append(element.getId());
        b.append("\"");

        b.append(" modelElement=\"");
        b.append(element.getSubject().getId());
        b.append("\">");
        b.append(lineEnd(level));

        if (element.getWayPoints().size() >= 2) {
            String cpad = getPadding(level + 1);
            for (java.awt.Point point : element.getWayPoints()) {
                b.append(cpad);
                b.append("<umldi:waypoint xmi:type=\"di:Point\" x=\"");
                b.append(point.x);
                b.append(".0\" y=\"");
                b.append(point.y);
                b.append(".0\"/>");
                b.append(lineEnd(level));
            }
        }

        b.append(pad);
        b.append("</umldi:ownedUmlDiagramElement>");
        b.append(lineEnd(level));
    }

    private void genUmlShape(StringBuilder b, UmlShape element, int level) {
        String pad = getPadding(level);
        b.append(pad);
        b.append("<umldi:ownedUmlDiagramElement xmi:type=\"umldi:UMLShape\" xmi:id=\"");
        b.append(element.getId());
        b.append("\"");

        b.append(" modelElement=\"");
        b.append(element.getSubject().getId());
        b.append("\"");

        if (element.getBounds() != null) {
            b.append(">");
            b.append(lineEnd(level));
            genBounds(b, element.getBounds(), (level > -1) ? level + 1 : level);
            b.append(pad);
            b.append("</umldi:ownedUmlDiagramElement>");
        } else {
            b.append("/>");
        }

        b.append(lineEnd(level));
    }

    private void genBounds(StringBuilder b, DiagramBounds bounds, int level) {
        b.append(getPadding(level));
        b.append("<dc:Bounds xmi:type=\"dc:Bounds\" x=\"");
        b.append(bounds.getXPosition());
        b.append(".0\" y=\"");
        b.append(bounds.getYPosition());
        b.append(".0\" width=\"");
        b.append(bounds.getWidth());
        b.append(".0\" height=\"");
        b.append(bounds.getHeight());
        b.append(".0\"/>");
        b.append(lineEnd(level));
    }

    /**
     *
     * @param b
     * @param element
     * @param level
     */
    private void genPackagedElements(StringBuilder b, UmlNamedElement element, int level) {
        String pad = getPadding(level);
        b.append(pad);
        b.append("<packagedElement xmi:type=\"uml:");
        b.append(element.getClassifier().toString());
        b.append("\" xmi:id=\"");
        b.append(element.getId());
        if (StringUtil.isNotEmpty(element.getName())) {
            b.append("\" name=\"");
            b.append(element.getName());
        }

        b.append("\" visibility=\"");
        b.append(element.getVisibility().toString());
        b.append("\"");

        boolean hasFeatures = (element instanceof UmlClassifier && !((UmlClassifier) element).getFeatures().isEmpty());
        boolean hasNestedTypes = (element instanceof UmlClassifier && !((UmlClassifier) element).getDataTypes().isEmpty());

        if (element.hasOwnedElements() || hasFeatures || hasNestedTypes || !element.getOwnedComments().isEmpty()) {
            b.append(">");
            b.append(lineEnd(level));

            if (element instanceof UmlClassifier) {
                UmlClassifier classifier = (UmlClassifier) element;
                for (UmlFeature feature : classifier.getFeatures()) {
                    genFeature(b, feature, (level > -1) ? level + 1 : level);
                }
                for (UmlDataType type : classifier.getDataTypes()) {
                    genPackagedElements(b, type, (level > -1) ? level + 1 : level);
                }
            }

            for (UmlNamedElement child : element.getOwnedElements()) {
                if (child.hasOwnedElements())
                    genPackagedElements(b, child, (level > -1) ? level + 1 : level);
                else
                    genElement(b, child, (level > -1) ? level + 1 : level);
            }

            for (UmlComment comment : element.getOwnedComments()) {
                genComment(b, comment, (level > -1) ? level + 1 : level);
            }

            // close up the packaged element section
            b.append(pad);
            b.append("</packagedElement>");

        } else {
            b.append("/>");
        }
        b.append(lineEnd(level));

        // Check to see if we need to register a stereotype
        checkForStereotype(element);

    }

    private void genFeature(StringBuilder b, UmlFeature element, int level) {
        if (element instanceof UmlProperty) {
            genProperty(b, (UmlProperty) element, level);
        } else if (element instanceof UmlOperation) {
            genOperation(b, (UmlOperation) element, level);
        }
    }

    private void genProperty(StringBuilder b, UmlProperty element, int level) {
        String pad = getPadding(level);
        b.append(pad);
        b.append("<ownedAttribute xmi:type=\"uml:Property\" xmi:id=\"");
        b.append(element.getId());
        b.append("\" name=\"");
        b.append(element.getName());
        b.append("\" visibility=\"");
        b.append(element.getVisibility().toString());
        b.append("\" isStatic=\"");
        b.append(element.isStatic() ? "true" : "false");
        b.append("\" isReadOnly=\"");
        b.append(element.isReadOnly() ? "true" : "false");
        b.append("\"");

        if (StringUtil.isNotEmpty(element.getTypeId())) {
            b.append(" type=\"");
            b.append(element.getTypeId());
            b.append("\"");
        }

        b.append("/>");
        b.append(lineEnd(level));
    }

    private void genOperation(StringBuilder b, UmlOperation element, int level) {
        String pad = getPadding(level);
        b.append(pad);
        b.append("<ownedOperation xmi:type=\"uml:Operation\" xmi:id=\"");
        b.append(element.getId());
        b.append("\" name=\"");
        b.append(element.getName());
        b.append("\" visibility=\"");
        b.append(element.getVisibility().toString());
        b.append("\" isStatic=\"");
        b.append(element.isStatic() ? "true" : "false");
        b.append("\" isQuery=\"");
        b.append(element.isQuery() ? "true" : "false");
        b.append("\"/>");
        b.append(lineEnd(level));
    }

    private void genComment(StringBuilder b, UmlComment comment, int level) {
        // <ownedComment xmi:type="uml:Comment" xmi:id="COMMENT_01">
        //    <body>Primary application server for the production environment.</body>
        //  </ownedComment>
        String pad = getPadding(level);
        b.append(pad);
        b.append("<ownedComment xmi:type=\"uml:Comment\" xmi:id=\"");
        b.append(comment.getId());
        b.append("\">");
        b.append(lineEnd(level));
        genCommentBody(b, comment, (level > -1) ? level + 1 : level);
        b.append(pad);
        b.append("</ownedComment>");
        b.append(lineEnd(level));
    }

    private void genCommentBody(StringBuilder b, UmlComment comment, int level) {
        String pad = getPadding(level);
        b.append(pad);
        b.append("<body>");
        b.append(StringUtil.StringToXML(comment.getBody()));
        b.append("</body>");
        b.append(lineEnd(level));
    }

    /**
     * The documentation section, usually just contains info about the
     * exporter/generator
     */
    private void genDocumentation(StringBuilder b, int level) {
        String pad = getPadding(level);
        b.append(pad);
        b.append("<xmi:Documentation");
        if (StringUtil.isNotBlank(getName())) {
            b.append(" exporter=\"");
            b.append(getName());
            b.append("\"");
        }
        if (StringUtil.isNotBlank(getVersion())) {
            b.append(" exporterVersion=\"");
            b.append(getVersion());
            b.append("\"");
        }
        if (StringUtil.isNotBlank(getId())) {
            b.append(" xmi:id=\"");
            b.append(getId());
            b.append("\"");
        }
        b.append("/>");
        b.append(lineEnd(level));
    }

    /**
     * In XMI2.5, the stereotypes are applied outside the model. This represents
     * an application of a stereotype to a node in the model.
     */
    class UmlStereotypeApplication {
        private final String id = UUID.randomUUID().toString();
        String name;
        String baseNode;

        UmlStereotypeApplication(String name, String elementId) {
            this.name = name;
            this.baseNode = elementId;
        }

        String getName() {
            return name;
        }

        String getBaseNode() {
            return baseNode;
        }

        String getId() {
            return id;
        }
    }

}
