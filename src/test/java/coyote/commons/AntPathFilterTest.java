/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons;

//import static org.junit.Assert.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * 
 */
public class AntPathFilterTest {

  private AntPathFilter filter;




  @Test
  public void combine() {
    assertEquals( "", filter.combine( null, null ) );
    assertEquals( "/orders", filter.combine( "/orders", null ) );
    assertEquals( "/orders", filter.combine( null, "/orders" ) );
    assertEquals( "/orders/booking", filter.combine( "/orders/*", "booking" ) );
    assertEquals( "/orders/booking", filter.combine( "/orders/*", "/booking" ) );
    assertEquals( "/orders/**/booking", filter.combine( "/orders/**", "booking" ) );
    assertEquals( "/orders/**/booking", filter.combine( "/orders/**", "/booking" ) );
    assertEquals( "/orders/booking", filter.combine( "/orders", "/booking" ) );
    assertEquals( "/orders/booking", filter.combine( "/orders", "booking" ) );
    assertEquals( "/orders/booking", filter.combine( "/orders/", "booking" ) );
    assertEquals( "/orders/{order}", filter.combine( "/orders/*", "{order}" ) );
    assertEquals( "/orders/**/{order}", filter.combine( "/orders/**", "{order}" ) );
    assertEquals( "/orders/{order}", filter.combine( "/orders", "{order}" ) );
    assertEquals( "/orders/{order}.*", filter.combine( "/orders", "{order}.*" ) );
    assertEquals( "/orders/*/booking/{booking}", filter.combine( "/orders/*/booking", "{booking}" ) );
    assertEquals( "/order.html", filter.combine( "/*.html", "/order.html" ) );
    assertEquals( "/order.html", filter.combine( "/*.html", "/order" ) );
    assertEquals( "/order.html", filter.combine( "/*.html", "/order.*" ) );
    assertEquals( "/*.html", filter.combine( "/**", "/*.html" ) );
    assertEquals( "/*.html", filter.combine( "/*", "/*.html" ) );
    assertEquals( "/*.html", filter.combine( "/*.*", "/*.html" ) );
    assertEquals( "/{foo}/bar", filter.combine( "/{foo}", "/bar" ) );
    assertEquals( "/user/user", filter.combine( "/user", "/user" ) );
    assertEquals( "/{foo:.*[^0-9].*}/edit/", filter.combine( "/{foo:.*[^0-9].*}", "/edit/" ) );
    assertEquals( "/1.0/foo/test", filter.combine( "/1.0", "/foo/test" ) );
  }




  @BeforeEach
  public void createMatcher() {
    filter = new AntPathFilter();
  }




  @Test
  public void extractPathWithinPattern() throws Exception {
    assertEquals( "", filter.extractPathWithinPattern( "/docs/commit.html", "/docs/commit.html" ) );

    assertEquals( "cvs/commit", filter.extractPathWithinPattern( "/docs/*", "/docs/cvs/commit" ) );
    assertEquals( "commit.html", filter.extractPathWithinPattern( "/docs/cvs/*.html", "/docs/cvs/commit.html" ) );
    assertEquals( "cvs/commit", filter.extractPathWithinPattern( "/docs/**", "/docs/cvs/commit" ) );
    assertEquals( "cvs/commit.html", filter.extractPathWithinPattern( "/docs/**/*.html", "/docs/cvs/commit.html" ) );
    assertEquals( "commit.html", filter.extractPathWithinPattern( "/docs/**/*.html", "/docs/commit.html" ) );
    assertEquals( "commit.html", filter.extractPathWithinPattern( "/*.html", "/commit.html" ) );
    assertEquals( "docs/commit.html", filter.extractPathWithinPattern( "/*.html", "/docs/commit.html" ) );
    assertEquals( "/commit.html", filter.extractPathWithinPattern( "*.html", "/commit.html" ) );
    assertEquals( "/docs/commit.html", filter.extractPathWithinPattern( "*.html", "/docs/commit.html" ) );
    assertEquals( "/docs/commit.html", filter.extractPathWithinPattern( "**/*.*", "/docs/commit.html" ) );
    assertEquals( "/docs/commit.html", filter.extractPathWithinPattern( "*", "/docs/commit.html" ) );
    // assertEquals( "/docs/cvs/other/commit.html", filter.extractPathWithinPattern( "**/commit.html", "/docs/cvs/other/commit.html" ) );
    //    assertEquals( "cvs/other/commit.html", filter.extractPathWithinPattern( "/docs/**/**/**/**", "/docs/cvs/other/commit.html" ) );

    assertEquals( "docs/cvs/commit", filter.extractPathWithinPattern( "/d?cs/*", "/docs/cvs/commit" ) );
    assertEquals( "cvs/commit.html", filter.extractPathWithinPattern( "/docs/c?s/*.html", "/docs/cvs/commit.html" ) );
    assertEquals( "docs/cvs/commit", filter.extractPathWithinPattern( "/d?cs/**", "/docs/cvs/commit" ) );
    assertEquals( "docs/cvs/commit.html", filter.extractPathWithinPattern( "/d?cs/**/*.html", "/docs/cvs/commit.html" ) );
  }




  @Test
  public void extractUriTemplateVariables() throws Exception {
    Map<String, String> result = filter.extractUriTemplateVariables( "/orders/{order}", "/orders/1" );
    assertEquals( Collections.singletonMap( "order", "1" ), result );

    result = filter.extractUriTemplateVariables( "/o?ders/{order}", "/orders/1" );
    assertEquals( Collections.singletonMap( "order", "1" ), result );

    result = filter.extractUriTemplateVariables( "/orders/{order}/bookings/{booking}", "/orders/1/bookings/2" );
    Map<String, String> expected = new LinkedHashMap<String, String>();
    expected.put( "order", "1" );
    expected.put( "booking", "2" );
    assertEquals( expected, result );

    result = filter.extractUriTemplateVariables( "/**/orders/**/{order}", "/foo/orders/bar/1" );
    assertEquals( Collections.singletonMap( "order", "1" ), result );

    result = filter.extractUriTemplateVariables( "/{page}.html", "/42.html" );
    assertEquals( Collections.singletonMap( "page", "42" ), result );

    result = filter.extractUriTemplateVariables( "/{page}.*", "/42.html" );
    assertEquals( Collections.singletonMap( "page", "42" ), result );

    result = filter.extractUriTemplateVariables( "/A-{B}-C", "/A-b-C" );
    assertEquals( Collections.singletonMap( "B", "b" ), result );

    result = filter.extractUriTemplateVariables( "/{name}.{extension}", "/test.html" );
    expected = new LinkedHashMap<String, String>();
    expected.put( "name", "test" );
    expected.put( "extension", "html" );
    assertEquals( expected, result );
  }




  @Test
  public void extractUriTemplateVariablesRegex() {
    Map<String, String> result = filter.extractUriTemplateVariables( "{symbolicName:[\\w\\.]+}-{version:[\\w\\.]+}.jar", "com.example-1.0.0.jar" );
    assertEquals( "com.example", result.get( "symbolicName" ) );
    assertEquals( "1.0.0", result.get( "version" ) );

    result = filter.extractUriTemplateVariables( "{symbolicName:[\\w\\.]+}-sources-{version:[\\w\\.]+}.jar", "com.example-sources-1.0.0.jar" );
    assertEquals( "com.example", result.get( "symbolicName" ) );
    assertEquals( "1.0.0", result.get( "version" ) );
  }




  @Test
  public void extractUriTemplateVarsRegexCapturingGroups() {
    try {
      filter.extractUriTemplateVariables( "/web/{id:foo(bar)?}", "/web/foobar" );
      fail( "Expected exception" );
    } catch ( final IllegalArgumentException ex ) {
      assertTrue( ex.getMessage().contains( "The number of capturing groups in the pattern" ),"Expected helpful message on the use of capturing groups" );
    }
  }




  @Test
  public void extractUriTemplateVarsRegexQualifiers() {
    Map<String, String> result = filter.extractUriTemplateVariables( "{symbolicName:[\\p{L}\\.]+}-sources-{version:[\\p{N}\\.]+}.jar", "com.example-sources-1.0.0.jar" );
    assertEquals( "com.example", result.get( "symbolicName" ) );
    assertEquals( "1.0.0", result.get( "version" ) );

    result = filter.extractUriTemplateVariables( "{symbolicName:[\\w\\.]+}-sources-{version:[\\d\\.]+}-{year:\\d{4}}{month:\\d{2}}{day:\\d{2}}.jar", "com.example-sources-1.0.0-20100220.jar" );
    assertEquals( "com.example", result.get( "symbolicName" ) );
    assertEquals( "1.0.0", result.get( "version" ) );
    assertEquals( "2010", result.get( "year" ) );
    assertEquals( "02", result.get( "month" ) );
    assertEquals( "20", result.get( "day" ) );

    result = filter.extractUriTemplateVariables( "{symbolicName:[\\p{L}\\.]+}-sources-{version:[\\p{N}\\.\\{\\}]+}.jar", "com.example-sources-1.0.0.{12}.jar" );
    assertEquals( "com.example", result.get( "symbolicName" ) );
    assertEquals( "1.0.0.{12}", result.get( "version" ) );
  }




  @Test
  public void match() {
    // test exact matching
    assertTrue( filter.match( "test", "test" ) );
    assertTrue( filter.match( "/test", "/test" ) );
    assertFalse( filter.match( "/test.jpg", "test.jpg" ) );
    assertFalse( filter.match( "test", "/test" ) );
    assertFalse( filter.match( "/test", "test" ) );

    // test matching with ?'s
    assertTrue( filter.match( "t?st", "test" ) );
    assertTrue( filter.match( "??st", "test" ) );
    assertTrue( filter.match( "tes?", "test" ) );
    assertTrue( filter.match( "te??", "test" ) );
    assertTrue( filter.match( "?es?", "test" ) );
    assertFalse( filter.match( "tes?", "tes" ) );
    assertFalse( filter.match( "tes?", "testt" ) );
    assertFalse( filter.match( "tes?", "tsst" ) );

    // test matchin with *'s
    assertTrue( filter.match( "*", "test" ) );
    assertTrue( filter.match( "test*", "test" ) );
    assertTrue( filter.match( "test*", "testTest" ) );
    assertTrue( filter.match( "test/*", "test/Test" ) );
    assertTrue( filter.match( "test/*", "test/t" ) );
    assertTrue( filter.match( "test/*", "test/" ) );
    assertTrue( filter.match( "*test*", "AnothertestTest" ) );
    assertTrue( filter.match( "*test", "Anothertest" ) );
    assertTrue( filter.match( "*.*", "test." ) );
    assertTrue( filter.match( "*.*", "test.test" ) );
    assertTrue( filter.match( "*.*", "test.test.test" ) );
    assertTrue( filter.match( "test*aaa", "testblaaaa" ) );
    assertFalse( filter.match( "test*", "tst" ) );
    assertFalse( filter.match( "test*", "tsttest" ) );
    assertFalse( filter.match( "test*", "test/" ) );
    assertFalse( filter.match( "test*", "test/t" ) );
    assertFalse( filter.match( "test/*", "test" ) );
    assertFalse( filter.match( "*test*", "tsttst" ) );
    assertFalse( filter.match( "*test", "tsttst" ) );
    assertFalse( filter.match( "*.*", "tsttst" ) );
    assertFalse( filter.match( "test*aaa", "test" ) );
    assertFalse( filter.match( "test*aaa", "testblaaab" ) );

    // test matching with ?'s and /'s
    assertTrue( filter.match( "/?", "/a" ) );
    assertTrue( filter.match( "/?/a", "/a/a" ) );
    assertTrue( filter.match( "/a/?", "/a/b" ) );
    assertTrue( filter.match( "/??/a", "/aa/a" ) );
    assertTrue( filter.match( "/a/??", "/a/bb" ) );
    assertTrue( filter.match( "/?", "/a" ) );

    // test matching with **'s
    assertTrue( filter.match( "/**", "/testing/testing" ) );
    assertTrue( filter.match( "/*/**", "/testing/testing" ) );
    assertTrue( filter.match( "/**/*", "/testing/testing" ) );
    assertTrue( filter.match( "/bla/**/bla", "/bla/testing/testing/bla" ) );
    assertTrue( filter.match( "/bla/**/bla", "/bla/testing/testing/bla/bla" ) );
    assertTrue( filter.match( "/**/test", "/bla/bla/test" ) );
    assertTrue( filter.match( "/bla/**/**/bla", "/bla/bla/bla/bla/bla/bla" ) );
    assertTrue( filter.match( "/bla*bla/test", "/blaXXXbla/test" ) );
    assertTrue( filter.match( "/*bla/test", "/XXXbla/test" ) );
    assertFalse( filter.match( "/bla*bla/test", "/blaXXXbl/test" ) );
    assertFalse( filter.match( "/*bla/test", "XXXblab/test" ) );
    assertFalse( filter.match( "/*bla/test", "XXXbl/test" ) );

    assertFalse( filter.match( "/????", "/bala/bla" ) );
    assertFalse( filter.match( "/**/*bla", "/bla/bla/bla/bbb" ) );

    assertTrue( filter.match( "/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing/" ) );
    assertTrue( filter.match( "/*bla*/**/bla/*", "/XXXblaXXXX/testing/testing/bla/testing" ) );
    assertTrue( filter.match( "/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing" ) );
    assertTrue( filter.match( "/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing.jpg" ) );

    assertTrue( filter.match( "*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing/" ) );
    assertTrue( filter.match( "*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing" ) );
    assertTrue( filter.match( "*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing" ) );
    assertFalse( filter.match( "*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing/testing" ) );

    assertFalse( filter.match( "/x/x/**/bla", "/x/x/x/" ) );

    assertTrue( filter.match( "", "" ) );

    assertTrue( filter.match( "/{bla}.*", "/testing.html" ) );
  }




  @Test
  public void patternComparator() {
    final Comparator<String> comparator = filter.getPatternComparator( "/orders/new" );

    assertEquals( 0, comparator.compare( null, null ) );
    assertEquals( 1, comparator.compare( null, "/orders/new" ) );
    assertEquals( -1, comparator.compare( "/orders/new", null ) );

    assertEquals( 0, comparator.compare( "/orders/new", "/orders/new" ) );

    assertEquals( -1, comparator.compare( "/orders/new", "/orders/*" ) );
    assertEquals( 1, comparator.compare( "/orders/*", "/orders/new" ) );
    assertEquals( 0, comparator.compare( "/orders/*", "/orders/*" ) );

    assertEquals( -1, comparator.compare( "/orders/new", "/orders/{order}" ) );
    assertEquals( 1, comparator.compare( "/orders/{order}", "/orders/new" ) );
    assertEquals( 0, comparator.compare( "/orders/{order}", "/orders/{order}" ) );
    assertEquals( -1, comparator.compare( "/orders/{order}/booking", "/orders/{order}/bookings/{booking}" ) );
    assertEquals( 1, comparator.compare( "/orders/{order}/bookings/{booking}", "/orders/{order}/booking" ) );

    assertEquals( -1, comparator.compare( "/orders/{order}/bookings/{booking}/cutomers/{customer}", "/**" ) );
    assertEquals( 1, comparator.compare( "/**", "/orders/{order}/bookings/{booking}/cutomers/{customer}" ) );
    assertEquals( 0, comparator.compare( "/**", "/**" ) );

    assertEquals( -1, comparator.compare( "/orders/{order}", "/orders/*" ) );
    assertEquals( 1, comparator.compare( "/orders/*", "/orders/{order}" ) );

    //assertEquals( -1, comparator.compare( "/orders/*", "/orders/*/**" ) );
    //assertEquals( 1, comparator.compare( "/orders/*/**", "/orders/*" ) );

    assertEquals( -1, comparator.compare( "/orders/new", "/orders/new.*" ) );
    assertEquals( 2, comparator.compare( "/orders/{order}", "/orders/{order}.*" ) );

    //assertEquals( -1, comparator.compare( "/orders/{order}/bookings/{booking}/cutomers/{customer}", "/orders/**" ) );
    //assertEquals( 1, comparator.compare( "/orders/**", "/orders/{order}/bookings/{booking}/cutomers/{customer}" ) );
    assertEquals( 1, comparator.compare( "/orders/foo/bar/**", "/orders/{order}" ) );
    assertEquals( -1, comparator.compare( "/orders/{order}", "/orders/foo/bar/**" ) );
    assertEquals( 2, comparator.compare( "/orders/**/bookings/**", "/orders/**" ) );
    assertEquals( -2, comparator.compare( "/orders/**", "/orders/**/bookings/**" ) );

    assertEquals( 1, comparator.compare( "/**", "/orders/{order}" ) );

    assertEquals( 1, comparator.compare( "/orders", "/orders2" ) );
  }




  @Test
  public void patternComparatorSort() {
    Comparator<String> comparator = filter.getPatternComparator( "/orders/new" );
    final List<String> paths = new ArrayList<String>( 3 );

    paths.add( null );
    paths.add( "/orders/new" );
    Collections.sort( paths, comparator );
    assertEquals( "/orders/new", paths.get( 0 ) );
    assertNull( paths.get( 1 ) );
    paths.clear();

    paths.add( "/orders/new" );
    paths.add( null );
    Collections.sort( paths, comparator );
    assertEquals( "/orders/new", paths.get( 0 ) );
    assertNull( paths.get( 1 ) );
    paths.clear();

    paths.add( "/orders/*" );
    paths.add( "/orders/new" );
    Collections.sort( paths, comparator );
    assertEquals( "/orders/new", paths.get( 0 ) );
    assertEquals( "/orders/*", paths.get( 1 ) );
    paths.clear();

    paths.add( "/orders/new" );
    paths.add( "/orders/*" );
    Collections.sort( paths, comparator );
    assertEquals( "/orders/new", paths.get( 0 ) );
    assertEquals( "/orders/*", paths.get( 1 ) );
    paths.clear();

    paths.add( "/orders/**" );
    paths.add( "/orders/*" );
    Collections.sort( paths, comparator );
    assertEquals( "/orders/*", paths.get( 0 ) );
    assertEquals( "/orders/**", paths.get( 1 ) );
    paths.clear();

    paths.add( "/orders/*" );
    paths.add( "/orders/**" );
    Collections.sort( paths, comparator );
    assertEquals( "/orders/*", paths.get( 0 ) );
    assertEquals( "/orders/**", paths.get( 1 ) );
    paths.clear();

    paths.add( "/orders/{order}" );
    paths.add( "/orders/new" );
    Collections.sort( paths, comparator );
    assertEquals( "/orders/new", paths.get( 0 ) );
    assertEquals( "/orders/{order}", paths.get( 1 ) );
    paths.clear();

    paths.add( "/orders/new" );
    paths.add( "/orders/{order}" );
    Collections.sort( paths, comparator );
    assertEquals( "/orders/new", paths.get( 0 ) );
    assertEquals( "/orders/{order}", paths.get( 1 ) );
    paths.clear();

    paths.add( "/orders/*" );
    paths.add( "/orders/{order}" );
    paths.add( "/orders/new" );
    Collections.sort( paths, comparator );
    assertEquals( "/orders/new", paths.get( 0 ) );
    assertEquals( "/orders/{order}", paths.get( 1 ) );
    assertEquals( "/orders/*", paths.get( 2 ) );
    paths.clear();

    paths.add( "/orders/ne*" );
    paths.add( "/orders/n*" );
    Collections.shuffle( paths );
    Collections.sort( paths, comparator );
    assertEquals( "/orders/ne*", paths.get( 0 ) );
    assertEquals( "/orders/n*", paths.get( 1 ) );
    paths.clear();

    comparator = filter.getPatternComparator( "/orders/new.html" );
    paths.add( "/orders/new.*" );
    paths.add( "/orders/{order}" );
    Collections.shuffle( paths );
    Collections.sort( paths, comparator );
    assertEquals( "/orders/new.*", paths.get( 0 ) );
    assertEquals( "/orders/{order}", paths.get( 1 ) );
    paths.clear();

    comparator = filter.getPatternComparator( "/web/endUser/action/login.html" );
    paths.add( "/**/login.*" );
    paths.add( "/**/endUser/action/login.*" );
    Collections.sort( paths, comparator );
    assertEquals( "/**/endUser/action/login.*", paths.get( 0 ) );
    assertEquals( "/**/login.*", paths.get( 1 ) );
    paths.clear();
  }




  @Test
  public void testCacheSetToFalse() {
    filter.setCachePatterns( false );
    match();
    assertTrue( filter.stringMatcherCache.isEmpty() );
  }




  @Test
  public void testCacheSetToTrue() {
    filter.setCachePatterns( true );
    match();
    assertTrue( filter.stringMatcherCache.size() > 20 );

    for ( int i = 0; i < 65536; i++ ) {
      filter.match( "test" + i, "test" );
    }
    // Cache keeps being alive due to the explicit cache setting
    assertTrue( filter.stringMatcherCache.size() > 65536 );
  }




  @Test
  public void testDefaultCacheSetting() {
    match();
    assertTrue( filter.stringMatcherCache.size() > 20 );

    for ( int i = 0; i < 65536; i++ ) {
      filter.match( "test" + i, "test" );
    }
    // Cache turned off because it went beyond the threshold
    assertTrue( filter.stringMatcherCache.isEmpty() );
  }





  public void testExtensionMappingWithDotPathSeparator() {
    filter.setPathSeparator( "." );
    assertEquals( "Extension mapping should be disabled with \".\" as path separator", "/*.html.order.*", filter.combine( "/*.html", "order.*" ) );
  }




  @Test
  public void trimTokensOff() {
    filter.setTrimTokens( false );

    assertTrue( filter.match( "/group/{groupName}/members", "/group/sales/members" ) );
    assertTrue( filter.match( "/group/{groupName}/members", "/group/  sales/members" ) );
  }




  @Test
  public void uniqueDeliminator() {
    filter.setPathSeparator( "." );

    // test exact matching
    assertTrue( filter.match( "test", "test" ) );
    assertTrue( filter.match( ".test", ".test" ) );
    assertFalse( filter.match( ".test/jpg", "test/jpg" ) );
    assertFalse( filter.match( "test", ".test" ) );
    assertFalse( filter.match( ".test", "test" ) );

    // test matching with ?'s
    assertTrue( filter.match( "t?st", "test" ) );
    assertTrue( filter.match( "??st", "test" ) );
    assertTrue( filter.match( "tes?", "test" ) );
    assertTrue( filter.match( "te??", "test" ) );
    assertTrue( filter.match( "?es?", "test" ) );
    assertFalse( filter.match( "tes?", "tes" ) );
    assertFalse( filter.match( "tes?", "testt" ) );
    assertFalse( filter.match( "tes?", "tsst" ) );

    // test matchin with *'s
    assertTrue( filter.match( "*", "test" ) );
    assertTrue( filter.match( "test*", "test" ) );
    assertTrue( filter.match( "test*", "testTest" ) );
    assertTrue( filter.match( "*test*", "AnothertestTest" ) );
    assertTrue( filter.match( "*test", "Anothertest" ) );
    assertTrue( filter.match( "*/*", "test/" ) );
    assertTrue( filter.match( "*/*", "test/test" ) );
    assertTrue( filter.match( "*/*", "test/test/test" ) );
    assertTrue( filter.match( "test*aaa", "testblaaaa" ) );
    assertFalse( filter.match( "test*", "tst" ) );
    assertFalse( filter.match( "test*", "tsttest" ) );
    assertFalse( filter.match( "*test*", "tsttst" ) );
    assertFalse( filter.match( "*test", "tsttst" ) );
    assertFalse( filter.match( "*/*", "tsttst" ) );
    assertFalse( filter.match( "test*aaa", "test" ) );
    assertFalse( filter.match( "test*aaa", "testblaaab" ) );

    // test matching with ?'s and .'s
    assertTrue( filter.match( ".?", ".a" ) );
    assertTrue( filter.match( ".?.a", ".a.a" ) );
    assertTrue( filter.match( ".a.?", ".a.b" ) );
    assertTrue( filter.match( ".??.a", ".aa.a" ) );
    assertTrue( filter.match( ".a.??", ".a.bb" ) );
    assertTrue( filter.match( ".?", ".a" ) );

    // test matching with **'s
    assertTrue( filter.match( ".**", ".testing.testing" ) );
    assertTrue( filter.match( ".*.**", ".testing.testing" ) );
    assertTrue( filter.match( ".**.*", ".testing.testing" ) );
    assertTrue( filter.match( ".bla.**.bla", ".bla.testing.testing.bla" ) );
    assertTrue( filter.match( ".bla.**.bla", ".bla.testing.testing.bla.bla" ) );
    assertTrue( filter.match( ".**.test", ".bla.bla.test" ) );
    assertTrue( filter.match( ".bla.**.**.bla", ".bla.bla.bla.bla.bla.bla" ) );
    assertTrue( filter.match( ".bla*bla.test", ".blaXXXbla.test" ) );
    assertTrue( filter.match( ".*bla.test", ".XXXbla.test" ) );
    assertFalse( filter.match( ".bla*bla.test", ".blaXXXbl.test" ) );
    assertFalse( filter.match( ".*bla.test", "XXXblab.test" ) );
    assertFalse( filter.match( ".*bla.test", "XXXbl.test" ) );
  }




  @Test
  public void withMatchStart() {
    // test exact matching
    assertTrue( filter.matchStart( "test", "test" ) );
    assertTrue( filter.matchStart( "/test", "/test" ) );
    assertFalse( filter.matchStart( "/test.jpg", "test.jpg" ) );
    assertFalse( filter.matchStart( "test", "/test" ) );
    assertFalse( filter.matchStart( "/test", "test" ) );

    // test matching with ?'s
    assertTrue( filter.matchStart( "t?st", "test" ) );
    assertTrue( filter.matchStart( "??st", "test" ) );
    assertTrue( filter.matchStart( "tes?", "test" ) );
    assertTrue( filter.matchStart( "te??", "test" ) );
    assertTrue( filter.matchStart( "?es?", "test" ) );
    assertFalse( filter.matchStart( "tes?", "tes" ) );
    assertFalse( filter.matchStart( "tes?", "testt" ) );
    assertFalse( filter.matchStart( "tes?", "tsst" ) );

    // test matchin with *'s
    assertTrue( filter.matchStart( "*", "test" ) );
    assertTrue( filter.matchStart( "test*", "test" ) );
    assertTrue( filter.matchStart( "test*", "testTest" ) );
    assertTrue( filter.matchStart( "test/*", "test/Test" ) );
    assertTrue( filter.matchStart( "test/*", "test/t" ) );
    assertTrue( filter.matchStart( "test/*", "test/" ) );
    assertTrue( filter.matchStart( "*test*", "AnothertestTest" ) );
    assertTrue( filter.matchStart( "*test", "Anothertest" ) );
    assertTrue( filter.matchStart( "*.*", "test." ) );
    assertTrue( filter.matchStart( "*.*", "test.test" ) );
    assertTrue( filter.matchStart( "*.*", "test.test.test" ) );
    assertTrue( filter.matchStart( "test*aaa", "testblaaaa" ) );
    assertFalse( filter.matchStart( "test*", "tst" ) );
    assertFalse( filter.matchStart( "test*", "test/" ) );
    assertFalse( filter.matchStart( "test*", "tsttest" ) );
    assertFalse( filter.matchStart( "test*", "test/" ) );
    assertFalse( filter.matchStart( "test*", "test/t" ) );
    assertTrue( filter.matchStart( "test/*", "test" ) );
    assertTrue( filter.matchStart( "test/t*.txt", "test" ) );
    assertFalse( filter.matchStart( "*test*", "tsttst" ) );
    assertFalse( filter.matchStart( "*test", "tsttst" ) );
    assertFalse( filter.matchStart( "*.*", "tsttst" ) );
    assertFalse( filter.matchStart( "test*aaa", "test" ) );
    assertFalse( filter.matchStart( "test*aaa", "testblaaab" ) );

    // test matching with ?'s and /'s
    assertTrue( filter.matchStart( "/?", "/a" ) );
    assertTrue( filter.matchStart( "/?/a", "/a/a" ) );
    assertTrue( filter.matchStart( "/a/?", "/a/b" ) );
    assertTrue( filter.matchStart( "/??/a", "/aa/a" ) );
    assertTrue( filter.matchStart( "/a/??", "/a/bb" ) );
    assertTrue( filter.matchStart( "/?", "/a" ) );

    // test matching with **'s
    assertTrue( filter.matchStart( "/**", "/testing/testing" ) );
    assertTrue( filter.matchStart( "/*/**", "/testing/testing" ) );
    assertTrue( filter.matchStart( "/**/*", "/testing/testing" ) );
    assertTrue( filter.matchStart( "test*/**", "test/" ) );
    assertTrue( filter.matchStart( "test*/**", "test/t" ) );
    assertTrue( filter.matchStart( "/bla/**/bla", "/bla/testing/testing/bla" ) );
    assertTrue( filter.matchStart( "/bla/**/bla", "/bla/testing/testing/bla/bla" ) );
    assertTrue( filter.matchStart( "/**/test", "/bla/bla/test" ) );
    assertTrue( filter.matchStart( "/bla/**/**/bla", "/bla/bla/bla/bla/bla/bla" ) );
    assertTrue( filter.matchStart( "/bla*bla/test", "/blaXXXbla/test" ) );
    assertTrue( filter.matchStart( "/*bla/test", "/XXXbla/test" ) );
    assertFalse( filter.matchStart( "/bla*bla/test", "/blaXXXbl/test" ) );
    assertFalse( filter.matchStart( "/*bla/test", "XXXblab/test" ) );
    assertFalse( filter.matchStart( "/*bla/test", "XXXbl/test" ) );

    assertFalse( filter.matchStart( "/????", "/bala/bla" ) );
    assertTrue( filter.matchStart( "/**/*bla", "/bla/bla/bla/bbb" ) );

    assertTrue( filter.matchStart( "/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing/" ) );
    assertTrue( filter.matchStart( "/*bla*/**/bla/*", "/XXXblaXXXX/testing/testing/bla/testing" ) );
    assertTrue( filter.matchStart( "/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing" ) );
    assertTrue( filter.matchStart( "/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing.jpg" ) );

    assertTrue( filter.matchStart( "*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing/" ) );
    assertTrue( filter.matchStart( "*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing" ) );
    assertTrue( filter.matchStart( "*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing" ) );
    assertTrue( filter.matchStart( "*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing/testing" ) );

    assertTrue( filter.matchStart( "/x/x/**/bla", "/x/x/x/" ) );

    assertTrue( filter.matchStart( "", "" ) );
  }

}