/*
 * Copyright (c) 2003 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial API and implementation
 */
package coyote.commons;

import java.io.Reader;


/**
 * SimpleReader is a scaled-down version, slightly faster version of
 * StringReader.
 */
public final class SimpleReader extends Reader
{

  /** Field string */
  String string;

  /** Field length */
  int length;

  /** Field next */
  int next;

  /** Field mark */
  int mark;




  /**
   * Constructor FastReader
   *
   * @param text The string to be read by the reader
   */
  public SimpleReader( String text )
  {
    next = 0;
    mark = 0;
    string = text;
    length = text.length();
  }




  /**
   * Method read
   *
   * @return
   */
  public int read()
  {
    return ( next < length ) ? string.charAt( next++ ) : -1;
  }




  /**
   * Method read
   *
   * @param buffer the destination character array
   * @param offset
   * @param length
   *
   * @return
   */
  public int read( char buffer[], int offset, int length )
  {
    if( length == 0 )
    {
      return 0;
    }

    if( next >= length )
    {
      return -1;
    }
    else
    {
      int bytesToRead = Math.min( length - next, length );
      string.getChars( next, next + bytesToRead, buffer, offset );

      next += bytesToRead;

      return bytesToRead;
    }
  }




  /**
   * Method skip
   *
   * @param amount
   *
   * @return The number skipped
   */
  public long skip( long amount )
  {
    if( next >= length )
    {
      return 0L;
    }
    else
    {
      long skipped = Math.min( length - next, amount );
      next += skipped;

      return skipped;
    }
  }




  /**
   * Method ready
   *
   * @return
   */
  public boolean ready()
  {
    return true;
  }




  /**
   * Method markSupported
   *
   * @return
   */
  public boolean markSupported()
  {
    return true;
  }




  /**
   * Method mark
   *
   * @param limit
   */
  public void mark( int limit )
  {
    mark = next;
  }




  /**
   * Method reset
   */
  public void reset()
  {
    next = mark;
  }




  /**
   * Method close
   */
  public void close()
  {
    string = null;
  }
}
