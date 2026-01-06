package coyote.commons.cli;

//import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class PosixParserTest extends ParserTestCase {
    @BeforeAll
    public static void setUp() {
        ParserTestCase.setUp();
        parser = new PosixParser();
    }


    @Test
    public void testBursting() throws Exception {
        final String[] args = new String[]{"-acbtoast", "foo", "bar"};

        final ArgumentList al = parser.parse(options, args);

        assertTrue(al.hasOption("a"), "Confirm -a is set");
        assertTrue(al.hasOption("b"), "Confirm -b is set");
        assertTrue(al.hasOption("c"), "Confirm -c is set");
        assertEquals("toast", al.getOptionValue("b"), "Confirm arg of -b");
        assertEquals(2, al.getArgList().size(), "Confirm size of extra args");
    }


    /**
     * Real world test with long and short options.
     */
    @Test
    public void testLongOptionWithShort() throws Exception {
        final Option help = new Option("h", "help", false, "print this message");
        final Option version = new Option("v", "version", false, "print version information");
        final Option newRun = new Option("n", "new", false, "Create NLT cache entries only for new items");
        final Option trackerRun = new Option("t", "tracker", false, "Create NLT cache entries only for tracker items");

        OptionBuilder.withLongOpt("limit");
        OptionBuilder.hasArg();
        OptionBuilder.withValueSeparator();
        OptionBuilder.withDescription("Set time limit for execution, in minutes");
        final Option timeLimit = OptionBuilder.create("l");

        OptionBuilder.withLongOpt("age");
        OptionBuilder.hasArg();
        OptionBuilder.withValueSeparator();
        OptionBuilder.withDescription("Age (in days) of cache item before being recomputed");
        final Option age = OptionBuilder.create("a");

        OptionBuilder.withLongOpt("server");
        OptionBuilder.hasArg();
        OptionBuilder.withValueSeparator();
        OptionBuilder.withDescription("The NLT server address");
        final Option server = OptionBuilder.create("s");

        OptionBuilder.withLongOpt("results");
        OptionBuilder.hasArg();
        OptionBuilder.withValueSeparator();
        OptionBuilder.withDescription("Number of results per item");
        final Option numResults = OptionBuilder.create("r");

        OptionBuilder.withLongOpt("file");
        OptionBuilder.hasArg();
        OptionBuilder.withValueSeparator();
        OptionBuilder.withDescription("Use the specified configuration file");
        final Option configFile = OptionBuilder.create();

        final Options options = new Options();
        options.addOption(help);
        options.addOption(version);
        options.addOption(newRun);
        options.addOption(trackerRun);
        options.addOption(timeLimit);
        options.addOption(age);
        options.addOption(server);
        options.addOption(numResults);
        options.addOption(configFile);

        // create the command line parser
        final ArgumentParser parser = new PosixParser();

        final String[] args = new String[]{"-v", "-l", "10", "-age", "5", "-file", "filename"};

        final ArgumentList line = parser.parse(options, args);
        assertTrue(line.hasOption("v"));
        assertEquals(line.getOptionValue("l"), "10");
        assertEquals(line.getOptionValue("limit"), "10");
        assertEquals(line.getOptionValue("a"), "5");
        assertEquals(line.getOptionValue("age"), "5");
        assertEquals(line.getOptionValue("file"), "filename");
    }


    @Override
    public void testLongWithEqualSingleDash() throws Exception {
        // not supported by the PosixParser
    }


    @Test
    public void testMissingArgWithBursting() throws Exception {
        final String[] args = new String[]{"-acb"};

        boolean caught = false;

        try {
            parser.parse(options, args);
        } catch (final MissingArgumentException e) {
            caught = true;
            assertEquals("b", e.getOption().getOpt(), "option missing an argument");
        }

        assertTrue(caught, "Confirm MissingArgumentException caught");
    }


    @Override
    public void testShortWithEqual() throws Exception {
        // not supported by the PosixParser
    }


    @Test
    public void testStopBursting() throws Exception {
        final String[] args = new String[]{"-azc"};

        final ArgumentList al = parser.parse(options, args, true);
        assertTrue(al.hasOption("a"), "Confirm -a is set");
        assertFalse(al.hasOption("c"), "Confirm -c is not set");

        assertEquals(1, al.getArgList().size(), "Confirm  1 extra arg: " + al.getArgList().size());
        assertTrue(al.getArgList().contains("zc"));
    }


    @Test
    public void testStopBursting2() throws Exception {
        final String[] args = new String[]{"-c", "foobar", "-btoast"};

        ArgumentList al = parser.parse(options, args, true);
        assertTrue(al.hasOption("c"), "Confirm -c is set");
        assertEquals(2, al.getArgList().size(), "Confirm  2 extra args: " + al.getArgList().size());

        al = parser.parse(options, al.getArgs());

        assertFalse(al.hasOption("c"), "Confirm -c is not set");
        assertTrue(al.hasOption("b"), "Confirm -b is set");
        assertEquals("toast", al.getOptionValue("b"), "Confirm arg of -b");
        assertEquals(1, al.getArgList().size(), "Confirm  1 extra arg: " + al.getArgList().size());
        assertEquals("foobar", al.getArgList().get(0), "Confirm  value of extra arg: " + al.getArgList().get(0));
    }


    @Test
    public void testUnrecognizedOptionWithBursting() throws Exception {
        final String[] args = new String[]{"-adbtoast", "foo", "bar"};

        try {
            parser.parse(options, args);
            fail("UnrecognizedOptionException wasn't thrown");
        } catch (final UnrecognizedOptionException e) {
            assertEquals("-adbtoast", e.getOption());
        }
    }
}
