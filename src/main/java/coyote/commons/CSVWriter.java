package coyote.commons;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * A very simple CSV writer.
 */
public class CSVWriter implements Closeable {

	public static final int INITIAL_STRING_SIZE = 128;

	private final Writer _writer;

	private final PrintWriter _printpriterw;

	private final char _separator;

	private final char _quotechar;

	private final char _escapechar;

	private final String _lineEnd;

	/** The character used for escaping quotes. */
	public static final char DEFAULT_ESCAPE_CHARACTER = '"';

	/** The default separator to use if none is supplied to the constructor. */
	public static final char DEFAULT_SEPARATOR = ',';

	/**
	 * The default quote character to use if none is supplied to the
	 * constructor.
	 */
	public static final char DEFAULT_QUOTE_CHARACTER = '"';

	/** The quote constant to use when you wish to suppress all quoting. */
	public static final char NO_QUOTE_CHARACTER = '\u0000';

	/** The escape constant to use when you wish to suppress all escaping. */
	public static final char NO_ESCAPE_CHARACTER = '\u0000';

	/** Default line terminator uses platform encoding. */
	public static final String DEFAULT_LINE_END = "\n";




	/**
	 * Constructs CSVWriter.
	 *
	 * @param writer the writer to an underlying CSV source.
	 */
	public CSVWriter(final Writer writer) {
		this(writer, DEFAULT_SEPARATOR);
	}




	/**
	 * Constructs CSVWriter.
	 *
	 * @param writer the writer to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries.
	 */
	public CSVWriter(final Writer writer, final char separator) {
		this(writer, separator, DEFAULT_QUOTE_CHARACTER);
	}




	/**
	 * Constructs CSVWriter.
	 *
	 * @param writer the writer to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 */
	public CSVWriter(final Writer writer, final char separator, final char quotechar) {
		this(writer, separator, quotechar, DEFAULT_ESCAPE_CHARACTER);
	}




	/**
	 * Constructs CSVWriter.
	 *
	 * @param writer the writer to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param escapechar the character to use for escaping quotechars or escapechars
	 */
	public CSVWriter(final Writer writer, final char separator, final char quotechar, final char escapechar) {
		this(writer, separator, quotechar, escapechar, DEFAULT_LINE_END);
	}




	/**
	 * Constructs CSVWriter.
	 *
	 * @param writer the writer to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param lineEnd the line feed terminator to use
	 */
	public CSVWriter(final Writer writer, final char separator, final char quotechar, final String lineEnd) {
		this(writer, separator, quotechar, DEFAULT_ESCAPE_CHARACTER, lineEnd);
	}




	/**
	 * Constructs CSVWriter.
	 *
	 * @param writer the writer to an underlying CSV source.
	 * @param separator the delimiter to use for separating entries
	 * @param quotechar the character to use for quoted elements
	 * @param escapechar the character to use for escaping quotechars or escapechars
	 * @param lineEnd the line feed terminator to use
	 */
	public CSVWriter(final Writer writer, final char separator, final char quotechar, final char escapechar, final String lineEnd) {
		this._writer = writer;
		this._printpriterw = new PrintWriter(writer);
		this._separator = separator;
		this._quotechar = quotechar;
		this._escapechar = escapechar;
		this._lineEnd = lineEnd;
	}




	/**
	 * Writes the entire list to a CSV file. The list is assumed to be a
	 * String[]
	 *
	 * @param allLines a List of String[], with each String[] representing a line of the file.
	 */
	public void writeAll(final List<String[]> allLines) {
		for (final String[] line : allLines) {
			writeNext(line);
		}
	}




	/**
	 * Writes the entire ResultSet to a CSV file.
	 *
	 * The caller is responsible for closing the ResultSet.
	 *
	 * @param rs the record set to write
	 * @param includeColumnNames true if you want column names in the output, false otherwise
	 *
	 */
	public void writeAll(final java.sql.ResultSet rs, final boolean includeColumnNames) throws SQLException, IOException {
		final ResultSetMetaData metadata = rs.getMetaData();

		if (includeColumnNames) {
			writeColumnNames(metadata);
		}

		final int columnCount = metadata.getColumnCount();

		while (rs.next()) {
			final String[] nextLine = new String[columnCount];

			for (int i = 0; i < columnCount; i++) {
				nextLine[i] = getColumnValue(rs, metadata.getColumnType(i + 1), i + 1);
			}

			writeNext(nextLine);
		}
	}




	/**
	 * Writes the next line to the file.
	 *
	 * @param nextLine a string array with each comma-separated element as a separate entry.
	 */
	public void writeNext(final String[] nextLine) {
		if (nextLine == null)
			return;

		final StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
		for (int i = 0; i < nextLine.length; i++) {

			if (i != 0) {
				sb.append(_separator);
			}

			final String nextElement = nextLine[i];
			if (nextElement == null)
				continue;
			if (_quotechar != NO_QUOTE_CHARACTER)
				sb.append(_quotechar);

			sb.append(stringContainsSpecialCharacters(nextElement) ? processLine(nextElement) : nextElement);

			if (_quotechar != NO_QUOTE_CHARACTER)
				sb.append(_quotechar);
		}

		sb.append(_lineEnd);
		_printpriterw.write(sb.toString());
	}




	/**
	 * Flush underlying stream to writer.
	 */
	public void flush(){
		_printpriterw.flush();
	}




	/**
	 * Close the underlying stream writer flushing any buffered content.
	 *
	 * @throws IOException problems closing the writer
	 */
	@Override
	public void close() throws IOException {
		_printpriterw.flush();
		_printpriterw.close();
		_writer.close();
	}




	protected void writeColumnNames(final ResultSetMetaData metadata) throws SQLException {
		final int columnCount = metadata.getColumnCount();

		final String[] nextLine = new String[columnCount];
		for (int i = 0; i < columnCount; i++) {
			nextLine[i] = metadata.getColumnName(i + 1);
		}
		writeNext(nextLine);
	}




	private static String getColumnValue(final ResultSet rs, final int colType, final int colIndex) throws SQLException, IOException {
		String value = "";

		switch (colType) {
		case Types.BIT:
			final Object bit = rs.getObject(colIndex);
			if (bit != null) {
				value = String.valueOf(bit);
			}
			break;
		case Types.BOOLEAN:
			final boolean b = rs.getBoolean(colIndex);
			if (!rs.wasNull()) {
				value = Boolean.valueOf(b).toString();
			}
			break;
		case Types.CLOB:
			final Clob c = rs.getClob(colIndex);
			if (c != null) {
				value = read(c);
			}
			break;
		case Types.BIGINT:
			final long lv = rs.getLong(colIndex);
			if (!rs.wasNull()) {
				value = Long.toString(lv);
			}
			break;
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.REAL:
		case Types.NUMERIC:
			final BigDecimal bd = rs.getBigDecimal(colIndex);
			if (bd != null) {
				value = bd.toString();
			}
			break;
		case Types.INTEGER:
		case Types.TINYINT:
		case Types.SMALLINT:
			final int intValue = rs.getInt(colIndex);
			if (!rs.wasNull()) {
				value = Integer.toString(intValue);
			}
			break;
		case Types.JAVA_OBJECT:
			final Object obj = rs.getObject(colIndex);
			if (obj != null) {
				value = String.valueOf(obj);
			}
			break;
		case Types.DATE:
			final java.sql.Date date = rs.getDate(colIndex);
			if (date != null) {
				final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
				value = dateFormat.format(date);
				;
			}
			break;
		case Types.TIME:
			final Time t = rs.getTime(colIndex);
			if (t != null) {
				value = t.toString();
			}
			break;
		case Types.TIMESTAMP:
			final Timestamp tstamp = rs.getTimestamp(colIndex);
			if (tstamp != null) {
				final SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
				value = timeFormat.format(tstamp);
			}
			break;
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
		case Types.CHAR:
			value = rs.getString(colIndex);
			break;
		default:
			value = "";
		}

		if (value == null) {
			value = "";
		}

		return value;
	}




	private static String read(final Clob c) throws SQLException, IOException {
		final StringBuilder sb = new StringBuilder((int) c.length());
		final Reader r = c.getCharacterStream();
		final char[] cbuf = new char[2048];
		int n = 0;
		while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
			if (n > 0) {
				sb.append(cbuf, 0, n);
			}
		}
		return sb.toString();
	}




	private boolean stringContainsSpecialCharacters(final String line) {
		return (line.indexOf(_quotechar) != -1) || (line.indexOf(_escapechar) != -1);
	}




	private StringBuilder processLine(final String nextElement) {
		final StringBuilder sb = new StringBuilder(INITIAL_STRING_SIZE);
		for (int j = 0; j < nextElement.length(); j++) {
			final char nextChar = nextElement.charAt(j);
			if ((_escapechar != NO_ESCAPE_CHARACTER) && (nextChar == _quotechar)) {
				sb.append(_escapechar).append(nextChar);
			} else if ((_escapechar != NO_ESCAPE_CHARACTER) && (nextChar == _escapechar)) {
				sb.append(_escapechar).append(nextChar);
			} else {
				sb.append(nextChar);
			}
		}

		return sb;
	}

}