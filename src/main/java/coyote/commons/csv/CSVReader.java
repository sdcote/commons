package coyote.commons.csv;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A CSV reader which uses the CSVParser to split lines into fields.
 */
public class CSVReader implements Closeable {

	private BufferedReader _br;

	private boolean _hasnext = true;

	private CSVParser _parser;

	private int _linetoskip;

	private boolean _linesskipped;

	/** The default line to start reading. */
	public static final int LINES_TO_SKIP = 0;




	/**
	 * Constructs CSVReader.
	 * 
	 * @param reader the reader to an underlying CSV source.
	 */
	public CSVReader(Reader reader) {
		this(reader, CSVParser.SEPARATOR, CSVParser.QUOTE_CHARACTER, CSVParser.ESCAPE_CHARACTER);
	}




	/**
	 * Constructs CSVReader.
	 * 
	 * @param reader the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries.
	 */
	public CSVReader(Reader reader, char separator) {
		this(reader, separator, CSVParser.QUOTE_CHARACTER, CSVParser.ESCAPE_CHARACTER);
	}




	/**
	 * Constructs CSVReader.
	 * 
	 * @param reader the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 */
	public CSVReader(Reader reader, char separator, char quotechar) {
		this(reader, separator, quotechar, CSVParser.ESCAPE_CHARACTER, LINES_TO_SKIP, CSVParser.STRICT_QUOTES);
	}




	/**
	 * Constructs CSVReader.
	 *
	 * @param reader the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param strictQuotes sets if characters outside the quotes are ignored
	 */
	public CSVReader(Reader reader, char separator, char quotechar, boolean strictQuotes) {
		this(reader, separator, quotechar, CSVParser.ESCAPE_CHARACTER, LINES_TO_SKIP, strictQuotes);
	}




	/**
	 * Constructs CSVReader.
	  *
	  * @param reader the reader to an underlying CSV source.
	  * @param separator the delimiter to use for separating entries
	  * @param quotechar the character to use for quoted elements
	  * @param escape the character to use for escaping a separator or quote
	  */

	public CSVReader(Reader reader, char separator, char quotechar, char escape) {
		this(reader, separator, quotechar, escape, LINES_TO_SKIP, CSVParser.STRICT_QUOTES);
	}




	/**
	 * Constructs CSVReader.
	 * 
	 * @param reader the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param line the line number to skip for start reading 
	 */
	public CSVReader(Reader reader, char separator, char quotechar, int line) {
		this(reader, separator, quotechar, CSVParser.ESCAPE_CHARACTER, line, CSVParser.STRICT_QUOTES);
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
	public CSVReader(Reader reader, char separator, char quotechar, char escape, int line) {
		this(reader, separator, quotechar, escape, line, CSVParser.STRICT_QUOTES);
	}




	/**
	 * Constructs CSVReader.
	 * 
	 * @param reader the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param escape the character to use for escaping a separator or quote
	 * @param line the line number to skip for start reading
	 * @param strictQuotes sets if characters outside the quotes are ignored
	 */
	public CSVReader(Reader reader, char separator, char quotechar, char escape, int line, boolean strictQuotes) {
		this(reader, separator, quotechar, escape, line, strictQuotes, CSVParser.IGNORE_LEADING_WHITESPACE);
	}




	/**
	 * Constructs CSVReader.
	 * 
	 * @param reader the reader to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param escape the character to use for escaping a separator or quote
	 * @param line the line number to skip for start reading
	 * @param strictQuotes sets if characters outside the quotes are ignored
	 * @param ignoreLeadingWhiteSpace it true, parser should ignore white space before a quote in a field
	 */
	public CSVReader(Reader reader, char separator, char quotechar, char escape, int line, boolean strictQuotes, boolean ignoreLeadingWhiteSpace) {
		this._br = new BufferedReader(reader);
		this._parser = new CSVParser(separator, quotechar, escape, strictQuotes, ignoreLeadingWhiteSpace);
		this._linetoskip = line;
	}




	/**
	 * Reads the entire file into a List with each element being a String[] of 
	 * tokens.
	 * 
	 * @return a List of String[], with each String[] representing a line of the file.
	 * 
	 * @throws IOException if the next line could not be read
	 * @throws ParseException if the read line could not be parsed
	 */
	public List<String[]> readAll() throws IOException, ParseException {

		List<String[]> allElements = new ArrayList<String[]>();
		while (_hasnext) {
			String[] nextLineAsTokens = readNext();
			if (nextLineAsTokens != null)
				allElements.add(nextLineAsTokens);
		}
		return allElements;

	}




	/**
	 * Reads the next line from the buffer and converts to a string array.
	 * 
	 * @return a string array with each comma-separated element as a separate entry.
	 * 
	 * @throws IOException if the next line could not be read
	 * @throws ParseException if the read line could not be parsed
	 */
	public String[] readNext() throws IOException, ParseException {

		String[] result = null;
		do {
			String nextLine = getNextLine();
			if (!_hasnext) {
				return result; // should throw if still pending?
			}
			String[] r = _parser.parseLineMulti(nextLine);
			if (r.length > 0) {
				if (result == null) {
					result = r;
				} else {
					String[] t = new String[result.length + r.length];
					System.arraycopy(result, 0, t, 0, result.length);
					System.arraycopy(r, 0, t, result.length, r.length);
					result = t;
				}
			}
		} while (_parser.isPending());
		return result;
	}




	/**
	 * Reads the next line from the file.
	 * 
	 * @return the next line from the file without trailing newline
	 * 
	 * @throws IOException if the next line could not be read
	 */
	private String getNextLine() throws IOException {
		if (!this._linesskipped) {
			for (int i = 0; i < _linetoskip; i++) {
				_br.readLine();
			}
			this._linesskipped = true;
		}
		String nextLine = _br.readLine();
		if (nextLine == null) {
			_hasnext = false;
		}
		return _hasnext ? nextLine : null;
	}




	/**
	 * Closes the underlying reader.
	 * 
	 * @throws IOException if the close fails
	 */
	public void close() throws IOException {
		_br.close();
	}

}
