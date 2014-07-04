package coyote.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CSVWriterTest {

	@Test
	public void testAlternateEscapeChar() throws IOException {
		final String[] line = { "Foo", "bar's" };
		final StringWriter sw = new StringWriter();
		final CSVWriter csvw = new CSVWriter(sw, CSVWriter.SEPARATOR, CSVWriter.QUOTE_CHARACTER, '\'');
		csvw.writeNext(line);
		csvw.close();

		assertEquals("\"Foo\",\"bar''s\"\n", sw.toString());
	}




	@Test
	public void testNoQuotingNoEscaping() throws IOException {
		final String[] line = { "\"Foo\",\"Bar\"" };
		final StringWriter sw = new StringWriter();
		final CSVWriter csvw = new CSVWriter(sw, CSVWriter.SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER);
		csvw.writeNext(line);
		csvw.close();

		assertEquals("\"Foo\",\"Bar\"\n", sw.toString());
	}




	@Test
	public void testNestedQuotes() {
		final String[] data = new String[] { "\"\"", "test" };
		final String oracle = new String("\"\"\"\"\"\",\"test\"\n");

		CSVWriter writer = null;
		File tempFile = null;
		FileWriter fwriter = null;

		try {
			tempFile = File.createTempFile("csvWriterTest", ".csv");
			tempFile.deleteOnExit();
			fwriter = new FileWriter(tempFile);
			writer = new CSVWriter(fwriter);
		} catch (final IOException e) {
			fail();
		}

		// write the test data:
		writer.writeNext(data);

		try {
			writer.close();
		} catch (final IOException e) {
			fail();
		}

		try {
			// assert that the writer was also closed.
			fwriter.flush();
			fail();
		} catch (final IOException e) {
			// we should go through here..
		}

		// read the data and compare.
		FileReader in = null;
		try {
			in = new FileReader(tempFile);
		} catch (final FileNotFoundException e) {
			fail();
		}

		final StringBuilder fileContents = new StringBuilder(CSVWriter.INITIAL_STRING_SIZE);
		try {
			int ch;
			while ((ch = in.read()) != -1) {
				fileContents.append((char) ch);
			}
			in.close();
		} catch (final IOException e) {
			fail();
		}

		assertTrue(oracle.equals(fileContents.toString()));
	}




	@Test
	public void testAlternateLineFeeds() throws IOException {
		final String[] line = { "Foo", "Bar", "baz" };
		final StringWriter sw = new StringWriter();
		final CSVWriter csvw = new CSVWriter(sw, CSVWriter.SEPARATOR, CSVWriter.QUOTE_CHARACTER, "\r");
		csvw.writeNext(line);
		final String result = sw.toString();
		csvw.close();
		assertTrue(result.endsWith("\r"));

	}




	private String invokeWriter(final String[] args) throws IOException {
		final StringWriter sw = new StringWriter();
		final CSVWriter csvw = new CSVWriter(sw, ',', '\'');
		csvw.writeNext(args);
		csvw.close();
		return sw.toString();
	}




	private String invokeNoEscapeWriter(final String[] args) throws IOException {
		final StringWriter sw = new StringWriter();
		final CSVWriter csvw = new CSVWriter(sw, CSVWriter.SEPARATOR, '\'', CSVWriter.NO_ESCAPE_CHARACTER);
		csvw.writeNext(args);
		csvw.close();
		return sw.toString();
	}




	@Test
	public void correctlyParseNullString() throws IOException {
		final StringWriter sw = new StringWriter();
		final CSVWriter csvw = new CSVWriter(sw, ',', '\'');
		csvw.writeNext(null);
		assertEquals(0, sw.toString().length());
		csvw.close();
	}




	@Test
	public void testParseLine() throws IOException {

		// test normal case
		final String[] normal = { "a", "b", "c" };
		String output = invokeWriter(normal);
		assertEquals("'a','b','c'\n", output);

		// test quoted commas
		final String[] quoted = { "a", "b,b,b", "c" };
		output = invokeWriter(quoted);
		assertEquals("'a','b,b,b','c'\n", output);

		// test empty elements
		final String[] empty = {,};
		output = invokeWriter(empty);
		assertEquals("\n", output);

		// test multiline quoted
		final String[] multiline = { "This is a \n multiline entry", "so is \n this" };
		output = invokeWriter(multiline);
		assertEquals("'This is a \n multiline entry','so is \n this'\n", output);

		// test quoted line
		final String[] quoteLine = { "This is a \" multiline entry", "so is \n this" };
		output = invokeWriter(quoteLine);
		assertEquals("'This is a \"\" multiline entry','so is \n this'\n", output);

	}




	@Test
	public void parseLineWithBothEscapeAndQuoteChar() throws IOException {
		// test quoted line
		final String[] quoteLine = { "This is a 'multiline' entry", "so is \n this" };
		final String output = invokeWriter(quoteLine);
		assertEquals("'This is a \"'multiline\"' entry','so is \n this'\n", output);
	}




	/**
	 * Tests parsing individual lines.
	 *
	 * @throws IOException
	 *             if the reader fails.
	 */
	@Test
	public void testParseLineWithNoEscapeChar() throws IOException {

		// test normal case
		final String[] normal = { "a", "b", "c" };
		String output = invokeNoEscapeWriter(normal);
		assertEquals("'a','b','c'\n", output);

		// test quoted commas
		final String[] quoted = { "a", "b,b,b", "c" };
		output = invokeNoEscapeWriter(quoted);
		assertEquals("'a','b,b,b','c'\n", output);

		// test empty elements
		final String[] empty = {,};
		output = invokeNoEscapeWriter(empty);
		assertEquals("\n", output);

		// test multi-line quoted
		final String[] multiline = { "This is a \n multiline entry", "so is \n this" };
		output = invokeNoEscapeWriter(multiline);
		assertEquals("'This is a \n multiline entry','so is \n this'\n", output);

	}




	@Test
	public void parseLineWithNoEscapeCharAndQuotes() throws IOException {
		final String[] quoteLine = { "This is a \" 'multiline' entry", "so is \n this" };
		final String output = invokeNoEscapeWriter(quoteLine);
		assertEquals("'This is a \" 'multiline' entry','so is \n this'\n", output);
	}




	/**
	 * Test parsing from to a list.
	 *
	 * @throws IOException if the reader fails.
	 */
	@Test
	public void testParseAll() throws IOException {

		final List<String[]> allElements = new ArrayList<String[]>();
		final String[] line1 = "Name#Phone#Email".split("#");
		final String[] line2 = "Roscoe#1234#Roscoe@dukes.com".split("#");
		final String[] line3 = "Boss#1234#Hogg@sukes.com".split("#");
		allElements.add(line1);
		allElements.add(line2);
		allElements.add(line3);

		final StringWriter sw = new StringWriter();
		final CSVWriter csvw = new CSVWriter(sw);
		csvw.writeAll(allElements);

		final String result = sw.toString();
		final String[] lines = result.split("\n");
		csvw.close();

		assertEquals(3, lines.length);

	}




	/**
	 * Tests the option of having omitting quotes in the output stream.
	 * 
	 * @throws IOException if bad things happen
	 */
	@Test
	public void testNoQuoteChars() throws IOException {

		final String[] line = { "Foo", "Bar", "Baz" };
		final StringWriter sw = new StringWriter();
		final CSVWriter csvw = new CSVWriter(sw, CSVWriter.SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
		csvw.writeNext(line);
		final String result = sw.toString();
		csvw.close();

		assertEquals("Foo,Bar,Baz\n", result);
	}




	/**
	 * Tests the option of having omitting quotes in the output stream.
	 *
	 * @throws IOException if bad things happen
	 */
	@Test
	public void testNoQuoteCharsAndNoEscapeChars() throws IOException {

		final String[] line = { "Foo", "Bar", "Baz" };
		final StringWriter sw = new StringWriter();
		final CSVWriter csvw = new CSVWriter(sw, CSVWriter.SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER);
		csvw.writeNext(line);
		final String result = sw.toString();
		csvw.close();

		assertEquals("Foo,Bar,Baz\n", result);
	}




	/**
	 * Test null values.
	 *
	 * @throws IOException if bad things happen
	 */
	@Test
	public void testNullValues() throws IOException {

		final String[] line = { "Foo", null, "Bar", "baz" };
		final StringWriter sw = new StringWriter();
		final CSVWriter csvw = new CSVWriter(sw);
		csvw.writeNext(line);
		final String result = sw.toString();
		csvw.close();

		assertEquals("\"Foo\",,\"Bar\",\"baz\"\n", result);

	}




	@Test
	public void testStreamFlushing() throws IOException {

		final String WRITE_FILE = "myfile.csv";

		final String[] nextLine = new String[] { "aaaa", "bbbb", "cccc", "dddd" };

		final FileWriter fileWriter = new FileWriter(WRITE_FILE);
		final CSVWriter writer = new CSVWriter(fileWriter);

		writer.writeNext(nextLine);

		// If this line is not executed, it is not written in the file.
		writer.close();

	}

}
