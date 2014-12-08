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
 * The SecurityContext class models a logical grouping of roles and logins each with a 
 * set of permissions.
 * 
 * <p>Roles are named group of permissions.</p>
 * 
 * <p>Logins are a list of roles and additional permissions for that login.</p>
 * 
 * <p>Security contexts allow an application to obtain Logins by querying for 
 * CredentialSet. Those Logins contain Permissions and links to Roles against 
 * which actions against which targets can be checked.</p>
 * 
 * <p>It is possible for each service to have it's own security context. This 
 * means each service can have its own unique combination of roles and 
 * permissions.
 * 
 * <p>All a component need do is to use a credential set to locate a login in 
 * its context. Once the login is retrieved, the principal associated to that 
 * login can be ascertained. The login also contains the set of associated 
 * roles and permissions can be checked for authorization.
 */
public interface SecurityContext {

  /**
   * Retrieve a login using the given credential set for authentication.
   * 
   * <p>This method uses the given credentials to find the associated login. 
   * All the credentials much match, so in cases of multi-factor authentication
   * several credentials may be passed to this method to return the appropriate
   * login.</p>
   * 
   * @param creds The set of credentials to use in locating the login
   * 
   * @return The login which has a match to the given credentials
   */
  public Login getLogin( CredentialSet creds );




  /**
   * Retrieve a login using the given session identifier.
   * 
   * <p>Not all contexts are required to support sessions. Even if they do, the 
   * identifier of the session may not be static; they may change frequently as
   * in the case of a session nonce.
   * 
   * @param id the identifier of the session to retrieve
   * 
   * @return The session in this context with the given identifier or null if 
   * no session could be found.
   */
  public Login getLogin( String sessionId );




  /**
   * Add the given login to this context.
   * 
   * @param login The login to add.
   */
  public void add( Login login );




  /**
   * Add the given role to the context.
   * 
   * <p>All roles have a name, and if the given role has a name which already 
   * exists in the context, the existing context will be over written.</p>
   * 
   * @param role The role to add.
   */
  public void add( Role role );




  /**
   * Retrieve the role with the given name
   * 
   * @param name Name of the role to retrieve
   * 
   * @return The role in the context with the given name or null if there is no 
   * role in the context with the given name.
   */
  public Role getRole( String name );




  public Session createSession( Login login );




  /**
   * Create a new session with the given identifier for the given login.
   * 
   * @param id The identifier of this session
   * @param login THe login to be associated to this session
   * 
   * @return the session created for this login.
   */
  public Session createSession( String id, Login login );




  /**
   * Check to see if the given login has the given named permissions.
   * 
   * <p>This is a basic authorization check. The underlying implementation can 
   * choose to perform the check using a variety of strategies including, but 
   * not limited to, Role Based Access Control (RBAC) or individualized 
   * permissions. The implementation may also support the concept of revocation
   * where the role grants permissions, but the login contains permissions 
   * which revoke specific permissions.</p> 
   * 
   * <p>HINT: Permissions can be AND'ed to create more specific checks and 
   * OR'ed to create more broad checks as permissions are essentially bit 
   * flags.</p>
   * 
   * @param login The login to check
   * @param name The name of the permission
   * @param perms The permission flags to check
   * 
   * @return True if the given login has the given permissions, false otherwise.
   */
  public boolean allows( Login login, String name, long perms );




  /**
   * Retrieve a session by it's identifier.
   * 
   * @param sessionId the identifier of the session to retrieve.
   * 
   * @return the session with the given identifier or null if the context 
   * does not have a session with that identifier.
   */
  public Session getSession( String sessionId );




  /**
   * Retrieve a session based on its associated login.
   * 
   * @param login the login to which the session is associated.
   * 
   * @return the session for the given login or null if the login does not 
   * have a session in this context.
   */
  public Session getSession( Login login );

}
