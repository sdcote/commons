/*
 * Copyright (c) 2003 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and implementation
 */
package coyote.commons.template;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import coyote.commons.ArrayUtil;
import coyote.commons.DateUtil;
import coyote.commons.StringUtil;


/**
 * Simply a table of named string values.
 */
public class SymbolTable extends HashMap {

  /**
   * Constructor SymbolTable
   */
  public SymbolTable() {}




  /**
   * Read all the System properties into the SymbolTable.
   */
  public synchronized void readSystemProperties() {
    for ( Enumeration en = System.getProperties().propertyNames(); en.hasMoreElements(); ) {
      String name = (String)en.nextElement();
      put( name, System.getProperty( name ) );
    }
  }




  /**
   * Remove all the System properties from the SymbolTable.
   */
  public synchronized void removeSystemProperties() {
    for ( Enumeration en = System.getProperties().propertyNames(); en.hasMoreElements(); ) {
      String name = (String)en.nextElement();
      remove( name );
    }
  }




  /**
   * Return the String value of the named symbol from the table.
   *
   * @param key
   *
   * @return
   */
  public synchronized String getString( String key ) {
    if ( key != null ) {
      if ( containsKey( key ) ) {
        return get( key ).toString();
      } else if ( key.equals( "time" ) ) {
        return DateUtil.toExtendedTime( new Date() );
      } else if ( key.equals( "currentMilliseconds" ) ) {
        return Long.toString( System.currentTimeMillis() );
      } else if ( key.equals( "currentSeconds" ) ) {
        return Long.toString( System.currentTimeMillis() / 1000 );
      } else if ( key.equals( "epocTime" ) ) {
        return Long.toString( System.currentTimeMillis() / 1000 );
      } else if ( key.equals( "rfc822date" ) ) {
        return DateUtil.RFC822Format( new Date() );
      } else if ( key.equals( "iso8601date" ) ) {
        return DateUtil.ISO8601Format( new Date() );
      } else if ( key.equals( "iso8601GMT" ) ) {
        return DateUtil.ISO8601GMT( new Date() );
      } else if ( key.equals( "CR" ) ) {
        return StringUtil.CR;
      } else if ( key.equals( "LF" ) ) {
        return StringUtil.LF;
      } else if ( key.equals( "CRLF" ) ) {
        return StringUtil.CRLF;
      } else if ( key.equals( "FS" ) ) {
        return StringUtil.FILE_SEPARATOR;
      } else if ( key.equals( "PS" ) ) {
        return StringUtil.PATH_SEPARATOR;
      } else if ( key.equals( "HT" ) ) {
        return StringUtil.HT;
      } else if ( key.equals( "NL" ) ) {
        return StringUtil.NL;
      } else if ( key.equals( "symbolDump" ) ) {
        return dump();
      }
    }

    return "null";
  }




  /**
   * Go through all the symbols in the given table and add/replace them to our
   * table.
   *
   * @param symbols the Hashtable of name value pairs to merge.
   */
  public synchronized void merge( HashMap symbols ) {
    for ( Iterator it = symbols.keySet().iterator(); it.hasNext(); ) {
      try {
        String key = (String)it.next();
        put( key, symbols.get( key ) );
      } catch ( Exception ex ) {
        // key was not a String?
        // value was null?
      }
    }
  }




  /**
   * Method getChildNames
   *
   * @param prefix
   *
   * @return
   */
  public synchronized String[] getChildNames( String prefix ) {
    String[] retval = new String[0];

    for ( Iterator it = keySet().iterator(); it.hasNext(); ) {
      String key = (String)it.next();

      if ( key.startsWith( prefix ) ) {
        retval = (String[])ArrayUtil.addElement( (String[])retval, (String)key );
      }
    }

    return retval;
  }




  /**
   * Method dump
   *
   * @return
   */
  public synchronized String dump() {
    StringBuffer retval = new StringBuffer();

    for ( Iterator it = keySet().iterator(); it.hasNext(); ) {
      String key = (String)it.next();
      retval.append( "'" + key + "' = '" + get( key ).toString() + "'\r\n" );
    }

    return retval.toString();
  }

}