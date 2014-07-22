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




  @BeforeClass
  public static void setUp() {
    _options = new Options().addOption( "a", "enable-a", false, "turn [a] on or off" ).addOption( OptionBuilder.withLongOpt( "bfile" ).hasArg().isRequired().withDescription( "set the value of [b]" ).create( 'b' ) );
  }

  private final ArgumentParser parser = new PosixParser();




  @Test
  public void testMissingRequiredOption() {
    final String[] args = new String[] { "-a" };

    try {
      parser.parse( _options, args );
      fail( "exception should have been thrown" );
    } catch ( final MissingOptionException e ) {
      assertEquals( "Incorrect exception message", "Missing required option: b", e.getMessage() );
      assertTrue( e.getMissingOptions().contains( "b" ) );
    } catch ( final ArgumentException e ) {
      fail( "expected to catch MissingOptionException" );
    }
  }




  @Test
  public void testMissingRequiredOptions() {
    final String[] args = new String[] { "-a" };

    _options.addOption( OptionBuilder.withLongOpt( "cfile" ).hasArg().isRequired().withDescription( "set the value of [c]" ).create( 'c' ) );

    try {
      parser.parse( _options, args );
      fail( "exception should have been thrown" );
    } catch ( final MissingOptionException e ) {
      assertEquals( "Incorrect exception message", "Missing required options: b, c", e.getMessage() );
      assertTrue( e.getMissingOptions().contains( "b" ) );
      assertTrue( e.getMissingOptions().contains( "c" ) );
    } catch ( final ArgumentException e ) {
      fail( "expected to catch MissingOptionException" );
    }
  }




  // TODO
  public void testOptionAndRequiredOption() throws Exception {
    final String[] args = new String[] { "-a", "-b", "file" };

    final ArgumentList argList = parser.parse( _options, args );

    assertTrue( "Confirm -a is set", argList.hasOption( "a" ) );
    assertTrue( "Confirm -b is set", argList.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", argList.getOptionValue( "b" ).equals( "file" ) );
    assertTrue( "Confirm NO of extra args", argList.getArgList().size() == 0 );
  }




  @Test
  public void testReuseOptionsTwice() throws Exception {
    final Options opts = new Options();
    opts.addOption( OptionBuilder.isRequired().create( 'v' ) );

    final GnuParser parser = new GnuParser();

    // first parsing
    parser.parse( opts, new String[] { "-v" } );

    try {
      // second parsing, with the same Options instance and an invalid command line
      parser.parse( opts, new String[0] );
      fail( "MissingOptionException not thrown" );
    } catch ( final MissingOptionException e ) {
      // expected
    }
  }




  // TODO
  public void testWithRequiredOption() throws Exception {
    final String[] args = new String[] { "-b", "file" };

    final ArgumentList argList = parser.parse( _options, args );

    assertTrue( "Confirm -a is NOT set", !argList.hasOption( "a" ) );
    assertTrue( "Confirm -b is set", argList.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", argList.getOptionValue( "b" ).equals( "file" ) );
    assertTrue( "Confirm NO of extra args", argList.getArgList().size() == 0 );
  }

}
