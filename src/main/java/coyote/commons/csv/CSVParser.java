package coyote.commons.csv;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A very simple CSV parser which splits a single line into fields.
 */
public class CSVParser {

	private final char _separator;

	private final char _quotechar;

	private final char _escape;

	private final boolean _usestrictquotes;

	private String _pending;
	private boolean _isinfield = false;

	private final boolean _ignoreleadingwhitespace;

	/** The default separator to use if none is supplied to the constructor. */
	public static final char SEPARATOR = ',';

	public static final int INITIAL_READ_SIZE = 128;

	/** The default quote character to use if none is supplied to the constructor. */
	public static final char QUOTE_CHARACTER = '"';

	/** The default escape character to use if none is supplied to the constructor. */
	public static final char ESCAPE_CHARACTER = '\\';

	/** The default strict quote behavior to use if none is supplied to the constructor */
	public static final boolean STRICT_QUOTES = false;

	/** The default leading whitespace behavior to use if none is supplied to the constructor */
	public static final boolean IGNORE_LEADING_WHITESPACE = true;

	/** This is the "null" character - if a value is set to this then it is ignored. I.E. if the quote character is set to null then there is no quote character. */
	public static final char NULL_CHARACTER = '\0';




	/**
	 * Constructs CSVParser.
	 */
	public CSVParser() {
		this(SEPARATOR, QUOTE_CHARACTER, ESCAPE_CHARACTER);
	}




	/**
	 * Constructs CSVParser.
	 *
	 * @param separator the delimiter to use for separating entries.
	 */
	public CSVParser(char separator) {
		this(separator, QUOTE_CHARACTER, ESCAPE_CHARACTER);
	}




	/**
	 * Constructs CSVParser.
	 *
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 */
	public CSVParser(char separator, char quotechar) {
		this(separator, quotechar, ESCAPE_CHARACTER);
	}




	/**
	 * Constructs CSVParser.
	 *
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param escape the character to use for escaping a separator or quote
	 */
	public CSVParser(char separator, char quotechar, char escape) {
		this(separator, quotechar, escape, STRICT_QUOTES);
	}




	/**
	 * Constructs CSVParser.
	 *
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param escape the character to use for escaping a separator or quote
	 * @param strictQuotes if true, characters outside the quotes are ignored
	 */
	public CSVParser(char separator, char quotechar, char escape, boolean strictQuotes) {
		this(separator, quotechar, escape, strictQuotes, IGNORE_LEADING_WHITESPACE);
	}




	/**
	 * Constructs CSVParser.
	 *
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param escape the character to use for escaping a separator or quote
	 * @param strictQuotes if true, characters outside the quotes are ignored
	 * @param ignoreLeadingWhiteSpace if true, white space in front of a quote in a field is ignored
	 */
	public CSVParser(char separator, char quotechar, char escape, boolean strictQuotes, boolean ignoreLeadingWhiteSpace) {
		if (anyCharactersAreTheSame(separator, quotechar, escape)) {
			throw new UnsupportedOperationException("The separator, quote, and escape characters must be different!");
		}
		if (separator == NULL_CHARACTER) {
			throw new UnsupportedOperationException("The separator character must be defined!");
		}
		this._separator = separator;
		this._quotechar = quotechar;
		this._escape = escape;
		this._usestrictquotes = strictQuotes;
		this._ignoreleadingwhitespace = ignoreLeadingWhiteSpace;
	}




	




	public String[] parseLineMulti(String nextLine) throws ParseException {
		return parseLine(nextLine, true);
	}




	public String[] parseLine(String nextLine) throws ParseException {
		return parseLine(nextLine, false);
	}




	/**
	 * Parses an incoming String and returns an array of elements.
	 *
	 * @param nextLine the string to parse
	 * @param multi
	 * 
	 * @return the comma delimited list of elements, or null if nextLine is null
	 * 
	 * @throws ParseException Un-terminated quoted field at end of CSV line
	 */
	private String[] parseLine(String nextLine, boolean multi) throws ParseException {

		if (!multi && _pending != null) {
			_pending = null;
		}

		if (nextLine == null) {
			if (_pending != null) {
				String s = _pending;
				_pending = null;
				return new String[] { s };
			} else {
				return null;
			}
		}

		List<String> tokensOnThisLine = new ArrayList<String>();
		StringBuilder sb = new StringBuilder(INITIAL_READ_SIZE);
		boolean inQuotes = false;
		if (_pending != null) {
			sb.append(_pending);
			_pending = null;
			inQuotes = true;
		}
		for (int i = 0; i < nextLine.length(); i++) {

			char c = nextLine.charAt(i);
			if (c == this._escape) {
				if (isNextCharacterEscapable(nextLine, inQuotes || _isinfield, i)) {
					sb.append(nextLine.charAt(i + 1));
					i++;
				}
			} else if (c == _quotechar) {
				if (isNextCharacterEscapedQuote(nextLine, inQuotes || _isinfield, i)) {
					sb.append(nextLine.charAt(i + 1));
					i++;
				} else {
					if (!_usestrictquotes) {
						if (i > 2 && nextLine.charAt(i - 1) != this._separator && nextLine.length() > (i + 1) && nextLine.charAt(i + 1) != this._separator) {

							if (_ignoreleadingwhitespace && sb.length() > 0 && isAllWhiteSpace(sb)) {
								sb.setLength(0);
							} else {
								sb.append(c);
							}

						}
					}

					inQuotes = !inQuotes;
				}
				_isinfield = !_isinfield;
			} else if (c == _separator && !inQuotes) {
				tokensOnThisLine.add(sb.toString());
				sb.setLength(0);
				_isinfield = false;
			} else {
				if (!_usestrictquotes || inQuotes) {
					sb.append(c);
					_isinfield = true;
				}
			}
		}

		if (inQuotes) {
			if (multi) {

				sb.append("\n");
				_pending = sb.toString();
				sb = null;
			} else {
				throw new ParseException("Un-terminated quoted field at end of CSV line", -1);
			}
		}
		if (sb != null) {
			tokensOnThisLine.add(sb.toString());
		}
		return tokensOnThisLine.toArray(new String[tokensOnThisLine.size()]);

	}




	/**
	 * precondition: the current character is a quote or an escape
	 *
	 * @param nextLine the current line
	 * @param inQuotes true if the current context is quoted
	 * @param i        current index in line
	 * 
	 * @return true if the following character is a quote
	 */
	private boolean isNextCharacterEscapedQuote(String nextLine, boolean inQuotes, int i) {
		return inQuotes && nextLine.length() > (i + 1) && nextLine.charAt(i + 1) == _quotechar;
	}




	/**
	 * precondition: the current character is an escape
	 *
	 * @param nextLine the current line
	 * @param inQuotes true if the current context is quoted
	 * @param i        current index in line
	 * 
	 * @return true if the following character is a quote
	 */
	protected boolean isNextCharacterEscapable(String nextLine, boolean inQuotes, int i) {
		return inQuotes && nextLine.length() > (i + 1) && (nextLine.charAt(i + 1) == _quotechar || nextLine.charAt(i + 1) == this._escape);
	}



	/**
	 * @return true if something was left over from last call(s)
	 */
	boolean isPending() {
		return _pending != null;
	}




	/**
	 * precondition: sb.length() > 0
	 *
	 * @param sb A sequence of characters to examine
	 * 
	 * @return true if every character in the sequence is whitespace
	 */
	protected boolean isAllWhiteSpace(CharSequence sb) {
		boolean result = true;
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);

			if (!Character.isWhitespace(c)) {
				return false;
			}
		}
		return result;
	}
	private boolean anyCharactersAreTheSame(char separator, char quotechar, char escape) {
		return isSameCharacter(separator, quotechar) || isSameCharacter(separator, escape) || isSameCharacter(quotechar, escape);
	}




	private boolean isSameCharacter(char c1, char c2) {
		return c1 != NULL_CHARACTER && c1 == c2;
	}

}
