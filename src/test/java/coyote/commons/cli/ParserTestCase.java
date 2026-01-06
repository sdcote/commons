package coyote.commons.cli;

//import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Abstract test case testing common parser features.
 */
public abstract class ParserTestCase {
    protected static Parser parser;

    protected static Options options;


    @BeforeAll
    public static void setUp() {
        options = new Options().addOption("a", "enable-a", false, "turn [a] on or off").addOption("b", "bfile", true, "set the value of [b]").addOption("c", "copt", false, "turn [c] on or off");
    }


    @Test
    public void testAnotherCase() throws Exception {
        // Posix
        Options options = new Options();
        OptionBuilder.hasOptionalArg();
        options.addOption(OptionBuilder.create('a'));
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create('b'));
        String[] args = new String[]{"-a", "-bvalue"};

        ArgumentParser parser = new PosixParser();

        ArgumentList argList = parser.parse(options, args);
        assertEquals(argList.getOptionValue('b'), "value");

        // GNU
        options = new Options();
        OptionBuilder.hasOptionalArg();
        options.addOption(OptionBuilder.create('a'));
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create('b'));
        args = new String[]{"-a", "-b", "value"};

        parser = new GnuParser();

        argList = parser.parse(options, args);
        assertEquals(argList.getOptionValue('b'), "value");
    }


    @Test
    public void testArgumentStartingWithHyphen() throws Exception {
        final String[] args = new String[]{"-b", "-foo"};

        final ArgumentList argList = parser.parse(options, args);
        assertEquals("-foo", argList.getOptionValue("b"));
    }


    @Test
    public void testConflictingOption() throws Exception {
        final ArgumentParser parser = new PosixParser();
        final String[] CLI_ARGS = new String[]{"-z", "c"};

        final Options options = new Options();
        options.addOption(new Option("z", "timezone", true, "affected option"));

        parser.parse(options, CLI_ARGS);

        //now add conflicting option
        options.addOption("c", "conflict", true, "conflict option");
        final ArgumentList argList = parser.parse(options, CLI_ARGS);
        assertEquals(argList.getOptionValue('z'), "c");
        assertFalse(argList.hasOption("c"));
    }


    @Test
    public void testDefaults() throws Exception {
        final Options options = new Options();
        options.addOption("f", true, "foobar");
        options.addOption("m", true, "missing");
        final String[] args = new String[]{"-f", "foo"};

        final ArgumentParser parser = new PosixParser();

        final ArgumentList argList = parser.parse(options, args);

        argList.getOptionValue("f", "default f");
        argList.getOptionValue("m", "default m");
    }


    @Test
    public void testDoubleDash() throws Exception {
        final String[] args = new String[]{"--copt", "--", "-b", "toast"};

        final ArgumentList argList = parser.parse(options, args);

        assertTrue(argList.hasOption("c"), "Confirm -c is set");
        assertFalse(argList.hasOption("b"), "Confirm -b is not set");
        assertEquals(2, argList.getArgList().size(), "Confirm 2 extra args: " + argList.getArgList().size());
    }


    @Test
    public void testEOLCharacter() throws Exception {
        final Options options = new Options();
        OptionBuilder.withDescription("dir");
        OptionBuilder.hasArg();
        final Option dir = OptionBuilder.create('d');
        options.addOption(dir);

        final PrintStream oldSystemOut = System.out;
        try {
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            final PrintStream print = new PrintStream(bytes);

            // capture this platform's eol symbol
            print.println();
            final String eol = bytes.toString();
            bytes.reset();

            System.setOut(new PrintStream(bytes));

            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("dir", options);

            assertEquals("usage: dir" + eol + " -d <arg>   dir" + eol, bytes.toString());
        } finally {
            System.setOut(oldSystemOut);
        }
    }


    @Test
    public void testGnuRequired() throws Exception {
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("test");
        final Option o = OptionBuilder.create("test");
        final Options opts = new Options();
        opts.addOption(o);
        opts.addOption(o);

        final ArgumentParser parser = new GnuParser();

        final String[] args = new String[]{"-test"};

        final ArgumentList argList = parser.parse(opts, args);
        assertTrue(argList.hasOption("test"));
    }


    @Test
    public void testLongOptOnly() throws Exception {
        final Options options = new Options();
        OptionBuilder.withLongOpt("verbose");
        options.addOption(OptionBuilder.create());
        final String[] args = new String[]{"--verbose"};

        final ArgumentParser parser = new PosixParser();

        final ArgumentList argList = parser.parse(options, args);
        assertTrue(argList.hasOption("verbose"));
    }


    @Test
    public void testLongWithEqual() throws Exception {
        final String[] args = new String[]{"--foo=bar"};

        final Options options = new Options();
        OptionBuilder.withLongOpt("foo");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create('f'));

        final ArgumentList argList = parser.parse(options, args);

        assertEquals("bar", argList.getOptionValue("foo"));
    }


    @Test
    public void testLongWithEqualSingleDash() throws Exception {
        final String[] args = new String[]{"-foo=bar"};

        final Options options = new Options();
        OptionBuilder.withLongOpt("foo");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create('f'));

        final ArgumentList argList = parser.parse(options, args);

        assertEquals("bar", argList.getOptionValue("foo"));
    }


    @Test
    public void testMissingArg() throws Exception {
        final String[] args = new String[]{"-b"};

        boolean caught = false;

        try {
            parser.parse(options, args);
        } catch (final MissingArgumentException e) {
            caught = true;
            assertEquals("b", e.getOption().getOpt(), "option missing an argument");
        }

        assertTrue(caught, "Confirm MissingArgumentException caught");
    }


    @Test
    public void testMultiArgs() throws ArgumentException {
        final Option multiArgOption = new Option("o", "option with multiple args");
        multiArgOption.setArgs(1);

        final Options options = new Options();
        options.addOption(multiArgOption);

        final Parser parser = new PosixParser();
        final String[] args = new String[]{};
        final Properties props = new Properties();
        props.setProperty("o", "ovalue");
        final ArgumentList argList = parser.parse(options, args, props);

        assertTrue(argList.hasOption('o'));
        assertEquals("ovalue", argList.getOptionValue('o'));
    }


    @Test
    public void testMultiple() throws Exception {
        final String[] args = new String[]{"-c", "foobar", "-b", "toast"};

        ArgumentList argList = parser.parse(options, args, true);
        assertTrue(argList.hasOption("c"), "Confirm -c is set");
        assertEquals(3, argList.getArgList().size(), "Confirm  3 extra args: " + argList.getArgList().size());

        argList = parser.parse(options, argList.getArgs());

        assertFalse(argList.hasOption("c"), "Confirm -c is not set");
        assertTrue(argList.hasOption("b"), "Confirm -b is set");
        assertEquals("toast", argList.getOptionValue("b"), "Confirm arg of -b");
        assertEquals(1, argList.getArgList().size(), "Confirm  1 extra arg: " + argList.getArgList().size());
        assertEquals("foobar", argList.getArgList().get(0), "Confirm  value of extra arg: " + argList.getArgList().get(0));
    }


    @Test
    public void testMultipleWithLong() throws Exception {
        final String[] args = new String[]{"--copt", "foobar", "--bfile", "toast"};

        ArgumentList argList = parser.parse(options, args, true);
        assertTrue(argList.hasOption("c"), "Confirm -c is set");
        assertEquals(3, argList.getArgList().size(), "Confirm  3 extra args: " + argList.getArgList().size());

        argList = parser.parse(options, argList.getArgs());

        assertFalse(argList.hasOption("c"), "Confirm -c is not set");
        assertTrue(argList.hasOption("b"), "Confirm -b is set");
        assertEquals("toast", argList.getOptionValue("b"), "Confirm arg of -b");
        assertEquals(1, argList.getArgList().size(), "Confirm  1 extra arg: " + argList.getArgList().size());
        assertEquals("foobar", argList.getArgList().get(0), "Confirm  value of extra arg: " + argList.getArgList().get(0));
    }


    @Test
    public void testNegativeArgument() throws Exception {
        final String[] args = new String[]{"-b", "-1"};

        final ArgumentList argList = parser.parse(options, args);
        assertEquals("-1", argList.getOptionValue("b"));
    }


    // @Test
    public void testOddCase() throws Exception {
        final OptionGroup directions = new OptionGroup();

        final Option left = new Option("l", "left", false, "go left");
        final Option right = new Option("r", "right", false, "go right");
        final Option straight = new Option("s", "straight", false, "go straight");
        final Option forward = new Option("f", "forward", false, "go forward");
        forward.setRequired(true);

        directions.addOption(left);
        directions.addOption(right);
        directions.setRequired(true);

        final Options opts = new Options();
        opts.addOptionGroup(directions);
        opts.addOption(straight);

        final ArgumentParser parser = new PosixParser();
        boolean exception = false;

        String[] args = new String[]{};
        try {
            parser.parse(opts, args);
        } catch (final ArgumentException exp) {
            exception = true;
        }

        if (!exception) {
            fail("Expected exception not caught.");
        }

        exception = false;

        args = new String[]{"-s"};
        try {
            parser.parse(opts, args);
        } catch (final ArgumentException exp) {
            exception = true;
        }

        if (!exception) {
            fail("Expected exception not caught.");
        }

        exception = false;

        args = new String[]{"-s", "-l"};
        try {
            parser.parse(opts, args);
        } catch (final ArgumentException exp) {
            fail("Unexpected exception: " + exp.getClass().getName() + ":" + exp.getMessage());
        }

        opts.addOption(forward);
        args = new String[]{"-s", "-l", "-f"};
        try {
            parser.parse(opts, args);
        } catch (final ArgumentException exp) {
            fail("Unexpected exception: " + exp.getClass().getName() + ":" + exp.getMessage());
        }
    }


    @Test
    public void testOptionGroup() throws Exception {
        // create the main options object which will handle the first parameter
        final Options mainOptions = new Options();
        // There can be 2 main exclusive options:  -exec|-rep

        // Therefore, place them in an option group

        String[] argv = new String[]{"-exec", "-exec_opt1", "-exec_opt2"};
        final OptionGroup grp = new OptionGroup();

        grp.addOption(new Option("exec", false, "description for this option"));

        grp.addOption(new Option("rep", false, "description for this option"));

        mainOptions.addOptionGroup(grp);

        // for the exec option, there are 2 options...
        final Options execOptions = new Options();
        execOptions.addOption("exec_opt1", false, " desc");
        execOptions.addOption("exec_opt2", false, " desc");

        // similarly, for rep there are 2 options...
        final Options repOptions = new Options();
        repOptions.addOption("repopto", false, "desc");
        repOptions.addOption("repoptt", false, "desc");

        // create the parser
        final GnuParser parser = new GnuParser();

        // finally, parse the arguments:

        // first parse the main options to see what the user has specified
        // We set stopAtNonOption to true so it does not touch the remaining
        // options
        ArgumentList cmd = parser.parse(mainOptions, argv, true);
        // get the remaining options...
        argv = cmd.getArgs();

        if (cmd.hasOption("exec")) {
            cmd = parser.parse(execOptions, argv, false);
            // process the exec_op1 and exec_opt2...
            assertTrue(cmd.hasOption("exec_opt1"));
            assertTrue(cmd.hasOption("exec_opt2"));
        } else if (cmd.hasOption("rep")) {
            cmd = parser.parse(repOptions, argv, false);
            // process the rep_op1 and rep_opt2...
        } else {
            fail("exec option not found");
        }
    }


    @Test
    public void testPosixPasswordCase() throws Exception {
        final Options options = new Options();
        OptionBuilder.withLongOpt("old-password");
        OptionBuilder.withDescription("Use this option to specify the old password");
        OptionBuilder.hasArg();
        final Option oldpass = OptionBuilder.create('o');
        OptionBuilder.withLongOpt("new-password");
        OptionBuilder.withDescription("Use this option to specify the new password");
        OptionBuilder.hasArg();
        final Option newpass = OptionBuilder.create('n');

        final String[] args = {"-o", "-n", "newpassword"};

        options.addOption(oldpass);
        options.addOption(newpass);

        final Parser parser = new PosixParser();

        try {
            parser.parse(options, args);
        }
        // catch the exception and leave the method
        catch (final Exception exp) {
            assertNotNull(exp);
            return;
        }
        fail("MissingArgumentException not caught.");
    }


    @Test
    public void testPropertiesOption() throws Exception {
        final String[] args = new String[]{"-Jsource=1.5", "-J", "target", "1.5", "foo"};

        final Options options = new Options();
        OptionBuilder.withValueSeparator();
        OptionBuilder.hasArgs(2);
        options.addOption(OptionBuilder.create('J'));

        final ArgumentList argList = parser.parse(options, args);

        final List values = Arrays.asList(argList.getOptionValues("J"));
        assertNotNull(values, "null values");
        assertEquals(4, values.size(), "number of values");
        assertEquals("source", values.get(0));
        assertEquals("1.5", values.get(1));
        assertEquals("target", values.get(2));
        assertEquals("1.5", values.get(3));
        final List argsleft = argList.getArgList();
        assertEquals(1, argsleft.size(), "Should be 1 arg left");
        assertEquals("foo", argsleft.get(0), "Expecting foo");
    }


    @Test
    public void testQuoted() throws Exception {
        final ArgumentParser parser = new PosixParser();
        final String[] args = new String[]{"-m", "\"Two Words\""};
        OptionBuilder.hasArgs();
        final Option m = OptionBuilder.create("m");
        final Options options = new Options();
        options.addOption(m);
        final ArgumentList line = parser.parse(options, args);
        assertEquals("Two Words", line.getOptionValue("m"));
    }


    @Test
    public void testSep() throws Exception {
        final Options options = new Options();
        OptionBuilder.withValueSeparator('=');
        OptionBuilder.hasArgs();
        options.addOption(OptionBuilder.create('D'));
        OptionBuilder.withValueSeparator(':');
        OptionBuilder.hasArgs();
        options.addOption(OptionBuilder.create('p'));
        final String[] args = new String[]{"-DJAVA_HOME=/opt/java", "-pfile1:file2:file3"};

        final ArgumentParser parser = new PosixParser();

        final ArgumentList cmd = parser.parse(options, args);

        String[] values = cmd.getOptionValues('D');

        assertEquals(values[0], "JAVA_HOME");
        assertEquals(values[1], "/opt/java");

        values = cmd.getOptionValues('p');

        assertEquals(values[0], "file1");
        assertEquals(values[1], "file2");
        assertEquals(values[2], "file3");

        final Iterator iter = cmd.iterator();
        while (iter.hasNext()) {
            final Option opt = (Option) iter.next();
            switch (opt.getId()) {
                case 'D':
                    assertEquals(opt.getValue(0), "JAVA_HOME");
                    assertEquals(opt.getValue(1), "/opt/java");
                    break;
                case 'p':
                    assertEquals(opt.getValue(0), "file1");
                    assertEquals(opt.getValue(1), "file2");
                    assertEquals(opt.getValue(2), "file3");
                    break;
                default:
                    fail("-D option not found");
            }
        }
    }


    @Test
    public void testShortWithEqual() throws Exception {
        final String[] args = new String[]{"-f=bar"};

        final Options options = new Options();
        OptionBuilder.withLongOpt("foo");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create('f'));

        final ArgumentList argList = parser.parse(options, args);

        assertEquals("bar", argList.getOptionValue("foo"));
    }


    @Test
    public void testShortWithoutEqual() throws Exception {
        final String[] args = new String[]{"-fbar"};

        final Options options = new Options();
        OptionBuilder.withLongOpt("foo");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create('f'));

        final ArgumentList argList = parser.parse(options, args);

        assertEquals("bar", argList.getOptionValue("foo"));
    }


    @Test
    public void testSimpleLong() throws Exception {
        final String[] args = new String[]{"--enable-a", "--bfile", "toast", "foo", "bar"};

        final ArgumentList argList = parser.parse(options, args);

        assertTrue(argList.hasOption("a"), "Confirm -a is set");
        assertTrue(argList.hasOption("b"), "Confirm -b is set");
        assertEquals("toast", argList.getOptionValue("b"), "Confirm arg of -b");
        assertEquals("toast", argList.getOptionValue("bfile"), "Confirm arg of --bfile");
        assertEquals(2, argList.getArgList().size(), "Confirm size of extra args");
    }


    @Test
    public void testSimpleShort() throws Exception {
        final String[] args = new String[]{"-a", "-b", "toast", "foo", "bar"};

        final ArgumentList argList = parser.parse(options, args);

        assertTrue(argList.hasOption("a"), "Confirm -a is set");
        assertTrue(argList.hasOption("b"), "Confirm -b is set");
        assertEquals("toast", argList.getOptionValue("b"), "Confirm arg of -b");
        assertEquals(2, argList.getArgList().size(), "Confirm size of extra args");
    }


    @Test
    public void testSingleDash() throws Exception {
        final String[] args = new String[]{"--copt", "-b", "-", "-a", "-"};

        final ArgumentList argList = parser.parse(options, args);

        assertTrue(argList.hasOption("a"), "Confirm -a is set");
        assertTrue(argList.hasOption("b"), "Confirm -b is set");
        assertEquals("-", argList.getOptionValue("b"), "Confirm arg of -b");
        assertEquals(1, argList.getArgList().size(), "Confirm 1 extra arg: " + argList.getArgList().size());
        assertEquals("-", argList.getArgList().get(0), "Confirm value of extra arg: " + argList.getArgList().get(0));
    }


    @Test
    public void testStopAtExpectedArg() throws Exception {
        final String[] args = new String[]{"-b", "foo"};

        final ArgumentList argList = parser.parse(options, args, true);

        assertTrue(argList.hasOption('b'), "Confirm -b is set");
        assertEquals("foo", argList.getOptionValue('b'), "Confirm -b is set");
        assertEquals(0, argList.getArgList().size(), "Confirm no extra args: " + argList.getArgList().size());
    }


    @Test
    public void testStopAtNonOptionLong() throws Exception {
        final String[] args = new String[]{"--zop==1", "-abtoast", "--b=bar"};

        final ArgumentList argList = parser.parse(options, args, true);

        assertFalse(argList.hasOption("a"), "Confirm -a is not set");
        assertFalse(argList.hasOption("b"), "Confirm -b is not set");
        // TODO: assertTrue( "Confirm  3 extra args: " + argList.getArgList().size(), argList.getArgList().size() == 3 );
    }


    @Test
    public void testStopAtNonOptionShort() throws Exception {
        final String[] args = new String[]{"-z", "-a", "-btoast"};

        final ArgumentList argList = parser.parse(options, args, true);
        assertFalse(argList.hasOption("a"), "Confirm -a is not set");
        assertEquals(3, argList.getArgList().size(), "Confirm  3 extra args: " + argList.getArgList().size());
    }


    @Test
    public void testStopAtUnexpectedArg() throws Exception {
        final String[] args = new String[]{"-c", "foober", "-b", "toast"};

        final ArgumentList argList = parser.parse(options, args, true);
        assertTrue(argList.hasOption("c"), "Confirm -c is set");
        assertEquals(3, argList.getArgList().size(), "Confirm 3 extra args: " + argList.getArgList().size());
    }


    @Test
    public void testUnrecognizedOption() throws Exception {
        final String[] args = new String[]{"-a", "-d", "-b", "toast", "foo", "bar"};

        try {
            parser.parse(options, args);
            fail("UnrecognizedOptionException wasn't thrown");
        } catch (final UnrecognizedOptionException e) {
            assertEquals("-d", e.getOption());
        }
    }
}
