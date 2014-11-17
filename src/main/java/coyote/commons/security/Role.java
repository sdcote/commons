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
 * The Role class models a named collection of permissions.
 * 
 * <p>Permissions are assigned to roles and permission checks are performed 
 * against roles as opposed to individuals to keep the number of permission 
 * collections to a minimum. This makes permission management easier.
 */
public final class Role {
  private String _name = null;
  private HashMap<String, Permission> _permissions = new HashMap<String, Permission>();




  public Role( String name ) {
    _name = name;
  }




  public String getName() {
    return _name;
  }




  public void addPermission( String target, long action ) {
    Permission perm = _permissions.get( target );
    if ( perm != null ) {
      perm.addAction( action );
    } else {
      _permissions.put( target, new Permission( target, action ) );
    }
  }




  public void addPermission( Permission perm ) {
    Permission p = _permissions.get( perm.getTarget() );
    if ( p != null ) {
      p.addAction( perm.getAction() );
    } else {
      _permissions.put( perm.getTarget(), perm );
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
    Permission perm = _permissions.get( target );
    if ( perm != null && perm.allows( action ) )
      return true;

    return false;
  }
}
