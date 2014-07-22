package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;


public class ArgumentIsOptionTest {
  private static Options options = null;
  private static ArgumentParser parser = null;




  @BeforeClass
  public static void setUp() {
    options = new Options().addOption( "p", false, "Option p" ).addOption( "attr", true, "Option accepts argument" );

    parser = new PosixParser();
  }




  @Test
  public void testOption() throws Exception {
    final String[] args = new String[] { "-p" };

    final ArgumentList argList = parser.parse( options, args );
    assertTrue( "Confirm -p is set", argList.hasOption( "p" ) );
    assertFalse( "Confirm -attr is not set", argList.hasOption( "attr" ) );
    assertTrue( "Confirm all arguments recognized", argList.getArgs().length == 0 );
  }




  @Test
  public void testOptionAndOptionWithArgument() throws Exception {
    final String[] args = new String[] { "-p", "-attr", "p" };

    final ArgumentList argList = parser.parse( options, args );
    assertTrue( "Confirm -p is set", argList.hasOption( "p" ) );
    assertTrue( "Confirm -attr is set", argList.hasOption( "attr" ) );
    assertTrue( "Confirm arg of -attr", argList.getOptionValue( "attr" ).equals( "p" ) );
    assertTrue( "Confirm all arguments recognized", argList.getArgs().length == 0 );
  }




  @Test
  public void testOptionWithArgument() throws Exception {
    final String[] args = new String[] { "-attr", "p" };

    final ArgumentList argList = parser.parse( options, args );
    assertFalse( "Confirm -p is set", argList.hasOption( "p" ) );
    assertTrue( "Confirm -attr is set", argList.hasOption( "attr" ) );
    assertTrue( "Confirm arg of -attr", argList.getOptionValue( "attr" ).equals( "p" ) );
    assertTrue( "Confirm all arguments recognized", argList.getArgs().length == 0 );
  }
}
