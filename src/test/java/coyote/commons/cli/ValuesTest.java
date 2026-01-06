package coyote.commons.cli;

//import static org.junit.Assert.*;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


public class ValuesTest {
  /** ArgumentList instance */
  private static ArgumentList _argList = null;




  @BeforeAll
  public static void setUp() throws Exception {
    final Options options = new Options();

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

    final String[] args = new String[] { "-a", "-b", "foo", "--c", "--d", "bar", "-e", "one", "two", "-f", "arg1", "arg2", "-g", "val1", "val2", "arg3", "-h", "val1", "-i", "-h", "val2", "-jkey=value", "-j", "key=value", "-kkey1=value1", "-kkey2=value2", "-mkey=value" };

    final ArgumentParser parser = new PosixParser();

    _argList = parser.parse( options, args );
  }




  @Test
  public void testCharSeparator() {
    // tests the char methods of ArgumentList that delegate to
    // the String methods
    String[] values = new String[] { "key", "value", "key", "value" };
    assertTrue( _argList.hasOption( "j" ) );
    assertTrue( _argList.hasOption( 'j' ) );
    assertEquals( 4, _argList.getOptionValues( "j" ).length );
    assertEquals( 4, _argList.getOptionValues( 'j' ).length );
    assertTrue( Arrays.equals( values, _argList.getOptionValues( "j" ) ) );
    assertTrue( Arrays.equals( values, _argList.getOptionValues( 'j' ) ) );

    values = new String[] { "key1", "value1", "key2", "value2" };
    assertTrue( _argList.hasOption( "k" ) );
    assertTrue( _argList.hasOption( 'k' ) );
    assertEquals( 4, _argList.getOptionValues( "k" ).length );
    assertEquals( 4, _argList.getOptionValues( 'k' ).length );
    assertTrue( Arrays.equals( values, _argList.getOptionValues( "k" ) ) );
    assertTrue( Arrays.equals( values, _argList.getOptionValues( 'k' ) ) );

    values = new String[] { "key", "value" };
    assertTrue( _argList.hasOption( "m" ) );
    assertTrue( _argList.hasOption( 'm' ) );
    assertEquals( 2, _argList.getOptionValues( "m" ).length );
    assertEquals( 2, _argList.getOptionValues( 'm' ).length );
    assertTrue( Arrays.equals( values, _argList.getOptionValues( "m" ) ) );
    assertTrue( Arrays.equals( values, _argList.getOptionValues( 'm' ) ) );
  }




  @Test
  public void testComplexValues() {
    _argList.getOptionValues( "h" );
    final String[] values = new String[] { "val1", "val2" };
    assertTrue( _argList.hasOption( "i" ) );
    assertTrue( _argList.hasOption( "h" ) );
    assertEquals( 2, _argList.getOptionValues( "h" ).length );
    assertTrue( Arrays.equals( values, _argList.getOptionValues( "h" ) ) );
  }




  @Test
  public void testExtraArgs() {
    final String[] args = new String[] { "arg1", "arg2", "arg3" };
    assertEquals( 3, _argList.getArgs().length );
    assertTrue( Arrays.equals( args, _argList.getArgs() ) );
  }




  @Test
  public void testMultipleArgValues() {
    _argList.getOptionValues( "e" );
    final String[] values = new String[] { "one", "two" };
    assertTrue( _argList.hasOption( "e" ) );
    assertEquals( 2, _argList.getOptionValues( "e" ).length );
    assertTrue( Arrays.equals( values, _argList.getOptionValues( "e" ) ) );
  }




  @Test
  public void testShortArgs() {
    assertTrue( _argList.hasOption( "a" ) );
    assertTrue( _argList.hasOption( "c" ) );

    assertNull( _argList.getOptionValues( "a" ) );
    assertNull( _argList.getOptionValues( "c" ) );
  }




  @Test
  public void testShortArgsWithValue() {
    assertTrue( _argList.hasOption( "b" ) );
    assertTrue( _argList.getOptionValue( "b" ).equals( "foo" ) );
    assertEquals( 1, _argList.getOptionValues( "b" ).length );

    assertTrue( _argList.hasOption( "d" ) );
    assertTrue( _argList.getOptionValue( "d" ).equals( "bar" ) );
    assertEquals( 1, _argList.getOptionValues( "d" ).length );
  }




  @Test
  public void testTwoArgValues() {
    _argList.getOptionValues( "g" );
    final String[] values = new String[] { "val1", "val2" };
    assertTrue( _argList.hasOption( "g" ) );
    assertEquals( 2, _argList.getOptionValues( "g" ).length );
    assertTrue( Arrays.equals( values, _argList.getOptionValues( "g" ) ) );
  }

}
