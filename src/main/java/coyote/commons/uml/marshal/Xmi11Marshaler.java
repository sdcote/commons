/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml.marshal;

import coyote.commons.StringUtil;
import coyote.commons.uml.*;

import java.util.Date;


/**
 * This marshals models into a generally-supported XMI 1.1 file.
 * 
 * <p>The processing instruction is not included as it is not known at the time 
 * of XML generation what encoding will be used.
 * <pre>&lt;?xml version = '1.0' encoding = 'UTF-8' ?&gt;</pre> might not be 
 * correct if the file is written using {@code windows-1252} encoding.
 *
 * @deprecated Use the Xmi25Marshaler instead for greater support
 */
@Deprecated
public class Xmi11Marshaler extends AbstractMarshaler {

  

  /**
   * Marshal the given model to an XMI string.
   * 
   * @param model The UML model to marshal
   * @param indent true to generate an indented XML string, false for compact
   * 
   * @return the XML string representing the model.
   */
  public  String marshal(UmlModel model, boolean indent) {
    StringBuilder b = new StringBuilder();
    genXmi(b, model, indent ? 0 : -1);
    return b.toString();
  }


  /** Top level of the document */
  protected  void genXmi(StringBuilder b, UmlModel model, int level) {
    int lvl = (level > -1) ? level + 1 : level;

    b.append("<XMI xmi.version=\"1.1\" xmlns:UML=\"omg.org/UML1.3\" timestamp=\"");
    b.append(DATEFORMAT.format(new Date()));
    b.append("\">");
    b.append(lineEnd(level));

    genHeader(b, model, lvl);
    b.append(lineEnd(level));

    genContent(b, model, lvl);
    b.append(lineEnd(level));

    b.append("</XMI>");
  }


  /** Contains documentation details */
   void genHeader(StringBuilder b, UmlModel model, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<XMI.header>");
    b.append(lineEnd(level));

    genDocumentation(b, model, (level > -1) ? level + 1 : level);
    b.append(lineEnd(level));

    b.append(pad);
    b.append("</XMI.header>");
  }


  /** The documentation section, usually just contain info about the exporter/generator */
  private  void genDocumentation(StringBuilder b, UmlModel model, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<XMI.documentation>");
    b.append(lineEnd(level));

    genExporterDetails(b, model, (level > -1) ? level + 1 : level);

    b.append(lineEnd(level));
    b.append(pad);
    b.append("</XMI.documentation>");
  }


  /** Usually part of the <XMI.documentation> in the <XMI.header> */
  private  void genExporterDetails(StringBuilder b, UmlModel model, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<XMI.exporter>");
    b.append(getName());
    b.append("</XMI.exporter>");
    b.append(lineEnd(level));
    b.append(pad);
    b.append("<XMI.exporterVersion>");
    b.append(getVersion());
    b.append("</XMI.exporterVersion>");
    b.append("<XMI.exporterID>");
    b.append(getId());
    b.append("</XMI.exporterID>");
  }


  /** This is main content of the model; usually right after the <XMI.header> */
   void genContent(StringBuilder b, UmlModel model, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<XMI.content>");
    b.append(lineEnd(level));

    generateElementXML(b, model, (level > -1) ? level + 1 : level);
    b.append(lineEnd(level));

    if (!model.getDiagrams().isEmpty()) {
      generateDiagramXML(b, model, (level > -1) ? level + 1 : level);
    }

    b.append(pad);
    b.append("</XMI.content>");

  }




  /**
   * Diagrams are at the same level as the model.
   * 
   * @param b
   * @param model
   * @param level
   */
  private  void generateDiagramXML(StringBuilder b, UmlModel model, int level) {
    String pad = getPadding(level);

    // <UML:Diagram name="Tables" xmi.id="EAID_D4C11700_1BFD_4b1f_A251_30159621BC34" diagramType="ClassDiagram" owner="EAPK_CCBFBC1C_BA54_49f8_927C_0D5329041989" toolName="Enterprise Architect 2.5">

    for (UmlDiagram diagram : model.getDiagrams()) {
      b.append(pad);
      b.append("<UML:");
      b.append(diagram.getClassifier().getName());

      if (StringUtil.isNotBlank(diagram.getName())) {
        b.append(" name=\"");
        b.append(diagram.getName());
        b.append("\"");
      }

      if (StringUtil.isNotBlank(diagram.getId())) {
        b.append(" xmi.id=\"");
        b.append(diagram.getId());
        b.append("\"");
      }

      b.append(" diagramType=\"");
      b.append(diagram.getDiagramType().getName());
      b.append("\"");

      if (diagram.getParent() != null && StringUtil.isNotBlank(diagram.getParent().getId())) {
        b.append(" owner=\"");
        b.append(diagram.getParent().getId());
        b.append("\"");
      }

      if (StringUtil.isNotBlank(diagram.getToolName())) {
        b.append(" toolName=\"");
        b.append(diagram.getToolName());
        b.append("\"");
      }
      b.append(">");
      b.append(lineEnd(level));

      // get tagged values
      if (!diagram.getTaggedValues().isEmpty()) {
        genTaggedValues(b, diagram, (level > -1) ? level + 1 : level);
        b.append(lineEnd(level));
      }

      // Get diagram elements
      if (!diagram.getDiagramElements().isEmpty()) {
        genDiagramElements(b, diagram, (level > -1) ? level + 1 : level);
        b.append(lineEnd(level));
      }

      b.append(pad);
      b.append("</UML:");
      b.append(diagram.getClassifier().getName());
      b.append(">");
      b.append(lineEnd(level));
    } // for each diagram 
  }




  private  void genDiagramElements(StringBuilder b, UmlDiagram diagram, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<UML:Diagram.element>");
    b.append(lineEnd(level));
    for (UmlDiagramElement delement : diagram.getDiagramElements()) {
      genDiagramElement(b, delement, (level > -1) ? level + 1 : level);
      b.append(lineEnd(level));
    }
    b.append(pad);
    b.append("</UML:Diagram.element>");
  }




  private  void genDiagramElement(StringBuilder b, UmlDiagramElement element, int level) {
    b.append(getPadding(level));

    //<UML:DiagramElement geometry="Left=1111;Top=420;Right=1311;Bottom=563;" subject="EAID_DF1EA826_7FDA_426b_B896_E7C0A28CD862" seqno="1" style="DUID=58CA8375;HideIcon=0;"/>

    b.append("<UML:");
    b.append(element.getClassifier().getName());

    if (StringUtil.isNotBlank(element.getGeometry())) {
      b.append(" geometry=\"");
      b.append(element.getGeometry());
      b.append("\"");
    }

    if (element.getSubject() != null && StringUtil.isNotBlank(element.getSubject().getId())) {
      b.append(" subject=\"");
      b.append(element.getSubject().getId());
      b.append("\"");
    }

    b.append(" seqno=\"");
    b.append(element.getSequence());
    b.append("\"");

    if (StringUtil.isNotBlank(element.getStyle())) {
      b.append(" style=\"");
      b.append(element.getStyle());
      b.append("\"");
    }

    b.append("/>");

  }




  private  void generateElementXML(StringBuilder b, UmlNamedElement element, int level) {
    if (element != null) {
      // handle different types differently

      if (element instanceof UmlGeneralization) {
        genGeneralization(b, (UmlGeneralization)element, level);
      } else {
        String pad = getPadding(level);
        b.append(pad);
        b.append("<UML:");
        b.append(element.getClassifier().getName());

        if (StringUtil.isNotBlank(element.getName())) {
          b.append(" name=\"");
          b.append(element.getName());
          b.append("\"");
        }

        if (StringUtil.isNotBlank(element.getId())) {
          b.append(" xmi.id=\"");
          b.append(element.getId());
          b.append("\"");
        }

        b.append(" visibility=\"");
        b.append(element.getVisibility().getName());
        b.append("\"");

        b.append(" isRoot=\"");
        b.append(element.isRoot());
        b.append("\"");

        b.append(" isLeaf=\"");
        b.append(element.isLeaf());
        b.append("\"");

        b.append(" isActive=\"");
        b.append(element.isActive());
        b.append("\">");
        b.append(lineEnd(level));

        if (!element.getStereotypes().isEmpty()) {
          genStereotypes(b, element, (level > -1) ? level + 1 : level);
          b.append(lineEnd(level));
        }
        if (!element.getTaggedValues().isEmpty()) {
          genTaggedValues(b, element, (level > -1) ? level + 1 : level);
          b.append(lineEnd(level));
        }
        if (!element.getOwnedElements().isEmpty()) {
          genOwnedElements(b, element, (level > -1) ? level + 1 : level);
          b.append(lineEnd(level));
        }
        if (element instanceof UmlClassifier) {
          UmlClassifier classifier = (UmlClassifier)element;
          if (!classifier.getFeatures().isEmpty()) {
            genFeatures(b, classifier, (level > -1) ? level + 1 : level);
            b.append(lineEnd(level));
          }

          if (!classifier.getDataTypes().isEmpty()) {
            genDataTypes(b, classifier, (level > -1) ? level + 1 : level);
          }

          if (element instanceof UmlAssociation) {
            UmlAssociation association = (UmlAssociation)element;
            genAssociationEnds(b, association, (level > -1) ? level + 1 : level);
            b.append(lineEnd(level));
          }
        }

        b.append(pad);
        b.append("</UML:");
        b.append(element.getClassifier().getName());
        b.append(">");
      }

    } // !null
  }




  private  void genGeneralization(StringBuilder b, UmlGeneralization element, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<UML:");
    b.append(element.getClassifier().getName());

    if (StringUtil.isNotBlank(element.getId())) {
      b.append(" xmi.id=\"");
      b.append(element.getId());
      b.append("\"");
    }

    b.append(" subtype=\"");
    b.append(element.getSubType());
    b.append("\"");

    b.append(" supertype=\"");
    b.append(element.getSuperType());
    b.append("\"");

    if (!element.getTaggedValues().isEmpty()) {
      b.append(">");
      b.append(lineEnd(level));
      genTaggedValues(b, element, (level > -1) ? level + 1 : level);
      b.append(lineEnd(level));
      b.append(pad);
      b.append("</UML:");
      b.append(element.getClassifier().getName());
      b.append(">");
    } else {

      b.append("/>");
    }

  }




  private  void genAssociationEnds(StringBuilder b, UmlAssociation association, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<UML:Association.connection>");
    b.append(lineEnd(level));
    for (UmlAssociationEnd end : association.getEnds()) {
      genAssociationEnd(b, end, (level > -1) ? level + 1 : level);
      b.append(lineEnd(level));
    }
    b.append(pad);
    b.append("</UML:Association.connection>");
  }




  private  void genAssociationEnd(StringBuilder b, UmlAssociationEnd end, int level) {
    b.append(getPadding(level));

    b.append("<UML:AssociationEnd");

    if (StringUtil.isNotBlank(end.getName())) {
      b.append(" name=\"");
      b.append(end.getName());
      b.append("\"");
    }

    if (StringUtil.isNotBlank(end.getId())) {
      b.append(" xmi.id=\"");
      b.append(end.getId());
      b.append("\"");
    }

    b.append(" visibility=\"");
    b.append(end.getVisibility().getName());
    b.append("\"");

    if (end.getMultiplicity() != null) {
      b.append(" multiplicity=\"");
      b.append(end.getMultiplicity().toString());
      b.append("\"");
    }

    b.append(" aggregation=\"");
    b.append(end.getAggregation().getName());
    b.append("\"");

    b.append(" isOrdered=\"");
    b.append(end.isOrdered());
    b.append("\"");

    b.append(" targetScope=\"");
    b.append(end.getTargetScope().getName());
    b.append("\"");

    b.append(" changeable=\"");
    b.append(end.getChangeability().getName());
    b.append("\"");

    b.append(" isNavigable=\"");
    b.append(end.isNavigable());
    b.append("\"");

    b.append(" type=\"");
    b.append(end.getType());
    b.append("\"");

    if (!end.getTaggedValues().isEmpty()) {
      b.append(">");
      b.append(lineEnd(level));
      genTaggedValues(b, end, (level > -1) ? level + 1 : level);
      b.append(lineEnd(level));
      b.append(getPadding(level));
      b.append("</UML:AssociationEnd>");
    } else {
      b.append("/>");
    }

  }




  /**
   * @param b
   * @param classifier
   * @param level
   */
  private  void genDataTypes(StringBuilder b, UmlClassifier classifier, int level) {
    String pad = getPadding(level);
    for (UmlDataType type : classifier.getDataTypes()) {
      b.append(pad);
      b.append("<UML:");
      b.append(type.getClassifier().getName());

      if (StringUtil.isNotBlank(type.getName())) {
        b.append(" name=\"");
        b.append(type.getName());
        b.append("\"");
      }
      b.append(" xmi.id=\"");
      b.append(type.getId());
      b.append("\"");

      b.append(" visibility=\"");
      b.append(type.getVisibility().getName());
      b.append("\"");

      b.append(" isRoot=\"");
      b.append(type.isRoot());
      b.append("\"");

      b.append(" isLeaf=\"");
      b.append(type.isLeaf());
      b.append("\"");

      b.append(" isAbstract=\"");
      b.append(type.isAbstract());
      b.append("\"");
      b.append("/>");
      b.append(lineEnd(level));

    }

  }




  private  void genOwnedElements(StringBuilder b, UmlElement element, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<UML:Namespace.ownedElement>");
    b.append(lineEnd(level));
    for (UmlNamedElement ownedElement : element.getOwnedElements()) {
      generateElementXML(b, ownedElement, (level > -1) ? level + 1 : level);
      b.append(lineEnd(level));
    }
    b.append(pad);
    b.append("</UML:Namespace.ownedElement>");
  }




  private  void genTaggedValues(StringBuilder b, UmlElement element, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<UML:ModelElement.taggedValue>");
    b.append(lineEnd(level));
    for (TaggedValue tv : element.getTaggedValues()) {
      genTaggedValue(b, tv, (level > -1) ? level + 1 : level);
      b.append(lineEnd(level));
    }
    b.append(pad);
    b.append("</UML:ModelElement.taggedValue>");
  }




  /**
   * @param b
   * @param tv
   * @param level
   */
  private  void genTaggedValue(StringBuilder b, TaggedValue tv, int level) {
    b.append(getPadding(level));
    b.append("<UML:TaggedValue tag=\"");
    b.append(tv.getName());
    b.append("\" value=\"");
    b.append(StringUtil.StringToXML(tv.getValue())); // make XML safe
    b.append("\"/>");
  }




  private  void genStereotypes(StringBuilder b, UmlElement element, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<UML:ModelElement.stereotype>");
    b.append(lineEnd(level));
    for (UmlStereotype stype : element.getStereotypes()) {
      genStereotype(b, stype, (level > -1) ? level + 1 : level);
      b.append(lineEnd(level));
    }
    b.append(pad);
    b.append("</UML:ModelElement.stereotype>");
  }




  /**
   * @param b
   * @param stype
   * @param i
   */
  private  void genStereotype(StringBuilder b, UmlStereotype stype, int level) {
    b.append(getPadding(level));
    b.append("<UML:Stereotype name=\"");
    b.append(stype.getName());
    b.append("\"/>");
  }




  private  void genFeatures(StringBuilder b, UmlClassifier element, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<UML:Classifier.feature>");
    b.append(lineEnd(level));

    // get properties first
    for (UmlFeature feature : element.getFeatures()) {

      if (feature instanceof UmlStructuralFeature) {
        // handle properties / attributes
        UmlStructuralFeature structFeature = (UmlStructuralFeature)feature;
        genProperty(b, structFeature, (level > -1) ? level + 1 : level);
        b.append(lineEnd(level));
      } else {
        // handle operations
        UmlBehavioralFeature behaveFeature = (UmlBehavioralFeature)feature;
        genOperation(b, behaveFeature, (level > -1) ? level + 1 : level);
        b.append(lineEnd(level));
      }

    }

    b.append(pad);
    b.append("</UML:Classifier.feature>");
  }




  /**
   * @param b
   * @param feature
   * @param level
   */
  private  void genProperty(StringBuilder b, UmlStructuralFeature feature, int level) {

    String pad = getPadding(level);
    b.append(pad);

    //<UML:Attribute name="APP_ASSIGNMENT_GRP" changeable="none" visibility="public" ownerScope="instance" targetScope="instance">
    b.append("<UML:");
    b.append(feature.getClassifier().getName());
    b.append(" name=\"");
    b.append(feature.getName());
    b.append("\" xmi.id=\"");
    b.append(feature.getId());
    b.append("\">");
    b.append(lineEnd(level));

    // TODO <UML:Attribute.initialValue/>

    // <UML:StructuralFeature.type/>
    if (!feature.getTypes().isEmpty()) {
      genTypes(b, feature, (level > -1) ? level + 1 : level);
      b.append(lineEnd(level));
    }

    // <UML:ModelElement.stereotype/>
    if (!feature.getStereotypes().isEmpty()) {
      genStereotypes(b, feature, (level > -1) ? level + 1 : level);
      b.append(lineEnd(level));
    }
    // <UML:ModelElement.taggedValue/>
    if (!feature.getTaggedValues().isEmpty()) {
      genTaggedValues(b, feature, (level > -1) ? level + 1 : level);
      b.append(lineEnd(level));
    }

    b.append(pad);
    b.append("</UML:");
    b.append(feature.getClassifier().getName());
    b.append(">");
  }




  /**
   * @param b
   * @param feature
   * @param level
   */
  private  void genTypes(StringBuilder b, UmlStructuralFeature feature, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<UML:StructuralFeature.type>");
    b.append(lineEnd(level));

    for (UmlType type : feature.getTypes()) {
      genType(b, type, (level > -1) ? level + 1 : level);
      b.append(lineEnd(level));
    }
    b.append(pad);
    b.append("</UML:StructuralFeature.type>");
  }




  /**
   * @param b
   * @param type
   * @param level
   */
  private  void genType(StringBuilder b, UmlType type, int level) {
    b.append(getPadding(level));
    b.append("<UML:Classifier");

    if (StringUtil.isNotBlank(type.getReference())) {
      b.append(" xmi.idref=\"");
      b.append(type.getReference());
      b.append("\"");
    } else {
      b.append(" xmi.id=\"");
      b.append(type.getId());
      b.append("\"");
    }

    b.append("/>");
  }




  /**
   * @param b
   * @param feature
   * @param level
   */
  private  void genOperation(StringBuilder b, UmlBehavioralFeature feature, int level) {
    String pad = getPadding(level);
    b.append(pad);
    b.append("<UML:");
    b.append(feature.getClassifier().getName());
    if (StringUtil.isNotBlank(feature.getName())) {
        b.append(" name=\"");
        b.append(feature.getName());
        b.append("\"");
    }
    b.append(" xmi.id=\"");
    b.append(feature.getId());
    b.append("\"");

    b.append(" visibility=\"");
    b.append(feature.getVisibility().getName());
    b.append("\"");

    b.append(" isRoot=\"");
    b.append(feature.isRoot());
    b.append("\"");

    b.append(" isLeaf=\"");
    b.append(feature.isLeaf());
    b.append("\"");

    b.append(">");
    b.append(lineEnd(level));  

    // maybe add other stuff?
    
    b.append(getPadding(level));
    b.append("</UML:");
    b.append(feature.getClassifier().getName());
    b.append(">");
    b.append(pad);
  }



}
