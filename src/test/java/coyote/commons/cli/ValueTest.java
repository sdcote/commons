package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;


public class ValueTest {
  private static ArgumentList _al = null;
  private static Options opts = new Options();




  @BeforeClass
  public static void setUp() throws Exception {
    opts.addOption( "a", false, "toggle -a" );
    opts.addOption( "b", true, "set -b" );
    opts.addOption( "c", "c", false, "toggle -c" );
    opts.addOption( "d", "d", true, "set -d" );

    opts.addOption( OptionBuilder.hasOptionalArg().create( 'e' ) );
    opts.addOption( OptionBuilder.hasOptionalArg().withLongOpt( "fish" ).create() );
    opts.addOption( OptionBuilder.hasOptionalArgs().withLongOpt( "gravy" ).create() );
    opts.addOption( OptionBuilder.hasOptionalArgs( 2 ).withLongOpt( "hide" ).create() );
    opts.addOption( OptionBuilder.hasOptionalArgs( 2 ).create( 'i' ) );
    opts.addOption( OptionBuilder.hasOptionalArgs().create( 'j' ) );
    opts.addOption( OptionBuilder.hasArgs().withValueSeparator( ',' ).create( 'k' ) );

    final String[] args = new String[] { "-a", "-b", "foo", "--c", "--d", "bar" };

    final Parser parser = new PosixParser();
    _al = parser.parse( opts, args );
  }




  @Test
  public void testLongNoArg() {
    assertTrue( _al.hasOption( "c" ) );
    assertNull( _al.getOptionValue( "c" ) );
  }




  @Test
  public void testLongOptionalArgValue() throws Exception {
    final String[] args = new String[] { "--fish", "face" };

    final Parser parser = new PosixParser();
    final ArgumentList argList = parser.parse( opts, args );
    assertTrue( argList.hasOption( "fish" ) );
    assertEquals( "face", argList.getOptionValue( "fish" ) );
  }




  @Test
  public void testLongOptionalArgValues() throws Exception {
    final String[] args = new String[] { "--gravy", "gold", "garden" };

    final Parser parser = new PosixParser();
    final ArgumentList argList = parser.parse( opts, args );
    assertTrue( argList.hasOption( "gravy" ) );
    assertEquals( "gold", argList.getOptionValue( "gravy" ) );
    assertEquals( "gold", argList.getOptionValues( "gravy" )[0] );
    assertEquals( "garden", argList.getOptionValues( "gravy" )[1] );
    assertEquals( argList.getArgs().length, 0 );
  }




  @Test
  public void testLongOptionalNArgValues() throws Exception {
    final String[] args = new String[] { "--hide", "house", "hair", "head" };

    final Parser parser = new PosixParser();

    final ArgumentList argList = parser.parse( opts, args );
    assertTrue( argList.hasOption( "hide" ) );
    assertEquals( "house", argList.getOptionValue( "hide" ) );
    assertEquals( "house", argList.getOptionValues( "hide" )[0] );
    assertEquals( "hair", argList.getOptionValues( "hide" )[1] );
    assertEquals( argList.getArgs().length, 1 );
    assertEquals( "head", argList.getArgs()[0] );
  }




  @Test
  public void testLongOptionalNoValue() throws Exception {
    final String[] args = new String[] { "--fish" };

    final Parser parser = new PosixParser();
    final ArgumentList argList = parser.parse( opts, args );
    assertTrue( argList.hasOption( "fish" ) );
    assertNull( argList.getOptionValue( "fish" ) );
  }




  @Test
  public void testLongWithArg() {
    assertTrue( _al.hasOption( "d" ) );
    assertNotNull( _al.getOptionValue( "d" ) );
    assertEquals( _al.getOptionValue( "d" ), "bar" );
  }




  @Test
  public void testPropertyOptionFlags() throws Exception {
    Properties properties = new Properties();
    properties.setProperty( "a", "true" );
    properties.setProperty( "c", "yes" );
    properties.setProperty( "e", "1" );

    final Parser parser = new PosixParser();

    ArgumentList argList = parser.parse( opts, null, properties );
    assertTrue( argList.hasOption( "a" ) );
    assertTrue( argList.hasOption( "c" ) );
    assertTrue( argList.hasOption( "e" ) );

    properties = new Properties();
    properties.setProperty( "a", "false" );
    properties.setProperty( "c", "no" );
    properties.setProperty( "e", "0" );

    argList = parser.parse( opts, null, properties );
    assertTrue( !argList.hasOption( "a" ) );
    assertTrue( !argList.hasOption( "c" ) );
    assertTrue( !argList.hasOption( "e" ) );

    properties = new Properties();
    properties.setProperty( "a", "TRUE" );
    properties.setProperty( "c", "nO" );
    properties.setProperty( "e", "TrUe" );

    argList = parser.parse( opts, null, properties );
    assertTrue( argList.hasOption( "a" ) );
    assertTrue( !argList.hasOption( "c" ) );
    assertTrue( argList.hasOption( "e" ) );

    properties = new Properties();
    properties.setProperty( "a", "just a string" );
    properties.setProperty( "e", "" );

    argList = parser.parse( opts, null, properties );
    assertTrue( !argList.hasOption( "a" ) );
    assertTrue( !argList.hasOption( "c" ) );
    assertTrue( !argList.hasOption( "e" ) );
  }




  @Test
  public void testPropertyOptionMultipleValues() throws Exception {
    final Properties properties = new Properties();
    properties.setProperty( "k", "one,two" );

    final Parser parser = new PosixParser();

    final String[] values = new String[] { "one", "two" };

    final ArgumentList argList = parser.parse( opts, null, properties );
    assertTrue( argList.hasOption( "k" ) );
    assertTrue( Arrays.equals( values, argList.getOptionValues( 'k' ) ) );
  }




  @Test
  public void testPropertyOptionSingularValue() throws Exception {
    final Properties properties = new Properties();
    properties.setProperty( "hide", "seek" );

    final Parser parser = new PosixParser();

    final ArgumentList argList = parser.parse( opts, null, properties );
    assertTrue( argList.hasOption( "hide" ) );
    assertEquals( "seek", argList.getOptionValue( "hide" ) );
    assertTrue( !argList.hasOption( "fake" ) );
  }




  @Test
  public void testPropertyOverrideValues() throws Exception {
    final String[] args = new String[] { "-j", "found", "-i", "ink" };

    final Properties properties = new Properties();
    properties.setProperty( "j", "seek" );

    final Parser parser = new PosixParser();
    final ArgumentList argList = parser.parse( opts, args, properties );
    assertTrue( argList.hasOption( "j" ) );
    assertEquals( "found", argList.getOptionValue( "j" ) );
    assertTrue( argList.hasOption( "i" ) );
    assertEquals( "ink", argList.getOptionValue( "i" ) );
    assertTrue( !argList.hasOption( "fake" ) );
  }




  @Test
  public void testShortNoArg() {
    assertTrue( _al.hasOption( "a" ) );
    assertNull( _al.getOptionValue( "a" ) );
  }




  @Test
  public void testShortOptionalArgNoValue() throws Exception {
    final String[] args = new String[] { "-e" };

    final Parser parser = new PosixParser();
    final ArgumentList argList = parser.parse( opts, args );
    assertTrue( argList.hasOption( "e" ) );
    assertNull( argList.getOptionValue( "e" ) );
  }




  @Test
  public void testShortOptionalArgValue() throws Exception {
    final String[] args = new String[] { "-e", "everything" };

    final Parser parser = new PosixParser();
    final ArgumentList argList = parser.parse( opts, args );
    assertTrue( argList.hasOption( "e" ) );
    assertEquals( "everything", argList.getOptionValue( "e" ) );
  }




  @Test
  public void testShortOptionalArgValues() throws Exception {
    final String[] args = new String[] { "-j", "ink", "idea" };

    final Parser parser = new PosixParser();
    final ArgumentList argList = parser.parse( opts, args );
    assertTrue( argList.hasOption( "j" ) );
    assertEquals( "ink", argList.getOptionValue( "j" ) );
    assertEquals( "ink", argList.getOptionValues( "j" )[0] );
    assertEquals( "idea", argList.getOptionValues( "j" )[1] );
    assertEquals( argList.getArgs().length, 0 );
  }




  @Test
  public void testShortOptionalNArgValues() throws Exception {
    final String[] args = new String[] { "-i", "ink", "idea", "isotope", "ice" };

    final Parser parser = new PosixParser();
    final ArgumentList argList = parser.parse( opts, args );
    assertTrue( argList.hasOption( "i" ) );
    assertEquals( "ink", argList.getOptionValue( "i" ) );
    assertEquals( "ink", argList.getOptionValues( "i" )[0] );
    assertEquals( "idea", argList.getOptionValues( "i" )[1] );
    assertEquals( argList.getArgs().length, 2 );
    assertEquals( "isotope", argList.getArgs()[0] );
    assertEquals( "ice", argList.getArgs()[1] );
  }




  @Test
  public void testShortWithArg() {
    assertTrue( _al.hasOption( "b" ) );
    assertNotNull( _al.getOptionValue( "b" ) );
    assertEquals( _al.getOptionValue( "b" ), "foo" );
  }

}
