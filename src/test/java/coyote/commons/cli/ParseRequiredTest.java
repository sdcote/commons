package coyote.commons.cli;

//import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class ParseRequiredTest {
    private static Options _options = null;


    @BeforeAll
    public static void setUp() {
        OptionBuilder.withLongOpt("bfile");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("set the value of [b]");
        _options = new Options().addOption("a", "enable-a", false, "turn [a] on or off").addOption(OptionBuilder.create('b'));
    }

    private final ArgumentParser parser = new PosixParser();


    @Test
    public void testMissingRequiredOption() {
        final String[] args = new String[]{"-a"};

        try {
            parser.parse(_options, args);
            fail("exception should have been thrown");
        } catch (final MissingOptionException e) {
            assertEquals("Missing required option: b", e.getMessage(), "Incorrect exception message");
            assertTrue(e.getMissingOptions().contains("b"));
        } catch (final ArgumentException e) {
            fail("expected to catch MissingOptionException");
        }
    }


    @Test
    public void testMissingRequiredOptions() {
        final String[] args = new String[]{"-a"};

        OptionBuilder.withLongOpt("cfile");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("set the value of [c]");
        _options.addOption(OptionBuilder.create('c'));

        try {
            parser.parse(_options, args);
            fail("exception should have been thrown");
        } catch (final MissingOptionException e) {
            assertEquals("Missing required options: b, c", e.getMessage(), "Incorrect exception message");
            assertTrue(e.getMissingOptions().contains("b"));
            assertTrue(e.getMissingOptions().contains("c"));
        } catch (final ArgumentException e) {
            fail("expected to catch MissingOptionException");
        }
    }


    // TODO
    public void testOptionAndRequiredOption() throws Exception {
        final String[] args = new String[]{"-a", "-b", "file"};

        final ArgumentList argList = parser.parse(_options, args);

        assertTrue(argList.hasOption("a"), "Confirm -a is set");
        assertTrue(argList.hasOption("b"), "Confirm -b is set");
        assertEquals("file", argList.getOptionValue("b"), "Confirm arg of -b");
        assertEquals(0, argList.getArgList().size(), "Confirm NO of extra args");
    }


    @Test
    public void testReuseOptionsTwice() throws Exception {
        final Options opts = new Options();
        OptionBuilder.isRequired();
        opts.addOption(OptionBuilder.create('v'));

        final GnuParser parser = new GnuParser();

        // first parsing
        parser.parse(opts, new String[]{"-v"});

        try {
            // second parsing, with the same Options instance and an invalid command line
            parser.parse(opts, new String[0]);
            fail("MissingOptionException not thrown");
        } catch (final MissingOptionException e) {
            // expected
        }
    }


    // TODO
    public void testWithRequiredOption() throws Exception {
        final String[] args = new String[]{"-b", "file"};

        final ArgumentList argList = parser.parse(_options, args);

        assertFalse(argList.hasOption("a"), "Confirm -a is NOT set");
        assertTrue(argList.hasOption("b"), "Confirm -b is set");
        assertEquals("file", argList.getOptionValue("b"), "Confirm arg of -b");
        assertEquals(0, argList.getArgList().size(), "Confirm NO of extra args");
    }

}
