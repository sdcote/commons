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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;


class StaticPageTestHandler extends StaticPageHandler {

  @Override
  protected BufferedInputStream fileToInputStream( File fileOrdirectory ) throws IOException {
    if ( "exception.html".equals( fileOrdirectory.getName() ) ) {
      throw new IOException( "trigger something wrong" );
    }
    return super.fileToInputStream( fileOrdirectory );
  }
}