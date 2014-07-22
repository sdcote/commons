package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 * 
 */
public class OptionGroupTest {
  private static Options _options = null;
  private static ArgumentParser parser = new PosixParser();




  @BeforeClass
  public static void setUp() {
    final Option file = new Option( "f", "file", false, "file to process" );
    final Option dir = new Option( "d", "directory", false, "directory to process" );
    final OptionGroup group = new OptionGroup();
    group.addOption( file );
    group.addOption( dir );
    _options = new Options().addOptionGroup( group );

    final Option section = new Option( "s", "section", false, "section to process" );
    final Option chapter = new Option( "c", "chapter", false, "chapter to process" );
    final OptionGroup group2 = new OptionGroup();
    group2.addOption( section );
    group2.addOption( chapter );

    _options.addOptionGroup( group2 );

    final Option importOpt = new Option( null, "import", false, "section to process" );
    final Option exportOpt = new Option( null, "export", false, "chapter to process" );
    final OptionGroup group3 = new OptionGroup();
    group3.addOption( importOpt );
    group3.addOption( exportOpt );
    _options.addOptionGroup( group3 );

    _options.addOption( "r", "revision", false, "revision number" );
  }




  @Test
  public void testGetNames() {
    final OptionGroup group = new OptionGroup();
    group.addOption( OptionBuilder.create( 'a' ) );
    group.addOption( OptionBuilder.create( 'b' ) );

    assertNotNull( "null names", group.getNames() );
    assertEquals( 2, group.getNames().size() );
    assertTrue( group.getNames().contains( "a" ) );
    assertTrue( group.getNames().contains( "b" ) );
  }




  @Test
  public void testNoOptionsExtraArgs() throws Exception {
    final String[] args = new String[] { "arg1", "arg2" };

    final ArgumentList argList = parser.parse( _options, args );

    assertTrue( "Confirm -r is NOT set", !argList.hasOption( "r" ) );
    assertTrue( "Confirm -f is NOT set", !argList.hasOption( "f" ) );
    assertTrue( "Confirm -d is NOT set", !argList.hasOption( "d" ) );
    assertTrue( "Confirm -s is NOT set", !argList.hasOption( "s" ) );
    assertTrue( "Confirm -c is NOT set", !argList.hasOption( "c" ) );
    assertTrue( "Confirm TWO extra args", argList.getArgList().size() == 2 );
  }




  @Test
  public void testSingleLongOption() throws Exception {
    final String[] args = new String[] { "--file" };

    final ArgumentList argList = parser.parse( _options, args );

    assertTrue( "Confirm -r is NOT set", !argList.hasOption( "r" ) );
    assertTrue( "Confirm -f is set", argList.hasOption( "f" ) );
    assertTrue( "Confirm -d is NOT set", !argList.hasOption( "d" ) );
    assertTrue( "Confirm -s is NOT set", !argList.hasOption( "s" ) );
    assertTrue( "Confirm -c is NOT set", !argList.hasOption( "c" ) );
    assertTrue( "Confirm no extra args", argList.getArgList().size() == 0 );
  }




  @Test
  public void testSingleOption() throws Exception {
    final String[] args = new String[] { "-r" };

    final ArgumentList argList = parser.parse( _options, args );

    assertTrue( "Confirm -r is set", argList.hasOption( "r" ) );
    assertTrue( "Confirm -f is NOT set", !argList.hasOption( "f" ) );
    assertTrue( "Confirm -d is NOT set", !argList.hasOption( "d" ) );
    assertTrue( "Confirm -s is NOT set", !argList.hasOption( "s" ) );
    assertTrue( "Confirm -c is NOT set", !argList.hasOption( "c" ) );
    assertTrue( "Confirm no extra args", argList.getArgList().size() == 0 );
  }




  @Test
  public void testSingleOptionFromGroup() throws Exception {
    final String[] args = new String[] { "-f" };

    final ArgumentList argList = parser.parse( _options, args );

    assertTrue( "Confirm -r is NOT set", !argList.hasOption( "r" ) );
    assertTrue( "Confirm -f is set", argList.hasOption( "f" ) );
    assertTrue( "Confirm -d is NOT set", !argList.hasOption( "d" ) );
    assertTrue( "Confirm -s is NOT set", !argList.hasOption( "s" ) );
    assertTrue( "Confirm -c is NOT set", !argList.hasOption( "c" ) );
    assertTrue( "Confirm no extra args", argList.getArgList().size() == 0 );
  }




  @Test
  public void testToString() {
    final OptionGroup group1 = new OptionGroup();
    group1.addOption( new Option( null, "foo", false, "Foo" ) );
    group1.addOption( new Option( null, "bar", false, "Bar" ) );

    if ( !"{--bar Bar, --foo Foo}".equals( group1.toString() ) ) {
      assertEquals( "{--foo Foo, --bar Bar}", group1.toString() );
    }

    final OptionGroup group2 = new OptionGroup();
    group2.addOption( new Option( "f", "foo", false, "Foo" ) );
    group2.addOption( new Option( "b", "bar", false, "Bar" ) );

    if ( !"{-b Bar, -f Foo}".equals( group2.toString() ) ) {
      assertEquals( "{-f Foo, -b Bar}", group2.toString() );
    }
  }




  @Test
  public void testTwoLongOptionsFromGroup() throws Exception {
    final String[] args = new String[] { "--file", "--directory" };

    try {
      parser.parse( _options, args );
      fail( "two arguments from group not allowed" );
    } catch ( final AlreadySelectedException e ) {
      assertNotNull( "null option group", e.getOptionGroup() );
      assertEquals( "selected option", "f", e.getOptionGroup().getSelected() );
      assertEquals( "option", "d", e.getOption().getOpt() );
    }
  }




  @Test
  public void testTwoOptionsFromDifferentGroup() throws Exception {
    final String[] args = new String[] { "-f", "-s" };

    final ArgumentList argList = parser.parse( _options, args );
    assertTrue( "Confirm -r is NOT set", !argList.hasOption( "r" ) );
    assertTrue( "Confirm -f is set", argList.hasOption( "f" ) );
    assertTrue( "Confirm -d is NOT set", !argList.hasOption( "d" ) );
    assertTrue( "Confirm -s is set", argList.hasOption( "s" ) );
    assertTrue( "Confirm -c is NOT set", !argList.hasOption( "c" ) );
    assertTrue( "Confirm NO extra args", argList.getArgList().size() == 0 );
  }




  @Test
  public void testTwoOptionsFromGroup() throws Exception {
    final String[] args = new String[] { "-f", "-d" };

    try {
      parser.parse( _options, args );
      fail( "two arguments from group not allowed" );
    } catch ( final AlreadySelectedException e ) {
      assertNotNull( "null option group", e.getOptionGroup() );
      assertEquals( "selected option", "f", e.getOptionGroup().getSelected() );
      assertEquals( "option", "d", e.getOption().getOpt() );
    }
  }




  @Test
  public void testTwoValidLongOptions() throws Exception {
    final String[] args = new String[] { "--revision", "--file" };

    final ArgumentList argList = parser.parse( _options, args );

    assertTrue( "Confirm -r is set", argList.hasOption( "r" ) );
    assertTrue( "Confirm -f is set", argList.hasOption( "f" ) );
    assertTrue( "Confirm -d is NOT set", !argList.hasOption( "d" ) );
    assertTrue( "Confirm -s is NOT set", !argList.hasOption( "s" ) );
    assertTrue( "Confirm -c is NOT set", !argList.hasOption( "c" ) );
    assertTrue( "Confirm no extra args", argList.getArgList().size() == 0 );
  }




  @Test
  public void testTwoValidOptions() throws Exception {
    final String[] args = new String[] { "-r", "-f" };

    final ArgumentList argList = parser.parse( _options, args );

    assertTrue( "Confirm -r is set", argList.hasOption( "r" ) );
    assertTrue( "Confirm -f is set", argList.hasOption( "f" ) );
    assertTrue( "Confirm -d is NOT set", !argList.hasOption( "d" ) );
    assertTrue( "Confirm -s is NOT set", !argList.hasOption( "s" ) );
    assertTrue( "Confirm -c is NOT set", !argList.hasOption( "c" ) );
    assertTrue( "Confirm no extra args", argList.getArgList().size() == 0 );
  }




  @Test
  public void testValidLongOnlyOptions() throws Exception {
    final ArgumentList cl1 = parser.parse( _options, new String[] { "--export" } );
    assertTrue( "Confirm --export is set", cl1.hasOption( "export" ) );

    final ArgumentList cl2 = parser.parse( _options, new String[] { "--import" } );
    assertTrue( "Confirm --import is set", cl2.hasOption( "import" ) );
  }
}
