package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;


public class OptionBuilderTest {

  @Test
  public void testCompleteOption() {
    Option simple = OptionBuilder.withLongOpt( "simple option" ).hasArg().isRequired().hasArgs().withType( new Float( 10 ) ).withDescription( "this is a simple option" ).create( 's' );

    assertEquals( "s", simple.getOpt() );
    assertEquals( "simple option", simple.getLongOpt() );
    assertEquals( "this is a simple option", simple.getDescription() );
    assertEquals( simple.getType().getClass(), Float.class );
    assertTrue( simple.hasArg() );
    assertTrue( simple.isRequired() );
    assertTrue( simple.hasArgs() );
  }




  @Test
  public void testTwoCompleteOptions() {
    Option simple = OptionBuilder.withLongOpt( "simple option" ).hasArg().isRequired().hasArgs().withType( new Float( 10 ) ).withDescription( "this is a simple option" ).create( 's' );

    assertEquals( "s", simple.getOpt() );
    assertEquals( "simple option", simple.getLongOpt() );
    assertEquals( "this is a simple option", simple.getDescription() );
    assertEquals( simple.getType().getClass(), Float.class );
    assertTrue( simple.hasArg() );
    assertTrue( simple.isRequired() );
    assertTrue( simple.hasArgs() );

    simple = OptionBuilder.withLongOpt( "dimple option" ).hasArg().withDescription( "this is a dimple option" ).create( 'd' );

    assertEquals( "d", simple.getOpt() );
    assertEquals( "dimple option", simple.getLongOpt() );
    assertEquals( "this is a dimple option", simple.getDescription() );
    assertNull( simple.getType() );
    assertTrue( simple.hasArg() );
    assertTrue( !simple.isRequired() );
    assertTrue( !simple.hasArgs() );
  }




  @Test
  public void testBaseOptionCharOpt() {
    Option base = OptionBuilder.withDescription( "option description" ).create( 'o' );

    assertEquals( "o", base.getOpt() );
    assertEquals( "option description", base.getDescription() );
    assertTrue( !base.hasArg() );
  }




  @Test
  public void testBaseOptionStringOpt() {
    Option base = OptionBuilder.withDescription( "option description" ).create( "o" );

    assertEquals( "o", base.getOpt() );
    assertEquals( "option description", base.getDescription() );
    assertTrue( !base.hasArg() );
  }




  @Test
  public void testSpecialOptChars() throws Exception {
    // '?'
    Option opt1 = OptionBuilder.withDescription( "help options" ).create( '?' );
    assertEquals( "?", opt1.getOpt() );

    // '@'
    Option opt2 = OptionBuilder.withDescription( "read from stdin" ).create( '@' );
    assertEquals( "@", opt2.getOpt() );
  }




  @Test
  public void testOptionArgNumbers() {
    Option opt = OptionBuilder.withDescription( "option description" ).hasArgs( 2 ).create( 'o' );
    assertEquals( 2, opt.getArgs() );
  }




  @Test
  public void testIllegalOptions() {
    // bad single character option
    try {
      OptionBuilder.withDescription( "option description" ).create( '"' );
      fail( "IllegalArgumentException not caught" );
    } catch ( IllegalArgumentException exp ) {
      // success
    }

    // bad character in option string
    try {
      Option opt = OptionBuilder.create( "opt`" );
      fail( "IllegalArgumentException not caught" );
    } catch ( IllegalArgumentException exp ) {
      // success
    }

    // valid option 
    try {
      Option opt = OptionBuilder.create( "opt" );
      // success
    } catch ( IllegalArgumentException exp ) {
      fail( "IllegalArgumentException caught" );
    }
  }




  @Test
  public void testCreateIncompleteOption() {
    try {
      OptionBuilder.hasArg().create();
      fail( "Incomplete option should be rejected" );
    } catch ( IllegalArgumentException e ) {
      // expected

      // implicitly reset the builder
      OptionBuilder.create( "opt" );
    }
  }




  @Test
  public void testBuilderIsResettedAlways() {
    try {
      OptionBuilder.withDescription( "JUnit" ).create( '"' );
      fail( "IllegalArgumentException expected" );
    } catch ( IllegalArgumentException e ) {
      // expected
    }
    assertNull( "we inherited a description", OptionBuilder.create( 'x' ).getDescription() );

    try {
      OptionBuilder.withDescription( "JUnit" ).create();
      fail( "IllegalArgumentException expected" );
    } catch ( IllegalArgumentException e ) {
      // expected
    }
    assertNull( "we inherited a description", OptionBuilder.create( 'x' ).getDescription() );
  }
}
