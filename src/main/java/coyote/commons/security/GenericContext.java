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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * The GenericContext class models a named collection of roles, permissions and 
 * login memberships.
 * 
 * <p>This version of the context is a fully functional security context which
 * can be used in a variety of applications.</p>
 */
public class GenericContext implements Context {
  private String _name = null;

  private Map<String, Session> sessions = new Hashtable<String, Session>();
  private List<Login> logins = new ArrayList<Login>();




  public GenericContext() {
    _name = UUID.randomUUID().toString();
  }




  public GenericContext( String name ) {
    _name = name;
  }




  /**
   * This is essentially an authentication operation.
   * 
   * @see coyote.commons.security.Context#getLogin(coyote.commons.security.Credentials)
   */
  public Login getLogin( CredentialSet creds ) {
    // for each credential in the given set, see if there is a login which contains a match for each
    for ( Login login : logins ) {
      if ( login.matchCredentials( creds ) ) {
        return login;
      }
    }
    return null;
  }




  /**
   * @see coyote.commons.security.Context#add(coyote.commons.security.Login)
   */
  public void add( Login login ) {
    // if the login is not currently found in the context...
    if ( getLogin( login._credentials ) == null ) {
      logins.add( login ); // ...add the login
    }
  }




  /**
   * @see coyote.commons.security.Context#add(coyote.commons.security.Role)
   */
  public void add( Role role ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see coyote.commons.security.Context#getLogin(java.lang.String)
   */
  @Override
  public Login getLogin( String sessionId ) {
    // TODO Auto-generated method stub
    return null;
  }




  /**
   * @see coyote.commons.security.Context#createSession(coyote.commons.security.Login)
   */
  @Override
  public Session createSession( Login login ) {
    Session retval = new GenericSession();
    retval.setLogin( login );
    Login l = retval.getLogin();
    return null;
  }

}
