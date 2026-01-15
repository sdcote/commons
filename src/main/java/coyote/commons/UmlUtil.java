/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons;

import coyote.commons.uml.UmlAssociation;
import coyote.commons.uml.UmlAssociationEnd;
import coyote.commons.uml.UmlElement;
import coyote.commons.uml.UmlGeneralization;


/**
 * 
 */
public class UmlUtil {

  public static UmlAssociation associate(UmlElement source, UmlElement target) {
    return associate(source, null, target, null);
  }




  /**
   * 
   * @param source
   * @param srcName
   * @param target
   * @param tgtName
   * 
   * @return an association between the two given elements with each end named
   */
  public static UmlAssociation associate(UmlElement source, String srcName, UmlElement target, String tgtName) {

    UmlAssociationEnd src = new UmlAssociationEnd(srcName);

    UmlAssociationEnd tgt = new UmlAssociationEnd(tgtName);

    UmlAssociation association = new UmlAssociation();
    association.addEnd(src);
    association.addEnd(tgt);

    return association;
  }




  /**
   * 
   * @param subtype
   * @param supertype
   * 
   * @return a generalization with the two elements
   */
  public static UmlGeneralization generalize(UmlElement subtype, UmlElement supertype) {
    return new UmlGeneralization(subtype.getId(), supertype.getId());
  }

}
