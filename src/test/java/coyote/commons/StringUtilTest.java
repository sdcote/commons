/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons;

//import static org.junit.Assert.*;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StringUtil class.
 * These tests also serve as documentation for how to use the various methods in StringUtil.
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
  @DisplayName("Test token substitution")
  public void testTokenSubst() {
    String[] tokens = {"[name]", "Junie", "[action]", "coding"};
    String template = "Hello [name], I am [action]!";
    assertEquals("Hello Junie, I am coding!", StringUtil.tokenSubst(tokens, template, true));
    
    // Testing order/reverse substitution
    String[] tokens2 = {"&", "&amp;", "<", "&lt;"};
    String text = "& <";
    // If fromStart is true, '&' is replaced first, then '<'
    assertEquals("&amp; &lt;", StringUtil.tokenSubst(tokens2, text, true));
  }

  @Test
  @DisplayName("Test character substitution")
  public void testCharSubst() {
    assertEquals("b-n-n-", StringUtil.charSubst('a', '-', "banana"));
    assertEquals("", StringUtil.charSubst('a', '-', ""));
    assertEquals("", StringUtil.charSubst('a', '-', null));
  }

  @Test
  @DisplayName("Test XML/HTML conversion")
  public void testXmlHtmlConversion() {
    String original = "John & Mary < 'smith' > \"cool\"";
    String xml = StringUtil.StringToXML(original);
    assertNotNull(xml);
    assertTrue(xml.contains("&amp;"));
    assertTrue(xml.contains("&lt;"));
    
    String back = StringUtil.XMLToString(xml);
    assertEquals(original, back);
    
    String html = StringUtil.StringToHTML(original);
    assertNotNull(html);
    assertTrue(html.contains("&amp;"));
    
    // Test HTMLToString (Note: current implementation removes &nbsp;)
    assertEquals("test", StringUtil.HTMLToString("test&nbsp;"));
  }

  @Test
  @DisplayName("Test text presence checks")
  public void testHasText() {
    assertTrue(StringUtil.hasText("abc"));
    assertTrue(StringUtil.hasText("  abc  "));
    assertFalse(StringUtil.hasText("   "));
    assertFalse(StringUtil.hasText(""));
    assertFalse(StringUtil.hasText((String)null));
    
    assertTrue(StringUtil.hasLength(" "));
    assertFalse(StringUtil.hasLength(""));
    assertFalse(StringUtil.hasLength((String)null));
  }

  @Test
  @DisplayName("Test blank checks")
  public void testIsBlank() {
    assertTrue(StringUtil.isBlank(null));
    assertTrue(StringUtil.isBlank(""));
    assertTrue(StringUtil.isBlank("   "));
    assertFalse(StringUtil.isBlank("abc"));
    
    assertFalse(StringUtil.isNotBlank("   "));
    assertTrue(StringUtil.isNotBlank("abc"));
  }

  @Test
  @DisplayName("Test number conversion and checks")
  public void testNumbers() throws Exception {
    assertTrue(StringUtil.isNumber("123"));
    assertFalse(StringUtil.isNumber("-123.45")); // StringUtil.isNumber only allows digits
    assertFalse(StringUtil.isNumber("abc"));
    
    assertTrue(StringUtil.isDigits("12345"));
    assertFalse(StringUtil.isDigits("123.45"));
    
    assertEquals("A", StringUtil.numberToLetter(1, true));
    assertEquals("z", StringUtil.numberToLetter(26, false));
    // StringUtil.numberToLetter currently only supports range 1-26
  }

  @Test
  @DisplayName("Test string replacement")
  public void testReplace() {
    assertEquals("hello world", StringUtil.replace("hello java", "java", "world"));
    assertEquals("aaaa", StringUtil.replace("abaaba", "b", ""));
  }

  @Test
  @DisplayName("Test soundex")
  public void testSoundex() {
    assertEquals("R16300", StringUtil.soundex("Robert"));
    assertEquals("R16300", StringUtil.soundex("Rupert"));
  }

  @Test
  @DisplayName("Test array utilities")
  public void testArrayUtils() {
    String[] array = {"zero", "one", "two"};
    assertEquals("one", StringUtil.safeGetStringFromArray(1, array, 0));
    assertEquals("zero", StringUtil.safeGetStringFromArray(5, array, 0));
    
    assertEquals(2, StringUtil.findStringInArray("two", array, -1));
    assertEquals(-1, StringUtil.findStringInArray("three", array, -1));
    
    assertEquals("zero, one and two", StringUtil.arrayToCommaList(array));
  }

  @Test
  @DisplayName("Test notation conversions")
  public void testNotation() {
    assertEquals("myjava_class", StringUtil.asJavaName("my-java_class"));
    assertEquals("myJavaClass", StringUtil.asCamelNotation("my java class"));
  }

  @Test
  @DisplayName("Test filename and path utilities")
  public void testPathUtils() {
    assertEquals("test", StringUtil.extension("file.test"));
    assertNull(StringUtil.extension("file"));
    
    assertEquals("coyote.commons", StringUtil.getJavaPackage("coyote.commons.StringUtil"));
    assertEquals("StringUtil", StringUtil.getLocalJavaName("coyote.commons.StringUtil"));
    
    assertEquals("file.txt", StringUtil.getLocalFileName("/path/to/file.txt"));
  }

  @Test
  @DisplayName("Test padding and centering")
  public void testPaddingMore() {
    assertEquals("  abc  ", StringUtil.padCenter("abc", 7));
    assertEquals("+++abc+++", StringUtil.padCenter("abc", '+', 9));
  }

  @Test
  @DisplayName("Test split and join")
  public void testSplit() {
    List<String> result = StringUtil.split("one,two,three", ",");
    assertEquals(3, result.size());
    assertEquals("two", result.get(1));
    
    String[] tokens = StringUtil.tokenizeToStringArray("one;two,three", ";,", true, true);
    assertEquals(3, tokens.length);
  }

  @Test
  @DisplayName("Test digest and random ID")
  public void testDigestAndRandom() {
    String id = StringUtil.generateRandomId(10);
    assertEquals(10, id.length());
    
    String digest = StringUtil.digestString("hello");
    assertNotNull(digest);
    assertEquals(StringUtil.digestString("hello"), digest);
  }

  @Test
  @DisplayName("Test miscellaneous methods")
  public void testMisc() {
    assertEquals("Hello", StringUtil.getCapitalized("hello"));
    assertEquals("123", StringUtil.stripChars("1a2b3c", "abc"));
    assertEquals("abc", StringUtil.unquote("\"abc\""));
    assertEquals("abc", StringUtil.unquote("'abc'"));
  }

  @Test
  @DisplayName("Test newString and getBytes with StandardCharsets")
  public void testStandardCharsets() {
    byte[] utf8Bytes = StringUtil.getBytesUtf8("hello");
    assertEquals("hello", StringUtil.newStringUtf8(utf8Bytes));
    
    byte[] isoBytes = StringUtil.getBytes("hello");
    assertEquals("hello", StringUtil.getString(isoBytes));
  }

  @Test
  @DisplayName("Test search methods")
  public void testSearch() {
    assertTrue(StringUtil.containsAny("hello", new char[]{'a', 'e', 'i'}));
    assertFalse(StringUtil.containsAny("hello", new char[]{'x', 'y', 'z'}));
    
    assertEquals(2, StringUtil.countMatches("hello hello", "hello"));
    assertTrue(StringUtil.equalsAny("one", new String[]{"one", "two"}));
  }

  @Test
  @DisplayName("Test UUID conversion")
  public void testUuid() {
    UUID uuid = UUID.randomUUID();
    String digest = StringUtil.digestUuid(uuid, 8);
    assertEquals(8, digest.length());
  }

  @Test
  public void testTrimChar() {
    String test = StringUtil.trim( "...this is a test...", '.' );
    assertEquals( "this is a test", test );
  }
}
