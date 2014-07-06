package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;


public class ValuesTest {
  /** CommandLine instance */
  private static CommandLine _cmdline = null;




  @BeforeClass
  public static void setUp() throws Exception {
    Options options = new Options();

    options.addOption( "a", false, "toggle -a" );
    options.addOption( "b", true, "set -b" );
    options.addOption( "c", "c", false, "toggle -c" );
    options.addOption( "d", "d", true, "set -d" );

    options.addOption( OptionBuilder.withLongOpt( "e" ).hasArgs().withDescription( "set -e " ).create( 'e' ) );
    options.addOption( "f", "f", false, "jk" );
    options.addOption( OptionBuilder.withLongOpt( "g" ).hasArgs( 2 ).withDescription( "set -g" ).create( 'g' ) );
    options.addOption( OptionBuilder.withLongOpt( "h" ).hasArgs( 2 ).withDescription( "set -h" ).create( 'h' ) );
    options.addOption( OptionBuilder.withLongOpt( "i" ).withDescription( "set -i" ).create( 'i' ) );
    options.addOption( OptionBuilder.withLongOpt( "j" ).hasArgs().withDescription( "set -j" ).withValueSeparator( '=' ).create( 'j' ) );
    options.addOption( OptionBuilder.withLongOpt( "k" ).hasArgs().withDescription( "set -k" ).withValueSeparator( '=' ).create( 'k' ) );
    options.addOption( OptionBuilder.withLongOpt( "m" ).hasArgs().withDescription( "set -m" ).withValueSeparator().create( 'm' ) );

    String[] args = new String[] { "-a", "-b", "foo", "--c", "--d", "bar", "-e", "one", "two", "-f", "arg1", "arg2", "-g", "val1", "val2", "arg3", "-h", "val1", "-i", "-h", "val2", "-jkey=value", "-j", "key=value", "-kkey1=value1", "-kkey2=value2", "-mkey=value" };

    CommandLineParser parser = new PosixParser();

    _cmdline = parser.parse( options, args );
  }




  @Test
  public void testShortArgs() {
    assertTrue( _cmdline.hasOption( "a" ) );
    assertTrue( _cmdline.hasOption( "c" ) );

    assertNull( _cmdline.getOptionValues( "a" ) );
    assertNull( _cmdline.getOptionValues( "c" ) );
  }




  @Test
  public void testShortArgsWithValue() {
    assertTrue( _cmdline.hasOption( "b" ) );
    assertTrue( _cmdline.getOptionValue( "b" ).equals( "foo" ) );
    assertEquals( 1, _cmdline.getOptionValues( "b" ).length );

    assertTrue( _cmdline.hasOption( "d" ) );
    assertTrue( _cmdline.getOptionValue( "d" ).equals( "bar" ) );
    assertEquals( 1, _cmdline.getOptionValues( "d" ).length );
  }




  @Test
  public void testMultipleArgValues() {
    String[] result = _cmdline.getOptionValues( "e" );
    String[] values = new String[] { "one", "two" };
    assertTrue( _cmdline.hasOption( "e" ) );
    assertEquals( 2, _cmdline.getOptionValues( "e" ).length );
    assertTrue( Arrays.equals( values, _cmdline.getOptionValues( "e" ) ) );
  }




  @Test
  public void testTwoArgValues() {
    String[] result = _cmdline.getOptionValues( "g" );
    String[] values = new String[] { "val1", "val2" };
    assertTrue( _cmdline.hasOption( "g" ) );
    assertEquals( 2, _cmdline.getOptionValues( "g" ).length );
    assertTrue( Arrays.equals( values, _cmdline.getOptionValues( "g" ) ) );
  }




  @Test
  public void testComplexValues() {
    String[] result = _cmdline.getOptionValues( "h" );
    String[] values = new String[] { "val1", "val2" };
    assertTrue( _cmdline.hasOption( "i" ) );
    assertTrue( _cmdline.hasOption( "h" ) );
    assertEquals( 2, _cmdline.getOptionValues( "h" ).length );
    assertTrue( Arrays.equals( values, _cmdline.getOptionValues( "h" ) ) );
  }




  @Test
  public void testExtraArgs() {
    String[] args = new String[] { "arg1", "arg2", "arg3" };
    assertEquals( 3, _cmdline.getArgs().length );
    assertTrue( Arrays.equals( args, _cmdline.getArgs() ) );
  }




  @Test
  public void testCharSeparator() {
    // tests the char methods of CommandLine that delegate to
    // the String methods
    String[] values = new String[] { "key", "value", "key", "value" };
    assertTrue( _cmdline.hasOption( "j" ) );
    assertTrue( _cmdline.hasOption( 'j' ) );
    assertEquals( 4, _cmdline.getOptionValues( "j" ).length );
    assertEquals( 4, _cmdline.getOptionValues( 'j' ).length );
    assertTrue( Arrays.equals( values, _cmdline.getOptionValues( "j" ) ) );
    assertTrue( Arrays.equals( values, _cmdline.getOptionValues( 'j' ) ) );

    values = new String[] { "key1", "value1", "key2", "value2" };
    assertTrue( _cmdline.hasOption( "k" ) );
    assertTrue( _cmdline.hasOption( 'k' ) );
    assertEquals( 4, _cmdline.getOptionValues( "k" ).length );
    assertEquals( 4, _cmdline.getOptionValues( 'k' ).length );
    assertTrue( Arrays.equals( values, _cmdline.getOptionValues( "k" ) ) );
    assertTrue( Arrays.equals( values, _cmdline.getOptionValues( 'k' ) ) );

    values = new String[] { "key", "value" };
    assertTrue( _cmdline.hasOption( "m" ) );
    assertTrue( _cmdline.hasOption( 'm' ) );
    assertEquals( 2, _cmdline.getOptionValues( "m" ).length );
    assertEquals( 2, _cmdline.getOptionValues( 'm' ).length );
    assertTrue( Arrays.equals( values, _cmdline.getOptionValues( "m" ) ) );
    assertTrue( Arrays.equals( values, _cmdline.getOptionValues( 'm' ) ) );
  }

}
