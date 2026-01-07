/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and implementation
 */
package coyote.commons.network.http.auth;

import java.util.Map;

import coyote.commons.network.http.HTTPSession;


/**
 * 
 */
public class TestAuthProvider implements AuthProvider {

  private volatile boolean allowConnections = true;
  private volatile boolean allowAuthentications = true;
  private volatile boolean allowAuthorizations = true;




  /**
   * @see AuthProvider#isSecureConnection(HTTPSession)
   */
  @Override
  public boolean isSecureConnection( HTTPSession session ) {

    return allowConnections;
  }




  /**
   * @see AuthProvider#isAuthenticated(HTTPSession)
   */
  @Override
  public boolean isAuthenticated( HTTPSession session ) {

    return allowAuthentications;
  }




  /**
   * @see AuthProvider#isAuthorized(HTTPSession, String)
   */
  @Override
  public boolean isAuthorized( HTTPSession session, String groups ) {

    return allowAuthorizations;
  }




  public void rejectAllConnections() {
    allowConnections = false;
  }




  public void allowAllConnections() {
    allowConnections = true;
  }




  public void rejectAllAuthentications() {
    allowAuthentications = false;
  }




  public void allowAllAuthentications() {
    allowAuthentications = true;
  }




  public void rejectAllAuthorizations() {
    allowAuthorizations = false;
  }




  public void allowAllAuthorizations() {
    allowAuthorizations = true;
  }




  /**
   * @see AuthProvider#authenticate(HTTPSession, Map)
   */
  @Override
  public boolean authenticate(HTTPSession session, Map<String, String> credentials) {
    return true;
  }

}
