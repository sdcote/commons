package coyote.commons.cli;

//import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class OptionGroupTest {
    private static Options _options = null;
    private static final ArgumentParser parser = new PosixParser();


    @BeforeAll
    public static void setUp() {
        final Option file = new Option("f", "file", false, "file to process");
        final Option dir = new Option("d", "directory", false, "directory to process");
        final OptionGroup group = new OptionGroup();
        group.addOption(file);
        group.addOption(dir);
        _options = new Options().addOptionGroup(group);

        final Option section = new Option("s", "section", false, "section to process");
        final Option chapter = new Option("c", "chapter", false, "chapter to process");
        final OptionGroup group2 = new OptionGroup();
        group2.addOption(section);
        group2.addOption(chapter);

        _options.addOptionGroup(group2);

        final Option importOpt = new Option(null, "import", false, "section to process");
        final Option exportOpt = new Option(null, "export", false, "chapter to process");
        final OptionGroup group3 = new OptionGroup();
        group3.addOption(importOpt);
        group3.addOption(exportOpt);
        _options.addOptionGroup(group3);

        _options.addOption("r", "revision", false, "revision number");
    }


    @Test
    public void testGetNames() {
        final OptionGroup group = new OptionGroup();
        group.addOption(OptionBuilder.create('a'));
        group.addOption(OptionBuilder.create('b'));

        assertNotNull(group.getNames(), "null names");
        assertEquals(2, group.getNames().size());
        assertTrue(group.getNames().contains("a"));
        assertTrue(group.getNames().contains("b"));
    }


    @Test
    public void testNoOptionsExtraArgs() throws Exception {
        final String[] args = new String[]{"arg1", "arg2"};

        final ArgumentList argList = parser.parse(_options, args);

        assertFalse(argList.hasOption("r"), "Confirm -r is NOT set");
        assertFalse(argList.hasOption("f"), "Confirm -f is NOT set");
        assertFalse(argList.hasOption("d"), "Confirm -d is NOT set");
        assertFalse(argList.hasOption("s"), "Confirm -s is NOT set");
        assertFalse(argList.hasOption("c"), "Confirm -c is NOT set");
        assertEquals(2, argList.getArgList().size(), "Confirm TWO extra args");
    }


    @Test
    public void testSingleLongOption() throws Exception {
        final String[] args = new String[]{"--file"};

        final ArgumentList argList = parser.parse(_options, args);

        assertFalse(argList.hasOption("r"), "Confirm -r is NOT set");
        assertTrue(argList.hasOption("f"), "Confirm -f is set");
        assertFalse(argList.hasOption("d"), "Confirm -d is NOT set");
        assertFalse(argList.hasOption("s"), "Confirm -s is NOT set");
        assertFalse(argList.hasOption("c"), "Confirm -c is NOT set");
        assertEquals(0, argList.getArgList().size(), "Confirm no extra args");
    }


    @Test
    public void testSingleOption() throws Exception {
        final String[] args = new String[]{"-r"};

        final ArgumentList argList = parser.parse(_options, args);

        assertTrue(argList.hasOption("r"), "Confirm -r is set");
        assertFalse(argList.hasOption("f"), "Confirm -f is NOT set");
        assertFalse(argList.hasOption("d"), "Confirm -d is NOT set");
        assertFalse(argList.hasOption("s"), "Confirm -s is NOT set");
        assertFalse(argList.hasOption("c"), "Confirm -c is NOT set");
        assertEquals(0, argList.getArgList().size(), "Confirm no extra args");
    }


    @Test
    public void testSingleOptionFromGroup() throws Exception {
        final String[] args = new String[]{"-f"};

        final ArgumentList argList = parser.parse(_options, args);

        assertFalse(argList.hasOption("r"), "Confirm -r is NOT set");
        assertTrue(argList.hasOption("f"), "Confirm -f is set");
        assertFalse(argList.hasOption("d"), "Confirm -d is NOT set");
        assertFalse(argList.hasOption("s"), "Confirm -s is NOT set");
        assertFalse(argList.hasOption("c"), "Confirm -c is NOT set");
        assertEquals(0, argList.getArgList().size(), "Confirm no extra args");
    }


    @Test
    public void testToString() {
        final OptionGroup group1 = new OptionGroup();
        group1.addOption(new Option(null, "foo", false, "Foo"));
        group1.addOption(new Option(null, "bar", false, "Bar"));

        if (!"{--bar Bar, --foo Foo}".equals(group1.toString())) {
            assertEquals("{--foo Foo, --bar Bar}", group1.toString());
        }

        final OptionGroup group2 = new OptionGroup();
        group2.addOption(new Option("f", "foo", false, "Foo"));
        group2.addOption(new Option("b", "bar", false, "Bar"));

        if (!"{-b Bar, -f Foo}".equals(group2.toString())) {
            assertEquals("{-f Foo, -b Bar}", group2.toString());
        }
    }


    @Test
    public void testTwoLongOptionsFromGroup() throws Exception {
        final String[] args = new String[]{"--file", "--directory"};

        try {
            parser.parse(_options, args);
            fail("two arguments from group not allowed");
        } catch (final AlreadySelectedException e) {
            assertNotNull(e.getOptionGroup(), "null option group");
            assertEquals("f", e.getOptionGroup().getSelected(), "selected option");
            assertEquals("d", e.getOption().getOpt(), "option");
        }
    }


    @Test
    public void testTwoOptionsFromDifferentGroup() throws Exception {
        final String[] args = new String[]{"-f", "-s"};

        final ArgumentList argList = parser.parse(_options, args);
        assertFalse(argList.hasOption("r"), "Confirm -r is NOT set");
        assertTrue(argList.hasOption("f"), "Confirm -f is set");
        assertFalse(argList.hasOption("d"), "Confirm -d is NOT set");
        assertTrue(argList.hasOption("s"), "Confirm -s is set");
        assertFalse(argList.hasOption("c"), "Confirm -c is NOT set");
        assertEquals(0, argList.getArgList().size(), "Confirm NO extra args");
    }


    @Test
    public void testTwoOptionsFromGroup() throws Exception {
        final String[] args = new String[]{"-f", "-d"};

        try {
            parser.parse(_options, args);
            fail("two arguments from group not allowed");
        } catch (final AlreadySelectedException e) {
            assertNotNull(e.getOptionGroup(), "null option group");
            assertEquals("f", e.getOptionGroup().getSelected(), "selected option");
            assertEquals("d", e.getOption().getOpt());
        }
    }


    @Test
    public void testTwoValidLongOptions() throws Exception {
        final String[] args = new String[]{"--revision", "--file"};

        final ArgumentList argList = parser.parse(_options, args);

        assertTrue(argList.hasOption("r"), "Confirm -r is set");
        assertTrue(argList.hasOption("f"), "Confirm -f is set");
        assertFalse(argList.hasOption("d"), "Confirm -d is NOT set");
        assertFalse(argList.hasOption("s"), "Confirm -s is NOT set");
        assertFalse(argList.hasOption("c"), "Confirm -c is NOT set");
        assertEquals(0, argList.getArgList().size(), "Confirm no extra args");
    }


    @Test
    public void testTwoValidOptions() throws Exception {
        final String[] args = new String[]{"-r", "-f"};

        final ArgumentList argList = parser.parse(_options, args);

        assertTrue(argList.hasOption("r"), "Confirm -r is set");
        assertTrue(argList.hasOption("f"), "Confirm -f is set");
        assertFalse(argList.hasOption("d"), "Confirm -d is NOT set");
        assertFalse(argList.hasOption("s"), "Confirm -s is NOT set");
        assertFalse(argList.hasOption("c"), "Confirm -c is NOT set");
        assertEquals(0, argList.getArgList().size(), "Confirm no extra args");
    }


    @Test
    public void testValidLongOnlyOptions() throws Exception {
        final ArgumentList cl1 = parser.parse(_options, new String[]{"--export"});
        assertTrue(cl1.hasOption("export"), "Confirm --export is set");

        final ArgumentList cl2 = parser.parse(_options, new String[]{"--import"});
        assertTrue(cl2.hasOption("import"), "Confirm --import is set");
    }

}
