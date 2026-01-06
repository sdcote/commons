package coyote.commons.cli;

//import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class OptionPropertyTest {

    @Test
    public void testGetOptionProperties() throws Exception {
        final String[] args = new String[]{"-Dparam1=value1", "-Dparam2=value2", "-Dparam3", "-Dparam4=value4", "-D", "--property", "foo=bar"};

        final Options options = new Options();
        OptionBuilder.withValueSeparator();
        OptionBuilder.hasOptionalArgs(2);
        options.addOption(OptionBuilder.create('D'));
        OptionBuilder.withValueSeparator();
        OptionBuilder.hasArgs(2);
        OptionBuilder.withLongOpt("property");
        options.addOption(OptionBuilder.create());

        final Parser parser = new GnuParser();
        final ArgumentList al = parser.parse(options, args);

        final Properties props = al.getOptionProperties("D");
        assertNotNull(props, "null properties");
        assertEquals(4, props.size(), "number of properties in " + props);
        assertEquals("value1", props.getProperty("param1"), "property 1");
        assertEquals("value2", props.getProperty("param2"), "property 2");
        assertEquals("true", props.getProperty("param3"), "property 3");
        assertEquals("value4", props.getProperty("param4"), "property 4");

        assertEquals("bar", al.getOptionProperties("property").getProperty("foo"), "property with long format");
    }
}
