package coyote.commons;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A very simple CSV reader
 */
public class CSVReader implements Closeable {

	private final BufferedReader _bufferedreader;

	private boolean _hasnext = true;

	private final char _separator;

	private final char _quotechar;

	private final char _escape;

	private final int _skiplines;

	private boolean _linesskipped;

	/** The default separator to use if none is supplied to the constructor. */
	public static final char DEFAULT_SEPARATOR = ',';

	public static final int INITIAL_READ_SIZE = 64;

	/**
	 * The default quote character to use if none is supplied to the
	 * constructor.
	 */
	public static final char DEFAULT_QUOTE_CHARACTER = '"';

	/**
	 * The default escape character to use if none is supplied to the
	 * constructor.
	 */
	public static final char DEFAULT_ESCAPE_CHARACTER = '\\';

	/**
	 * The default line to start reading.
	 */
	public static final int DEFAULT_SKIP_LINES = 0;




	/**
	 * Constructs CSVReader.
	 * 
	 * @param reader the reader to an underlying CSV source.
	 */
	public CSVReader(final Reader reader) {
		this(reader, DEFAULT_SEPARATOR);
	}




	/**
	 * Constructs CSVReader.
	 * 
	 * @param reader the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries.
	 */
	public CSVReader(final Reader reader, final char separator) {
		this(reader, separator, DEFAULT_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER);
	}




	/**
	 * Constructs CSVReader.
	 * 
	 * @param reader the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 */
	public CSVReader(final Reader reader, final char separator, final char quotechar) {
		this(reader, separator, quotechar, DEFAULT_ESCAPE_CHARACTER, DEFAULT_SKIP_LINES);
	}




	/**
	 * Constructs CSVReader.
	 * 
	 * @param reader the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param escape the character to use for escaping a separator or quote
	 */
	public CSVReader(final Reader reader, final char separator, final char quotechar, final char escape) {
		this(reader, separator, quotechar, escape, DEFAULT_SKIP_LINES);
	}




	/**
	 * Constructs CSVReader.
	 * 
	 * @param reader the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param line the line number to skip for start reading 
	 */
	public CSVReader(final Reader reader, final char separator, final char quotechar, final int line) {
		this(reader, separator, quotechar, DEFAULT_ESCAPE_CHARACTER, line);
	}




	/**
	 * Constructs CSVReader.
	 * 
	 * @param reader the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param escape the character to use for escaping a separator or quote
	 * @param line the line number to skip for start reading 
	 */
	public CSVReader(final Reader reader, final char separator, final char quotechar, final char escape, final int line) {
		this._bufferedreader = new BufferedReader(reader);
		this._separator = separator;
		this._quotechar = quotechar;
		this._escape = escape;
		this._skiplines = line;
	}




	/**
	 * Reads the next line from the buffer and converts to a string array.
	 * 
	 * @return a string array with each comma-separated element as a separate entry.
	 * 
	 * @throws IOException if anything happens during the read
	 */
	public String[] readNext() throws IOException {
		final String nextLine = getNextLine();
		return _hasnext ? parseLine(nextLine) : null;
	}




	/**
	 * Reads the entire file into a List with each element being a String[] of
	 * tokens.
	 * 
	 * @return a List of String[], with each String[] representing a line of the file.
	 * 
	 * @throws IOException if anything happens during the read
	 */
	public List<String[]> readAll() throws IOException {

		final List<String[]> allElements = new ArrayList<String[]>();
		while (_hasnext) {
			final String[] nextLineAsTokens = readNext();
			if (nextLineAsTokens != null)
				allElements.add(nextLineAsTokens);
		}
		return allElements;

	}




	/**
	 * Closes the underlying reader.
	 * 
	 * @throws IOException if the close fails
	 */
	@Override
	public void close() throws IOException {
		_bufferedreader.close();
	}




	/**
	 * Reads the next line from the file.
	 * 
	 * @return the next line from the file without trailing newline
	 * 
	 * @throws IOException if anything happens during the read
	 */
	private String getNextLine() throws IOException {
		if (!this._linesskipped) {
			for (int i = 0; i < _skiplines; i++) {
				_bufferedreader.readLine();
			}
			this._linesskipped = true;
		}
		final String nextLine = _bufferedreader.readLine();
		if (nextLine == null) {
			_hasnext = false;
		}
		return _hasnext ? nextLine : null;
	}




	/**
	 * Parses an incoming String and returns an array of elements.
	 * 
	 * @param nextLine the string to parse
	 * @return the comma-delimited list of elements, or null if nextLine is null
	 * 
	 * @throws IOException if anything happens during the read
	 */
	private String[] parseLine(String nextLine) throws IOException {

		if (nextLine == null) {
			return null;
		}

		final List<String> tokensOnThisLine = new ArrayList<String>();
		StringBuilder sb = new StringBuilder(INITIAL_READ_SIZE);
		boolean inQuotes = false;
		do {
			if (inQuotes) {
				// continuing a quoted section, re-append newline
				sb.append("\n");
				nextLine = getNextLine();
				if (nextLine == null)
					break;
			}
			for (int i = 0; i < nextLine.length(); i++) {

				final char c = nextLine.charAt(i);
				if (c == this._escape) {
					if (isEscapable(nextLine, inQuotes, i)) {
						sb.append(nextLine.charAt(i + 1));
						i++;
					} else {
						i++; // ignore the escape
					}
				} else if (c == _quotechar) {
					if (isEscapedQuote(nextLine, inQuotes, i)) {
						sb.append(nextLine.charAt(i + 1));
						i++;
					} else {
						inQuotes = !inQuotes;
						// a case of an embedded quote -> a,bc"d"ef,g
						if ((i > 2) && (nextLine.charAt(i - 1) != this._separator) && (nextLine.length() > (i + 1)) && (nextLine.charAt(i + 1) != this._separator)) {
							sb.append(c);
						}
					}
				} else if ((c == _separator) && !inQuotes) {
					tokensOnThisLine.add(sb.toString());
					sb = new StringBuilder(INITIAL_READ_SIZE); // next token
				} else {
					sb.append(c);
				}
			}
		} while (inQuotes);
		tokensOnThisLine.add(sb.toString());
		return tokensOnThisLine.toArray(new String[0]);

	}




	/**  
	 * precondition: the current character is a quote or an escape
	 * 
	 * @param nextLine the current line
	 * @param inQuotes true if the current context is quoted
	 * @param i current index in line
	 * 
	 * @return true if the following character is a quote
	 */
	private boolean isEscapedQuote(final String nextLine, final boolean inQuotes, final int i) {
		return inQuotes && (nextLine.length() > (i + 1)) && (nextLine.charAt(i + 1) == _quotechar);
	}




	/**  
	 * precondition: the current character is an escape
	 * 
	 * @param nextLine the current line
	 * @param inQuotes true if the current context is quoted
	 * @param i current index in line
	 * 
	 * @return true if the following character is a quote
	 */
	private boolean isEscapable(final String nextLine, final boolean inQuotes, final int i) {
		return inQuotes && (nextLine.length() > (i + 1)) && ((nextLine.charAt(i + 1) == _quotechar) || (nextLine.charAt(i + 1) == this._escape));
	}

}
