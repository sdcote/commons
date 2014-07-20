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
  private static ArgumentList _cl = null;
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
    _cl = parser.parse( opts, args );
  }




  @Test
  public void testLongNoArg() {
    assertTrue( _cl.hasOption( "c" ) );
    assertNull( _cl.getOptionValue( "c" ) );
  }




  @Test
  public void testLongOptionalArgValue() throws Exception {
    final String[] args = new String[] { "--fish", "face" };

    final Parser parser = new PosixParser();
    final ArgumentList cmd = parser.parse( opts, args );
    assertTrue( cmd.hasOption( "fish" ) );
    assertEquals( "face", cmd.getOptionValue( "fish" ) );
  }




  @Test
  public void testLongOptionalArgValues() throws Exception {
    final String[] args = new String[] { "--gravy", "gold", "garden" };

    final Parser parser = new PosixParser();
    final ArgumentList cmd = parser.parse( opts, args );
    assertTrue( cmd.hasOption( "gravy" ) );
    assertEquals( "gold", cmd.getOptionValue( "gravy" ) );
    assertEquals( "gold", cmd.getOptionValues( "gravy" )[0] );
    assertEquals( "garden", cmd.getOptionValues( "gravy" )[1] );
    assertEquals( cmd.getArgs().length, 0 );
  }




  @Test
  public void testLongOptionalNArgValues() throws Exception {
    final String[] args = new String[] { "--hide", "house", "hair", "head" };

    final Parser parser = new PosixParser();

    final ArgumentList cmd = parser.parse( opts, args );
    assertTrue( cmd.hasOption( "hide" ) );
    assertEquals( "house", cmd.getOptionValue( "hide" ) );
    assertEquals( "house", cmd.getOptionValues( "hide" )[0] );
    assertEquals( "hair", cmd.getOptionValues( "hide" )[1] );
    assertEquals( cmd.getArgs().length, 1 );
    assertEquals( "head", cmd.getArgs()[0] );
  }




  @Test
  public void testLongOptionalNoValue() throws Exception {
    final String[] args = new String[] { "--fish" };

    final Parser parser = new PosixParser();
    final ArgumentList cmd = parser.parse( opts, args );
    assertTrue( cmd.hasOption( "fish" ) );
    assertNull( cmd.getOptionValue( "fish" ) );
  }




  @Test
  public void testLongWithArg() {
    assertTrue( _cl.hasOption( "d" ) );
    assertNotNull( _cl.getOptionValue( "d" ) );
    assertEquals( _cl.getOptionValue( "d" ), "bar" );
  }




  @Test
  public void testPropertyOptionFlags() throws Exception {
    Properties properties = new Properties();
    properties.setProperty( "a", "true" );
    properties.setProperty( "c", "yes" );
    properties.setProperty( "e", "1" );

    final Parser parser = new PosixParser();

    ArgumentList cmd = parser.parse( opts, null, properties );
    assertTrue( cmd.hasOption( "a" ) );
    assertTrue( cmd.hasOption( "c" ) );
    assertTrue( cmd.hasOption( "e" ) );

    properties = new Properties();
    properties.setProperty( "a", "false" );
    properties.setProperty( "c", "no" );
    properties.setProperty( "e", "0" );

    cmd = parser.parse( opts, null, properties );
    assertTrue( !cmd.hasOption( "a" ) );
    assertTrue( !cmd.hasOption( "c" ) );
    assertTrue( !cmd.hasOption( "e" ) );

    properties = new Properties();
    properties.setProperty( "a", "TRUE" );
    properties.setProperty( "c", "nO" );
    properties.setProperty( "e", "TrUe" );

    cmd = parser.parse( opts, null, properties );
    assertTrue( cmd.hasOption( "a" ) );
    assertTrue( !cmd.hasOption( "c" ) );
    assertTrue( cmd.hasOption( "e" ) );

    properties = new Properties();
    properties.setProperty( "a", "just a string" );
    properties.setProperty( "e", "" );

    cmd = parser.parse( opts, null, properties );
    assertTrue( !cmd.hasOption( "a" ) );
    assertTrue( !cmd.hasOption( "c" ) );
    assertTrue( !cmd.hasOption( "e" ) );
  }




  @Test
  public void testPropertyOptionMultipleValues() throws Exception {
    final Properties properties = new Properties();
    properties.setProperty( "k", "one,two" );

    final Parser parser = new PosixParser();

    final String[] values = new String[] { "one", "two" };

    final ArgumentList cmd = parser.parse( opts, null, properties );
    assertTrue( cmd.hasOption( "k" ) );
    assertTrue( Arrays.equals( values, cmd.getOptionValues( 'k' ) ) );
  }




  @Test
  public void testPropertyOptionSingularValue() throws Exception {
    final Properties properties = new Properties();
    properties.setProperty( "hide", "seek" );

    final Parser parser = new PosixParser();

    final ArgumentList cmd = parser.parse( opts, null, properties );
    assertTrue( cmd.hasOption( "hide" ) );
    assertEquals( "seek", cmd.getOptionValue( "hide" ) );
    assertTrue( !cmd.hasOption( "fake" ) );
  }




  @Test
  public void testPropertyOverrideValues() throws Exception {
    final String[] args = new String[] { "-j", "found", "-i", "ink" };

    final Properties properties = new Properties();
    properties.setProperty( "j", "seek" );

    final Parser parser = new PosixParser();
    final ArgumentList cmd = parser.parse( opts, args, properties );
    assertTrue( cmd.hasOption( "j" ) );
    assertEquals( "found", cmd.getOptionValue( "j" ) );
    assertTrue( cmd.hasOption( "i" ) );
    assertEquals( "ink", cmd.getOptionValue( "i" ) );
    assertTrue( !cmd.hasOption( "fake" ) );
  }




  @Test
  public void testShortNoArg() {
    assertTrue( _cl.hasOption( "a" ) );
    assertNull( _cl.getOptionValue( "a" ) );
  }




  @Test
  public void testShortOptionalArgNoValue() throws Exception {
    final String[] args = new String[] { "-e" };

    final Parser parser = new PosixParser();
    final ArgumentList cmd = parser.parse( opts, args );
    assertTrue( cmd.hasOption( "e" ) );
    assertNull( cmd.getOptionValue( "e" ) );
  }




  @Test
  public void testShortOptionalArgValue() throws Exception {
    final String[] args = new String[] { "-e", "everything" };

    final Parser parser = new PosixParser();
    final ArgumentList cmd = parser.parse( opts, args );
    assertTrue( cmd.hasOption( "e" ) );
    assertEquals( "everything", cmd.getOptionValue( "e" ) );
  }




  @Test
  public void testShortOptionalArgValues() throws Exception {
    final String[] args = new String[] { "-j", "ink", "idea" };

    final Parser parser = new PosixParser();
    final ArgumentList cmd = parser.parse( opts, args );
    assertTrue( cmd.hasOption( "j" ) );
    assertEquals( "ink", cmd.getOptionValue( "j" ) );
    assertEquals( "ink", cmd.getOptionValues( "j" )[0] );
    assertEquals( "idea", cmd.getOptionValues( "j" )[1] );
    assertEquals( cmd.getArgs().length, 0 );
  }




  @Test
  public void testShortOptionalNArgValues() throws Exception {
    final String[] args = new String[] { "-i", "ink", "idea", "isotope", "ice" };

    final Parser parser = new PosixParser();
    final ArgumentList cmd = parser.parse( opts, args );
    assertTrue( cmd.hasOption( "i" ) );
    assertEquals( "ink", cmd.getOptionValue( "i" ) );
    assertEquals( "ink", cmd.getOptionValues( "i" )[0] );
    assertEquals( "idea", cmd.getOptionValues( "i" )[1] );
    assertEquals( cmd.getArgs().length, 2 );
    assertEquals( "isotope", cmd.getArgs()[0] );
    assertEquals( "ice", cmd.getArgs()[1] );
  }




  @Test
  public void testShortWithArg() {
    assertTrue( _cl.hasOption( "b" ) );
    assertNotNull( _cl.getOptionValue( "b" ) );
    assertEquals( _cl.getOptionValue( "b" ), "foo" );
  }

}
