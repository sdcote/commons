/*
 * Copyright (c) 2006 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.cfg;

/**
 * The exception thrown when there are problems with configuration parameters
 */
public class ConfigurationException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 229675499440617423L;




  /**
   *
   */
  public ConfigurationException() {
    super();
  }




  /**
   * @param message the message of the exception
   */
  public ConfigurationException( final String message ) {
    super( message );
  }




  /**
   * @param message the message of the exception
   * @param newNested the cause of the exception
   */
  public ConfigurationException( final String message, final Throwable newNested ) {
    super( message, newNested );
  }




  /**
   * @param newNested the cause of the exception
   */
  public ConfigurationException( final Throwable newNested ) {
    super( newNested );
  }

}
