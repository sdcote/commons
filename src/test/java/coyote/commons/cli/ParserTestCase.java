package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

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
  public void testSimpleShort() throws Exception {
    String[] args = new String[] { "-a", "-b", "toast", "foo", "bar" };

    CommandLine cl = parser.parse( options, args );

    assertTrue( "Confirm -a is set", cl.hasOption( "a" ) );
    assertTrue( "Confirm -b is set", cl.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", cl.getOptionValue( "b" ).equals( "toast" ) );
    assertTrue( "Confirm size of extra args", cl.getArgList().size() == 2 );
  }




  @Test
  public void testSimpleLong() throws Exception {
    String[] args = new String[] { "--enable-a", "--bfile", "toast", "foo", "bar" };

    CommandLine cl = parser.parse( options, args );

    assertTrue( "Confirm -a is set", cl.hasOption( "a" ) );
    assertTrue( "Confirm -b is set", cl.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", cl.getOptionValue( "b" ).equals( "toast" ) );
    assertTrue( "Confirm arg of --bfile", cl.getOptionValue( "bfile" ).equals( "toast" ) );
    assertTrue( "Confirm size of extra args", cl.getArgList().size() == 2 );
  }




  @Test
  public void testMultiple() throws Exception {
    String[] args = new String[] { "-c", "foobar", "-b", "toast" };

    CommandLine cl = parser.parse( options, args, true );
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
    String[] args = new String[] { "--copt", "foobar", "--bfile", "toast" };

    CommandLine cl = parser.parse( options, args, true );
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
  public void testUnrecognizedOption() throws Exception {
    String[] args = new String[] { "-a", "-d", "-b", "toast", "foo", "bar" };

    try {
      parser.parse( options, args );
      fail( "UnrecognizedOptionException wasn't thrown" );
    } catch ( UnrecognizedOptionException e ) {
      assertEquals( "-d", e.getOption() );
    }
  }




  @Test
  public void testMissingArg() throws Exception {
    String[] args = new String[] { "-b" };

    boolean caught = false;

    try {
      parser.parse( options, args );
    } catch ( MissingArgumentException e ) {
      caught = true;
      assertEquals( "option missing an argument", "b", e.getOption().getOpt() );
    }

    assertTrue( "Confirm MissingArgumentException caught", caught );
  }




  @Test
  public void testDoubleDash() throws Exception {
    String[] args = new String[] { "--copt", "--", "-b", "toast" };

    CommandLine cl = parser.parse( options, args );

    assertTrue( "Confirm -c is set", cl.hasOption( "c" ) );
    assertTrue( "Confirm -b is not set", !cl.hasOption( "b" ) );
    assertTrue( "Confirm 2 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 2 );
  }




  @Test
  public void testSingleDash() throws Exception {
    String[] args = new String[] { "--copt", "-b", "-", "-a", "-" };

    CommandLine cl = parser.parse( options, args );

    assertTrue( "Confirm -a is set", cl.hasOption( "a" ) );
    assertTrue( "Confirm -b is set", cl.hasOption( "b" ) );
    assertTrue( "Confirm arg of -b", cl.getOptionValue( "b" ).equals( "-" ) );
    assertTrue( "Confirm 1 extra arg: " + cl.getArgList().size(), cl.getArgList().size() == 1 );
    assertTrue( "Confirm value of extra arg: " + cl.getArgList().get( 0 ), cl.getArgList().get( 0 ).equals( "-" ) );
  }




  @Test
  public void testStopAtUnexpectedArg() throws Exception {
    String[] args = new String[] { "-c", "foober", "-b", "toast" };

    CommandLine cl = parser.parse( options, args, true );
    assertTrue( "Confirm -c is set", cl.hasOption( "c" ) );
    assertTrue( "Confirm  3 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 3 );
  }




  @Test
  public void testStopAtExpectedArg() throws Exception {
    String[] args = new String[] { "-b", "foo" };

    CommandLine cl = parser.parse( options, args, true );

    assertTrue( "Confirm -b is set", cl.hasOption( 'b' ) );
    assertEquals( "Confirm -b is set", "foo", cl.getOptionValue( 'b' ) );
    assertTrue( "Confirm no extra args: " + cl.getArgList().size(), cl.getArgList().size() == 0 );
  }




  @Test
  public void testStopAtNonOptionShort() throws Exception {
    String[] args = new String[] { "-z", "-a", "-btoast" };

    CommandLine cl = parser.parse( options, args, true );
    assertFalse( "Confirm -a is not set", cl.hasOption( "a" ) );
    assertTrue( "Confirm  3 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 3 );
  }




  @Test
  public void testStopAtNonOptionLong() throws Exception {
    String[] args = new String[] { "--zop==1", "-abtoast", "--b=bar" };

    CommandLine cl = parser.parse( options, args, true );

    assertFalse( "Confirm -a is not set", cl.hasOption( "a" ) );
    assertFalse( "Confirm -b is not set", cl.hasOption( "b" ) );
    // TODO: assertTrue( "Confirm  3 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 3 );
  }




  @Test
  public void testNegativeArgument() throws Exception {
    String[] args = new String[] { "-b", "-1" };

    CommandLine cl = parser.parse( options, args );
    assertEquals( "-1", cl.getOptionValue( "b" ) );
  }




  @Test
  public void testArgumentStartingWithHyphen() throws Exception {
    String[] args = new String[] { "-b", "-foo" };

    CommandLine cl = parser.parse( options, args );
    assertEquals( "-foo", cl.getOptionValue( "b" ) );
  }




  @Test
  public void testShortWithEqual() throws Exception {
    String[] args = new String[] { "-f=bar" };

    Options options = new Options();
    options.addOption( OptionBuilder.withLongOpt( "foo" ).hasArg().create( 'f' ) );

    CommandLine cl = parser.parse( options, args );

    assertEquals( "bar", cl.getOptionValue( "foo" ) );
  }




  @Test
  public void testShortWithoutEqual() throws Exception {
    String[] args = new String[] { "-fbar" };

    Options options = new Options();
    options.addOption( OptionBuilder.withLongOpt( "foo" ).hasArg().create( 'f' ) );

    CommandLine cl = parser.parse( options, args );

    assertEquals( "bar", cl.getOptionValue( "foo" ) );
  }




  @Test
  public void testLongWithEqual() throws Exception {
    String[] args = new String[] { "--foo=bar" };

    Options options = new Options();
    options.addOption( OptionBuilder.withLongOpt( "foo" ).hasArg().create( 'f' ) );

    CommandLine cl = parser.parse( options, args );

    assertEquals( "bar", cl.getOptionValue( "foo" ) );
  }




  @Test
  public void testLongWithEqualSingleDash() throws Exception {
    String[] args = new String[] { "-foo=bar" };

    Options options = new Options();
    options.addOption( OptionBuilder.withLongOpt( "foo" ).hasArg().create( 'f' ) );

    CommandLine cl = parser.parse( options, args );

    assertEquals( "bar", cl.getOptionValue( "foo" ) );
  }




  @Test
  public void testPropertiesOption() throws Exception {
    String[] args = new String[] { "-Jsource=1.5", "-J", "target", "1.5", "foo" };

    Options options = new Options();
    options.addOption( OptionBuilder.withValueSeparator().hasArgs( 2 ).create( 'J' ) );

    CommandLine cl = parser.parse( options, args );

    List values = Arrays.asList( cl.getOptionValues( "J" ) );
    assertNotNull( "null values", values );
    assertEquals( "number of values", 4, values.size() );
    assertEquals( "value 1", "source", values.get( 0 ) );
    assertEquals( "value 2", "1.5", values.get( 1 ) );
    assertEquals( "value 3", "target", values.get( 2 ) );
    assertEquals( "value 4", "1.5", values.get( 3 ) );
    List argsleft = cl.getArgList();
    assertEquals( "Should be 1 arg left", 1, argsleft.size() );
    assertEquals( "Expecting foo", "foo", argsleft.get( 0 ) );
  }
}
