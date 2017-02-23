/*
 * Copyright (c) 2002 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.network.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Default strategy for creating and cleaning up temporary files.
 * 
 * <p>By default, files are created by {@code File.createTempFile()} in the 
 * directory specified.
 */
public class DefaultTempFile implements TempFile {

  private final File file;

  private final OutputStream fstream;




  public DefaultTempFile( final File tempdir ) throws IOException {
    file = File.createTempFile( "HTTPD-", "", tempdir );
    fstream = new FileOutputStream( file );
  }




  @Override
  public void delete() throws Exception {
    HTTPD.safeClose( fstream );
    if ( !file.delete() ) {
      throw new Exception( "Could not delete temporary file" );
    }
  }




  @Override
  public String getName() {
    return file.getAbsolutePath();
  }




  @Override
  public OutputStream open() throws Exception {
    return fstream;
  }
}