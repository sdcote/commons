/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.network.http.nugget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.IHTTPSession;
import coyote.commons.network.http.IStatus;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;


/**
 * General nugget to print static files and directories as a html page.
 */
public class StaticPageHandler extends DefaultHandler {

  /**
   * Split the given path into an array of tokens
   * 
   * @param uri the path to split on the '/' character
   * 
   * @return string array of the path elements
   */
  private static String[] getPathArray( final String uri ) {
    final String array[] = uri.split( "/" );
    final ArrayList<String> pathArray = new ArrayList<String>();

    for ( final String s : array ) {
      if ( s.length() > 0 ) {
        pathArray.add( s );
      }
    }

    return pathArray.toArray( new String[] {} );

  }




  /**
   * Returns an input stream of the given file or directory.
   * 
   * @param fileOrdirectory THe reference to the file ot directory
   * 
   * @return The input stream
   * 
   * @throws IOException if there were problems reading the file
   */
  protected BufferedInputStream fileToInputStream( final File fileOrdirectory ) throws IOException {
    return new BufferedInputStream( new FileInputStream( fileOrdirectory ) );
  }




  /**
   * Serve up the URI as a path to the 
   * @see coyote.commons.network.http.nugget.DefaultHandler#get(coyote.commons.network.http.nugget.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response get( final UriResource uriResource, final Map<String, String> urlParams, final IHTTPSession session ) {
    final String baseUri = uriResource.getUri();
    String realUri = HTTPDRouter.normalizeUri( session.getUri() );
    for ( int index = 0; index < Math.min( baseUri.length(), realUri.length() ); index++ ) {
      if ( baseUri.charAt( index ) != realUri.charAt( index ) ) {
        realUri = HTTPDRouter.normalizeUri( realUri.substring( index ) );
        break;
      }
    }
    File fileOrdirectory = uriResource.initParameter( File.class );
    for ( final String pathPart : getPathArray( realUri ) ) {
      fileOrdirectory = new File( fileOrdirectory, pathPart );
    }
    if ( fileOrdirectory.isDirectory() ) {
      fileOrdirectory = new File( fileOrdirectory, "index.html" );
      if ( !fileOrdirectory.exists() ) {
        fileOrdirectory = new File( fileOrdirectory.getParentFile(), "index.htm" );
      }
    }
    if ( !fileOrdirectory.exists() || !fileOrdirectory.isFile() ) {
      return new Error404UriHandler().get( uriResource, urlParams, session );
    } else {
      try {
        return HTTPD.newChunkedResponse( getStatus(), HTTPD.getMimeTypeForFile( fileOrdirectory.getName() ), fileToInputStream( fileOrdirectory ) );
      } catch ( final IOException ioe ) {
        return HTTPD.newFixedLengthResponse( Status.REQUEST_TIMEOUT, "text/plain", null );
      }
    }
  }




  @Override
  public String getMimeType() {
    throw new IllegalStateException( "This method should not be called" );
  }




  @Override
  public IStatus getStatus() {
    return Status.OK;
  }




  @Override
  public String getText() {
    throw new IllegalStateException( "This method should not be called" );
  }
}