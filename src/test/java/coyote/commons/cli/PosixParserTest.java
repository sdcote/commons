package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 */
public class PosixParserTest extends ParserTestCase {
  @BeforeClass
  public static void setUp() {
    ParserTestCase.setUp();
    parser = new PosixParser();
  }




  @Test
  public void testBursting() throws Exception {
    final String[] args = new String[] { "-acbtoast", "foo", "bar" };

    final ArgumentList cl = parser.parse( options, args );

    assertTrue( "Confirm -a is set", cl.hasOption( "a" ) );
    assertTrue( "Confirm -b is set", cl.hasOption( "b" ) );
    assertTrue( "Confirm -c is set", cl.hasOption( "c" ) );
    assertTrue( "Confirm arg of -b", cl.getOptionValue( "b" ).equals( "toast" ) );
    assertTrue( "Confirm size of extra args", cl.getArgList().size() == 2 );
  }




  /**
   * Real world test with long and short options.
   */
  @Test
  public void testLongOptionWithShort() throws Exception {
    final Option help = new Option( "h", "help", false, "print this message" );
    final Option version = new Option( "v", "version", false, "print version information" );
    final Option newRun = new Option( "n", "new", false, "Create NLT cache entries only for new items" );
    final Option trackerRun = new Option( "t", "tracker", false, "Create NLT cache entries only for tracker items" );

    OptionBuilder.withLongOpt( "limit" );
    OptionBuilder.hasArg();
    OptionBuilder.withValueSeparator();
    OptionBuilder.withDescription( "Set time limit for execution, in minutes" );
    final Option timeLimit = OptionBuilder.create( "l" );

    OptionBuilder.withLongOpt( "age" );
    OptionBuilder.hasArg();
    OptionBuilder.withValueSeparator();
    OptionBuilder.withDescription( "Age (in days) of cache item before being recomputed" );
    final Option age = OptionBuilder.create( "a" );

    OptionBuilder.withLongOpt( "server" );
    OptionBuilder.hasArg();
    OptionBuilder.withValueSeparator();
    OptionBuilder.withDescription( "The NLT server address" );
    final Option server = OptionBuilder.create( "s" );

    OptionBuilder.withLongOpt( "results" );
    OptionBuilder.hasArg();
    OptionBuilder.withValueSeparator();
    OptionBuilder.withDescription( "Number of results per item" );
    final Option numResults = OptionBuilder.create( "r" );

    OptionBuilder.withLongOpt( "file" );
    OptionBuilder.hasArg();
    OptionBuilder.withValueSeparator();
    OptionBuilder.withDescription( "Use the specified configuration file" );
    final Option configFile = OptionBuilder.create();

    final Options options = new Options();
    options.addOption( help );
    options.addOption( version );
    options.addOption( newRun );
    options.addOption( trackerRun );
    options.addOption( timeLimit );
    options.addOption( age );
    options.addOption( server );
    options.addOption( numResults );
    options.addOption( configFile );

    // create the command line parser
    final CommandLineParser parser = new PosixParser();

    final String[] args = new String[] { "-v", "-l", "10", "-age", "5", "-file", "filename" };

    final ArgumentList line = parser.parse( options, args );
    assertTrue( line.hasOption( "v" ) );
    assertEquals( line.getOptionValue( "l" ), "10" );
    assertEquals( line.getOptionValue( "limit" ), "10" );
    assertEquals( line.getOptionValue( "a" ), "5" );
    assertEquals( line.getOptionValue( "age" ), "5" );
    assertEquals( line.getOptionValue( "file" ), "filename" );
  }




  @Override
  public void testLongWithEqualSingleDash() throws Exception {
    // not supported by the PosixParser
  }




  @Test
  public void testMissingArgWithBursting() throws Exception {
    final String[] args = new String[] { "-acb" };

    boolean caught = false;

    try {
      parser.parse( options, args );
    } catch ( final MissingArgumentException e ) {
      caught = true;
      assertEquals( "option missing an argument", "b", e.getOption().getOpt() );
    }

    assertTrue( "Confirm MissingArgumentException caught", caught );
  }




  @Override
  public void testShortWithEqual() throws Exception {
    // not supported by the PosixParser
  }




  @Test
  public void testStopBursting() throws Exception {
    final String[] args = new String[] { "-azc" };

    final ArgumentList cl = parser.parse( options, args, true );
    assertTrue( "Confirm -a is set", cl.hasOption( "a" ) );
    assertFalse( "Confirm -c is not set", cl.hasOption( "c" ) );

    assertTrue( "Confirm  1 extra arg: " + cl.getArgList().size(), cl.getArgList().size() == 1 );
    assertTrue( cl.getArgList().contains( "zc" ) );
  }




  @Test
  public void testStopBursting2() throws Exception {
    final String[] args = new String[] { "-c", "foobar", "-btoast" };

    ArgumentList cl = parser.parse( options, args, true );
    assertTrue( "Confirm -c is set", cl.hasOption( "c" ) );
    assertTrue( "Confirm  2 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 2 );

    cl = parser.parse( options, cl.getArgs() );

    assertTrue( "Confirm -c is not set", !cl.hasOption( "c" ) );
    assertTrue( "Confirm -b is set", cl.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", cl.getOptionValue( "b" ).equals( "toast" ) );
    assertTrue( "Confirm  1 extra arg: " + cl.getArgList().size(), cl.getArgList().size() == 1 );
    assertTrue( "Confirm  value of extra arg: " + cl.getArgList().get( 0 ), cl.getArgList().get( 0 ).equals( "foobar" ) );
  }




  @Test
  public void testUnrecognizedOptionWithBursting() throws Exception {
    final String[] args = new String[] { "-adbtoast", "foo", "bar" };

    try {
      parser.parse( options, args );
      fail( "UnrecognizedOptionException wasn't thrown" );
    } catch ( final UnrecognizedOptionException e ) {
      assertEquals( "-adbtoast", e.getOption() );
    }
  }
}
