package coyote.commons.cli;

//import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ArgumentIsOptionTest {
    private static Options options = null;
    private static ArgumentParser parser = null;


    @BeforeAll
    public static void setUp() {
        options = new Options().addOption("p", false, "Option p").addOption("attr", true, "Option accepts argument");

        parser = new PosixParser();
    }


    @Test
    public void testOption() throws Exception {
        final String[] args = new String[]{"-p"};

        final ArgumentList argList = parser.parse(options, args);
        assertTrue(argList.hasOption("p"), "Confirm -p is set");
        assertFalse(argList.hasOption("attr"), "Confirm -attr is not set");
        assertEquals(0, argList.getArgs().length, "Confirm all arguments recognized");
    }


    @Test
    public void testOptionAndOptionWithArgument() throws Exception {
        final String[] args = new String[]{"-p", "-attr", "p"};

        final ArgumentList argList = parser.parse(options, args);
        assertTrue(argList.hasOption("p"), "Confirm -p is set");
        assertTrue(argList.hasOption("attr"), "Confirm -attr is set");
        assertEquals("p", argList.getOptionValue("attr"), "Confirm arg of -attr");
        assertEquals(0, argList.getArgs().length, "Confirm all arguments recognized");
    }


    @Test
    public void testOptionWithArgument() throws Exception {
        final String[] args = new String[]{"-attr", "p"};

        final ArgumentList argList = parser.parse(options, args);
        assertFalse(argList.hasOption("p"), "Confirm -p is set");
        assertTrue(argList.hasOption("attr"), "Confirm -attr is set");
        assertEquals("p", argList.getOptionValue("attr"), "Confirm arg of -attr");
        assertEquals(0, argList.getArgs().length, "Confirm all arguments recognized");
    }
}
