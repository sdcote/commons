package coyote.commons.cli;

//import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class PatternOptionBuilderTest {
    @Test
    public void testClassPattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("c+d+");
        final ArgumentParser parser = new PosixParser();
        final ArgumentList line = parser.parse(options, new String[]{"-c", "java.util.Calendar", "-d", "System.DateTime"});

        assertEquals(Calendar.class, line.getParsedOptionValue("c"));
        // TODO: assertNull( "d value", line.getParsedOptionValue( "d" ) );
    }


    @Test
    public void testEmptyPattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("");
        assertTrue(options.getOptions().isEmpty());
    }


    @Test
    public void testExistingFilePattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("f<");
        final ArgumentParser parser = new PosixParser();
        final ArgumentList line = parser.parse(options, new String[]{"-f", "test.properties"});

        assertEquals(new File("test.properties"), line.getParsedOptionValue("f"));

        // TODO: test if an error is returned if the file doesn't exists (when it's implemented)
    }


    @Test
    public void testNumberPattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("n%d%x%");
        final ArgumentParser parser = new PosixParser();
        final ArgumentList line = parser.parse(options, new String[]{"-n", "1", "-d", "2.1", "-x", "3,5"});

        assertEquals(Long.class, line.getParsedOptionValue("n").getClass());
        assertEquals(Long.valueOf(1), line.getParsedOptionValue("n"));

        assertEquals(Double.class, line.getParsedOptionValue("d").getClass());
        assertEquals(new Double(2.1), line.getParsedOptionValue("d"));

        // TODO: assertNull( "x object", line.getParsedOptionValue( "x" ) );
    }


    @Test
    public void testObjectPattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("o@i@n@");
        final ArgumentParser parser = new PosixParser();
        final ArgumentList line = parser.parse(options, new String[]{"-o", "java.lang.String", "-i", "java.util.Calendar", "-n", "System.DateTime"});

        assertEquals("", line.getParsedOptionValue("o"));
        // TODO: assertNull( "i value", line.getParsedOptionValue( "i" ) );
        // TODO: assertNull( "n value", line.getParsedOptionValue( "n" ) );
    }


    @Test
    public void testRequiredOption() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("!n%m%");
        final ArgumentParser parser = new PosixParser();

        try {
            parser.parse(options, new String[]{""});
            fail("MissingOptionException wasn't thrown");
        } catch (final MissingOptionException e) {
            assertEquals(1, e.getMissingOptions().size());
            assertTrue(e.getMissingOptions().contains("n"));
        }
    }


    @Test
    public void testSimplePattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("a:b@cde>f+n%t/m*z#");
        final String[] args = new String[]{"-c", "-a", "foo", "-b", "java.util.Vector", "-e", "build.xml", "-f", "java.util.Calendar", "-n", "4.5", "-t", "http://commons.apache.org", "-z", "Thu Jun 06 17:48:57 EDT 2002", "-m", "test*"};

        final ArgumentParser parser = new PosixParser();
        final ArgumentList argList = parser.parse(options, args);

        assertEquals("foo", argList.getOptionValue("a"));
        assertEquals("foo", argList.getParsedOptionValue("a"));
        assertEquals(new Vector(), argList.getParsedOptionValue("b"));
        assertTrue(argList.hasOption("c"));
        assertFalse(argList.hasOption("d"));
        assertEquals(new File("build.xml"), argList.getParsedOptionValue("e"));
        assertEquals(Calendar.class, argList.getParsedOptionValue("f"));
        assertEquals(new Double(4.5), argList.getParsedOptionValue("n"));
        assertEquals(new URL("http://commons.apache.org"), argList.getParsedOptionValue("t"));

        // tests the char methods of ArgumentList that delegate to the String methods
        assertEquals("foo", argList.getOptionValue('a'));
        assertEquals("foo", argList.getParsedOptionValue('a'));
        assertEquals(new Vector(), argList.getParsedOptionValue('b'));
        assertTrue(argList.hasOption('c'));
        assertFalse(argList.hasOption('d'));
        assertEquals(new File("build.xml"), argList.getParsedOptionValue('e'));
        assertEquals(Calendar.class, argList.getParsedOptionValue('f'));
        assertEquals(new Double(4.5), argList.getParsedOptionValue('n'));
        assertEquals(new URL("http://commons.apache.org"), argList.getParsedOptionValue('t'));

        // FILES NOT SUPPORTED YET
        try {
            assertEquals(new File[0], argList.getParsedOptionValue('m'));
            fail("Multiple files are not supported yet, should have failed");
        } catch (final UnsupportedOperationException uoe) {
            // expected
        }

        // DATES NOT SUPPORTED YET
        try {
            assertEquals(new Date(1023400137276L), argList.getParsedOptionValue('z'));
            fail("Date is not supported yet, should have failed");
        } catch (final UnsupportedOperationException uoe) {
            // expected
        }
    }


    @Test
    public void testUntypedPattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("abc");
        final ArgumentParser parser = new PosixParser();
        final ArgumentList argList = parser.parse(options, new String[]{"-abc"});

        assertTrue(argList.hasOption('a'));
        assertNull(argList.getParsedOptionValue('a'));
        assertTrue(argList.hasOption('b'));
        assertNull(argList.getParsedOptionValue('b'));
        assertTrue(argList.hasOption('c'));
        assertNull(argList.getParsedOptionValue('c'));
    }


    @Test
    public void testURLPattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("u/v/");
        final ArgumentParser parser = new PosixParser();
        final ArgumentList argList = parser.parse(options, new String[]{"-u", "http://commons.apache.org", "-v", "foo://commons.apache.org"});

        assertEquals(new URL("http://commons.apache.org"), argList.getParsedOptionValue("u"));
        // TODO: assertNull( "v value", line.getParsedOptionValue( "v" ) );
    }
}
