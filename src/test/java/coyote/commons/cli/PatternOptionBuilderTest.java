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
  public void testSimplePattern() throws Exception {
    Options options = PatternOptionBuilder.parsePattern( "a:b@cde>f+n%t/m*z#" );
    String[] args = new String[] { "-c", "-a", "foo", "-b", "java.util.Vector", "-e", "build.xml", "-f", "java.util.Calendar", "-n", "4.5", "-t", "http://commons.apache.org", "-z", "Thu Jun 06 17:48:57 EDT 2002", "-m", "test*" };

    CommandLineParser parser = new PosixParser();
    CommandLine line = parser.parse( options, args );

    assertEquals( "flag a", "foo", line.getOptionValue( "a" ) );
    assertEquals( "string flag a", "foo", line.getParsedOptionValue( "a" ) );
    assertEquals( "object flag b", new Vector(), line.getParsedOptionValue( "b" ) );
    assertTrue( "boolean true flag c", line.hasOption( "c" ) );
    assertFalse( "boolean false flag d", line.hasOption( "d" ) );
    assertEquals( "file flag e", new File( "build.xml" ), line.getParsedOptionValue( "e" ) );
    assertEquals( "class flag f", Calendar.class, line.getParsedOptionValue( "f" ) );
    assertEquals( "number flag n", new Double( 4.5 ), line.getParsedOptionValue( "n" ) );
    assertEquals( "url flag t", new URL( "http://commons.apache.org" ), line.getParsedOptionValue( "t" ) );

    // tests the char methods of CommandLine that delegate to the String methods
    assertEquals( "flag a", "foo", line.getOptionValue( 'a' ) );
    assertEquals( "string flag a", "foo", line.getParsedOptionValue( 'a' ) );
    assertEquals( "object flag b", new Vector(), line.getParsedOptionValue( 'b' ) );
    assertTrue( "boolean true flag c", line.hasOption( 'c' ) );
    assertFalse( "boolean false flag d", line.hasOption( 'd' ) );
    assertEquals( "file flag e", new File( "build.xml" ), line.getParsedOptionValue( 'e' ) );
    assertEquals( "class flag f", Calendar.class, line.getParsedOptionValue( 'f' ) );
    assertEquals( "number flag n", new Double( 4.5 ), line.getParsedOptionValue( 'n' ) );
    assertEquals( "url flag t", new URL( "http://commons.apache.org" ), line.getParsedOptionValue( 't' ) );

    // FILES NOT SUPPORTED YET
    try {
      assertEquals( "files flag m", new File[0], line.getParsedOptionValue( 'm' ) );
      fail( "Multiple files are not supported yet, should have failed" );
    } catch ( UnsupportedOperationException uoe ) {
      // expected
    }

    // DATES NOT SUPPORTED YET
    try {
      assertEquals( "date flag z", new Date( 1023400137276L ), line.getParsedOptionValue( 'z' ) );
      fail( "Date is not supported yet, should have failed" );
    } catch ( UnsupportedOperationException uoe ) {
      // expected
    }
  }




  @Test
  public void testEmptyPattern() throws Exception {
    Options options = PatternOptionBuilder.parsePattern( "" );
    assertTrue( options.getOptions().isEmpty() );
  }




  @Test
  public void testUntypedPattern() throws Exception {
    Options options = PatternOptionBuilder.parsePattern( "abc" );
    CommandLineParser parser = new PosixParser();
    CommandLine line = parser.parse( options, new String[] { "-abc" } );

    assertTrue( line.hasOption( 'a' ) );
    assertNull( "value a", line.getParsedOptionValue( 'a' ) );
    assertTrue( line.hasOption( 'b' ) );
    assertNull( "value b", line.getParsedOptionValue( 'b' ) );
    assertTrue( line.hasOption( 'c' ) );
    assertNull( "value c", line.getParsedOptionValue( 'c' ) );
  }




  @Test
  public void testNumberPattern() throws Exception {
    Options options = PatternOptionBuilder.parsePattern( "n%d%x%" );
    CommandLineParser parser = new PosixParser();
    CommandLine line = parser.parse( options, new String[] { "-n", "1", "-d", "2.1", "-x", "3,5" } );

    assertEquals( "n object class", Long.class, line.getParsedOptionValue( "n" ).getClass() );
    assertEquals( "n value", new Long( 1 ), line.getParsedOptionValue( "n" ) );

    assertEquals( "d object class", Double.class, line.getParsedOptionValue( "d" ).getClass() );
    assertEquals( "d value", new Double( 2.1 ), line.getParsedOptionValue( "d" ) );

    // TODO: assertNull( "x object", line.getParsedOptionValue( "x" ) );
  }




  @Test
  public void testClassPattern() throws Exception {
    Options options = PatternOptionBuilder.parsePattern( "c+d+" );
    CommandLineParser parser = new PosixParser();
    CommandLine line = parser.parse( options, new String[] { "-c", "java.util.Calendar", "-d", "System.DateTime" } );

    assertEquals( "c value", Calendar.class, line.getParsedOptionValue( "c" ) );
    // TODO: assertNull( "d value", line.getParsedOptionValue( "d" ) );
  }




  @Test
  public void testObjectPattern() throws Exception {
    Options options = PatternOptionBuilder.parsePattern( "o@i@n@" );
    CommandLineParser parser = new PosixParser();
    CommandLine line = parser.parse( options, new String[] { "-o", "java.lang.String", "-i", "java.util.Calendar", "-n", "System.DateTime" } );

    assertEquals( "o value", "", line.getParsedOptionValue( "o" ) );
    // TODO: assertNull( "i value", line.getParsedOptionValue( "i" ) );
    // TODO: assertNull( "n value", line.getParsedOptionValue( "n" ) );
  }




  @Test
  public void testURLPattern() throws Exception {
    Options options = PatternOptionBuilder.parsePattern( "u/v/" );
    CommandLineParser parser = new PosixParser();
    CommandLine line = parser.parse( options, new String[] { "-u", "http://commons.apache.org", "-v", "foo://commons.apache.org" } );

    assertEquals( "u value", new URL( "http://commons.apache.org" ), line.getParsedOptionValue( "u" ) );
    // TODO: assertNull( "v value", line.getParsedOptionValue( "v" ) );
  }




  @Test
  public void testExistingFilePattern() throws Exception {
    Options options = PatternOptionBuilder.parsePattern( "f<" );
    CommandLineParser parser = new PosixParser();
    CommandLine line = parser.parse( options, new String[] { "-f", "test.properties" } );

    assertEquals( "f value", new File( "test.properties" ), line.getParsedOptionValue( "f" ) );

    // todo test if an error is returned if the file doesn't exists (when it's implemented)
  }




  @Test
  public void testRequiredOption() throws Exception {
    Options options = PatternOptionBuilder.parsePattern( "!n%m%" );
    CommandLineParser parser = new PosixParser();

    try {
      parser.parse( options, new String[] { "" } );
      fail( "MissingOptionException wasn't thrown" );
    } catch ( MissingOptionException e ) {
      assertEquals( 1, e.getMissingOptions().size() );
      assertTrue( e.getMissingOptions().contains( "n" ) );
    }
  }
}
