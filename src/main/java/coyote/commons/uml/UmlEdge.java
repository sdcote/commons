package coyote.commons.uml;

public class UmlEdge extends UmlDiagramElement {
    private static final Classifier CLASSIFIER = Classifier.UML_EDGE;

    public UmlEdge(UmlElement element) {
        super(element);
        this.setId("EDGE_" + element.getId());
    }
}
