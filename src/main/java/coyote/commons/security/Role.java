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


/**
 * The Role class models a named collection of permissions.
 * 
 * <p>Permissions are assigned to roles and permission checks are performed 
 * against roles as opposed to individuals to keep the number of permission 
 * collections to a minimum. This makes permission management easier.</p>
 */
public final class Role extends PermissionEnabledSubject implements Principal {
  private String _name = null;




  public Role( String name ) {
    _name = name;
  }




  public String getName() {
    return _name;
  }

}
