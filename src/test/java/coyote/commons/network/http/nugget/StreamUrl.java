/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.network.http.nugget;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import coyote.commons.network.http.IStatus;
import coyote.commons.network.http.Status;


public class StreamUrl extends DefaultStreamHandler {

  @Override
  public String getMimeType() {
    return "text/plain";
  }




  @Override
  public IStatus getStatus() {
    return Status.OK;
  }




  @Override
  public InputStream getData() {
    return new ByteArrayInputStream( "This is a stream of data.".getBytes() );
  }

}