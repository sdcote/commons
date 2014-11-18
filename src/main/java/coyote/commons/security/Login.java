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

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * This class models a list of roles and their associated permissions.
 */
public class Login {

  /** This logins identifier */
  String id;

  /** The credentials used to authenticate this login */
  CredentialSet credentials;

  /** The principal (entity) of this login. */
  Principal principal;

  /** A map of role names this login assumes. */
  HashSet<String> roles = new HashSet<String>();




  public Login( CredentialSet creds ) {
    credentials = creds;
  }




  /**
   * Constructor Login
   */
  public Login() {}




  /**
   * Add a role name to this login.
   * 
   * @param role The name of the role to add.
   */
  public void addRole( String role ) {
    if ( role != null && role.length() > 0 ) {
      roles.add( role );
    }
  }




  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder( "Login: Principal=" );
    if ( principal != null ) {
      if ( principal.getName() != null )
        b.append( principal.getName() );
      else
        b.append( "null" );
    } else {
      b.append( "NULL" );
    }

    b.append( " Creds:" );
    b.append( credentials.size() );

    return b.toString();
  }




  /**
   * Test to see if all the given credentials match what is recorded in in 
   * this login.
   * 
   * <p>The most common scenario is two credentials being passed to this method
   * for matching; username and password. It is therefore important to match 
   * both credentials.</p>
   * 
   * <p>Other scenarios involve multi-factor authentication with password and 
   * some other credential passed such as a biometric digest, or challenge 
   * response. The more credentials passed and matched, the higher the 
   * confidence of the authentication operation.</p>
   * 
   * <p>While this login may have dozens of credentials, the given credentials 
   * are expected to be a subset, maybe even one credential. If all the given 
   * credentials match, return true. If even one of the given credentials fail 
   * to match, then return false.</p>
   * 
   * @param creds The set of credentials to match.
   * 
   * @return True if all the given credentials match, false otherwise.
   */
  public boolean matchCredentials( CredentialSet creds ) {
    if ( creds != null ) {
      return credentials.matchAll( creds );
    } else {
      return false;
    }
  }




  /**
   * @return a list of role names to which this login belongs.
   */
  public List<String> getRoles() {
    return new ArrayList<String>( roles );
  }

}