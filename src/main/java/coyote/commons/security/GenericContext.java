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

import coyote.commons.StringUtil;


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
  private Map<String, Role> roles = new Hashtable<String, Role>();




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
    if ( getLogin( login.credentials ) == null ) {
      logins.add( login ); // ...add the login
    }
  }




  /**
   * @see coyote.commons.security.Context#add(coyote.commons.security.Role)
   */
  public void add( Role role ) {
    if ( role != null && StringUtil.isNotBlank( role.getName() ) )
      roles.put( role.getName(), role );
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




  /**
   * @see coyote.commons.security.Context#getRole(java.lang.String)
   */
  @Override
  public Role getRole( String name ) {
    Role retval = null;
    if ( StringUtil.isNotBlank( name ) ) {
      retval = roles.get( name );
    }
    return retval;
  }




  /**
   * @see coyote.commons.security.Context#loginHasPermission(coyote.commons.security.Login, java.lang.String, long)
   */
  @Override
  public boolean loginHasPermission( Login login, String name, long perms ) {

    boolean retval = false;

    // for each role in the login
    List<String> roles = login.getRoles();
    Role role = null;
    for ( String roleName : roles ) {
      role = getRole( roleName );
      if ( role != null && role.allows( name, perms ) ) {
        retval = true;
        break;
      }
    }

    // TODO: Check the login for specific permissions

    // TODO: Now check for revocations at the login level

    return retval;
  }

}
