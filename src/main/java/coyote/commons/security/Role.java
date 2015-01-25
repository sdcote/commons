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
 * 
 * <p>In some simplified security schemes, components only check for the role 
 * to grant access to components function. For example, if the 'admin' role is 
 * presented, then the component may allow access to all its functions.</p>
 */
public final class Role extends PermissionEnabledSubject implements Principal {
  private String _name = null;
  private String _desc = null;




  public Role( String name ) {
    _name = name;
  }




  public Role( String name, String description ) {
    _name = name;
    _desc = description;
  }




  public String getName() {
    return _name;
  }




  /**
   * @return the description of the role.
   */
  public String getDescription() {
    return _desc;
  }




  /**
   * Set the description of the role.
   * 
   * @param _desc the description to set
   */
  public void setDescription( String _desc ) {
    this._desc = _desc;
  }

}
