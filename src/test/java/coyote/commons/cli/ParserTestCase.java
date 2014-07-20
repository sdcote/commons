package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Abstract test case testing common parser features.
 */
public abstract class ParserTestCase {
  protected static Parser parser;

  protected static Options options;




  @BeforeClass
  public static void setUp() {
    options = new Options().addOption( "a", "enable-a", false, "turn [a] on or off" ).addOption( "b", "bfile", true, "set the value of [b]" ).addOption( "c", "copt", false, "turn [c] on or off" );
  }




  @Test
  public void testAnotherCase() throws Exception {
    // Posix 
    Options options = new Options();
    options.addOption( OptionBuilder.hasOptionalArg().create( 'a' ) );
    options.addOption( OptionBuilder.hasArg().create( 'b' ) );
    String[] args = new String[] { "-a", "-bvalue" };

    CommandLineParser parser = new PosixParser();

    ArgumentList cmd = parser.parse( options, args );
    assertEquals( cmd.getOptionValue( 'b' ), "value" );

    // GNU
    options = new Options();
    options.addOption( OptionBuilder.hasOptionalArg().create( 'a' ) );
    options.addOption( OptionBuilder.hasArg().create( 'b' ) );
    args = new String[] { "-a", "-b", "value" };

    parser = new GnuParser();

    cmd = parser.parse( options, args );
    assertEquals( cmd.getOptionValue( 'b' ), "value" );
  }




  @Test
  public void testArgumentStartingWithHyphen() throws Exception {
    final String[] args = new String[] { "-b", "-foo" };

    final ArgumentList cl = parser.parse( options, args );
    assertEquals( "-foo", cl.getOptionValue( "b" ) );
  }




  @Test
  public void testConflictingOption() throws Exception {
    final CommandLineParser parser = new PosixParser();
    final String[] CLI_ARGS = new String[] { "-z", "c" };

    final Options options = new Options();
    options.addOption( new Option( "z", "timezone", true, "affected option" ) );

    parser.parse( options, CLI_ARGS );

    //now add conflicting option
    options.addOption( "c", "conflict", true, "conflict option" );
    final ArgumentList line = parser.parse( options, CLI_ARGS );
    assertEquals( line.getOptionValue( 'z' ), "c" );
    assertTrue( !line.hasOption( "c" ) );
  }




  @Test
  public void testDefaults() throws Exception {
    final Options options = new Options();
    options.addOption( "f", true, "foobar" );
    options.addOption( "m", true, "missing" );
    final String[] args = new String[] { "-f", "foo" };

    final CommandLineParser parser = new PosixParser();

    final ArgumentList cmd = parser.parse( options, args );

    cmd.getOptionValue( "f", "default f" );
    cmd.getOptionValue( "m", "default m" );
  }




  @Test
  public void testDoubleDash() throws Exception {
    final String[] args = new String[] { "--copt", "--", "-b", "toast" };

    final ArgumentList cl = parser.parse( options, args );

    assertTrue( "Confirm -c is set", cl.hasOption( "c" ) );
    assertTrue( "Confirm -b is not set", !cl.hasOption( "b" ) );
    assertTrue( "Confirm 2 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 2 );
  }




  @Test
  public void testEOLCharacter() throws Exception {
    final Options options = new Options();
    OptionBuilder.withDescription( "dir" );
    OptionBuilder.hasArg();
    final Option dir = OptionBuilder.create( 'd' );
    options.addOption( dir );

    final PrintStream oldSystemOut = System.out;
    try {
      final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      final PrintStream print = new PrintStream( bytes );

      // capture this platform's eol symbol
      print.println();
      final String eol = bytes.toString();
      bytes.reset();

      System.setOut( new PrintStream( bytes ) );

      final HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp( "dir", options );

      assertEquals( "usage: dir" + eol + " -d <arg>   dir" + eol, bytes.toString() );
    }
    finally {
      System.setOut( oldSystemOut );
    }
  }




  @Test
  public void testGnuRequired() throws Exception {
    OptionBuilder.isRequired();
    OptionBuilder.withDescription( "test" );
    final Option o = OptionBuilder.create( "test" );
    final Options opts = new Options();
    opts.addOption( o );
    opts.addOption( o );

    final CommandLineParser parser = new GnuParser();

    final String[] args = new String[] { "-test" };

    final ArgumentList line = parser.parse( opts, args );
    assertTrue( line.hasOption( "test" ) );
  }




  @Test
  public void testLongOptOnly() throws Exception {
    final Options options = new Options();
    options.addOption( OptionBuilder.withLongOpt( "verbose" ).create() );
    final String[] args = new String[] { "--verbose" };

    final CommandLineParser parser = new PosixParser();

    final ArgumentList cmd = parser.parse( options, args );
    assertTrue( cmd.hasOption( "verbose" ) );
  }




  @Test
  public void testLongWithEqual() throws Exception {
    final String[] args = new String[] { "--foo=bar" };

    final Options options = new Options();
    options.addOption( OptionBuilder.withLongOpt( "foo" ).hasArg().create( 'f' ) );

    final ArgumentList cl = parser.parse( options, args );

    assertEquals( "bar", cl.getOptionValue( "foo" ) );
  }




  @Test
  public void testLongWithEqualSingleDash() throws Exception {
    final String[] args = new String[] { "-foo=bar" };

    final Options options = new Options();
    options.addOption( OptionBuilder.withLongOpt( "foo" ).hasArg().create( 'f' ) );

    final ArgumentList cl = parser.parse( options, args );

    assertEquals( "bar", cl.getOptionValue( "foo" ) );
  }




  @Test
  public void testMissingArg() throws Exception {
    final String[] args = new String[] { "-b" };

    boolean caught = false;

    try {
      parser.parse( options, args );
    } catch ( final MissingArgumentException e ) {
      caught = true;
      assertEquals( "option missing an argument", "b", e.getOption().getOpt() );
    }

    assertTrue( "Confirm MissingArgumentException caught", caught );
  }




  @Test
  public void testMultiArgs() throws ArgumentException {
    final Option multiArgOption = new Option( "o", "option with multiple args" );
    multiArgOption.setArgs( 1 );

    final Options options = new Options();
    options.addOption( multiArgOption );

    final Parser parser = new PosixParser();
    final String[] args = new String[] {};
    final Properties props = new Properties();
    props.setProperty( "o", "ovalue" );
    final ArgumentList cl = parser.parse( options, args, props );

    assertTrue( cl.hasOption( 'o' ) );
    assertEquals( "ovalue", cl.getOptionValue( 'o' ) );
  }




  @Test
  public void testMultiple() throws Exception {
    final String[] args = new String[] { "-c", "foobar", "-b", "toast" };

    ArgumentList cl = parser.parse( options, args, true );
    assertTrue( "Confirm -c is set", cl.hasOption( "c" ) );
    assertTrue( "Confirm  3 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 3 );

    cl = parser.parse( options, cl.getArgs() );

    assertTrue( "Confirm -c is not set", !cl.hasOption( "c" ) );
    assertTrue( "Confirm -b is set", cl.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", cl.getOptionValue( "b" ).equals( "toast" ) );
    assertTrue( "Confirm  1 extra arg: " + cl.getArgList().size(), cl.getArgList().size() == 1 );
    assertTrue( "Confirm  value of extra arg: " + cl.getArgList().get( 0 ), cl.getArgList().get( 0 ).equals( "foobar" ) );
  }




  @Test
  public void testMultipleWithLong() throws Exception {
    final String[] args = new String[] { "--copt", "foobar", "--bfile", "toast" };

    ArgumentList cl = parser.parse( options, args, true );
    assertTrue( "Confirm -c is set", cl.hasOption( "c" ) );
    assertTrue( "Confirm  3 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 3 );

    cl = parser.parse( options, cl.getArgs() );

    assertTrue( "Confirm -c is not set", !cl.hasOption( "c" ) );
    assertTrue( "Confirm -b is set", cl.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", cl.getOptionValue( "b" ).equals( "toast" ) );
    assertTrue( "Confirm  1 extra arg: " + cl.getArgList().size(), cl.getArgList().size() == 1 );
    assertTrue( "Confirm  value of extra arg: " + cl.getArgList().get( 0 ), cl.getArgList().get( 0 ).equals( "foobar" ) );
  }




  @Test
  public void testNegativeArgument() throws Exception {
    final String[] args = new String[] { "-b", "-1" };

    final ArgumentList cl = parser.parse( options, args );
    assertEquals( "-1", cl.getOptionValue( "b" ) );
  }




  // @Test
  public void testOddCase() throws Exception {
    final OptionGroup directions = new OptionGroup();

    final Option left = new Option( "l", "left", false, "go left" );
    final Option right = new Option( "r", "right", false, "go right" );
    final Option straight = new Option( "s", "straight", false, "go straight" );
    final Option forward = new Option( "f", "forward", false, "go forward" );
    forward.setRequired( true );

    directions.addOption( left );
    directions.addOption( right );
    directions.setRequired( true );

    final Options opts = new Options();
    opts.addOptionGroup( directions );
    opts.addOption( straight );

    final CommandLineParser parser = new PosixParser();
    boolean exception = false;

    String[] args = new String[] {};
    try {
      parser.parse( opts, args );
    } catch ( final ArgumentException exp ) {
      exception = true;
    }

    if ( !exception ) {
      fail( "Expected exception not caught." );
    }

    exception = false;

    args = new String[] { "-s" };
    try {
      parser.parse( opts, args );
    } catch ( final ArgumentException exp ) {
      exception = true;
    }

    if ( !exception ) {
      fail( "Expected exception not caught." );
    }

    exception = false;

    args = new String[] { "-s", "-l" };
    try {
      parser.parse( opts, args );
    } catch ( final ArgumentException exp ) {
      fail( "Unexpected exception: " + exp.getClass().getName() + ":" + exp.getMessage() );
    }

    opts.addOption( forward );
    args = new String[] { "-s", "-l", "-f" };
    try {
      parser.parse( opts, args );
    } catch ( final ArgumentException exp ) {
      fail( "Unexpected exception: " + exp.getClass().getName() + ":" + exp.getMessage() );
    }
  }




  @Test
  public void testOptionGroup() throws Exception {
    // create the main options object which will handle the first parameter
    final Options mainOptions = new Options();
    // There can be 2 main exclusive options:  -exec|-rep

    // Therefore, place them in an option group

    String[] argv = new String[] { "-exec", "-exec_opt1", "-exec_opt2" };
    final OptionGroup grp = new OptionGroup();

    grp.addOption( new Option( "exec", false, "description for this option" ) );

    grp.addOption( new Option( "rep", false, "description for this option" ) );

    mainOptions.addOptionGroup( grp );

    // for the exec option, there are 2 options...
    final Options execOptions = new Options();
    execOptions.addOption( "exec_opt1", false, " desc" );
    execOptions.addOption( "exec_opt2", false, " desc" );

    // similarly, for rep there are 2 options...
    final Options repOptions = new Options();
    repOptions.addOption( "repopto", false, "desc" );
    repOptions.addOption( "repoptt", false, "desc" );

    // create the parser
    final GnuParser parser = new GnuParser();

    // finally, parse the arguments:

    // first parse the main options to see what the user has specified
    // We set stopAtNonOption to true so it does not touch the remaining
    // options
    ArgumentList cmd = parser.parse( mainOptions, argv, true );
    // get the remaining options...
    argv = cmd.getArgs();

    if ( cmd.hasOption( "exec" ) ) {
      cmd = parser.parse( execOptions, argv, false );
      // process the exec_op1 and exec_opt2...
      assertTrue( cmd.hasOption( "exec_opt1" ) );
      assertTrue( cmd.hasOption( "exec_opt2" ) );
    } else if ( cmd.hasOption( "rep" ) ) {
      cmd = parser.parse( repOptions, argv, false );
      // process the rep_op1 and rep_opt2...
    } else {
      fail( "exec option not found" );
    }
  }




  @Test
  public void testPosixPasswordCase() throws Exception {
    final Options options = new Options();
    OptionBuilder.withLongOpt( "old-password" );
    OptionBuilder.withDescription( "Use this option to specify the old password" );
    OptionBuilder.hasArg();
    final Option oldpass = OptionBuilder.create( 'o' );
    OptionBuilder.withLongOpt( "new-password" );
    OptionBuilder.withDescription( "Use this option to specify the new password" );
    OptionBuilder.hasArg();
    final Option newpass = OptionBuilder.create( 'n' );

    final String[] args = { "-o", "-n", "newpassword" };

    options.addOption( oldpass );
    options.addOption( newpass );

    final Parser parser = new PosixParser();

    try {
      parser.parse( options, args );
    }
    // catch the exception and leave the method
    catch ( final Exception exp ) {
      assertTrue( exp != null );
      return;
    }
    fail( "MissingArgumentException not caught." );
  }




  @Test
  public void testPropertiesOption() throws Exception {
    final String[] args = new String[] { "-Jsource=1.5", "-J", "target", "1.5", "foo" };

    final Options options = new Options();
    options.addOption( OptionBuilder.withValueSeparator().hasArgs( 2 ).create( 'J' ) );

    final ArgumentList cl = parser.parse( options, args );

    final List values = Arrays.asList( cl.getOptionValues( "J" ) );
    assertNotNull( "null values", values );
    assertEquals( "number of values", 4, values.size() );
    assertEquals( "value 1", "source", values.get( 0 ) );
    assertEquals( "value 2", "1.5", values.get( 1 ) );
    assertEquals( "value 3", "target", values.get( 2 ) );
    assertEquals( "value 4", "1.5", values.get( 3 ) );
    final List argsleft = cl.getArgList();
    assertEquals( "Should be 1 arg left", 1, argsleft.size() );
    assertEquals( "Expecting foo", "foo", argsleft.get( 0 ) );
  }




  @Test
  public void testQuoted() throws Exception {
    final CommandLineParser parser = new PosixParser();
    final String[] args = new String[] { "-m", "\"Two Words\"" };
    OptionBuilder.hasArgs();
    final Option m = OptionBuilder.create( "m" );
    final Options options = new Options();
    options.addOption( m );
    final ArgumentList line = parser.parse( options, args );
    assertEquals( "Two Words", line.getOptionValue( "m" ) );
  }




  @Test
  public void testSep() throws Exception {
    final Options options = new Options();
    options.addOption( OptionBuilder.withValueSeparator( '=' ).hasArgs().create( 'D' ) );
    options.addOption( OptionBuilder.withValueSeparator( ':' ).hasArgs().create( 'p' ) );
    final String[] args = new String[] { "-DJAVA_HOME=/opt/java", "-pfile1:file2:file3" };

    final CommandLineParser parser = new PosixParser();

    final ArgumentList cmd = parser.parse( options, args );

    String[] values = cmd.getOptionValues( 'D' );

    assertEquals( values[0], "JAVA_HOME" );
    assertEquals( values[1], "/opt/java" );

    values = cmd.getOptionValues( 'p' );

    assertEquals( values[0], "file1" );
    assertEquals( values[1], "file2" );
    assertEquals( values[2], "file3" );

    final Iterator iter = cmd.iterator();
    while ( iter.hasNext() ) {
      final Option opt = (Option)iter.next();
      switch ( opt.getId() ) {
        case 'D':
          assertEquals( opt.getValue( 0 ), "JAVA_HOME" );
          assertEquals( opt.getValue( 1 ), "/opt/java" );
          break;
        case 'p':
          assertEquals( opt.getValue( 0 ), "file1" );
          assertEquals( opt.getValue( 1 ), "file2" );
          assertEquals( opt.getValue( 2 ), "file3" );
          break;
        default:
          fail( "-D option not found" );
      }
    }
  }




  @Test
  public void testShortWithEqual() throws Exception {
    final String[] args = new String[] { "-f=bar" };

    final Options options = new Options();
    options.addOption( OptionBuilder.withLongOpt( "foo" ).hasArg().create( 'f' ) );

    final ArgumentList cl = parser.parse( options, args );

    assertEquals( "bar", cl.getOptionValue( "foo" ) );
  }




  @Test
  public void testShortWithoutEqual() throws Exception {
    final String[] args = new String[] { "-fbar" };

    final Options options = new Options();
    options.addOption( OptionBuilder.withLongOpt( "foo" ).hasArg().create( 'f' ) );

    final ArgumentList cl = parser.parse( options, args );

    assertEquals( "bar", cl.getOptionValue( "foo" ) );
  }




  @Test
  public void testSimpleLong() throws Exception {
    final String[] args = new String[] { "--enable-a", "--bfile", "toast", "foo", "bar" };

    final ArgumentList cl = parser.parse( options, args );

    assertTrue( "Confirm -a is set", cl.hasOption( "a" ) );
    assertTrue( "Confirm -b is set", cl.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", cl.getOptionValue( "b" ).equals( "toast" ) );
    assertTrue( "Confirm arg of --bfile", cl.getOptionValue( "bfile" ).equals( "toast" ) );
    assertTrue( "Confirm size of extra args", cl.getArgList().size() == 2 );
  }




  @Test
  public void testSimpleShort() throws Exception {
    final String[] args = new String[] { "-a", "-b", "toast", "foo", "bar" };

    final ArgumentList cl = parser.parse( options, args );

    assertTrue( "Confirm -a is set", cl.hasOption( "a" ) );
    assertTrue( "Confirm -b is set", cl.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", cl.getOptionValue( "b" ).equals( "toast" ) );
    assertTrue( "Confirm size of extra args", cl.getArgList().size() == 2 );
  }




  @Test
  public void testSingleDash() throws Exception {
    final String[] args = new String[] { "--copt", "-b", "-", "-a", "-" };

    final ArgumentList cl = parser.parse( options, args );

    assertTrue( "Confirm -a is set", cl.hasOption( "a" ) );
    assertTrue( "Confirm -b is set", cl.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", cl.getOptionValue( "b" ).equals( "-" ) );
    assertTrue( "Confirm 1 extra arg: " + cl.getArgList().size(), cl.getArgList().size() == 1 );
    assertTrue( "Confirm value of extra arg: " + cl.getArgList().get( 0 ), cl.getArgList().get( 0 ).equals( "-" ) );
  }




  @Test
  public void testStopAtExpectedArg() throws Exception {
    final String[] args = new String[] { "-b", "foo" };

    final ArgumentList cl = parser.parse( options, args, true );

    assertTrue( "Confirm -b is set", cl.hasOption( 'b' ) );
    assertEquals( "Confirm -b is set", "foo", cl.getOptionValue( 'b' ) );
    assertTrue( "Confirm no extra args: " + cl.getArgList().size(), cl.getArgList().size() == 0 );
  }




  @Test
  public void testStopAtNonOptionLong() throws Exception {
    final String[] args = new String[] { "--zop==1", "-abtoast", "--b=bar" };

    final ArgumentList cl = parser.parse( options, args, true );

    assertFalse( "Confirm -a is not set", cl.hasOption( "a" ) );
    assertFalse( "Confirm -b is not set", cl.hasOption( "b" ) );
    // TODO: assertTrue( "Confirm  3 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 3 );
  }




  @Test
  public void testStopAtNonOptionShort() throws Exception {
    final String[] args = new String[] { "-z", "-a", "-btoast" };

    final ArgumentList cl = parser.parse( options, args, true );
    assertFalse( "Confirm -a is not set", cl.hasOption( "a" ) );
    assertTrue( "Confirm  3 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 3 );
  }




  @Test
  public void testStopAtUnexpectedArg() throws Exception {
    final String[] args = new String[] { "-c", "foober", "-b", "toast" };

    final ArgumentList cl = parser.parse( options, args, true );
    assertTrue( "Confirm -c is set", cl.hasOption( "c" ) );
    assertTrue( "Confirm  3 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 3 );
  }




  @Test
  public void testUnrecognizedOption() throws Exception {
    final String[] args = new String[] { "-a", "-d", "-b", "toast", "foo", "bar" };

    try {
      parser.parse( options, args );
      fail( "UnrecognizedOptionException wasn't thrown" );
    } catch ( final UnrecognizedOptionException e ) {
      assertEquals( "-d", e.getOption() );
    }
  }
}
