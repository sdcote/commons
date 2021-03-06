package coyote.commons.cli;

//import static org.junit.Assert.*;
import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.Test;


/** 
 *
 */
public class PatternOptionBuilderTest extends TestCase {
  @Test
  public void testClassPattern() throws Exception {
    final Options options = PatternOptionBuilder.parsePattern( "c+d+" );
    final ArgumentParser parser = new PosixParser();
    final ArgumentList line = parser.parse( options, new String[] { "-c", "java.util.Calendar", "-d", "System.DateTime" } );

    assertEquals( "c value", Calendar.class, line.getParsedOptionValue( "c" ) );
    // TODO: assertNull( "d value", line.getParsedOptionValue( "d" ) );
  }




  @Test
  public void testEmptyPattern() throws Exception {
    final Options options = PatternOptionBuilder.parsePattern( "" );
    assertTrue( options.getOptions().isEmpty() );
  }




  @Test
  public void testExistingFilePattern() throws Exception {
    final Options options = PatternOptionBuilder.parsePattern( "f<" );
    final ArgumentParser parser = new PosixParser();
    final ArgumentList line = parser.parse( options, new String[] { "-f", "test.properties" } );

    assertEquals( "f value", new File( "test.properties" ), line.getParsedOptionValue( "f" ) );

    // TODO: test if an error is returned if the file doesn't exists (when it's implemented)
  }




  @Test
  public void testNumberPattern() throws Exception {
    final Options options = PatternOptionBuilder.parsePattern( "n%d%x%" );
    final ArgumentParser parser = new PosixParser();
    final ArgumentList line = parser.parse( options, new String[] { "-n", "1", "-d", "2.1", "-x", "3,5" } );

    assertEquals( "n object class", Long.class, line.getParsedOptionValue( "n" ).getClass() );
    assertEquals( "n value", new Long( 1 ), line.getParsedOptionValue( "n" ) );

    assertEquals( "d object class", Double.class, line.getParsedOptionValue( "d" ).getClass() );
    assertEquals( "d value", new Double( 2.1 ), line.getParsedOptionValue( "d" ) );

    // TODO: assertNull( "x object", line.getParsedOptionValue( "x" ) );
  }




  @Test
  public void testObjectPattern() throws Exception {
    final Options options = PatternOptionBuilder.parsePattern( "o@i@n@" );
    final ArgumentParser parser = new PosixParser();
    final ArgumentList line = parser.parse( options, new String[] { "-o", "java.lang.String", "-i", "java.util.Calendar", "-n", "System.DateTime" } );

    assertEquals( "o value", "", line.getParsedOptionValue( "o" ) );
    // TODO: assertNull( "i value", line.getParsedOptionValue( "i" ) );
    // TODO: assertNull( "n value", line.getParsedOptionValue( "n" ) );
  }




  @Test
  public void testRequiredOption() throws Exception {
    final Options options = PatternOptionBuilder.parsePattern( "!n%m%" );
    final ArgumentParser parser = new PosixParser();

    try {
      parser.parse( options, new String[] { "" } );
      fail( "MissingOptionException wasn't thrown" );
    } catch ( final MissingOptionException e ) {
      assertEquals( 1, e.getMissingOptions().size() );
      assertTrue( e.getMissingOptions().contains( "n" ) );
    }
  }




  @Test
  public void testSimplePattern() throws Exception {
    final Options options = PatternOptionBuilder.parsePattern( "a:b@cde>f+n%t/m*z#" );
    final String[] args = new String[] { "-c", "-a", "foo", "-b", "java.util.Vector", "-e", "build.xml", "-f", "java.util.Calendar", "-n", "4.5", "-t", "http://commons.apache.org", "-z", "Thu Jun 06 17:48:57 EDT 2002", "-m", "test*" };

    final ArgumentParser parser = new PosixParser();
    final ArgumentList argList = parser.parse( options, args );

    assertEquals( "flag a", "foo", argList.getOptionValue( "a" ) );
    assertEquals( "string flag a", "foo", argList.getParsedOptionValue( "a" ) );
    assertEquals( "object flag b", new Vector(), argList.getParsedOptionValue( "b" ) );
    assertTrue( "boolean true flag c", argList.hasOption( "c" ) );
    assertFalse( "boolean false flag d", argList.hasOption( "d" ) );
    assertEquals( "file flag e", new File( "build.xml" ), argList.getParsedOptionValue( "e" ) );
    assertEquals( "class flag f", Calendar.class, argList.getParsedOptionValue( "f" ) );
    assertEquals( "number flag n", new Double( 4.5 ), argList.getParsedOptionValue( "n" ) );
    assertEquals( "url flag t", new URL( "http://commons.apache.org" ), argList.getParsedOptionValue( "t" ) );

    // tests the char methods of ArgumentList that delegate to the String methods
    assertEquals( "flag a", "foo", argList.getOptionValue( 'a' ) );
    assertEquals( "string flag a", "foo", argList.getParsedOptionValue( 'a' ) );
    assertEquals( "object flag b", new Vector(), argList.getParsedOptionValue( 'b' ) );
    assertTrue( "boolean true flag c", argList.hasOption( 'c' ) );
    assertFalse( "boolean false flag d", argList.hasOption( 'd' ) );
    assertEquals( "file flag e", new File( "build.xml" ), argList.getParsedOptionValue( 'e' ) );
    assertEquals( "class flag f", Calendar.class, argList.getParsedOptionValue( 'f' ) );
    assertEquals( "number flag n", new Double( 4.5 ), argList.getParsedOptionValue( 'n' ) );
    assertEquals( "url flag t", new URL( "http://commons.apache.org" ), argList.getParsedOptionValue( 't' ) );

    // FILES NOT SUPPORTED YET
    try {
      assertEquals( "files flag m", new File[0], argList.getParsedOptionValue( 'm' ) );
      fail( "Multiple files are not supported yet, should have failed" );
    } catch ( final UnsupportedOperationException uoe ) {
      // expected
    }

    // DATES NOT SUPPORTED YET
    try {
      assertEquals( "date flag z", new Date( 1023400137276L ), argList.getParsedOptionValue( 'z' ) );
      fail( "Date is not supported yet, should have failed" );
    } catch ( final UnsupportedOperationException uoe ) {
      // expected
    }
  }




  @Test
  public void testUntypedPattern() throws Exception {
    final Options options = PatternOptionBuilder.parsePattern( "abc" );
    final ArgumentParser parser = new PosixParser();
    final ArgumentList argList = parser.parse( options, new String[] { "-abc" } );

    assertTrue( argList.hasOption( 'a' ) );
    assertNull( "value a", argList.getParsedOptionValue( 'a' ) );
    assertTrue( argList.hasOption( 'b' ) );
    assertNull( "value b", argList.getParsedOptionValue( 'b' ) );
    assertTrue( argList.hasOption( 'c' ) );
    assertNull( "value c", argList.getParsedOptionValue( 'c' ) );
  }




  @Test
  public void testURLPattern() throws Exception {
    final Options options = PatternOptionBuilder.parsePattern( "u/v/" );
    final ArgumentParser parser = new PosixParser();
    final ArgumentList argList = parser.parse( options, new String[] { "-u", "http://commons.apache.org", "-v", "foo://commons.apache.org" } );

    assertEquals( "u value", new URL( "http://commons.apache.org" ), argList.getParsedOptionValue( "u" ) );
    // TODO: assertNull( "v value", line.getParsedOptionValue( "v" ) );
  }
}
