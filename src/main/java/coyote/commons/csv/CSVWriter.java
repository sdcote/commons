package coyote.commons.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A simple CSV writer
 */
public class CSVWriter implements Closeable {

	private Writer _writer;

	private PrintWriter _printwriter;

	private char _separator;

	private char _quotechar;

	private char _escapechar;

	private String _linedelim;

	public static final int INITIAL_STRING_SIZE = 128;

	/** The character used for escaping quotes. */
	public static final char ESCAPE_CHARACTER = '"';

	/** The default separator to use if none is supplied to the constructor. */
	public static final char SEPARATOR = ',';

	/**
	 * The default quote character to use if none is supplied to the
	 * constructor.
	 */
	public static final char QUOTE_CHARACTER = '"';

	/** The quote constant to use when you wish to suppress all quoting. */
	public static final char NO_QUOTE_CHARACTER = '\u0000';

	/** The escape constant to use when you wish to suppress all escaping. */
	public static final char NO_ESCAPE_CHARACTER = '\u0000';

	/** Default line terminator uses platform encoding. */
	public static final String LINE_DELIMITER = "\n";





	/**
	 * Constructs CSVWriter using a comma for the separator.
	 *
	 * @param writer
	 *            the writer to an underlying CSV source.
	 */
	public CSVWriter(Writer writer) {
		this(writer, SEPARATOR);
	}




	/**
	 * Constructs CSVWriter with supplied separator.
	 *
	 * @param writer
	 *            the writer to an underlying CSV source.
	 * @param separator
	 *            the delimiter to use for separating entries.
	 */
	public CSVWriter(Writer writer, char separator) {
		this(writer, separator, QUOTE_CHARACTER);
	}




	/**
	 * Constructs CSVWriter with supplied separator and quote char.
	 *
	 * @param writer
	 *            the writer to an underlying CSV source.
	 * @param separator
	 *            the delimiter to use for separating entries
	 * @param quotechar
	 *            the character to use for quoted elements
	 */
	public CSVWriter(Writer writer, char separator, char quotechar) {
		this(writer, separator, quotechar, ESCAPE_CHARACTER);
	}




	/**
	 * Constructs CSVWriter with supplied separator and quote char.
	 *
	 * @param writer
	 *            the writer to an underlying CSV source.
	 * @param separator
	 *            the delimiter to use for separating entries
	 * @param quotechar
	 *            the character to use for quoted elements
	 * @param escapechar
	 *            the character to use for escaping quotechars or escapechars
	 */
	public CSVWriter(Writer writer, char separator, char quotechar, char escapechar) {
		this(writer, separator, quotechar, escapechar, LINE_DELIMITER);
	}




	/**
	 * Constructs CSVWriter with supplied separator and quote char.
	 *
	 * @param writer
	 *            the writer to an underlying CSV source.
	 * @param separator
	 *            the delimiter to use for separating entries
	 * @param quotechar
	 *            the character to use for quoted elements
	 * @param lineEnd
	 * 			  the line feed terminator to use
	 */
	public CSVWriter(Writer writer, char separator, char quotechar, String lineEnd) {
		this(writer, separator, quotechar, ESCAPE_CHARACTER, lineEnd);
	}




	/**
	 * Constructs CSVWriter with supplied separator, quote char, escape char and line ending.
	 *
	 * @param writer
	 *            the writer to an underlying CSV source.
	 * @param separator
	 *            the delimiter to use for separating entries
	 * @param quotechar
	 *            the character to use for quoted elements
	 * @param escapechar
	 *            the character to use for escaping quotechars or escapechars
	 * @param lineEnd
	 * 			  the line feed terminator to use
	 */
	public CSVWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd) {
		this._writer = writer;
		this._printwriter = new PrintWriter(writer);
		this._separator = separator;
		this._quotechar = quotechar;
		this._escapechar = escapechar;
		this._linedelim = lineEnd;
	}




	/**
	 * Writes the entire list to a CSV file. The list is assumed to be a
	 * String[]
	 *
	 * @param allLines
	 *            a List of String[], with each String[] representing a line of
	 *            the file.
	 */
	public void writeAll(List<String[]> allLines) {
		for (String[] line : allLines) {
			writeNext(line);
		}
	}






	/**
	 * Writes the next line to the file.
	 *
	 * @param nextLine
	 *            a string array with each comma-separated element as a separate
	 *            entry.
	 */
	public void writeNext(String[] nextLine) {
		if (nextLine == null)
			return;

		StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
		for (int i = 0; i < nextLine.length; i++) {

			if (i != 0) {
				sb.append(_separator);
			}

			String nextElement = nextLine[i];
			if (nextElement == null)
				continue;
			if (_quotechar != NO_QUOTE_CHARACTER)
				sb.append(_quotechar);

			sb.append(stringContainsSpecialCharacters(nextElement) ? processLine(nextElement) : nextElement);

			if (_quotechar != NO_QUOTE_CHARACTER)
				sb.append(_quotechar);
		}

		sb.append(_linedelim);
		_printwriter.write(sb.toString());

	}




	private boolean stringContainsSpecialCharacters(String line) {
		return line.indexOf(_quotechar) != -1 || line.indexOf(_escapechar) != -1;
	}




	protected StringBuilder processLine(String nextElement) {
		StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
		for (int j = 0; j < nextElement.length(); j++) {
			char nextChar = nextElement.charAt(j);
			if (_escapechar != NO_ESCAPE_CHARACTER && nextChar == _quotechar) {
				sb.append(_escapechar).append(nextChar);
			} else if (_escapechar != NO_ESCAPE_CHARACTER && nextChar == _escapechar) {
				sb.append(_escapechar).append(nextChar);
			} else {
				sb.append(nextChar);
			}
		}

		return sb;
	}




	/**
	 * Flush underlying stream to writer.
	 * 
	 * @throws IOException if bad things happen
	 */
	public void flush() throws IOException {

		_printwriter.flush();

	}




	/**
	 * Close the underlying stream writer flushing any buffered content.
	 *
	 * @throws IOException if bad things happen
	 */
	public void close() throws IOException {
		flush();
		_printwriter.close();
		_writer.close();
	}




	/**
	 *  Checks to see if the there has been an error in the printstream. 
	 */
	public boolean checkError() {
		return _printwriter.checkError();
	}




}
