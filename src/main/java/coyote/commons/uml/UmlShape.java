package coyote.commons.uml;

public class UmlShape extends UmlDiagramElement {
    private static final Classifier CLASSIFIER = Classifier.UML_SHAPE;

    public UmlShape(UmlElement element) {
        super(element);
        this.setId("SHAPE_" + element.getId());
    }
}
