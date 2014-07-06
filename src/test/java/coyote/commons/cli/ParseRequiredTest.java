package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 * 
 */
public class ParseRequiredTest {
  private static Options _options = null;
  private CommandLineParser parser = new PosixParser();




  @BeforeClass
  public static void setUp() {
    _options = new Options().addOption( "a", "enable-a", false, "turn [a] on or off" ).addOption( OptionBuilder.withLongOpt( "bfile" ).hasArg().isRequired().withDescription( "set the value of [b]" ).create( 'b' ) );
  }




  // TODO
  public void testWithRequiredOption() throws Exception {
    String[] args = new String[] { "-b", "file" };

    CommandLine cl = parser.parse( _options, args );

    assertTrue( "Confirm -a is NOT set", !cl.hasOption( "a" ) );
    assertTrue( "Confirm -b is set", cl.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", cl.getOptionValue( "b" ).equals( "file" ) );
    assertTrue( "Confirm NO of extra args", cl.getArgList().size() == 0 );
  }




  // TODO
  public void testOptionAndRequiredOption() throws Exception {
    String[] args = new String[] { "-a", "-b", "file" };

    CommandLine cl = parser.parse( _options, args );

    assertTrue( "Confirm -a is set", cl.hasOption( "a" ) );
    assertTrue( "Confirm -b is set", cl.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", cl.getOptionValue( "b" ).equals( "file" ) );
    assertTrue( "Confirm NO of extra args", cl.getArgList().size() == 0 );
  }




  @Test
  public void testMissingRequiredOption() {
    String[] args = new String[] { "-a" };

    try {
      CommandLine cl = parser.parse( _options, args );
      fail( "exception should have been thrown" );
    } catch ( MissingOptionException e ) {
      assertEquals( "Incorrect exception message", "Missing required option: b", e.getMessage() );
      assertTrue( e.getMissingOptions().contains( "b" ) );
    } catch ( ParseException e ) {
      fail( "expected to catch MissingOptionException" );
    }
  }




  @Test
  public void testMissingRequiredOptions() {
    String[] args = new String[] { "-a" };

    _options.addOption( OptionBuilder.withLongOpt( "cfile" ).hasArg().isRequired().withDescription( "set the value of [c]" ).create( 'c' ) );

    try {
      CommandLine cl = parser.parse( _options, args );
      fail( "exception should have been thrown" );
    } catch ( MissingOptionException e ) {
      assertEquals( "Incorrect exception message", "Missing required options: b, c", e.getMessage() );
      assertTrue( e.getMissingOptions().contains( "b" ) );
      assertTrue( e.getMissingOptions().contains( "c" ) );
    } catch ( ParseException e ) {
      fail( "expected to catch MissingOptionException" );
    }
  }




  @Test
  public void testReuseOptionsTwice() throws Exception {
    Options opts = new Options();
    opts.addOption( OptionBuilder.isRequired().create( 'v' ) );

    GnuParser parser = new GnuParser();

    // first parsing
    parser.parse( opts, new String[] { "-v" } );

    try {
      // second parsing, with the same Options instance and an invalid command line
      parser.parse( opts, new String[0] );
      fail( "MissingOptionException not thrown" );
    } catch ( MissingOptionException e ) {
      // expected
    }
  }

}
