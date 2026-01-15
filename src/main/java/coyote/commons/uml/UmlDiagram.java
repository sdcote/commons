/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

import coyote.commons.Assert;

import java.util.ArrayList;
import java.util.List;


/**
 *  
 * &lt;UML:Diagram name="Tables" xmi.id="EAID_D4C11700_1BFD_4b1f_A251_30159621BC34" diagramType="ClassDiagram" owner="EAPK_CCBFBC1C_BA54_49f8_927C_0D5329041989" toolName="Enterprise Architect 2.5"&gt;
 */
public class UmlDiagram extends UmlNamedElement {
  private static final Classifier CLASSIFIER = Classifier.DIAGRAM;

  private final List<UmlDiagramElement> diagramElements = new ArrayList<UmlDiagramElement>();
  private DiagramType diagramType = DiagramType.CLASS;
  private String toolName = null;




  /**
   * @see UmlNamedElement#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }




  public UmlDiagram() {}




  /**
   * @param name
   * @param id
   */
  public UmlDiagram(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlDiagram(String name) {
    super(name);
  }




  /**
   * @return the diagram type
   */
  public DiagramType getDiagramType() {
    return diagramType;
  }




  /**
   * @param type the diagramType to set
   */
  public void setDiagramType(DiagramType type) {
    Assert.notNull(type);
    diagramType = type;
  }




  /**
   * @return the toolName
   */
  public String getToolName() {
    return toolName;
  }




  /**
   * @param toolName the toolName to set
   */
  public void setToolName(String toolName) {
    this.toolName = toolName;
  }




  public void add(UmlDiagramElement element) {
    diagramElements.add(element);
  }




  /**
   * Convenience method to add diagram elements.
   * 
   * <p>The sequence number is based on the current size/position of the 
   * element list.</p>
   * 
   * @param subject the UML Element to add to the diagram
   * 
   * @return the UML Diagram Element created and added to this diagram.
   */
  public UmlDiagramElement addSubject(UmlElement subject) {
    UmlDiagramElement retval = new UmlDiagramElement(subject, diagramElements.size());
    diagramElements.add(retval);
    return retval;
  }




  /**
   * @return the subjects in this diagram
   */
  public List<UmlDiagramElement> getDiagramElements() {
    return diagramElements;
  }

}
