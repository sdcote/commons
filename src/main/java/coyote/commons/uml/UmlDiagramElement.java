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
 * &lt;UML:DiagramElement geometry="Left=1111;Top=420;Right=1311;Bottom=563;" subject="EAID_DF1EA826_7FDA_426b_B896_E7C0A28CD862" seqno="1" style="DUID=58CA8375;HideIcon=0;"/&gt;
 */
public class UmlDiagramElement extends UmlNamedElement {
  private static final Classifier CLASSIFIER = Classifier.DIAGRAM_ELEMENT;

  private String geometry = null;
  private UmlElement subject;
  private int seqno = 0;
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

}
