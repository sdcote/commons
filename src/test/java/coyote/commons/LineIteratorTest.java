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
package coyote.commons;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;


/**
 * 
 */
public class LineIteratorTest {

  /**
   * Basic usage pattern for a LineIterator:<pre>
   * File file = new File("somefile.txt");
   * LineIterator it = FileUtil.lineIterator(file);
   * try {
   *   while (it.hasNext()) {
   *     String line = it.nextLine();
   *     // process the line
   *   }
   * } finally {
   *  it.close();
   * }</pre>
   */
  @Test
  public void testLineIterator() {
    // Create a reference to a file to read ; the source of LineIterator
    File file = new File( "src/main/java/coyote/commons/LineIterator.java" );
    if ( file.exists() ) {

      // Create a LineIterator for the file
      LineIterator it = FileUtil.lineIterator( file );
      assertNotNull( it );
      long lines = 0;

      // Place the iteration in a try-finally block to ensure the iterator
      // and its resources are closed
      try {
        while ( it.hasNext() ) {
          String line = it.nextLine();
          lines++;
          assertNotNull( line );
        }
        assertTrue( lines > 100 );
      }
      finally {
        // always close a line iterator!
        it.close();
      }
    } else {
      System.out.println( "Skipping" );
    }

  }

}
