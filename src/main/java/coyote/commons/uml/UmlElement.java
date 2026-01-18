/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

import coyote.commons.StringUtil;

import java.util.*;


/**
 * Element is the abstract root UML metaclass, it has no superclass in the 
 * hierarchy of UML elements. 
 * 
 * <p>It is the superclass for all metaclasses in the UML infrastructure 
 * library.</p>
 *  
 * http://www.uml-diagrams.org/uml-core.html#element
 * 
 * Although not part of the latest specification, any element in the model can 
 * contain tagged values as a convenience - it can be used in a variety of ways
 * including the specification of properties.
 */
public abstract class UmlElement {

  private UmlElement parent = null;

  protected final List<UmlNamedElement> ownedElements = new ArrayList<UmlNamedElement>();
  protected final Set<UmlStereotype> stereotypes = new HashSet<UmlStereotype>();
  protected final List<TaggedValue> taggedValues = new ArrayList<TaggedValue>();

  private boolean active = false;
  private static final Visibility DEFAULT_VISIBILITY = Visibility.PUBLIC;
  private Visibility visibility = DEFAULT_VISIBILITY;

  private String id = UUID.randomUUID().toString();
  private String reference = null;




  public void addElement(final UmlNamedElement child) {
    if(this.equals(child)) throw new IllegalArgumentException("Cannot add self as a child element.");
    child.setParent(this);
    ownedElements.add(child);
  }




  public void addStereotype(final UmlStereotype type) {
    if (type != null) {
      stereotypes.add(type);
    }
  }




  public UmlNamedElement findElement(final String name) {
    if (StringUtil.isNotBlank(name)) {
      for (final UmlNamedElement element : ownedElements) {
        if (name.equalsIgnoreCase(element.getName())) {
          return element;
        }
      }
    }
    return null;
  }




  public TaggedValue findTaggedValue(final String name) {
    if (StringUtil.isNotBlank(name)) {
      for (final TaggedValue tv : taggedValues) {
        if (name.equalsIgnoreCase(tv.getName())) {
          return tv;
        }
      }
    }
    return null;
  }




  public String findValue(final String name) {
    if (StringUtil.isNotBlank(name)) {
      for (final TaggedValue tv : taggedValues) {
        if (name.equalsIgnoreCase(tv.getName())) {
          return tv.getValue();
        }
      }
    }
    return null;
  }




  public UmlNamedElement getElement(final String name) {
    if (StringUtil.isNotBlank(name)) {
      for (final UmlNamedElement element : ownedElements) {
        if (name.equals(element.getName())) {
          return element;
        }
      }
    }
    return null;
  }




  /**
   * @return the id
   */
  public String getId() {
    return id;
  }




  /**
   * @return the list of owned model elements
   */
  public List<UmlNamedElement> getOwnedElements() {
    return ownedElements;
  }




  /**
   * @return the parent
   */
  public UmlElement getParent() {
    return parent;
  }




  /**
   * @return the reference
   */
  public String getReference() {
    return reference;
  }




  public List<String> getStereotypeNames() {
    final List<String> retval = new ArrayList<String>();
    for (final UmlStereotype type : stereotypes) {
      retval.add(type.getName());
    }
    return retval;
  }




  public List<UmlStereotype> getStereotypes() {
    final List<UmlStereotype> retval = new ArrayList<UmlStereotype>();
    for (final UmlStereotype type : stereotypes) {
      retval.add(type);
    }
    return retval;
  }




  public TaggedValue getTaggedValue(final String name) {
    if (StringUtil.isNotBlank(name)) {
      for (final TaggedValue tv : taggedValues) {
        if (name.equals(tv.getName())) {
          return tv;
        }
      }
    }
    return null;
  }




  /**
   * @return the taggedValues
   */
  public List<TaggedValue> getTaggedValues() {
    return taggedValues;
  }




  public String getValue(final String name) {
    if (StringUtil.isNotBlank(name)) {
      for (final TaggedValue tv : taggedValues) {
        if (name.equals(tv.getName())) {
          return tv.getValue();
        }
      }
    }
    return null;
  }




  /**
   * @return the active
   */
  public boolean isActive() {
    return active;
  }




  /**
   * @return true of this element does not contain any other elements
   */
  public boolean isLeaf() {
    return ownedElements.size() == 0;
  }




  /**
   * @return the root
   */
  public boolean isRoot() {
    return parent == null;
  }




  /**
   * @param active the active to set
   */
  public void setActive(final boolean active) {
    this.active = active;
  }




  /**
   * @param id the id to set
   */
  public void setId(final String id) {
    this.id = id;
  }




  /**
   * @param parent the parent to set
   */
  public void setParent(final UmlElement parent) {
    this.parent = parent;
  }




  /**
   * @param reference the reference to set
   */
  public void setReference(final String reference) {
    this.reference = reference;
  }




  public void setTaggedValue(final String name, final String value) {
    if (StringUtil.isNotBlank(name)) {
      final TaggedValue tv = getTaggedValue(name);

      if (value == null) {
        // remove the tagged value if the value being set is null
        if (tv != null) {
          taggedValues.remove(tv);
        }
      } else {
        if (tv != null) {
          // set the value in the existing tv
          tv.setValue(value);
        } else {
          // create a new tv to hold the value
          taggedValues.add(new TaggedValue(name, value, this));
        }
      }
    }
  }




  /**
   * @return the visibility
   */
  public Visibility getVisibility() {
    return visibility;
  }




  /**
   * @param visibility the visibility to set
   */
  public void setVisibility(Visibility visibility) {
    this.visibility = visibility;
  }

}
