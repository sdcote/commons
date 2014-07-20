package coyote.commons.cli;

//import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Test;


/**
 * 
 */
public class OptionsTest extends TestCase {
  @Test
  public void testDuplicateLong() {
    final Options opts = new Options();
    opts.addOption( "a", "--a", false, "toggle -a" );
    opts.addOption( "a", "--a", false, "toggle -a*" );
    assertEquals( "last one in wins", "toggle -a*", opts.getOption( "a" ).getDescription() );
  }




  @Test
  public void testDuplicateSimple() {
    final Options opts = new Options();
    opts.addOption( "a", false, "toggle -a" );
    opts.addOption( "a", true, "toggle -a*" );

    assertEquals( "last one in wins", "toggle -a*", opts.getOption( "a" ).getDescription() );
  }




  @Test
  public void testGetOptionsGroups() {
    final Options options = new Options();

    final OptionGroup group1 = new OptionGroup();
    group1.addOption( OptionBuilder.create( 'a' ) );
    group1.addOption( OptionBuilder.create( 'b' ) );

    final OptionGroup group2 = new OptionGroup();
    group2.addOption( OptionBuilder.create( 'x' ) );
    group2.addOption( OptionBuilder.create( 'y' ) );

    options.addOptionGroup( group1 );
    options.addOptionGroup( group2 );

    assertNotNull( options.getOptionGroups() );
    assertEquals( 2, options.getOptionGroups().size() );
  }




  @Test
  public void testHelpOptions() {
    OptionBuilder.withLongOpt( "long-only1" );
    final Option longOnly1 = OptionBuilder.create();
    OptionBuilder.withLongOpt( "long-only2" );
    final Option longOnly2 = OptionBuilder.create();
    final Option shortOnly1 = OptionBuilder.create( "1" );
    final Option shortOnly2 = OptionBuilder.create( "2" );
    OptionBuilder.withLongOpt( "bothA" );
    final Option bothA = OptionBuilder.create( "a" );
    OptionBuilder.withLongOpt( "bothB" );
    final Option bothB = OptionBuilder.create( "b" );

    final Options options = new Options();
    options.addOption( longOnly1 );
    options.addOption( longOnly2 );
    options.addOption( shortOnly1 );
    options.addOption( shortOnly2 );
    options.addOption( bothA );
    options.addOption( bothB );

    final Collection allOptions = new ArrayList();
    allOptions.add( longOnly1 );
    allOptions.add( longOnly2 );
    allOptions.add( shortOnly1 );
    allOptions.add( shortOnly2 );
    allOptions.add( bothA );
    allOptions.add( bothB );

    final Collection helpOptions = options.helpOptions();

    assertTrue( "Everything in all should be in help", helpOptions.containsAll( allOptions ) );
    assertTrue( "Everything in help should be in all", allOptions.containsAll( helpOptions ) );
  }




  @Test
  public void testLong() {
    final Options opts = new Options();

    opts.addOption( "a", "--a", false, "toggle -a" );
    opts.addOption( "b", "--b", true, "set -b" );

    assertTrue( opts.hasOption( "a" ) );
    assertTrue( opts.hasOption( "b" ) );
  }




  @Test
  public void testMissingOptionException() throws ArgumentException {
    final Options options = new Options();
    options.addOption( OptionBuilder.isRequired().create( "f" ) );
    try {
      new PosixParser().parse( options, new String[0] );
      fail( "Expected MissingOptionException to be thrown" );
    } catch ( final MissingOptionException e ) {
      assertEquals( "Missing required option: f", e.getMessage() );
    }
  }




  @Test
  public void testMissingOptionsException() throws ArgumentException {
    final Options options = new Options();
    options.addOption( OptionBuilder.isRequired().create( "f" ) );
    options.addOption( OptionBuilder.isRequired().create( "x" ) );
    try {
      new PosixParser().parse( options, new String[0] );
      fail( "Expected MissingOptionException to be thrown" );
    } catch ( final MissingOptionException e ) {
      assertEquals( "Missing required options: f, x", e.getMessage() );
    }
  }




  @Test
  public void testSimple() {
    final Options opts = new Options();

    opts.addOption( "a", false, "toggle -a" );
    opts.addOption( "b", true, "toggle -b" );

    assertTrue( opts.hasOption( "a" ) );
    assertTrue( opts.hasOption( "b" ) );
  }




  @Test
  public void testToString() {
    final Options options = new Options();
    options.addOption( "f", "foo", true, "Foo" );
    options.addOption( "b", "bar", false, "Bar" );

    final String s = options.toString();
    assertNotNull( "null string returned", s );
    assertTrue( "foo option missing", s.toLowerCase().indexOf( "foo" ) != -1 );
    assertTrue( "bar option missing", s.toLowerCase().indexOf( "bar" ) != -1 );
  }
}
