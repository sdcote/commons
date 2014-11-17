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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.Test;


/**
 * 
 */
public class StringUtilTest {
  private static final byte[] BYTES_FIXTURE = { 'a', 'b', 'c' };

  // This is valid input for UTF-16BE
  private static final byte[] BYTES_FIXTURE_16BE = { 0, 'a', 0, 'b', 0, 'c' };
  // This is valid for UTF-16LE
  private static final byte[] BYTES_FIXTURE_16LE = { 'a', 0, 'b', 0, 'c', 0 };

  private static final String STRING_FIXTURE = "ABC";




  @Test
  public void testGetBytesIso8859_1() throws UnsupportedEncodingException {
    final String charsetName = "ISO-8859-1";
    testGetBytesUnchecked( charsetName );
    final byte[] expected = STRING_FIXTURE.getBytes( charsetName );
    final byte[] actual = StringUtil.getBytesIso8859_1( STRING_FIXTURE );
    assertTrue( Arrays.equals( expected, actual ) );
  }




  private void testGetBytesUnchecked( final String charsetName ) throws UnsupportedEncodingException {
    final byte[] expected = STRING_FIXTURE.getBytes( charsetName );
    final byte[] actual = StringUtil.getBytesUnchecked( STRING_FIXTURE, charsetName );
    assertTrue( Arrays.equals( expected, actual ) );
  }




  @Test
  public void testGetBytesUncheckedBadName() {
    try {
      StringUtil.getBytesUnchecked( STRING_FIXTURE, "UNKNOWN" );
      fail( "Expected " + IllegalStateException.class.getName() );
    } catch ( final IllegalStateException e ) {
      // Expected
    }
  }




  @Test
  public void testGetBytesUncheckedNullInput() {
    assertNull( StringUtil.getBytesUnchecked( null, "UNKNOWN" ) );
  }




  @Test
  public void testGetBytesUsAscii() throws UnsupportedEncodingException {
    final String charsetName = "US-ASCII";
    testGetBytesUnchecked( charsetName );
    final byte[] expected = STRING_FIXTURE.getBytes( charsetName );
    final byte[] actual = StringUtil.getBytesUsAscii( STRING_FIXTURE );
    assertTrue( Arrays.equals( expected, actual ) );
  }




  @Test
  public void testGetBytesUtf16() throws UnsupportedEncodingException {
    final String charsetName = "UTF-16";
    testGetBytesUnchecked( charsetName );
    final byte[] expected = STRING_FIXTURE.getBytes( charsetName );
    final byte[] actual = StringUtil.getBytesUtf16( STRING_FIXTURE );
    assertTrue( Arrays.equals( expected, actual ) );
  }




  @Test
  public void testGetBytesUtf16Be() throws UnsupportedEncodingException {
    final String charsetName = "UTF-16BE";
    testGetBytesUnchecked( charsetName );
    final byte[] expected = STRING_FIXTURE.getBytes( charsetName );
    final byte[] actual = StringUtil.getBytesUtf16Be( STRING_FIXTURE );
    assertTrue( Arrays.equals( expected, actual ) );
  }




  @Test
  public void testGetBytesUtf16Le() throws UnsupportedEncodingException {
    final String charsetName = "UTF-16LE";
    testGetBytesUnchecked( charsetName );
    final byte[] expected = STRING_FIXTURE.getBytes( charsetName );
    final byte[] actual = StringUtil.getBytesUtf16Le( STRING_FIXTURE );
    assertTrue( Arrays.equals( expected, actual ) );
  }




  @Test
  public void testGetBytesUtf8() throws UnsupportedEncodingException {
    final String charsetName = "UTF-8";
    testGetBytesUnchecked( charsetName );
    final byte[] expected = STRING_FIXTURE.getBytes( charsetName );
    final byte[] actual = StringUtil.getBytesUtf8( STRING_FIXTURE );
    assertTrue( Arrays.equals( expected, actual ) );
  }




  private void testNewString( final String charsetName ) throws UnsupportedEncodingException {
    final String expected = new String( BYTES_FIXTURE, charsetName );
    final String actual = StringUtil.newString( BYTES_FIXTURE, charsetName );
    assertEquals( expected, actual );
  }




  @Test
  public void testNewStringBadEnc() {
    try {
      StringUtil.newString( BYTES_FIXTURE, "UNKNOWN" );
      fail( "Expected " + IllegalStateException.class.getName() );
    } catch ( final IllegalStateException e ) {
      // Expected
    }
  }




  @Test
  public void testNewStringIso8859_1() throws UnsupportedEncodingException {
    final String charsetName = "ISO-8859-1";
    testNewString( charsetName );
    final String expected = new String( BYTES_FIXTURE, charsetName );
    final String actual = StringUtil.newStringIso8859_1( BYTES_FIXTURE );
    assertEquals( expected, actual );
  }




  @Test
  public void testNewStringNullInput() {
    assertNull( StringUtil.newString( null, "UNKNOWN" ) );
  }




  @Test
  public void testNewStringUsAscii() throws UnsupportedEncodingException {
    final String charsetName = "US-ASCII";
    testNewString( charsetName );
    final String expected = new String( BYTES_FIXTURE, charsetName );
    final String actual = StringUtil.newStringUsAscii( BYTES_FIXTURE );
    assertEquals( expected, actual );
  }




  @Test
  public void testNewStringUtf16() throws UnsupportedEncodingException {
    final String charsetName = "UTF-16";
    testNewString( charsetName );
    final String expected = new String( BYTES_FIXTURE, charsetName );
    final String actual = StringUtil.newStringUtf16( BYTES_FIXTURE );
    assertEquals( expected, actual );
  }




  @Test
  public void testNewStringUtf16Be() throws UnsupportedEncodingException {
    final String charsetName = "UTF-16BE";
    testNewString( charsetName );
    final String expected = new String( BYTES_FIXTURE_16BE, charsetName );
    final String actual = StringUtil.newStringUtf16Be( BYTES_FIXTURE_16BE );
    assertEquals( expected, actual );
  }




  @Test
  public void testNewStringUtf16Le() throws UnsupportedEncodingException {
    final String charsetName = "UTF-16LE";
    testNewString( charsetName );
    final String expected = new String( BYTES_FIXTURE_16LE, charsetName );
    final String actual = StringUtil.newStringUtf16Le( BYTES_FIXTURE_16LE );
    assertEquals( expected, actual );
  }




  @Test
  public void testNewStringUtf8() throws UnsupportedEncodingException {
    final String charsetName = "UTF-8";
    testNewString( charsetName );
    final String expected = new String( BYTES_FIXTURE, charsetName );
    final String actual = StringUtil.newStringUtf8( BYTES_FIXTURE );
    assertEquals( expected, actual );
  }




  @Test
  public void testStripLeadingAndTrailingQuotes() {
    assertEquals( "bar", StringUtil.stripLeadingAndTrailingQuotes( "\"bar\"" ) );
  }




  @Test
  public void testStripLeadingHyphens() {
    assertEquals( "f", StringUtil.stripLeadingHyphens( "-f" ) );
    assertEquals( "foo", StringUtil.stripLeadingHyphens( "--foo" ) );
    assertEquals( "-foo", StringUtil.stripLeadingHyphens( "---foo" ) );
    assertNull( StringUtil.stripLeadingHyphens( null ) );
  }
}
