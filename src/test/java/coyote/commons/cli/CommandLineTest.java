package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class CommandLineTest {
  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}




  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {}




  @Test
  public void testGetOptionProperties() throws Exception {
    String[] args = new String[] { "-Dparam1=value1", "-Dparam2=value2", "-Dparam3", "-Dparam4=value4", "-D", "--property", "foo=bar" };

    Options options = new Options();
    options.addOption( OptionBuilder.withValueSeparator().hasOptionalArgs( 2 ).create( 'D' ) );
    options.addOption( OptionBuilder.withValueSeparator().hasArgs( 2 ).withLongOpt( "property" ).create() );

    Parser parser = new GnuParser();
    CommandLine cl = parser.parse( options, args );

    Properties props = cl.getOptionProperties( "D" );
    assertNotNull( "null properties", props );
    assertEquals( "number of properties in " + props, 4, props.size() );
    assertEquals( "property 1", "value1", props.getProperty( "param1" ) );
    assertEquals( "property 2", "value2", props.getProperty( "param2" ) );
    assertEquals( "property 3", "true", props.getProperty( "param3" ) );
    assertEquals( "property 4", "value4", props.getProperty( "param4" ) );

    assertEquals( "property with long format", "bar", cl.getOptionProperties( "property" ).getProperty( "foo" ) );
  }
}
