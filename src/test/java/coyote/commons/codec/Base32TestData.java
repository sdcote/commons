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
package coyote.commons.codec;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


/**
 * 
 */
public class Base32TestData {

  static final String STRING_FIXTURE = "Hello World";

  static final String BASE32_FIXTURE = "JBSWY3DPEBLW64TMMQ======\r\n";

  // Some utility code to help test chunked reads of the InputStream.

  private final static int SIZE_KEY = 0;
  private final static int LAST_READ_KEY = 1;




  /**
   * Tests the supplied byte[] array to see if it contains the specified byte c.
   *
   * @param bytes byte[] array to test
   * @param c byte to look for
   * @return true if bytes contains c, false otherwise
   */
  static boolean bytesContain( final byte[] bytes, final byte c ) {
    for ( final byte b : bytes ) {
      if ( b == c ) {
        return true;
      }
    }
    return false;
  }




  private static int[] fill( final byte[] buf, final int offset, final InputStream in ) throws IOException {
    int read = in.read( buf, offset, buf.length - offset );
    int lastRead = read;
    if ( read == -1 ) {
      read = 0;
    }
    while ( ( lastRead != -1 ) && ( ( read + offset ) < buf.length ) ) {
      lastRead = in.read( buf, offset + read, buf.length - read - offset );
      if ( lastRead != -1 ) {
        read += lastRead;
      }
    }
    return new int[] { offset + read, lastRead };
  }




  /**
   * Returns an encoded and decoded copy of the same random data.
   *
   * @param codec the codec to use
   * @param size amount of random data to generate and encode
   * @return two byte[] arrays:  [0] = decoded, [1] = encoded
   */
  static byte[][] randomData( final BaseNCodec codec, final int size ) {
    final Random r = new Random();
    final byte[] decoded = new byte[size];
    r.nextBytes( decoded );
    final byte[] encoded = codec.encode( decoded );
    return new byte[][] { decoded, encoded };
  }




  private static byte[] resizeArray( final byte[] bytes ) {
    final byte[] biggerBytes = new byte[bytes.length * 2];
    System.arraycopy( bytes, 0, biggerBytes, 0, bytes.length );
    return biggerBytes;
  }




  static byte[] streamToBytes( final InputStream in ) throws IOException {
    // new byte[7] is obviously quite slow, but helps exercise the code.
    return streamToBytes( in, new byte[7] );
  }




  static byte[] streamToBytes( final InputStream in, byte[] buf ) throws IOException {
    try {
      int[] status = fill( buf, 0, in );
      int size = status[SIZE_KEY];
      int lastRead = status[LAST_READ_KEY];
      while ( lastRead != -1 ) {
        buf = resizeArray( buf );
        status = fill( buf, size, in );
        size = status[SIZE_KEY];
        lastRead = status[LAST_READ_KEY];
      }
      if ( buf.length != size ) {
        final byte[] smallerBuf = new byte[size];
        System.arraycopy( buf, 0, smallerBuf, 0, size );
        buf = smallerBuf;
      }
    }
    finally {
      in.close();
    }
    return buf;
  }

}