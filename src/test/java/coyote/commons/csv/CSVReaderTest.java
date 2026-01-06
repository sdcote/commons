package coyote.commons.csv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;


public class CSVReaderTest {

    CSVReader csvr;


    /**
     * Setup the test.
     */
    @BeforeEach
    public void setUp() throws Exception {
        String sb = "a,b,c" + "\n" +
                "a,\"b,b,b\",c" + "\n" +
                ",," + "\n" +
                "Dude,\"45 Rockefeller Plaza,\nNew York, NY\n10111\",USA.\n" +
                "\"Rosco \"\"P\"\" Coltrane\",Sheriff\n" +
                "\"\"\"\"\"\",\"test\"\n" +
                "\"a\nb\",b,\"\nd\",e\n";
        csvr = new CSVReader(new StringReader(sb));
    }


    @Test
    public void testParseLine() throws IOException, ParseException {

        // test normal case
        String[] nextLine = csvr.readNext();
        assertEquals("a", nextLine[0]);
        assertEquals("b", nextLine[1]);
        assertEquals("c", nextLine[2]);

        // test quoted commas
        nextLine = csvr.readNext();
        assertEquals("a", nextLine[0]);
        assertEquals("b,b,b", nextLine[1]);
        assertEquals("c", nextLine[2]);

        // test empty elements
        nextLine = csvr.readNext();
        assertEquals(3, nextLine.length);

        // test multiline quoted
        nextLine = csvr.readNext();
        assertEquals(3, nextLine.length);

        // test quoted quote chars
        nextLine = csvr.readNext();
        assertEquals("Rosco \"P\" Coltrane", nextLine[0]);

        nextLine = csvr.readNext();
        assertEquals("\"\"", nextLine[0]);
        assertEquals("test", nextLine[1]);

        nextLine = csvr.readNext();
        assertEquals(4, nextLine.length);

        // test end of stream
        assertNull(csvr.readNext());

    }


    @Test
    public void testParseLineStrictQuote() throws IOException, ParseException {
        String sb = "a,b,c" + "\n" +
                "a,\"b,b,b\",c" + "\n" +
                ",," + "\n" +
                "Dude,\"45 Rockefeller Plaza,\nNew York, NY\n10111\",USA.\n" +
                "\"Rosco \"\"P\"\" Coltrane\",Sheriff\n" +
                "\"\"\"\"\"\",\"test\"\n" +
                "\"a\nb\",b,\"\nd\",e\n";
        csvr = new CSVReader(new StringReader(sb), ',', '\"', true);

        // test normal case
        String[] nextLine = csvr.readNext();
        assertEquals("", nextLine[0]);
        assertEquals("", nextLine[1]);
        assertEquals("", nextLine[2]);

        // test quoted commas
        nextLine = csvr.readNext();
        assertEquals("", nextLine[0]);
        assertEquals("b,b,b", nextLine[1]);
        assertEquals("", nextLine[2]);

        // test empty elements
        nextLine = csvr.readNext();
        assertEquals(3, nextLine.length);

        // test multiline quoted
        nextLine = csvr.readNext();
        assertEquals(3, nextLine.length);

        // test quoted quote chars
        nextLine = csvr.readNext();
        assertEquals("Rosco \"P\" Coltrane", nextLine[0]);

        nextLine = csvr.readNext();
        assertEquals("\"\"", nextLine[0]);
        assertEquals("test", nextLine[1]);

        nextLine = csvr.readNext();
        assertEquals(4, nextLine.length);
        assertEquals("a\nb", nextLine[0]);
        assertEquals("", nextLine[1]);
        assertEquals("\nd", nextLine[2]);
        assertEquals("", nextLine[3]);

        // test end of stream
        assertNull(csvr.readNext());
    }


    @Test
    public void testParseAll() throws IOException, ParseException {
        assertEquals(7, csvr.readAll().size());
    }


    @Test
    public void testSingleQuoted() throws IOException, ParseException {

        final CSVReader c = new CSVReader(new StringReader("a,'''',c" + "\n"), ',', '\'');

        final String[] nextLine = c.readNext();
        assertEquals(3, nextLine.length);

        assertEquals("a", nextLine[0]);
        assertEquals(1, nextLine[1].length());
        assertEquals("'", nextLine[1]);
        assertEquals("c", nextLine[2]);
        c.close();

    }


    @Test
    public void testEmptySingleQuoted() throws IOException, ParseException {

        final CSVReader c = new CSVReader(new StringReader("a,'',c" + "\n"), ',', '\'');

        final String[] nextLine = c.readNext();
        assertEquals(3, nextLine.length);

        assertEquals("a", nextLine[0]);
        assertEquals(0, nextLine[1].length());
        assertEquals("", nextLine[1]);
        assertEquals("c", nextLine[2]);
        c.close();

    }


    @Test
    public void testExtraWhitespace() throws IOException, ParseException {

        final CSVReader c = new CSVReader(new StringReader("\"a\",\"b\",\"c\"   "), CSVParser.SEPARATOR, CSVParser.QUOTE_CHARACTER, true);

        final String[] nextLine = c.readNext();
        assertEquals(3, nextLine.length);

        assertEquals("a", nextLine[0]);
        assertEquals("b", nextLine[1]);
        assertEquals("c", nextLine[2]);
        c.close();
    }


    @Test
    public void testEscapedQuote() throws IOException, ParseException {

        final CSVReader c = new CSVReader(new StringReader("a,\"123\\\"4567\",c" + "\n"));

        final String[] nextLine = c.readNext();
        assertEquals(3, nextLine.length);

        assertEquals("123\"4567", nextLine[1]);
        c.close();
    }


    @Test
    public void testEscapedEscape() throws IOException, ParseException {

        final CSVReader c = new CSVReader(new StringReader("a,\"123\\\\4567\",c" + "\n"));

        final String[] nextLine = c.readNext();
        assertEquals(3, nextLine.length);

        assertEquals("123\\4567", nextLine[1]);
        c.close();
    }


    @Test
    public void testDoubleQuotedSingleQuote() throws IOException, ParseException {

        final CSVReader c = new CSVReader(new StringReader("a,'',c" + "\n"// a,'',c
        ));

        final String[] nextLine = c.readNext();
        assertEquals(3, nextLine.length);

        assertEquals("a", nextLine[0]);
        assertEquals(2, nextLine[1].length());
        assertEquals("''", nextLine[1]);
        assertEquals("c", nextLine[2]);
        c.close();
    }


    @Test
    public void testQuotedParsedLine() throws IOException, ParseException {

        final CSVReader c = new CSVReader(new StringReader("\"a\",\"1234567\",\"c\"" + "\n"), CSVParser.SEPARATOR, CSVParser.QUOTE_CHARACTER, true);

        final String[] nextLine = c.readNext();
        assertEquals(3, nextLine.length);

        assertEquals("a", nextLine[0]);
        assertEquals(1, nextLine[0].length());

        assertEquals("1234567", nextLine[1]);
        assertEquals("c", nextLine[2]);
        c.close();

    }


    @Test
    public void testOutOfPlaceQuotes() throws IOException, ParseException {

        final CSVReader c = new CSVReader(new StringReader("a,b,c,ddd\\\"eee\nf,g,h,\"iii,jjj\""));

        final String[] nextLine = c.readNext();

        assertEquals("a", nextLine[0]);
        assertEquals("b", nextLine[1]);
        assertEquals("c", nextLine[2]);
        assertEquals("ddd\"eee", nextLine[3]);
        c.close();

    }


    //@Test(expected = UnsupportedOperationException.class)
    @Test
    public void quoteAndEscapeMustBeDifferent() {
        assertThrows(UnsupportedOperationException.class, () -> {
            new CSVReader(new StringReader("a,b,c,ddd\\\"eee\nf,g,h,\"iii,jjj\""), CSVParser.SEPARATOR, CSVParser.QUOTE_CHARACTER, CSVParser.QUOTE_CHARACTER, CSVReader.LINES_TO_SKIP, CSVParser.STRICT_QUOTES, CSVParser.IGNORE_LEADING_WHITESPACE);
        });
    }


    @Test
    public void testSepAndEsc() {
        assertThrows(UnsupportedOperationException.class, () -> {
            new CSVReader(new StringReader("a,b,c,ddd\\\"eee\nf,g,h,\"iii,jjj\""), CSVParser.SEPARATOR, CSVParser.QUOTE_CHARACTER, CSVParser.SEPARATOR, CSVReader.LINES_TO_SKIP, CSVParser.STRICT_QUOTES, CSVParser.IGNORE_LEADING_WHITESPACE);
        });
    }


    @Test
    public void testSepAndQuote() {
        assertThrows(UnsupportedOperationException.class, () -> {
            new CSVReader(new StringReader("a,b,c,ddd\\\"eee\nf,g,h,\"iii,jjj\""), CSVParser.SEPARATOR, CSVParser.SEPARATOR, CSVParser.ESCAPE_CHARACTER, CSVReader.LINES_TO_SKIP, CSVParser.STRICT_QUOTES, CSVParser.IGNORE_LEADING_WHITESPACE);
        });
    }


    @Test
    public void testOptionalConstructors() throws IOException, ParseException {

        String sb = "a\tb\tc" + "\n" +
                "a\t'b\tb\tb'\tc" + "\n";
        final CSVReader c = new CSVReader(new StringReader(sb), '\t', '\'');

        String[] nextLine = c.readNext();
        assertEquals(3, nextLine.length);

        nextLine = c.readNext();
        assertEquals(3, nextLine.length);
        c.close();

    }


    @Test
    public void testDelim() throws IOException, ParseException {

        final CSVReader c = new CSVReader(new StringReader("a\tb\tc" + "\n"), '\t');

        final String[] nextLine = c.readNext();
        assertEquals(3, nextLine.length);
        c.close();

    }


    @Test
    public void testSkippingLines() throws IOException, ParseException {

        String sb = "Skip this line\t with tab" + "\n" +
                "And this line too" + "\n" +
                "a\t'b\tb\tb'\tc" + "\n";
        final CSVReader c = new CSVReader(new StringReader(sb), '\t', '\'', 2);

        final String[] nextLine = c.readNext();
        assertEquals(3, nextLine.length);

        assertEquals("a", nextLine[0]);
        c.close();

    }


    @Test
    public void testDiffEsc() throws IOException, ParseException {

        String sb = "Skip this line?t with tab" + "\n" +
                "And this line too" + "\n" +
                "a\t'b\tb\tb'\t'c'" + "\n";
        final CSVReader c = new CSVReader(new StringReader(sb), '\t', '\'', '?', 2);

        final String[] nextLine = c.readNext();

        assertEquals(3, nextLine.length);

        assertEquals("a", nextLine[0]);
        assertEquals("c", nextLine[2]);
        c.close();
    }


    @Test
    public void testNormalParsedLine() throws IOException, ParseException {

        final CSVReader c = new CSVReader(new StringReader("a,1234567,c" + "\n"));

        final String[] nextLine = c.readNext();
        assertEquals(3, nextLine.length);

        assertEquals("a", nextLine[0]);
        assertEquals("1234567", nextLine[1]);
        assertEquals("c", nextLine[2]);
        c.close();

    }

}
