package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 *
 */
public class OptionTest {
  private static class DefaultOption extends Option {
    private static final long serialVersionUID = -7947152607975698810L;
    private final String defaultValue;




    public DefaultOption( final String opt, final String description, final String defaultValue ) throws IllegalArgumentException {
      super( opt, true, description );
      this.defaultValue = defaultValue;
    }




    @Override
    public String getValue() {
      return super.getValue() != null ? super.getValue() : defaultValue;
    }
  }

  private static class TestOption extends Option {
    private static final long serialVersionUID = -4341840485935398838L;




    public TestOption( final String opt, final boolean hasArg, final String description ) throws IllegalArgumentException {
      super( opt, hasArg, description );
    }




    public boolean addValue( final String value ) {
      addValueForProcessing( value );
      return true;
    }
  }




  @Test
  public void testClear() {
    final TestOption option = new TestOption( "x", true, "" );
    assertEquals( 0, option.getValuesList().size() );
    option.addValue( "a" );
    assertEquals( 1, option.getValuesList().size() );
    option.clearValues();
    assertEquals( 0, option.getValuesList().size() );
  }




  @Test
  public void testClone() throws CloneNotSupportedException {
    final TestOption a = new TestOption( "a", true, "" );
    final TestOption b = (TestOption)a.clone();
    assertEquals( a, b );
    assertNotSame( a, b );
    a.setDescription( "a" );
    assertEquals( "", b.getDescription() );
    b.setArgs( 2 );
    b.addValue( "b1" );
    b.addValue( "b2" );
    assertEquals( 1, a.getArgs() );
    assertEquals( 0, a.getValuesList().size() );
    assertEquals( 2, b.getValues().length );
  }




  @Test
  public void testGetValue() {
    final Option option = new Option( "f", null );
    option.setArgs( Option.UNLIMITED_VALUES );

    assertEquals( "default", option.getValue( "default" ) );
    assertEquals( null, option.getValue( 0 ) );

    option.addValueForProcessing( "foo" );

    assertEquals( "foo", option.getValue() );
    assertEquals( "foo", option.getValue( 0 ) );
    assertEquals( "foo", option.getValue( "default" ) );
  }




  @Test
  public void testHasArgName() {
    final Option option = new Option( "f", null );

    option.setArgName( null );
    assertFalse( option.hasArgName() );

    option.setArgName( "" );
    assertFalse( option.hasArgName() );

    option.setArgName( "file" );
    assertTrue( option.hasArgName() );
  }




  @Test
  public void testHasArgs() {
    final Option option = new Option( "f", null );

    option.setArgs( 0 );
    assertFalse( option.hasArgs() );

    option.setArgs( 1 );
    assertFalse( option.hasArgs() );

    option.setArgs( 10 );
    assertTrue( option.hasArgs() );

    option.setArgs( Option.UNLIMITED_VALUES );
    assertTrue( option.hasArgs() );

    option.setArgs( Option.UNINITIALIZED );
    assertFalse( option.hasArgs() );
  }




  @Test
  public void testSubclass() throws CloneNotSupportedException {
    final Option option = new DefaultOption( "f", "file", "myfile.txt" );
    final Option clone = (Option)option.clone();
    assertEquals( "myfile.txt", clone.getValue() );
    assertEquals( DefaultOption.class, clone.getClass() );
  }
}
