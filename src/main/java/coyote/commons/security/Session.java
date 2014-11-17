/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.security;

/**
 * 
 */
public interface Session {

  /**
   * Associate the given login with this session.
   * 
   * @param login The login associated to this session.
   */
  public void setLogin( Login login );




  /**
   * @return the Login associated with this session.
   */
  public Login getLogin();

}
