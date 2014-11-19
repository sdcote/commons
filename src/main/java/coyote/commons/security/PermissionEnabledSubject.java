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

import java.util.HashMap;


/**
 * This class represents a security subject to which permissions can be 
 * assigned.
 * 
 * <p>This is intended to be the base class for both the Role class which 
 * represents a group of security subject and the Login, which represents an 
 * individual security subject. This allows permissions to be assigned at 
 * both the group and individual levels.</p>
 */
public class PermissionEnabledSubject {
  private HashMap<String, Permission> permissions = new HashMap<String, Permission>();




  public void addPermission( String target, long action ) {
    Permission perm = permissions.get( target );
    if ( perm != null ) {
      perm.addAction( action );
    } else {
      permissions.put( target, new Permission( target, action ) );
    }
  }




  public void addPermission( Permission perm ) {
    Permission p = permissions.get( perm.getTarget() );
    if ( p != null ) {
      p.addAction( perm.getAction() );
    } else {
      permissions.put( perm.getTarget(), perm );
    }
  }




  /**
   * Check to see if the given target allows the given action in this role.
   * 
   * @param target The target of the permission.
   * @param action The action being checked.
   * 
   * @return True if the role allows the action on the target, false otherwise.
   */
  public boolean allows( String target, long action ) {
    Permission perm = permissions.get( target );
    if ( perm != null && perm.allows( action ) )
      return true;

    return false;
  }
}
