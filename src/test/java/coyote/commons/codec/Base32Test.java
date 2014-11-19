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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import coyote.commons.StringUtil;


public class Base32Test {

  private static final String[][] BASE32_TEST_CASES = { // RFC 4648
  { "", "" }, { "f", "MY======" }, { "fo", "MZXQ====" }, { "foo", "MZXW6===" }, { "foob", "MZXW6YQ=" }, { "fooba", "MZXW6YTB" }, { "foobar", "MZXW6YTBOI======" }, };

  private static final String[][] BASE32HEX_TEST_CASES = { // RFC 4648
  { "", "" }, { "f", "CO======" }, { "fo", "CPNG====" }, { "foo", "CPNMU===" }, { "foob", "CPNMUOG=" }, { "fooba", "CPNMUOJ1" }, { "foobar", "CPNMUOJ1E8======" }, };

  private static final String[][] BASE32_TEST_CASES_CHUNKED = { //Chunked
  { "", "" }, { "f", "MY======\r\n" }, { "fo", "MZXQ====\r\n" }, { "foo", "MZXW6===\r\n" }, { "foob", "MZXW6YQ=\r\n" }, { "fooba", "MZXW6YTB\r\n" }, { "foobar", "MZXW6YTBOI======\r\n" }, };

  private static final String[][] BASE32_PAD_TEST_CASES = { // RFC 4648
  { "", "" }, { "f", "MY%%%%%%" }, { "fo", "MZXQ%%%%" }, { "foo", "MZXW6%%%" }, { "foob", "MZXW6YQ%" }, { "fooba", "MZXW6YTB" }, { "foobar", "MZXW6YTBOI%%%%%%" }, };




  @Test
  public void testBase32Chunked() throws Exception {
    final Base32 codec = new Base32( 20 );
    for ( final String[] element : BASE32_TEST_CASES_CHUNKED ) {
      assertEquals( element[1], codec.encodeAsString( element[0].getBytes( StringUtil.UTF_8 ) ) );
    }
  }




  @Test
  public void testBase32HexSamples() throws Exception {
    final Base32 codec = new Base32( true );
    for ( final String[] element : BASE32HEX_TEST_CASES ) {
      assertEquals( element[1], codec.encodeAsString( element[0].getBytes( StringUtil.UTF_8 ) ) );
    }
  }




  @Test
  public void testBase32Samples() throws Exception {
    final Base32 codec = new Base32();
    for ( final String[] element : BASE32_TEST_CASES ) {
      assertEquals( element[1], codec.encodeAsString( element[0].getBytes( StringUtil.UTF_8 ) ) );
    }
  }




  @Test
  public void testBase32SamplesNonDefaultPadding() throws Exception {
    final Base32 codec = new Base32( (byte)0x25 ); // '%' <=> 0x25

    for ( final String[] element : BASE32_PAD_TEST_CASES ) {
      assertEquals( element[1], codec.encodeAsString( element[0].getBytes( StringUtil.UTF_8 ) ) );
    }
  }




  @Test
  public void testRandomBytes() {
    for ( int i = 0; i < 20; i++ ) {
      final Base32 codec = new Base32();
      final byte[][] b = Base32TestData.randomData( codec, i );
      assertEquals( "" + i + " " + codec.lineLength, b[1].length, codec.getEncodedLength( b[0] ) );
      //assertEquals(b[0],codec.decode(b[1]));
    }
  }




  @Test
  public void testRandomBytesChunked() {
    for ( int i = 0; i < 20; i++ ) {
      final Base32 codec = new Base32( 10 );
      final byte[][] b = Base32TestData.randomData( codec, i );
      assertEquals( "" + i + " " + codec.lineLength, b[1].length, codec.getEncodedLength( b[0] ) );
      //assertEquals(b[0],codec.decode(b[1]));
    }
  }




  @Test
  public void testRandomBytesHex() {
    for ( int i = 0; i < 20; i++ ) {
      final Base32 codec = new Base32( true );
      final byte[][] b = Base32TestData.randomData( codec, i );
      assertEquals( "" + i + " " + codec.lineLength, b[1].length, codec.getEncodedLength( b[0] ) );
      //assertEquals(b[0],codec.decode(b[1]));
    }
  }
}