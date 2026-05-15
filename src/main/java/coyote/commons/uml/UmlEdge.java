package coyote.commons.uml;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class UmlEdge extends UmlDiagramElement {
    private static final Classifier CLASSIFIER = Classifier.UML_EDGE;

    private final List<Point> waypoints = new ArrayList<>();

    public UmlEdge(UmlElement element) {
        super(element);
        this.setId("EDGE_" + element.getId());
    }

    public void addWayPoint(int x, int y) {
        waypoints.add(new Point(x, y));
    }

    public List<Point> getWayPoints() {
        return waypoints;
    }
}
