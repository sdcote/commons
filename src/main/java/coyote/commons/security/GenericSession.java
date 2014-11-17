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
public class GenericSession implements Session {
  private Login _login = null;




  /**
   * @see coyote.commons.security.Session#setLogin(coyote.commons.security.Login)
   */
  @Override
  public void setLogin( Login login ) {
    _login = login;
  }




  /**
   * @see coyote.commons.security.Session#getLogin()
   */
  @Override
  public Login getLogin() {
    return _login;
  }

}
