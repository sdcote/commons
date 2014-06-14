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

/**
 * The ConfigException class models problems processing a configuration.
 */
public class ConfigException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 229675499440617423L;




  /**
   * 
   */
  public ConfigException() {}




  /**
   * @param message
   */
  public ConfigException( final String message ) {
    super( message );
  }




  /**
   * @param message
   * @param newNested
   */
  public ConfigException( final String message, final Throwable newNested ) {
    super( message, newNested );
  }




  /**
   * @param newNested
   */
  public ConfigException( final Throwable newNested ) {
    super( newNested );
  }

}
