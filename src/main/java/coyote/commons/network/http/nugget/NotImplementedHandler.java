/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.network.http.nugget;

import coyote.commons.network.http.IStatus;
import coyote.commons.network.http.Status;


public class NotImplementedHandler extends DefaultHandler {

  @Override
  public String getMimeType() {
    return "text/html";
  }




  @Override
  public IStatus getStatus() {
    return Status.OK;
  }




  @Override
  public String getText() {
    return "<html><body><h3>Not implemented</h3><p>The uri is mapped in the router, but no handler is specified.</body></html>";
  }
}