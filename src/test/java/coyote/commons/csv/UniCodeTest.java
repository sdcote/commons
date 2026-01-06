package coyote.commons.csv;

//import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.List;




public class UniCodeTest {
  CSVParser csvParser;
  private static final String COMPOUND_STRING = "??,??";
  private static final String COMPOUND_STRING_WITH_QUOTES = "\"??\",\"??\"";
  private static final String FIRST_STRING = "??";
  private static final String SECOND_STRING = "??";
  private static final String[] UNICODE_ARRAY = { FIRST_STRING, SECOND_STRING };
  private static final String[] MIXED_ARRAY = { "foo, 1", "bar", FIRST_STRING, SECOND_STRING };
  private static final String[] ASCII_ARRAY = { "foo", "bar" };
  private static final String ASCII_STRING_WITH_QUOTES = "\"foo\",\"bar\"";




  @Test
  public void runASCIIThroughCSVWriter() {
    final StringWriter sw = new StringWriter();
    final CSVWriter writer = new CSVWriter( sw );
    writer.writeNext( ASCII_ARRAY );
    assertEquals( ASCII_STRING_WITH_QUOTES.trim(), sw.toString().trim() );
    try {
      writer.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }

  }




  @Test
  public void writeThenReadAscii() throws IOException, ParseException {
    final StringWriter sw = new StringWriter();
    final CSVWriter writer = new CSVWriter( sw );
    writer.writeNext( ASCII_ARRAY );

    final CSVReader reader = new CSVReader( new StringReader( sw.toString() ) );
    final String[] items = reader.readNext();
    assertEquals( 2, items.length );
    assertArrayEquals( ASCII_ARRAY, items );
    try {
      reader.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }
    try {
      writer.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }

  }




  @Test
  public void writeThenReadTwiceAscii() throws IOException, ParseException {
    final StringWriter sw = new StringWriter();
    final CSVWriter writer = new CSVWriter( sw );
    writer.writeNext( ASCII_ARRAY );
    writer.writeNext( ASCII_ARRAY );

    final CSVReader reader = new CSVReader( new StringReader( sw.toString() ) );
    final List<String[]> lines = reader.readAll();
    assertEquals( 2, lines.size() );

    String[] items = lines.get( 0 );
    assertEquals( 2, items.length );
    assertArrayEquals( ASCII_ARRAY, items );

    items = lines.get( 1 );
    assertEquals( 2, items.length );
    assertArrayEquals( ASCII_ARRAY, items );
    try {
      writer.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }
    try {
      reader.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }

  }




  @Test
  public void writeThenReadTwiceUnicode() throws IOException, ParseException {
    final StringWriter sw = new StringWriter();
    final CSVWriter writer = new CSVWriter( sw );
    writer.writeNext( UNICODE_ARRAY );
    writer.writeNext( UNICODE_ARRAY );

    final CSVReader reader = new CSVReader( new StringReader( sw.toString() ) );
    final List<String[]> lines = reader.readAll();
    assertEquals( 2, lines.size() );

    String[] items = lines.get( 0 );
    assertEquals( 2, items.length );
    assertArrayEquals( UNICODE_ARRAY, items );

    items = lines.get( 1 );
    assertEquals( 2, items.length );
    assertArrayEquals( UNICODE_ARRAY, items );
    try {
      writer.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }
    try {
      reader.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }
  }




  @Test
  public void writeThenReadTwiceMixedUnicode() throws IOException, ParseException {
    final StringWriter sw = new StringWriter();
    final CSVWriter writer = new CSVWriter( sw );
    writer.writeNext( MIXED_ARRAY );
    writer.writeNext( MIXED_ARRAY );

    final CSVReader reader = new CSVReader( new StringReader( sw.toString() ) );
    final List<String[]> lines = reader.readAll();
    assertEquals( 2, lines.size() );

    String[] items = lines.get( 0 );
    assertEquals( 4, items.length );
    assertArrayEquals( MIXED_ARRAY, items );

    items = lines.get( 1 );
    assertEquals( 4, items.length );
    assertArrayEquals( MIXED_ARRAY, items );
    try {
      writer.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }
    try {
      reader.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }
  }




  @Test
  public void canParseUnicode() throws IOException, ParseException {
    csvParser = new CSVParser();
    final String simpleString = COMPOUND_STRING;
    final String[] items = csvParser.parseLine( simpleString );
    assertEquals( 2, items.length );
    assertEquals( FIRST_STRING, items[0] );
    assertEquals( SECOND_STRING, items[1] );
    assertArrayEquals( UNICODE_ARRAY, items );
  }




  @Test
  public void readerTest() throws IOException {
    final BufferedReader reader = new BufferedReader( new StringReader( FIRST_STRING ) );
    final String testString = reader.readLine();
    assertEquals( FIRST_STRING, testString );
  }




  @Test
  public void writerTest() {
    final StringWriter sw = new StringWriter();
    sw.write( FIRST_STRING );
    assertEquals( FIRST_STRING, sw.toString() );
  }




  @Test
  public void runUniCodeThroughCSVReader() throws IOException, ParseException {
    final CSVReader reader = new CSVReader( new StringReader( COMPOUND_STRING ) );
    final String[] items = reader.readNext();
    assertEquals( 2, items.length );
    assertEquals( FIRST_STRING, items[0] );
    assertEquals( SECOND_STRING, items[1] );
    assertArrayEquals( UNICODE_ARRAY, items );
    try {
      reader.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }

  }




  @Test
  public void runUniCodeThroughCSVWriter() {
    final StringWriter sw = new StringWriter();
    final CSVWriter writer = new CSVWriter( sw );
    writer.writeNext( UNICODE_ARRAY );
    assertEquals( COMPOUND_STRING_WITH_QUOTES.trim(), sw.toString().trim() );
    try {
      writer.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }

  }

}
