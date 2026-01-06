/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.cfg;

/**
 * This class models an attribute expected to be in a configuration.
 */
public class ConfigSlot {
  protected String name = null;
  protected String description = null;
  protected int type = 0;
  protected String defaultValue = null;
  protected String message = null;




  /**
   * Constructor ConfigSlot
   */
  public ConfigSlot() {}




  /**
   * Constructor ConfigSlot
   *
   * @param slot
   */
  public ConfigSlot( final ConfigSlot slot ) {
    name = slot.name;
    description = slot.description;
    type = slot.type;
    defaultValue = slot.defaultValue;
  }




  /**
   * Constructor ConfigSlot
   *
   * @param name The name of the attribute slot
   * @param description A string of descriptive text for the use/meaning of this attribute
   * @param dflt the default object value of this attribute
   */
  public ConfigSlot( final String name, final String description, final String dflt ) {
    if ( name != null ) {
      setName( name );
      setDescription( description );
      setDefaultValue( dflt );
    } else {
      throw new IllegalArgumentException( "ConfigSlot name is null" );
    }
  }




  /**
   * @return The default value for this named slot
   */
  public String getDefaultValue() {
    return defaultValue;
  }




  /**
   * @return A description of this configuration slot
   */
  public String getDescription() {
    return description;
  }




  /**
   * @return The user-defined message for this slot.
   */
  public String getMessage() {
    return message;
  }




  /**
   * @return The name of this slot
   */
  public String getName() {
    return name;
  }




  /**
   * Set the default value for this slot
   *
   * @param val The value to set as the default for this slot
   */
  public void setDefaultValue( final String val ) {
    defaultValue = val;
  }




  /**
   * Set the description of this configuration element.
   * 
   * <p>This is useful in UIs which prompt the user for configuration input.
   *
   * @param description The description of the configuration slot.
   */
  public void setDescription( final String description ) {
    this.description = description;
  }




  /**
   * Set a user-defined message for this slot.
   *
   * <p>Many times, the ConfigSlot is used to represent a mutable Attribute
   * instance, as in GUIs, where using an Attribute instance can be prohibitive
   * in its type checking. In such cases, it is useful to be able to pass an
   * ConfigSlot instead and then create an Attribute after all edits are
   * completed. In such cases, the ability to pass a user-defined message field
   * is useful as in the case where value failed some validity check and the
   * ConfigSlot is passed back to the GUI with the invalid value in the
   * defaultValue field and an error message in the Message field.
   *
   * @param message
   */
  public void setMessage( final String message ) {
    this.message = message;
  }




  /**
   * Set the name of this configuration slot.
   *
   * @param name the name of this configuration attribute.
   */
  public void setName( final String name ) {
    if ( name != null ) {
      this.name = name;
    } else {
      throw new IllegalArgumentException( "ConfigSlot name is null" );
    }
  }
}
