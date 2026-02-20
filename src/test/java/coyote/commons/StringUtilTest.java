/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons;

//import static org.junit.Assert.*;


import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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




  @Test
  public void fixedLengthTest() {
    String text = "Coyote";
    String field = null;
    int LEFT = 0;
    int CENTER = 1;
    int RIGHT = 2;

    // Alignment Tests = = = = = =
    field = StringUtil.fixedLength( text, 10, LEFT, '*' );
    //System.out.println( field );
    assertTrue( field.length() == 10 );
    assertEquals( field, "Coyote****" );

    field = StringUtil.fixedLength( text, 10, CENTER, '*' );
    assertTrue( field.length() == 10 );
    assertEquals( field, "**Coyote**" );

    field = StringUtil.fixedLength( text, 10, RIGHT, '*' );
    assertTrue( field.length() == 10 );
    assertEquals( field, "****Coyote" );

    // Size Match Tests = = = = = 
    field = StringUtil.fixedLength( text, 6, LEFT, '*' );
    assertTrue( field.length() == 6 );
    assertEquals( field, "Coyote" );

    field = StringUtil.fixedLength( text, 6, CENTER, '*' );
    assertTrue( field.length() == 6 );
    assertEquals( field, "Coyote" );

    field = StringUtil.fixedLength( text, 6, RIGHT, '*' );
    assertTrue( field.length() == 6 );
    assertEquals( field, "Coyote" );

    // Truncation Tests = = = = =
    field = StringUtil.fixedLength( text, 5, LEFT, '*' );
    assertTrue( field.length() == 5 );
    assertEquals( field, "Coyot" );

    field = StringUtil.fixedLength( text, 5, CENTER, '*' );
    assertTrue( field.length() == 5 );
    assertEquals( field, "Coyot" );

    field = StringUtil.fixedLength( text, 5, RIGHT, '*' );
    assertTrue( field.length() == 5 );
    assertEquals( field, "oyote" );

    field = StringUtil.fixedLength( text, 4, LEFT, '*' );
    assertTrue( field.length() == 4 );
    assertEquals( field, "Coyo" );

    field = StringUtil.fixedLength( text, 4, CENTER, '*' );
    assertTrue( field.length() == 4 );
    assertEquals( field, "oyot" );

    field = StringUtil.fixedLength( text, 4, RIGHT, '*' );
    assertTrue( field.length() == 4 );
    assertEquals( field, "yote" );
  }

  @Test
  public void fixedLengthSpaceTest() {
    String text = "Coyote";
    String field = null;

    field = StringUtil.fixedLength(text, 20, StringUtil.LEFT_ALIGNMENT, ' ');
    assertEquals(20, field.length());
    assertEquals(field, "Coyote              ");
  }




  @Test
  public void empty() {
    assertTrue( StringUtil.isEmpty( "" ) );
    assertTrue( StringUtil.isEmpty( null ) );
    assertFalse( StringUtil.isEmpty( " " ) );
  }




  @Test
  public void notEmpty() {
    assertFalse( StringUtil.isNotEmpty( "" ) );
    assertFalse( StringUtil.isNotEmpty( null ) );
    assertTrue( StringUtil.isNotEmpty( " " ) );
  }




  @Test
  public void testIsNullOrEmpty() {
    assertTrue( StringUtil.isNullOrEmpty( null ) );
    assertTrue( StringUtil.isNullOrEmpty( "" ) );
    assertTrue( StringUtil.isNullOrEmpty( "   ", true ) );
    assertFalse( StringUtil.isNullOrEmpty( "   ", false ) );
  }




  @Test
  public void testIsNotNullOrEmpty() {
    assertFalse( StringUtil.isNotNullOrEmpty( null ) );
    assertFalse( StringUtil.isNotNullOrEmpty( "" ) );
    assertFalse( StringUtil.isNotNullOrEmpty( "   ", true ) );
    assertTrue( StringUtil.isNotNullOrEmpty( "   ", false ) );
  }




  @Test
  public void testSourceContainsTarget() {
    assertTrue( StringUtil.contains( "source-data-source", "data" ) );
    assertFalse( StringUtil.contains( "source-data-source", "****" ) );
  }




  @Test
  public void testSourcesContainsTarget() {
    String sources[] = { "one", "two", "three", "four", "five" };
    assertTrue( StringUtil.contains( sources, "ou" ) );
    assertFalse( StringUtil.contains( sources, "+" ) );
  }




  @Test
  public void testSourceContainsTargets() {
    String targets[] = { "one", "two", "three", "four", "five" };
    assertTrue( StringUtil.contains( "This is one fine day!", targets ) );
    assertFalse( StringUtil.contains( "This is not a fine day!", targets ) );
  }




  @Test
  public void testSourcesContainsTargets() {
    String sources[] = { "one", "two", "three", "four", "five" };
    String targets1[] = { "apple", "orange", "bananna", "peach", "pear" };
    String targets2[] = { "qq", "zz", "tw", "uu", "yy" };
    assertTrue( StringUtil.contains( sources, targets2 ) );
    assertFalse( StringUtil.contains( sources, targets1 ) );
  }




  @Test
  public void testCreateSpace() {
    String test = StringUtil.create( 5 );
    assertEquals( "     ", test );
  }




  @Test
  public void testCreateChar() {
    String test = StringUtil.create( '-', 5 );
    assertEquals( "-----", test );
  }




  @Test
  public void testCreateString() {
    String test = StringUtil.create( "+-", 5 );
    assertEquals( "+-+-+-+-+-", test );
  }




  @Test
  public void testPadLeftSpace() {
    String test = StringUtil.padLeft( "test-data", 5 );
    assertEquals( "     test-data", test );
  }




  @Test
  public void testPadLeftChar() {
    String test = StringUtil.padLeft( "test-data", '+', 5 );
    assertEquals( "+++++test-data", test );
  }




  @Test
  public void testPadLeftString() {
    String test = StringUtil.padLeft( "test-data", "<>", 5 );
    assertEquals( "<><><><><>test-data", test );
  }




  @Test
  public void testPadRightSpace() {
    String test = StringUtil.padRight( "test-data", 5 );
    assertEquals( "test-data     ", test );
  }




  @Test
  public void testPadRightChar() {
    String test = StringUtil.padRight( "test-data", '+', 5 );
    assertEquals( "test-data+++++", test );
  }




  @Test
  public void testPadRightString() {
    String test = StringUtil.padRight( "test-data", "<>", 5 );
    assertEquals( "test-data<><><><><>", test );
  }




  @Test
  public void testPadBothSpace() {
    String test = StringUtil.pad( "test-data", 5 );
    assertEquals( "     test-data     ", test );
  }




  @Test
  public void testPadBothChar() {
    String test = StringUtil.pad( "test-data", '+', 5 );
    assertEquals( "+++++test-data+++++", test );
  }




  @Test
  public void testPadBothString() {
    String test = StringUtil.pad( "test-data", "<>", 5 );
    assertEquals( "<><><><><>test-data<><><><><>", test );
  }




  @Test
  public void testPadCenterSpace() {
    String test1 = StringUtil.padCenter( "test", 10 );
    assertEquals( "   test   ", test1 );

    String test2 = StringUtil.padCenter( "tst", 10 );
    assertEquals( "   tst    ", test2 );
  }




  @Test
  public void testPadCenterChar() {
    String test1 = StringUtil.padCenter( "test", '+', 10 );
    assertEquals( "+++test+++", test1 );

    String test2 = StringUtil.padCenter( "tst", '+', 10 );
    assertEquals( "+++tst++++", test2 );
  }




  @Test
  public void testTrimLeftSpace() {
    String test = StringUtil.trimLeft( "  this is a test  " );
    assertEquals( "this is a test  ", test );
  }




  @Test
  public void testTrimLeftChar() {
    String test = StringUtil.trimLeft( "...this is a test...", '.' );
    assertEquals( "this is a test...", test );
  }




  @Test
  public void testTrimRightSpace() {
    String test = StringUtil.trimRight( "  this is a test  " );
    assertEquals( "  this is a test", test );
  }




  @Test
  public void testTrimRightChar() {
    String test = StringUtil.trimRight( "...this is a test...", '.' );
    assertEquals( "...this is a test", test );
  }




  @Test
  public void testTrimSpace() {
    String test = StringUtil.trim( "  this is a test  " );
    assertEquals( "this is a test", test );
  }




  @Test
  public void testTrimChar() {
    String test = StringUtil.trim( "...this is a test...", '.' );
    assertEquals( "this is a test", test );
  }
}
