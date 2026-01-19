package coyote.commons.uml.marshal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import coyote.commons.StringUtil;
import coyote.commons.log.Log;
import coyote.commons.uml.TaggedValue;
import coyote.commons.uml.UmlDependency;
import coyote.commons.uml.UmlElement;
import coyote.commons.uml.UmlFeature;
import coyote.commons.uml.UmlModel;
import coyote.commons.uml.UmlNamedElement;
import coyote.commons.uml.UmlPort;
import coyote.commons.uml.UmlProperty;
import coyote.commons.uml.UmlStructuralFeature;

public class Xmi25Marshaler extends AbstractMarshaler {
    private static final String NAMESPACE = "CoyoteUML";
    private static final String METADATA_STEREOTYPE = "ElementMetadata";

    private List<UmlStereotypeApplication> stereotypeToApply = new ArrayList<>();

    /**
     * Marshal the given model to an XMI string.
     * 
     * @param model  The UML model to marshal
     * @param indent true to generate an indented XML string, false for compact
     * 
     * @return the XML string representing the model.
     */
    public String marshal(UmlModel model, boolean indent) {
        StringBuilder b = new StringBuilder();
        genXmi(b, model, indent ? 0 : -1);
        return b.toString();
    }

    /** Top level of the document */
    protected void genXmi(StringBuilder b, UmlModel model, int level) {
        b.append(
                "<xmi:XMI xmlns:xmi=\"http://www.omg.org/spec/XMI/20131001\" xmlns:uml=\"http://www.omg.org/spec/UML/20161101\" xmlns:");
        b.append(NAMESPACE);
        b.append("=\"urn:coyote-uml:profile:host:1.0\" xmi:version=\"2.5.1\">");
        b.append(lineEnd(level));
        genDocumentation(b, (level > -1) ? level + 1 : level);
        genModel(b, model, (level > -1) ? level + 1 : level);
        b.append("</xmi:XMI>");
    }

    /** create the model XML */
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

        // Only package elements at the model level
        for (UmlNamedElement element : model.getOwnedElements()) {
            genPackagedElements(b, element, (level > -1) ? level + 1 : level);
        }

        // close up the model section
        b.append(pad);
        b.append("</uml:Model>");
        b.append(lineEnd(level));

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
     * If the given element has stereotypes, add it to the list of stereotypes to
     * apply later.
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

    /** */
    private void genElement(StringBuilder b, UmlNamedElement element, int level) {
        b.append(getPadding(level));
        String classifier = element.getClassifier().toString();

        if (element instanceof UmlPort) {
            b.append("<ownedAttribute");
        } else if (element instanceof UmlDependency) {
            b.append("<packagedElement");
        } else {
            Log.error("Unsupported element encountered - " + element.getClass().getName());
        }

        b.append(" xmi:type=\"uml:");
        b.append(classifier);
        b.append("\" xmi:id=\"");
        b.append(element.getId());
        if (StringUtil.isNotEmpty(element.getName())) {
            b.append("\" name=\"");
            b.append(element.getName());
                            b.append("\"");
        }

        // Different elements have different attributes
        if (element instanceof UmlDependency) {
            UmlDependency dependency = (UmlDependency) element;
            if (StringUtil.isNotEmpty(dependency.getClientId())) {
                b.append(" client=\"");
                b.append(dependency.getClientId());
                b.append("\"");
            }
            if (StringUtil.isNotEmpty(dependency.getSupplierId())) {
                b.append(" supplier=\"");
                b.append(dependency.getSupplierId());
                b.append("\"");
            }
        } else {
            if (element instanceof UmlPort) {
                UmlPort port = (UmlPort) element;
                b.append(" isService=\"");
                b.append((port.isService()) ? "true" : "false");
                b.append("\"");
            }

            if (element instanceof UmlFeature) {
                b.append(" isStatic=\"");
                b.append(((UmlFeature) element).isStatic() ? "true" : "false");
                b.append("\"");
            }

            if (element instanceof UmlStructuralFeature) {
                b.append(" isReadOnly=\"");
                b.append(((UmlStructuralFeature) element).isReadOnly() ? "true" : "false");
                b.append("\"");
            }

            if (element instanceof UmlProperty) {
                // type
                // lowerValue
                // upperValue
                b.append(" aggregation=\"");
                b.append(((UmlProperty) element).getAggregation().toString());
                b.append("\"");
            }

            b.append(" visibility=\"");
            b.append(element.getVisibility().toString());
            b.append("\"");
        }

        b.append("/>");
        b.append(lineEnd(level));

        // Check to see if we need to register a stereotype
        checkForStereotype(element);

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

        if (element.hasOwnedElements()) {
            b.append(">");
            b.append(lineEnd(level));

            for (UmlNamedElement child : element.getOwnedElements()) {
                if (child.hasOwnedElements())
                    genPackagedElements(b, child, (level > -1) ? level + 1 : level);
                else
                    genElement(b, child, (level > -1) ? level + 1 : level);

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
        String name;
        String baseNode;
        private String id = UUID.randomUUID().toString();

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
