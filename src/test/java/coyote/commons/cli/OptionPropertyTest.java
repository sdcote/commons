package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class OptionPropertyTest {


  @Test
  public void testGetOptionProperties() throws Exception {
    final String[] args = new String[] { "-Dparam1=value1", "-Dparam2=value2", "-Dparam3", "-Dparam4=value4", "-D", "--property", "foo=bar" };

    final Options options = new Options();
    options.addOption( OptionBuilder.withValueSeparator().hasOptionalArgs( 2 ).create( 'D' ) );
    options.addOption( OptionBuilder.withValueSeparator().hasArgs( 2 ).withLongOpt( "property" ).create() );

    final Parser parser = new GnuParser();
    final ArgumentList al = parser.parse( options, args );

    final Properties props = al.getOptionProperties( "D" );
    assertNotNull( "null properties", props );
    assertEquals( "number of properties in " + props, 4, props.size() );
    assertEquals( "property 1", "value1", props.getProperty( "param1" ) );
    assertEquals( "property 2", "value2", props.getProperty( "param2" ) );
    assertEquals( "property 3", "true", props.getProperty( "param3" ) );
    assertEquals( "property 4", "value4", props.getProperty( "param4" ) );

    assertEquals( "property with long format", "bar", al.getOptionProperties( "property" ).getProperty( "foo" ) );
  }
}
