package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;


public class OptionBuilderTest {

  @Test
  public void testBaseOptionCharOpt() {
    OptionBuilder.withDescription( "option description" );
    final Option base = OptionBuilder.create( 'o' );

    assertEquals( "o", base.getOpt() );
    assertEquals( "option description", base.getDescription() );
    assertTrue( !base.hasArg() );
  }




  @Test
  public void testBaseOptionStringOpt() {
    OptionBuilder.withDescription( "option description" );
    final Option base = OptionBuilder.create( "o" );

    assertEquals( "o", base.getOpt() );
    assertEquals( "option description", base.getDescription() );
    assertTrue( !base.hasArg() );
  }




  @Test
  public void testBuilderIsResettedAlways() {
    try {
      OptionBuilder.withDescription( "JUnit" );
      OptionBuilder.create( '"' );
      fail( "IllegalArgumentException expected" );
    } catch ( final IllegalArgumentException e ) {
      // expected
    }
    assertNull( "we inherited a description", OptionBuilder.create( 'x' ).getDescription() );

    try {
      OptionBuilder.withDescription( "JUnit" );
      OptionBuilder.create();
      fail( "IllegalArgumentException expected" );
    } catch ( final IllegalArgumentException e ) {
      // expected
    }
    assertNull( "we inherited a description", OptionBuilder.create( 'x' ).getDescription() );
  }




  @Test
  public void testCompleteOption() {
    OptionBuilder.withLongOpt( "simple option" );
    OptionBuilder.hasArg();
    OptionBuilder.isRequired();
    OptionBuilder.hasArgs();
    OptionBuilder.withType( new Float( 10 ) );
    OptionBuilder.withDescription( "this is a simple option" );
    final Option simple = OptionBuilder.create( 's' );

    assertEquals( "s", simple.getOpt() );
    assertEquals( "simple option", simple.getLongOpt() );
    assertEquals( "this is a simple option", simple.getDescription() );
    assertEquals( simple.getType().getClass(), Float.class );
    assertTrue( simple.hasArg() );
    assertTrue( simple.isRequired() );
    assertTrue( simple.hasArgs() );
  }




  @Test
  public void testCreateIncompleteOption() {
    try {
      OptionBuilder.hasArg();
      OptionBuilder.create();
      fail( "Incomplete option should be rejected" );
    } catch ( final IllegalArgumentException e ) {
      // expected

      // implicitly reset the builder
      OptionBuilder.create( "opt" );
    }
  }




  @Test
  public void testIllegalOptions() {
    // bad single character option
    try {
      OptionBuilder.withDescription( "option description" );
      OptionBuilder.create( '"' );
      fail( "IllegalArgumentException not caught" );
    } catch ( final IllegalArgumentException exp ) {
      // success
    }

    // bad character in option string
    try {
      OptionBuilder.create( "opt`" );
      fail( "IllegalArgumentException not caught" );
    } catch ( final IllegalArgumentException exp ) {
      // success
    }

    // valid option 
    try {
      OptionBuilder.create( "opt" );
    } catch ( final IllegalArgumentException exp ) {
      fail( "IllegalArgumentException caught" );
    }
  }




  @Test
  public void testOptionArgNumbers() {
    OptionBuilder.withDescription( "option description" );
    OptionBuilder.hasArgs( 2 );
    final Option opt = OptionBuilder.create( 'o' );
    assertEquals( 2, opt.getArgs() );
  }




  @Test
  public void testSpecialOptChars() throws Exception {
    OptionBuilder.withDescription( "help options" );
    // '?'
    final Option opt1 = OptionBuilder.create( '?' );
    assertEquals( "?", opt1.getOpt() );

    OptionBuilder.withDescription( "read from stdin" );
    // '@'
    final Option opt2 = OptionBuilder.create( '@' );
    assertEquals( "@", opt2.getOpt() );
  }




  @Test
  public void testTwoCompleteOptions() {
    OptionBuilder.withLongOpt( "simple option" );
    OptionBuilder.hasArg();
    OptionBuilder.isRequired();
    OptionBuilder.hasArgs();
    OptionBuilder.withType( new Float( 10 ) );
    OptionBuilder.withDescription( "this is a simple option" );
    Option simple = OptionBuilder.create( 's' );

    assertEquals( "s", simple.getOpt() );
    assertEquals( "simple option", simple.getLongOpt() );
    assertEquals( "this is a simple option", simple.getDescription() );
    assertEquals( simple.getType().getClass(), Float.class );
    assertTrue( simple.hasArg() );
    assertTrue( simple.isRequired() );
    assertTrue( simple.hasArgs() );

    OptionBuilder.withLongOpt( "dimple option" );
    OptionBuilder.hasArg();
    OptionBuilder.withDescription( "this is a dimple option" );
    simple = OptionBuilder.create( 'd' );

    assertEquals( "d", simple.getOpt() );
    assertEquals( "dimple option", simple.getLongOpt() );
    assertEquals( "this is a dimple option", simple.getDescription() );
    assertNull( simple.getType() );
    assertTrue( simple.hasArg() );
    assertTrue( !simple.isRequired() );
    assertTrue( !simple.hasArgs() );
  }
}
