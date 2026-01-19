/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

import coyote.commons.Assert;


/**
 *
 */
public class UmlDiagramElement extends UmlNamedElement {
    private static final Classifier CLASSIFIER = Classifier.DIAGRAM_ELEMENT;

    DiagramBounds diagramBounds = new DiagramBounds(100,100,80,80);

    private UmlElement subject;
    private int seqno = 0;

    private String geometry = null;
    @Deprecated
    private String style = null;


    public UmlDiagramElement(UmlElement subject) {
        this(subject, 0);
    }


    public UmlDiagramElement(UmlElement subject, int seq) {
        Assert.notNull(subject);
        setSubject(subject);
        setSequence(seq);
    }


    public Classifier getClassifier() {
        return CLASSIFIER;
    }


    /**
     * @return the geometry
     */
    public String getGeometry() {
        return geometry;
    }


    /**
     * @param geometry the geometry to set
     */
    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }


    /**
     * @return the subject
     */
    public UmlElement getSubject() {
        return subject;
    }


    /**
     * @param element the subject to set
     */
    public void setSubject(UmlElement element) {
        subject = element;
    }


    /**
     * @return the sequence
     */
    public int getSequence() {
        return seqno;
    }


    /**
     * @param seq the seqno to set
     */
    public void setSequence(int seq) {
        seqno = seq;
    }


    /**
     * @return the style
     */
    public String getStyle() {
        return style;
    }


    /**
     * @param style the style to set
     */
    public void setStyle(String style) {
        this.style = style;
    }


    /**
     * @param bounds the bounds or geometry of the element
     */
    public void setBounds(DiagramBounds bounds) {
        diagramBounds = bounds;
    }

    /**
     * @return the bounds or geometry of the element
     */
    public DiagramBounds getBounds() {
        return diagramBounds;
    }

}
