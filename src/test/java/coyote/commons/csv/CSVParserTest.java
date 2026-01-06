package coyote.commons.csv;

//import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;


public class CSVParserTest {

    CSVParser csvParser;

    private static final String ESCAPE_TEST_STRING = "\\\\1\\2\\\"3\\";


    @BeforeEach
    public void setUp() {
        csvParser = new CSVParser();
    }


    @Test
    public void testParseLine() throws Exception {
        final String[] nextItem = csvParser.parseLine("This, is, a, test.");
        assertEquals(4, nextItem.length);
        assertEquals("This", nextItem[0]);
        assertEquals(" is", nextItem[1]);
        assertEquals(" a", nextItem[2]);
        assertEquals(" test.", nextItem[3]);
    }


    @Test
    public void parseSimpleString() throws IOException, ParseException {

        final String[] nextLine = csvParser.parseLine("a,b,c");
        assertEquals(3, nextLine.length);
        assertEquals("a", nextLine[0]);
        assertEquals("b", nextLine[1]);
        assertEquals("c", nextLine[2]);
        assertFalse(csvParser.isPending());
    }


    /**
     * Tests quotes in the middle of an element.
     *
     * @throws IOException if bad things happen
     */
    @Test
    public void testParsedLineWithInternalQuota() throws IOException, ParseException {

        final String[] nextLine = csvParser.parseLine("a,123\"4\"567,b");
        assertEquals(3, nextLine.length);

        assertEquals("123\"4\"567", nextLine[1]);

    }


    @Test
    public void parseQuotedStringWithCommas() throws IOException, ParseException {
        final String[] nextLine = csvParser.parseLine("a,\"b,b,b\",c");
        assertEquals("a", nextLine[0]);
        assertEquals("b,b,b", nextLine[1]);
        assertEquals("c", nextLine[2]);
        assertEquals(3, nextLine.length);
    }


    @Test
    public void parseQuotedStringWithDefinedSeperator() throws IOException, ParseException {
        csvParser = new CSVParser(':');

        final String[] nextLine = csvParser.parseLine("a:\"b:b:b\":c");
        assertEquals("a", nextLine[0]);
        assertEquals("b:b:b", nextLine[1]);
        assertEquals("c", nextLine[2]);
        assertEquals(3, nextLine.length);
    }


    @Test
    public void parseQuotedStringWithDefinedSeperatorAndQuote() throws IOException, ParseException {
        csvParser = new CSVParser(':', '\'');

        final String[] nextLine = csvParser.parseLine("a:'b:b:b':c");
        assertEquals("a", nextLine[0]);
        assertEquals("b:b:b", nextLine[1]);
        assertEquals("c", nextLine[2]);
        assertEquals(3, nextLine.length);
    }


    @Test
    public void parseEmptyElements() throws IOException, ParseException {
        final String[] nextLine = csvParser.parseLine(",,");
        assertEquals(3, nextLine.length);
        assertEquals("", nextLine[0]);
        assertEquals("", nextLine[1]);
        assertEquals("", nextLine[2]);
    }


    @Test
    public void parseMultiLinedQuoted() throws IOException, ParseException {
        final String[] nextLine = csvParser.parseLine("Dude,\"45 Rockefeller Plaza,\nNew York, NY\n10111\",USA.\n");
        assertEquals(3, nextLine.length);
        assertEquals("Dude", nextLine[0]);
        assertEquals("45 Rockefeller Plaza,\nNew York, NY\n10111", nextLine[1]);
        assertEquals("USA.\n", nextLine[2]);
    }


    @Test
    public void testADoubleQuoteAsDataElement() throws IOException, ParseException {

        final String[] nextLine = csvParser.parseLine("a,\"\"\"\",b");

        assertEquals(3, nextLine.length);

        assertEquals("a", nextLine[0]);
        assertEquals(1, nextLine[1].length());
        assertEquals("\"", nextLine[1]);
        assertEquals("b", nextLine[2]);

    }


    @Test
    public void testEscapedDoubleQuoteAsDataElement() throws IOException, ParseException {
        final String[] nextLine = csvParser.parseLine("\"test\",\"this,test,is,good\",\"\\\"test\\\"\",\"\\\"quote\\\"\"");

        assertEquals(4, nextLine.length);

        assertEquals("test", nextLine[0]);
        assertEquals("this,test,is,good", nextLine[1]);
        assertEquals("\"test\"", nextLine[2]);
        assertEquals("\"quote\"", nextLine[3]);

    }


    // @Test
    public void testEscapingSeparator() throws IOException, ParseException {
        final String[] nextLine = csvParser.parseLine("test,this\\,test\\,is\\,good");
        // "test","this,test,is,good","\"test\",\"quote\""

        assertEquals(2, nextLine.length);

        assertEquals("test", nextLine[0]);
        assertEquals("this,test,is,good", nextLine[1]);
    }


    @Test
    public void parseQuotedQuoteCharacters() throws IOException, ParseException {
        final String[] nextLine = csvParser.parseLineMulti("\"Rosco \"\"P\"\" Coltrane\",Sheriff\n");
        assertEquals(2, nextLine.length);
        assertEquals("Rosco \"P\" Coltrane", nextLine[0]);
        assertEquals("Sheriff\n", nextLine[1]);
    }


    @Test
    public void parseMultipleQuotes() throws IOException, ParseException {
        final String[] nextLine = csvParser.parseLine("\"\"\"\"\"\",\"test\"\n");

        assertEquals("\"\"", nextLine[0]);
        assertEquals("test\"\n", nextLine[1]);
        assertEquals(2, nextLine.length);
    }


    @Test
    public void parseTrickyString() throws IOException, ParseException {
        final String[] nextLine = csvParser.parseLine("\"a\nb\",b,\"\nd\",e\n");
        assertEquals(4, nextLine.length);
        assertEquals("a\nb", nextLine[0]);
        assertEquals("b", nextLine[1]);
        assertEquals("\nd", nextLine[2]);
        assertEquals("e\n", nextLine[3]);
    }


    private String setUpMultiLineInsideQuotes() {

        return "Small test,\"This is a test across \ntwo lines.\"";
    }


    @Test
    public void testAMultiLineInsideQuotes() throws IOException, ParseException {

        final String testString = setUpMultiLineInsideQuotes();

        final String[] nextLine = csvParser.parseLine(testString);
        assertEquals(2, nextLine.length);
        assertEquals("Small test", nextLine[0]);
        assertEquals("This is a test across \ntwo lines.", nextLine[1]);
        assertFalse(csvParser.isPending());
    }


    @Test
    public void testStrictQuoteSimple() throws IOException, ParseException {
        csvParser = new CSVParser(',', '\"', '\\', true);
        final String testString = "\"a\",\"b\",\"c\"";

        final String[] nextLine = csvParser.parseLine(testString);
        assertEquals(3, nextLine.length);
        assertEquals("a", nextLine[0]);
        assertEquals("b", nextLine[1]);
        assertEquals("c", nextLine[2]);
    }


    @Test
    public void testStrictQuoteWithSpacesAndTabs() throws IOException, ParseException {
        csvParser = new CSVParser(',', '\"', '\\', true);
        final String testString = " \t      \"a\",\"b\"      \t       ,   \"c\"   ";

        final String[] nextLine = csvParser.parseLine(testString);
        assertEquals(3, nextLine.length);
        assertEquals("a", nextLine[0]);
        assertEquals("b", nextLine[1]);
        assertEquals("c", nextLine[2]);
    }


    @Test
    public void testStrictQuoteWithGarbage() throws IOException, ParseException {
        csvParser = new CSVParser(',', '\"', '\\', true);
        final String testString = "abc',!@#\",\\\"\"   xyz,";

        final String[] nextLine = csvParser.parseLine(testString);
        assertEquals(3, nextLine.length);
        assertEquals("", nextLine[0]);
        assertEquals(",\"", nextLine[1]);
        assertEquals("", nextLine[2]);
    }


    @Test
    public void testNumbers() throws IOException, ParseException {
        csvParser = new CSVParser(',', '\'');

        final String[] nextLine = csvParser.parseLine("8675309,42,'Jenny\\'s_Number','',294,0,0,0.734338696798625,'20081002052147',242429208,222");

        assertEquals(11, nextLine.length);

        assertEquals("8675309", nextLine[0]);
        assertEquals("42", nextLine[1]);
        assertEquals("Jenny's_Number", nextLine[2]);
        assertEquals("", nextLine[3]);
        assertEquals("222", nextLine[10]);

    }


    @Test
    public void testIssue2859181() throws IOException, ParseException {
        csvParser = new CSVParser(';');
        final String[] nextLine = csvParser.parseLine("field1;\\=field2;\"\"\"field3\"\"\"");

        assertEquals(3, nextLine.length);

        assertEquals("field1", nextLine[0]);
        assertEquals("=field2", nextLine[1]);
        assertEquals("\"field3\"", nextLine[2]);

    }


    @Test
    public void testIssue2726363() throws IOException, ParseException {

        final String[] nextLine = csvParser.parseLine("\"Circuit\",\"Loop\",\"\"Circuit\"sonet\",\"size\",\"2.14159\",\"dark\"");

        assertEquals(6, nextLine.length);

        assertEquals("Circuit", nextLine[0]);
        assertEquals("Loop", nextLine[1]);
        assertEquals("\"Circuit\"sonet", nextLine[2]);
        assertEquals("size", nextLine[3]);
        assertEquals("2.14159", nextLine[4]);
        assertEquals("dark", nextLine[5]);

    }


    @Test
    public void anIOExceptionThrownifStringEndsInsideAQuotedString() throws IOException, ParseException {
        try {
            csvParser.parseLine("This,is a \"bad line to parse.");
            fail("Should throw an exception");
        } catch (final Exception e) {
            assertInstanceOf(ParseException.class, e);

        }

    }


    @Test
    public void parseLineMultiAllowsQuotesAcrossMultipleLines() throws IOException, ParseException {
        String[] nextLine = csvParser.parseLineMulti("This,\"is a \"good\" line\\\\ to parse");

        assertEquals(1, nextLine.length);
        assertEquals("This", nextLine[0]);
        assertTrue(csvParser.isPending());

        nextLine = csvParser.parseLineMulti("because we are using parseLineMulti.\"");

        assertEquals(1, nextLine.length);
        assertEquals("is a \"good\" line\\ to parse\nbecause we are using parseLineMulti.", nextLine[0]);
        assertFalse(csvParser.isPending());
    }


    @Test
    public void pendingIsClearedAfterCallToParseLine() throws IOException, ParseException {
        String[] nextLine = csvParser.parseLineMulti("This,\"is a \"good\" line\\\\ to parse");

        assertEquals(1, nextLine.length);
        assertEquals("This", nextLine[0]);
        assertTrue(csvParser.isPending());

        nextLine = csvParser.parseLine("because we are using parseLineMulti.");

        assertEquals(1, nextLine.length);
        assertEquals("because we are using parseLineMulti.", nextLine[0]);
        assertFalse(csvParser.isPending());
    }


    @Test
    public void returnPendingIfNullIsPassedIntoParseLineMulti() throws IOException, ParseException {
        String[] nextLine = csvParser.parseLineMulti("This,\"is a \"goo\\d\" line\\\\ to parse\\");

        assertEquals(1, nextLine.length);
        assertEquals("This", nextLine[0]);
        assertTrue(csvParser.isPending());

        nextLine = csvParser.parseLineMulti(null);

        assertEquals(1, nextLine.length);
        assertEquals("is a \"good\" line\\ to parse\n", nextLine[0]);
        assertFalse(csvParser.isPending());
    }


    @Test
    public void spacesAtEndOfQuotedStringDoNotCountIfStrictQuotesIsTrue() throws IOException, ParseException {
        final CSVParser parser = new CSVParser(CSVParser.SEPARATOR, CSVParser.QUOTE_CHARACTER, CSVParser.ESCAPE_CHARACTER, true);
        final String[] nextLine = parser.parseLine("\"Line with\", \"spaces at end\"  ");

        assertEquals(2, nextLine.length);
        assertEquals("Line with", nextLine[0]);
        assertEquals("spaces at end", nextLine[1]);
    }


    @Test
    public void returnNullWhenNullPassedIn() throws IOException, ParseException {
        final String[] nextLine = csvParser.parseLine(null);
        assertNull(nextLine);
    }


    @Test
    public void validateEscapeStringBeforeRealTest() {
        assertNotNull(ESCAPE_TEST_STRING);
        assertEquals(9, ESCAPE_TEST_STRING.length());
    }


    @Test
    public void whichCharactersAreEscapable() {
        assertTrue(csvParser.isNextCharacterEscapable(ESCAPE_TEST_STRING, true, 0));
        assertFalse(csvParser.isNextCharacterEscapable(ESCAPE_TEST_STRING, false, 0));
        // Second character is not escapable because there is a non quote or non
        // slash after it.

        assertFalse(csvParser.isNextCharacterEscapable(ESCAPE_TEST_STRING, true, 1));
        assertFalse(csvParser.isNextCharacterEscapable(ESCAPE_TEST_STRING, false, 1));
        // Fourth character is not escapable because there is a non quote or non
        // slash after it.

        assertFalse(csvParser.isNextCharacterEscapable(ESCAPE_TEST_STRING, true, 3));
        assertFalse(csvParser.isNextCharacterEscapable(ESCAPE_TEST_STRING, false, 3));

        assertTrue(csvParser.isNextCharacterEscapable(ESCAPE_TEST_STRING, true, 5));
        assertFalse(csvParser.isNextCharacterEscapable(ESCAPE_TEST_STRING, false, 5));

        final int lastChar = ESCAPE_TEST_STRING.length() - 1;
        assertFalse(csvParser.isNextCharacterEscapable(ESCAPE_TEST_STRING, true, lastChar));
        assertFalse(csvParser.isNextCharacterEscapable(ESCAPE_TEST_STRING, false, lastChar));

    }


    @Test
    public void whitespaceBeforeEscape() throws IOException, ParseException {
        final String[] nextItem = csvParser.parseLine("\"this\", \"is\",\"a test\"");

        assertEquals("this", nextItem[0]);
        assertEquals("is", nextItem[1]);
        assertEquals("a test", nextItem[2]);
    }


    @Test
    public void testIssue2958242WithoutQuotes() throws IOException, ParseException {
        final CSVParser testParser = new CSVParser('\t');
        final String[] nextItem = testParser.parseLine("buga\"\"boo\"\"at\t10-02-1980\t29\tC:\\\\foo.txt");
        assertEquals(4, nextItem.length);
        assertEquals("buga\"boo\"at", nextItem[0]);
        assertEquals("10-02-1980", nextItem[1]);
        assertEquals("29", nextItem[2]);
        assertEquals("C:\\foo.txt", nextItem[3]);
    }


    @Test
    public void quoteAndEscapeCanBeTheSameIfNull() {
        new CSVParser(CSVParser.SEPARATOR, CSVParser.NULL_CHARACTER, CSVParser.NULL_CHARACTER);
    }


    /**
     * Validates that the CSVParser constructor enforces distinct characters
     * for separators and quotes to prevent parsing ambiguity.
     */
    @Test
    @DisplayName("Constructor should throw exception when separator and quote are identical")
    void separatorAndQuoteCannotBeTheSame() {
        // The assertThrows method captures the exception thrown by the lambda expression.
        // This ensures that the exception is thrown specifically during instantiation.
        assertThrows(UnsupportedOperationException.class, () -> {
            new CSVParser(
                    CSVParser.SEPARATOR,
                    CSVParser.SEPARATOR,
                    CSVParser.ESCAPE_CHARACTER
            );
        });
    }

    @Test
    void separatorAndEscapeCannotBeTheSame() {
        assertThrows(UnsupportedOperationException.class, () -> {
            new CSVParser(CSVParser.SEPARATOR, CSVParser.QUOTE_CHARACTER, CSVParser.SEPARATOR);
        });
    }


    @Test
    void separatorCharacterCannotBeNull() {
        assertThrows(UnsupportedOperationException.class, () -> {
            new CSVParser(CSVParser.NULL_CHARACTER);
        });
    }


    @Test
    void quoteAndEscapeCannotBeTheSame() {
        assertThrows(UnsupportedOperationException.class, () -> {
            new CSVParser(CSVParser.SEPARATOR, CSVParser.QUOTE_CHARACTER, CSVParser.QUOTE_CHARACTER);
        });
    }


}