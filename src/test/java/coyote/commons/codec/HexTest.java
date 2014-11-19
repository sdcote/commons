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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

import junit.framework.TestCase;

import org.junit.Assert;

import coyote.commons.StringUtil;


/**
 * 
 */
public class HexTest extends TestCase {

  private static final String BAD_ENCODING_NAME = "UNKNOWN";

  private final static boolean LOG = false;




  public HexTest( final String name ) {
    super( name );
  }




  private boolean charsetSanityCheck( final String name ) {
    final String source = "the quick brown dog jumped over the lazy fox";
    try {
      final byte[] bytes = source.getBytes( name );
      final String str = new String( bytes, name );
      final boolean equals = source.equals( str );
      if ( equals == false ) {
        // Here with:
        //
        // Java Sun 1.4.2_19 x86 32-bits on Windows XP
        // JIS_X0212-1990
        // x-JIS0208
        //
        // Java Sun 1.5.0_17 x86 32-bits on Windows XP
        // JIS_X0212-1990
        // x-IBM834
        // x-JIS0208
        // x-MacDingbat
        // x-MacSymbol
        //
        // Java Sun 1.6.0_14 x86 32-bits
        // JIS_X0212-1990
        // x-IBM834
        // x-JIS0208
        // x-MacDingbat
        // x-MacSymbol
        // 
        log( "FAILED charsetSanityCheck=Interesting Java charset oddity: Roundtrip failed for " + name );
      }
      return equals;
    } catch ( final UnsupportedEncodingException e ) {
      // Should NEVER happen since we are getting the name from the Charset class.
      if ( LOG ) {
        log( "FAILED charsetSanityCheck=" + name + ", e=" + e );
        log( e );
      }
      return false;
    } catch ( final UnsupportedOperationException e ) {
      // Caught here with:
      // x-JISAutoDetect on Windows XP and Java Sun 1.4.2_19 x86 32-bits
      // x-JISAutoDetect on Windows XP and Java Sun 1.5.0_17 x86 32-bits
      // x-JISAutoDetect on Windows XP and Java Sun 1.6.0_14 x86 32-bits
      if ( LOG ) {
        log( "FAILED charsetSanityCheck=" + name + ", e=" + e );
        log( e );
      }
      return false;
    }
  }




  /**
   * 
   */
  private void checkDecodeHexOddCharacters( final char[] data ) {
    try {
      Hex.decodeHex( data );
      fail( "An exception wasn't thrown when trying to decode an odd number of characters" );
    } catch ( final DecoderException e ) {
      // Expected exception
    }
  }




  private void log( final String s ) {
    if ( LOG ) {
      System.out.println( s );
      System.out.flush();
    }
  }




  private void log( final Throwable t ) {
    if ( LOG ) {
      t.printStackTrace( System.out );
      System.out.flush();
    }
  }




  public void testCustomCharset() throws UnsupportedEncodingException, DecoderException {
    final SortedMap map = Charset.availableCharsets();
    final Set keys = map.keySet();
    final Iterator iterator = keys.iterator();
    log( "testCustomCharset: Checking " + keys.size() + " charsets..." );
    while ( iterator.hasNext() ) {
      final String name = (String)iterator.next();
      testCustomCharset( name, "testCustomCharset" );
    }
  }




  /**
   * 
   */
  private void testCustomCharset( final String name, final String parent ) throws UnsupportedEncodingException, DecoderException {
    if ( charsetSanityCheck( name ) == false ) {
      return;
    }
    log( parent + "=" + name );
    final Hex customCodec = new Hex( name );
    // source data
    final String sourceString = "Hello World";
    final byte[] sourceBytes = sourceString.getBytes( name );
    // test 1
    // encode source to hex string to bytes with charset
    final byte[] actualEncodedBytes = customCodec.encode( sourceBytes );
    // encode source to hex string...
    String expectedHexString = Hex.encodeHexString( sourceBytes );
    // ... and get the bytes in the expected charset
    final byte[] expectedHexStringBytes = expectedHexString.getBytes( name );
    Assert.assertTrue( Arrays.equals( expectedHexStringBytes, actualEncodedBytes ) );
    // test 2
    String actualStringFromBytes = new String( actualEncodedBytes, name );
    assertEquals( name + ", expectedHexString=" + expectedHexString + ", actualStringFromBytes=" + actualStringFromBytes, expectedHexString, actualStringFromBytes );
    // second test:
    final Hex utf8Codec = new Hex();
    expectedHexString = "48656c6c6f20576f726c64";
    final byte[] decodedUtf8Bytes = (byte[])utf8Codec.decode( expectedHexString );
    actualStringFromBytes = new String( decodedUtf8Bytes, utf8Codec.getCharsetName() );
    // sanity check:
    assertEquals( name, sourceString, actualStringFromBytes );
    // actual check:
    final byte[] decodedCustomBytes = customCodec.decode( actualEncodedBytes );
    actualStringFromBytes = new String( decodedCustomBytes, name );
    assertEquals( name, sourceString, actualStringFromBytes );
  }




  public void testCustomCharsetBadNameDecodeObject() {
    try {
      new Hex( BAD_ENCODING_NAME ).decode( "Hello World".getBytes() );
      fail( "Expected " + DecoderException.class.getName() );
    } catch ( final DecoderException e ) {
      // Expected
    }
  }




  public void testCustomCharsetBadNameEncodeByteArray() {
    try {
      new Hex( BAD_ENCODING_NAME ).encode( "Hello World".getBytes() );
      fail( "Expected " + IllegalStateException.class.getName() );
    } catch ( final IllegalStateException e ) {
      // Expected
    }
  }




  public void testCustomCharsetBadNameEncodeObject() {
    try {
      new Hex( BAD_ENCODING_NAME ).encode( "Hello World" );
      fail( "Expected " + EncoderException.class.getName() );
    } catch ( final EncoderException e ) {
      // Expected
    }
  }




  public void testCustomCharsetToString() {
    assertTrue( new Hex().toString().indexOf( Hex.DEFAULT_CHARSET_NAME ) >= 0 );
  }




  public void testDecodeArrayOddCharacters() {
    try {
      new Hex().decode( new byte[] { 65 } );
      fail( "An exception wasn't thrown when trying to decode an odd number of characters" );
    } catch ( final DecoderException e ) {
      // Expected exception
    }
  }




  public void testDecodeBadCharacterPos0() {
    try {
      new Hex().decode( "q0" );
      fail( "An exception wasn't thrown when trying to decode an illegal character" );
    } catch ( final DecoderException e ) {
      // Expected exception
    }
  }




  public void testDecodeBadCharacterPos1() {
    try {
      new Hex().decode( "0q" );
      fail( "An exception wasn't thrown when trying to decode an illegal character" );
    } catch ( final DecoderException e ) {
      // Expected exception
    }
  }




  public void testDecodeClassCastException() {
    try {
      new Hex().decode( new int[] { 65 } );
      fail( "An exception wasn't thrown when trying to decode." );
    } catch ( final DecoderException e ) {
      // Expected exception
    }
  }




  public void testDecodeHexOddCharacters1() {
    checkDecodeHexOddCharacters( new char[] { 'A' } );
  }




  public void testDecodeHexOddCharacters3() {
    checkDecodeHexOddCharacters( new char[] { 'A', 'B', 'C' } );
  }




  public void testDecodeHexOddCharacters5() {
    checkDecodeHexOddCharacters( new char[] { 'A', 'B', 'C', 'D', 'E' } );
  }




  public void testDecodeStringOddCharacters() {
    try {
      new Hex().decode( "6" );
      fail( "An exception wasn't thrown when trying to decode an odd number of characters" );
    } catch ( final DecoderException e ) {
      // Expected exception
    }
  }




  public void testDencodeEmpty() throws DecoderException {
    assertTrue( Arrays.equals( new byte[0], Hex.decodeHex( new char[0] ) ) );
    assertTrue( Arrays.equals( new byte[0], new Hex().decode( new byte[0] ) ) );
    assertTrue( Arrays.equals( new byte[0], (byte[])new Hex().decode( "" ) ) );
  }




  public void testEncodeClassCastException() {
    try {
      new Hex().encode( new int[] { 65 } );
      fail( "An exception wasn't thrown when trying to encode." );
    } catch ( final EncoderException e ) {
      // Expected exception
    }
  }




  public void testEncodeDecodeRandom() throws DecoderException, EncoderException {
    final Random random = new Random();

    final Hex hex = new Hex();
    for ( int i = 5; i > 0; i-- ) {
      final byte[] data = new byte[random.nextInt( 10000 ) + 1];
      random.nextBytes( data );

      // static API
      final char[] encodedChars = Hex.encodeHex( data );
      byte[] decodedBytes = Hex.decodeHex( encodedChars );
      assertTrue( Arrays.equals( data, decodedBytes ) );

      // instance API with array parameter
      final byte[] encodedStringBytes = hex.encode( data );
      decodedBytes = hex.decode( encodedStringBytes );
      assertTrue( Arrays.equals( data, decodedBytes ) );

      // instance API with char[] (Object) parameter
      String dataString = new String( encodedChars );
      char[] encodedStringChars = (char[])hex.encode( dataString );
      decodedBytes = (byte[])hex.decode( encodedStringChars );
      assertTrue( Arrays.equals( StringUtil.getBytesUtf8( dataString ), decodedBytes ) );

      // instance API with String (Object) parameter
      dataString = new String( encodedChars );
      encodedStringChars = (char[])hex.encode( dataString );
      decodedBytes = (byte[])hex.decode( new String( encodedStringChars ) );
      assertTrue( Arrays.equals( StringUtil.getBytesUtf8( dataString ), decodedBytes ) );
    }
  }




  public void testEncodeEmpty() throws EncoderException {
    assertTrue( Arrays.equals( new char[0], Hex.encodeHex( new byte[0] ) ) );
    assertTrue( Arrays.equals( new byte[0], new Hex().encode( new byte[0] ) ) );
    assertTrue( Arrays.equals( new char[0], (char[])new Hex().encode( "" ) ) );
  }




  public void testEncodeZeroes() {
    final char[] c = Hex.encodeHex( new byte[36] );
    assertEquals( "000000000000000000000000000000000000000000000000000000000000000000000000", new String( c ) );
  }




  public void testHelloWorldLowerCaseHex() {
    final byte[] b = StringUtil.getBytesUtf8( "Hello World" );
    final String expected = "48656c6c6f20576f726c64";
    char[] actual;
    actual = Hex.encodeHex( b );
    assertTrue( expected.equals( new String( actual ) ) );
    actual = Hex.encodeHex( b, true );
    assertTrue( expected.equals( new String( actual ) ) );
    actual = Hex.encodeHex( b, false );
    assertFalse( expected.equals( new String( actual ) ) );
  }




  public void testHelloWorldUpperCaseHex() {
    final byte[] b = StringUtil.getBytesUtf8( "Hello World" );
    final String expected = "48656C6C6F20576F726C64";
    char[] actual;
    actual = Hex.encodeHex( b );
    assertFalse( expected.equals( new String( actual ) ) );
    actual = Hex.encodeHex( b, true );
    assertFalse( expected.equals( new String( actual ) ) );
    actual = Hex.encodeHex( b, false );
    assertTrue( expected.equals( new String( actual ) ) );
  }




  public void testRequiredCharset() throws UnsupportedEncodingException, DecoderException {
    testCustomCharset( "UTF-8", "testRequiredCharset" );
    testCustomCharset( "UTF-16", "testRequiredCharset" );
    testCustomCharset( "UTF-16BE", "testRequiredCharset" );
    testCustomCharset( "UTF-16LE", "testRequiredCharset" );
    testCustomCharset( "US-ASCII", "testRequiredCharset" );
    testCustomCharset( "ISO8859_1", "testRequiredCharset" );
  }
}