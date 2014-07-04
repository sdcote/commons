/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and implementation
 */
package coyote.commons.cfg;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * The Config class models a component that is used to make file-based 
 * configuration of components easier than using property files.
 * 
 * <p>The primary goal of this class is to allow hierarchical configurations to
 * be specified using XML as a formatting strategy. Basic File and network
 * protocol I/O is supported in a simple interface.</p>
 * 
 * <p>This is designed to be a simple class to perform a simple task. It is NOT 
 * designed to be all things to all people. There is no concept of data types,
 * inheritance, modification listeners, structure validation or data integrity
 * in this class.</p>
 */
public class Config implements Cloneable, Serializable {

  /**
   * 
   */
  private class ConfigField {
    private String name = null;
    private String value = null;




    ConfigField( final String name, final String value ) {
      this.name = name;
      this.value = value;
    }
  }

  /** Platform specific line separator (default = CRLF) */
  public static final String LINE_FEED = System.getProperty( "line.separator", "\r\n" );

  public static final String CLASS_TAG = "Class";
  static final String ID_ATTR = "id";

  static final String SEQ_ATTR = "seq";

  /** Serialization identifier */
  private static final long serialVersionUID = -6020161245846637528L;




  /**
   * Read a configuration XML file and return a Config reference.
   * 
   * @param file File reference from which to read.
   * 
   * @return A Config object populated with data from the given XML file/
   * 
   * @throws IOException in the file could not be read
   * @throws ConfigException if the XML was malformed or otherwise invalid.
   */
  public static Config read( final File file ) throws IOException, ConfigException {
    return Config.read( new FileInputStream( file ) );
  }




  /**
   * Read configuration XML data from an InputStream and return a Config 
   * reference.
   * 
   * @param configStream The input stream from ehich to read the XML data
   * 
   * @return A Config object populated with data from the given XML file/
   * 
   * @throws ConfigException if the XML was malformed or otherwise invalid.
   */
  public static Config read( final InputStream configStream ) throws ConfigException {
    final Config retval = new Config();
    Document document = null;

    try {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      final DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse( configStream );
    } catch ( final Throwable e ) {
      throw new ConfigException( e );
    }

    if ( document != null ) {
      retval.parse( document );
    }

    return retval;
  }




  /**
   * Read a configuration XML file using the given name and return a Config 
   * reference.
   * 
   * @param filename name of the file to open read and parse
   * 
   * @return A Config object populated with data from the given XML file/
   * 
   * @throws IOException if the file could not be found or read
   * @throws ConfigException if the XML was malformed or otherwise invalid.
   */
  public static Config read( final String filename ) throws IOException, ConfigException {
    return Config.read( new FileInputStream( filename ) );
  }




  /**
   * Read in XML configuration data using the file or network resource 
   * specified in the given URI.
   * 
   * @param uri Identifier of the resource to load
   * 
   * @return A Config object populated with data from the given XML file/
   * 
   * @throws IOException if the resource specified by the URI could not be read
   * @throws ConfigException if the XML was malformed or otherwise invalid.
   */
  public static Config read( final URI uri ) throws IOException, ConfigException {
    if ( uri.getScheme().toLowerCase().startsWith( "file" ) ) {
      return Config.read( new FileInputStream( uri.getAuthority() ) );
    } else {
      return Config.read( uri.toURL().openStream() );
    }
  }

  /** Name of this configuration section */
  private String name = null;

  /** An identifier to be used with this configuration section */
  private String id = null;

  /** A sequence attribute */
  private long seq = 0;

  /** sections or child configurations nested in this config */
  ArrayList<Config> sections = new ArrayList<Config>();

  /**
   * The array of elements this configuration holds
   */
  ArrayList<ConfigField> fields = new ArrayList<ConfigField>();

  /**
   * A collection of ConfigSlots we use to optionally validate the completeness 
   * of the Config object or to provide default configurations.
   */
  private HashMap<String, ConfigSlot> slots = null;

  private final StringBuffer comments = new StringBuffer();




  public Config() {
    name = "Config";
  }




  public Config( final String xml ) throws ConfigException {
    Document document = null;

    try {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      final DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse( new ByteArrayInputStream( xml.getBytes() ) );
    } catch ( final Exception e ) {
      throw new ConfigException( e );
    }

    if ( document != null ) {
      this.parse( document );
    }
  }




  /**
   * Add a child configuration object to the list of children.
   * 
   * <p>Note: No check is made to see if the config has already been added. It
   * is therefore possible to add the same configuration multiple times.</p>
   *
   * @param config config to be added
   *
   * @throws IllegalArgumentException If the configuration cannot be added (see setParent)
   */
  public void add( final Config config ) throws IllegalArgumentException {
    if ( config == null ) {
      return;
    }

    if ( config == this ) {
      throw new IllegalArgumentException( "Configuration can not be added to itself" );
    }

    sections.add( config );
  }




  public int add( final String name, final String value ) {
    fields.add( new ConfigField( name, value ) );

    return fields.size() - 1;
  }




  /**
   * Add the referenced ConfigSlot.
   *
   * @param slot the reference to the ConfigSlot to add.
   */
  public void addConfigSlot( final ConfigSlot slot ) {
    if ( slots == null ) {
      slots = new HashMap<String,ConfigSlot>();
    }

    if ( slot != null ) {
      slots.put( slot.getName(), slot );
    }
  }




  /**
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    final Config retval = new Config();
    retval.setName( name );

    final String[] anames = getElementNames();
    if ( anames.length > 0 ) {
      for ( final String aname : anames ) {
        retval.set( aname, get( aname ) );
      }
    }

    for ( final Iterator<Config> it = sections.iterator(); it.hasNext(); retval.add( (Config)( (Config)it.next() ).clone() ) ) {
      ;
    }

    return retval;
  }




  /**
   * Return an Iterator over all the ConfigSlots
   *
   * @return an Iterator over all the ConfigSlot, never returns null;
   */
  public Iterator<ConfigSlot> configSlotIterator() {
    if ( slots != null ) {
      return slots.values().iterator();
    } else {
      return new Vector<ConfigSlot>().iterator();
    }
  }




  /**
   * Checks to see if the configuration contains the named element
   *
   * @param name String which represents the name of the element to check
   *
   * @return boolean True if an Attribute with the given name is contained in
   *         the configuration, False if otherwise.
   */
  public boolean contains( final String name ) {
    ConfigField field;
    for ( int i = 0; i < fields.size(); i++ ) {
      field = (ConfigField)fields.get( i );
      if ( ( field.name != null ) && field.name.equals( name ) ) {
        return true;
      }
    }
    return false;
  }




  /**
   * Retrieve the first occurrence of a named element's data from the 
   * configuration.
   * 
   * @param name The name of the element to retrieve.
   * 
   * @return The value of the named element or null if the element contained 
   *         null or an element with the given name was not found.
   */
  public String get( final String name ) {
    return get( name, null );
  }




  /**
   * Retrieve the first occurrence of a named element's data from the 
   * configuration or the passed default value if the named element is not 
   * found.
   * 
   * <p>It is possible for this method to return null as an empty node may be 
   * stored with the given name. In this case, the default value will NOT be 
   * returned as that null value is the true value of the named node.</p>
   * 
   * <p>The default argument will only be returned if the named element is not 
   * found.</p>
   *
   * @param name String which represents the name of the data to retrieve
   * @param deflt default value to return if there is no element found with
   *          that name
   *
   * @return value The data value with the given name, or the default argument 
   *         passed if the element with the given name was not found.
   */
  public String get( final String name, final String deflt ) {
    ConfigField field;
    for ( int i = 0; i < fields.size(); i++ ) {
      field = (ConfigField)fields.get( i );

      if ( ( field.name != null ) && field.name.equals( name ) ) {
        return field.value;
      }
    }

    return deflt;
  }




  /**
   * Retrieve all the element values with the given name.
   * 
   * @param name The name of the elements to retrieve.
   * 
   * @return The array of values with the given name. An empty array implies no
   *         elements with the given name exists. Array elements with null 
   *         values indicate named elements exist but no value was specified.
   */
  public String[] getArray( final String name ) {
    final ArrayList<String> list = new ArrayList<String>();
    ConfigField field;
    for ( final Iterator<ConfigField> it = fields.iterator(); it.hasNext(); ) {
      field = (ConfigField)it.next();
      if ( field.name.equals( name ) ) {
        list.add( field.value );
      }
    }

    final String[] retval = new String[list.size()];
    for ( int x = 0; x < retval.length; retval[x] = (String)list.get( x++ ) ) {
      ;
    }
    return retval;

  }




  /**
   * @return the className
   */
  public String getClassName() {
    return get( Config.CLASS_TAG );
  }




  /**
   * Returns the first occurrence of a named section.
   *
   * @param type The node name to match.
   *
   * @return Reference to the first child configuration (section) with the 
   *         given node name.
   */

  public Config getConfig( final String name ) {
    if ( ( name != null ) && ( name.length() > 0 ) ) {
      for ( int x = 0; x < sections.size(); x++ ) {
        final Config retval = (Config)sections.get( x );

        if ( name.equals( retval.getName() ) ) {
          return retval;
        }
      }
    }
    return null;
  }




  public int getConfigCount() {
    return sections.size();
  }




  /**
   * Returns an Iterator through the child configurations.
   *
   * <p>The Iterator will allow one to access each child configuration.</p>
   *
   * @return Iterator through the child configurations.
   */
  public Iterator<Config> getConfigIterator() {
    return sections.iterator();
  }




  /**
   * Returns an Iterator through the child configuration sections that match 
   * the given name.
   *
   * <p>The Iterator will allow one to access each child configuration whose node
   * type (XML element name) matches the given type.</p>
   * 
   * <p>If the argument of '*' is passed as the type argument, then all child 
   * configurations will be returned.</p>
   *
   * @param type The node name to match or '*" for all children.
   *
   * @return Iterator through the child configurations.
   */
  public Iterator<Config> getConfigIterator( final String type ) {
    if ( "*".equals( type ) ) {
      return getConfigIterator();
    } else {
      final Vector<Config> retval = new Vector<Config>();

      for ( int x = 0; x < sections.size(); x++ ) {
        if ( type.equals(  sections.get( x ).getName()) ) {
          retval.add( sections.get( x ) );
        }
      }

      return retval.iterator();
    }

  }




  /**
   * Retrieve a named ConfigSlot from the configuration
   *
   * @param name String which represents the name of the slot to retrieve
   *
   * @return value ConfigSlot object with the given name or null if it does
   *         not exist
   */
  public ConfigSlot getConfigSlot( final String name ) {
    if ( slots != null ) {
      synchronized( slots ) {
        return (ConfigSlot)slots.get( name );
      }
    } else {
      return null;
    }
  }




  /**
   * Access the current number of elements set in this configuration.
   * 
   * @return number of named values in this configuration
   */
  public int getElementCount() {
    return fields.size();
  }




  public Iterator<String> getElementNameIterator() {
    final HashSet<String> retval = new HashSet<String>();
    ConfigField field;
    for ( final Iterator<ConfigField> it = fields.iterator(); it.hasNext(); ) {
      field = it.next();

      if ( field.name != null ) {
        retval.add( field.name );
      }
    }

    return retval.iterator();
  }




  /**
   * Get the names of all the elements in this configuration.
   *
   * @return the names used to access the element values
   */
  public String[] getElementNames() {
    final ArrayList<String> list = new ArrayList<String>();
    for ( final Iterator<String> it = getElementNameIterator(); it.hasNext(); list.add( it.next() ) ) {
      ;
    }
    final String[] retval = new String[list.size()];
    for ( int x = 0; x < retval.length; retval[x] = (String)list.get( x++ ) ) {
      ;
    }
    return retval;
  }




  /**
   * @return the id
   */
  public String getId() {
    return id;
  }




  /**
   * @return the name
   */
  public String getName() {
    return name;
  }




  void parse( final Document dom ) throws ConfigException {
    // this gives the class the ability to pull info and attributes off the 
    // document level

    // get
    Node root = dom.getFirstChild();

    while ( root != null ) {
      if ( root.getNodeType() == Node.COMMENT_NODE ) {
        // Java5 comments.append( root.getTextContent() );
        comments.append( root.getNodeValue() );
        // System.out.println("COMMENTS: "+comments);

        root = root.getNextSibling();
      }

      // if the node is an element
      if ( root.getNodeType() == Node.ELEMENT_NODE ) {
        // System.out.println( "ROOT name:" + root.getNodeName() + "  type:" + root.getNodeType() + "  attr:" + root.getAttributes().getLength() );

        // parse it for this config
        parse( root, 0 );

        // only parse the first node
        break;
      }
    }

    // System.out.println( "Completed parsing document " + comments.toString() );
  }




  /**
   * Parse the XML into sub-configs and elements.
   *
   * <p>This method is NOT thread-safe. It is designed to be externally
   * synchronized.</p>
   *
   * @param node XML node to parse for data
   * @param lvl the level this node is in the configuration
   */
  public void parse( Node node, final int lvl ) throws ConfigException {
    if ( node == null ) {
      throw new IllegalArgumentException( "Unable to parse a null XML document" );
    }

    // http://java.sun.com/j2se/1.5.0/docs/api/index.html?javax/xml/package-summary.html
    // http://java.sun.com/j2se/1.4.2/docs/api/index.html?javax/xml/package-summary.html

    // Skip past any non-elements
    if ( node.getNodeType() != Node.ELEMENT_NODE ) {
      node = node.getNextSibling();
    }

    // System.out.println( lvl + " SECTION name:" + node.getNodeName() + " type:" + node.getNodeType() + " attrs:" + node.getAttributes().getLength() );

    name = node.getNodeName();
    // Try to preserve the ID and SEQ attributes
    if ( node.getAttributes().getLength() > 0 ) {
      final NamedNodeMap map = node.getAttributes();
      for ( int x = 0; x < map.getLength(); x++ ) {
        final Node anode = map.item( x );
        if ( ID_ATTR.equalsIgnoreCase( anode.getNodeName() ) ) {
          id = anode.getNodeValue();
        } else if ( SEQ_ATTR.equalsIgnoreCase( anode.getNodeName() ) ) {
          try {
            seq = Long.parseLong( anode.getNodeValue() );
          } catch ( final NumberFormatException e ) {
            // System.out.println( "NAN SEQ ATTR:" + anode.getNodeName() + " = " + anode.getNodeValue() );
          } catch ( final DOMException e ) {
            e.printStackTrace();
          }
        } else {
          // System.out.println( "ATTR:" + anode.getNodeName() + " = " + anode.getNodeValue() );
        }
      }

    }

    // Process all the children for this node
    Node child = node.getFirstChild();

    // while there are sections
    while ( child != null ) {
      // System.out.println( lvl + " CHILD name:" + child.getNodeName() + " type:" + child.getNodeType() + " attrs:" + child.getAttributes() );

      // only process XML element types
      if ( child.getNodeType() == Node.ELEMENT_NODE ) {
        // System.out.println( lvl + " CHILD ELEMENT name:" + child.getNodeName() + " children:" + child.getChildNodes().getLength() );
        // Empty nodes have no children
        if ( child.getChildNodes().getLength() == 0 ) {
          // System.out.println( level + " EMPTY CHILD ELEMENT:" + child.getNodeName() );
          fields.add( new ConfigField( child.getNodeName(), null ) );
        }
        // scalar node have only one child
        else if ( child.getChildNodes().getLength() == 1 ) {

          // System.out.println( level + " SCALAR CHILD ELEMENT:" + child.getNodeName() + " VALUE:" + child.getFirstChild().getNodeValue() );
          fields.add( new ConfigField( child.getNodeName(), child.getFirstChild().getNodeValue() ) );
        } else {
          // System.out.println( level + " MULTI ("+child.getChildNodes().getLength()+") CHILD ELEMENT:" + child.getNodeName() + " VALUE:" + child.getNodeValue() );
          // Assume a section is being specified
          try {
            final Config section = new Config();
            section.parse( child, lvl + 1 );
            sections.add( section );
          } catch ( final ConfigException e ) {
            throw new ConfigException( "Problems at " + node.getNodeName(), e );
          }
        }
      }

      child = child.getNextSibling();

    }
  }




  /**
   * Remove the referenced ConfigSlot
   *
   * @param slot The reference to the ConfigSlot to remove.
   */
  public void removeConfigSlot( final ConfigSlot slot ) {
    if ( slots == null ) {
      return;
    } else {
      synchronized( slots ) {
        slots.remove( slot );
      }
    }
  }




  /**
   * Set and possibly over write an existing element with the given name with 
   * the given value.
   * 
   * <p>Used to avoid duplicate elements.</p>
   * 
   * @param name Name of the value to set.
   * @param value The value to set
   * 
   * @see #add(String, String)
   */
  public void set( final String name, final String value ) {
    ConfigField field;
    for ( int i = 0; i < fields.size(); i++ ) {
      field = (ConfigField)fields.get( i );

      if ( ( field.name != null ) && field.name.equals( name ) ) {
        field.value = value;
        return;
      }
    }

    // Not found, so add a value with this name
    add( name, value );
  }




  /**
   * @param name the class name to set
   */
  public void setClassName( final String name ) {
    set( Config.CLASS_TAG, name );
  }




  /**
   * Use the set configuration slots and prime the configuration with those defaults.
   */
  public void setDefaults() {
    final Iterator<ConfigSlot> it = configSlotIterator();

    while ( it.hasNext() ) {
      final ConfigSlot slot = it.next();

      if ( slot != null ) {
        final String defaultValue = slot.getDefaultValue();

        if ( defaultValue != null ) {
          set( slot.getName(), defaultValue );
        }
      }
    }

  }




  /**
   * @param id the id to set
   */
  public void setId( final String id ) {
    this.id = id;
  }




  /**
   * @param name the name to set
   */
  public void setName( final String name ) {
    this.name = name;
  }




  /**
   * Convert this configuration into an indented XML string.
   *
   * <p>This will start the XML at position 0 and indent all child nodes two
   * spaces. This is the same as calling <code>toIndentedXML( 0 )</code></p>
   *
   * @return String XML which represents this configuration
   */
  public String toIndentedXML() {
    return toIndentedXML( 0 );
  }




  /**
   * Convert this configuration into an indented XML string.
   *
   * <p>If the indent argument is negative, then no indenting will occur and
   * all the XML will be placed in a single line. If the indent argument is
   * zero or greater, then the resultant XML will be indented the given number
   * of spaces with its children being indented incrementally by 2 spaces.</p>
   *
   * @param indent The number of spaces to indent this configuration
   *
   * @return String XML which represents this configuration
   */
  public String toIndentedXML( final int indent ) {
    String padding = null;
    int nextindent = -1;

    if ( indent > -1 ) {
      final char[] pad = new char[indent];
      for ( int i = 0; i < indent; pad[i++] = ' ' ) {
        ;
      }

      padding = new String( pad );
      nextindent = indent + 2;
    } else {
      padding = new String( "" );
    }

    final StringBuffer xml = new StringBuffer( padding + "<" );

    xml.append( name );

    if ( ( id != null ) && ( id.length() > 0 ) ) {
      xml.append( " id=\"" + id + "\"" );
    }
    if ( seq != 0 ) {
      xml.append( " seq=\"" + seq + "\"" );
    }

    if ( ( fields.size() > 0 ) || ( sections.size() > 0 ) ) {
      xml.append( ">" );

      if ( indent >= 0 ) {
        xml.append( LINE_FEED );
      }

      // Output any child configurations first
      Config section = null;

      for ( final Iterator<Config> it = sections.iterator(); it.hasNext(); ) {
        section = (Config)it.next();

        xml.append( section.toIndentedXML( nextindent ) );

        if ( indent >= 0 ) {
          xml.append( LINE_FEED );
        }
      }

      // Elements are last

      if ( fields.size() > 0 ) {
        char[] apad = null;
        if ( nextindent >= 0 ) {
          apad = new char[nextindent];
          for ( int i = 0; i < nextindent; apad[i++] = ' ' ) {
            ;
          }
        } else {
          apad = new char[0];
        }

        for ( final Iterator<ConfigField> it = fields.iterator(); it.hasNext(); ) {
          final ConfigField field = it.next();

          xml.append( apad );
          xml.append( "<" );
          xml.append( field.name );

          if ( field.value != null ) {
            xml.append( ">" );
            xml.append( field.value );
            xml.append( "</" );
            xml.append( field.name );
            xml.append( ">" );
          } else {
            xml.append( "/>" );
          }

          if ( indent >= 0 ) {
            xml.append( LINE_FEED );
          }
        }
      }

      xml.append( padding );
      xml.append( "</" );
      xml.append( name );
      xml.append( ">" );
    } else {
      xml.append( "/>" );
    }

    return xml.toString();
  }




  /**
   * Returns the XML representation of the entire configuration in a single line.
   *
   * @return a single-line String representing the configuration.
   */
  public String toXML() {
    return toIndentedXML( -1 );
  }

}
