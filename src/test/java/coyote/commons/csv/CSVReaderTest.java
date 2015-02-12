package coyote.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;


public class CSVReaderTest {

  CSVReader csvr;




  /**
   * Setup the test.
   */
  @Before
  public void setUp() throws Exception {
    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );
    sb.append( "a,b,c" ).append( "\n" );
    sb.append( "a,\"b,b,b\",c" ).append( "\n" );
    sb.append( ",," ).append( "\n" );
    sb.append( "Dude,\"45 Rockefeller Plaza,\nNew York, NY\n10111\",USA.\n" );
    sb.append( "\"Rosco \"\"P\"\" Coltrane\",Sheriff\n" );
    sb.append( "\"\"\"\"\"\",\"test\"\n" );
    sb.append( "\"a\nb\",b,\"\nd\",e\n" );
    csvr = new CSVReader( new StringReader( sb.toString() ) );
  }




  @Test
  public void testParseLine() throws IOException, ParseException {

    // test normal case
    String[] nextLine = csvr.readNext();
    assertEquals( "a", nextLine[0] );
    assertEquals( "b", nextLine[1] );
    assertEquals( "c", nextLine[2] );

    // test quoted commas
    nextLine = csvr.readNext();
    assertEquals( "a", nextLine[0] );
    assertEquals( "b,b,b", nextLine[1] );
    assertEquals( "c", nextLine[2] );

    // test empty elements
    nextLine = csvr.readNext();
    assertEquals( 3, nextLine.length );

    // test multiline quoted
    nextLine = csvr.readNext();
    assertEquals( 3, nextLine.length );

    // test quoted quote chars
    nextLine = csvr.readNext();
    assertEquals( "Rosco \"P\" Coltrane", nextLine[0] );

    nextLine = csvr.readNext();
    assertEquals( "\"\"", nextLine[0] );
    assertEquals( "test", nextLine[1] );

    nextLine = csvr.readNext();
    assertEquals( 4, nextLine.length );

    // test end of stream
    assertNull( csvr.readNext() );

  }




  @Test
  public void testParseLineStrictQuote() throws IOException, ParseException {
    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );
    sb.append( "a,b,c" ).append( "\n" );
    sb.append( "a,\"b,b,b\",c" ).append( "\n" );
    sb.append( ",," ).append( "\n" );
    sb.append( "Dude,\"45 Rockefeller Plaza,\nNew York, NY\n10111\",USA.\n" );
    sb.append( "\"Rosco \"\"P\"\" Coltrane\",Sheriff\n" );
    sb.append( "\"\"\"\"\"\",\"test\"\n" );
    sb.append( "\"a\nb\",b,\"\nd\",e\n" );
    csvr = new CSVReader( new StringReader( sb.toString() ), ',', '\"', true );

    // test normal case
    String[] nextLine = csvr.readNext();
    assertEquals( "", nextLine[0] );
    assertEquals( "", nextLine[1] );
    assertEquals( "", nextLine[2] );

    // test quoted commas
    nextLine = csvr.readNext();
    assertEquals( "", nextLine[0] );
    assertEquals( "b,b,b", nextLine[1] );
    assertEquals( "", nextLine[2] );

    // test empty elements
    nextLine = csvr.readNext();
    assertEquals( 3, nextLine.length );

    // test multiline quoted
    nextLine = csvr.readNext();
    assertEquals( 3, nextLine.length );

    // test quoted quote chars
    nextLine = csvr.readNext();
    assertEquals( "Rosco \"P\" Coltrane", nextLine[0] );

    nextLine = csvr.readNext();
    assertTrue( nextLine[0].equals( "\"\"" ) );
    assertTrue( nextLine[1].equals( "test" ) );

    nextLine = csvr.readNext();
    assertEquals( 4, nextLine.length );
    assertEquals( "a\nb", nextLine[0] );
    assertEquals( "", nextLine[1] );
    assertEquals( "\nd", nextLine[2] );
    assertEquals( "", nextLine[3] );

    // test end of stream
    assertNull( csvr.readNext() );
  }




  @Test
  public void testParseAll() throws IOException, ParseException {
    assertEquals( 7, csvr.readAll().size() );
  }




  @Test
  public void testSingleQuoted() throws IOException, ParseException {

    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );

    sb.append( "a,'''',c" ).append( "\n" );

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ), ',', '\'' );

    final String[] nextLine = c.readNext();
    assertEquals( 3, nextLine.length );

    assertEquals( "a", nextLine[0] );
    assertEquals( 1, nextLine[1].length() );
    assertEquals( "\'", nextLine[1] );
    assertEquals( "c", nextLine[2] );
    c.close();

  }




  @Test
  public void testEmptySingleQuoted() throws IOException, ParseException {

    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );

    sb.append( "a,'',c" ).append( "\n" );

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ), ',', '\'' );

    final String[] nextLine = c.readNext();
    assertEquals( 3, nextLine.length );

    assertEquals( "a", nextLine[0] );
    assertEquals( 0, nextLine[1].length() );
    assertEquals( "", nextLine[1] );
    assertEquals( "c", nextLine[2] );
    c.close();

  }




  @Test
  public void testExtraWhitespace() throws IOException, ParseException {
    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );

    sb.append( "\"a\",\"b\",\"c\"   " );

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ), CSVParser.SEPARATOR, CSVParser.QUOTE_CHARACTER, true );

    final String[] nextLine = c.readNext();
    assertEquals( 3, nextLine.length );

    assertEquals( "a", nextLine[0] );
    assertEquals( "b", nextLine[1] );
    assertEquals( "c", nextLine[2] );
    c.close();
  }




  @Test
  public void testEscapedQuote() throws IOException, ParseException {

    final StringBuffer sb = new StringBuffer();

    sb.append( "a,\"123\\\"4567\",c" ).append( "\n" );

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ) );

    final String[] nextLine = c.readNext();
    assertEquals( 3, nextLine.length );

    assertEquals( "123\"4567", nextLine[1] );
    c.close();
  }




  @Test
  public void testEscapedEscape() throws IOException, ParseException {

    final StringBuffer sb = new StringBuffer();

    sb.append( "a,\"123\\\\4567\",c" ).append( "\n" );

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ) );

    final String[] nextLine = c.readNext();
    assertEquals( 3, nextLine.length );

    assertEquals( "123\\4567", nextLine[1] );
    c.close();
  }




  @Test
  public void testDoubleQuotedSingleQuote() throws IOException, ParseException {

    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );

    sb.append( "a,'',c" ).append( "\n" );// a,'',c

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ) );

    final String[] nextLine = c.readNext();
    assertEquals( 3, nextLine.length );

    assertEquals( "a", nextLine[0] );
    assertEquals( 2, nextLine[1].length() );
    assertEquals( "''", nextLine[1] );
    assertEquals( "c", nextLine[2] );
    c.close();
  }




  @Test
  public void testQuotedParsedLine() throws IOException, ParseException {

    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );

    sb.append( "\"a\",\"1234567\",\"c\"" ).append( "\n" );

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ), CSVParser.SEPARATOR, CSVParser.QUOTE_CHARACTER, true );

    final String[] nextLine = c.readNext();
    assertEquals( 3, nextLine.length );

    assertEquals( "a", nextLine[0] );
    assertEquals( 1, nextLine[0].length() );

    assertEquals( "1234567", nextLine[1] );
    assertEquals( "c", nextLine[2] );
    c.close();

  }




  @Test
  public void testOutOfPlaceQuotes() throws IOException, ParseException {
    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );

    sb.append( "a,b,c,ddd\\\"eee\nf,g,h,\"iii,jjj\"" );

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ) );

    final String[] nextLine = c.readNext();

    assertEquals( "a", nextLine[0] );
    assertEquals( "b", nextLine[1] );
    assertEquals( "c", nextLine[2] );
    assertEquals( "ddd\"eee", nextLine[3] );
    c.close();

  }




  @Test(expected = UnsupportedOperationException.class)
  public void quoteAndEscapeMustBeDifferent() {
    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );

    sb.append( "a,b,c,ddd\\\"eee\nf,g,h,\"iii,jjj\"" );

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ), CSVParser.SEPARATOR, CSVParser.QUOTE_CHARACTER, CSVParser.QUOTE_CHARACTER, CSVReader.LINES_TO_SKIP, CSVParser.STRICT_QUOTES, CSVParser.IGNORE_LEADING_WHITESPACE );
    try {
      c.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }

  }




  @Test(expected = UnsupportedOperationException.class)
  public void testSepAndEsc() {
    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );

    sb.append( "a,b,c,ddd\\\"eee\nf,g,h,\"iii,jjj\"" );

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ), CSVParser.SEPARATOR, CSVParser.QUOTE_CHARACTER, CSVParser.SEPARATOR, CSVReader.LINES_TO_SKIP, CSVParser.STRICT_QUOTES, CSVParser.IGNORE_LEADING_WHITESPACE );
    try {
      c.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }
  }




  @Test(expected = UnsupportedOperationException.class)
  public void testSepAndQuote() {
    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );

    sb.append( "a,b,c,ddd\\\"eee\nf,g,h,\"iii,jjj\"" );

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ), CSVParser.SEPARATOR, CSVParser.SEPARATOR, CSVParser.ESCAPE_CHARACTER, CSVReader.LINES_TO_SKIP, CSVParser.STRICT_QUOTES, CSVParser.IGNORE_LEADING_WHITESPACE );
    try {
      c.close();
    } catch ( final IOException e ) {
      fail( e.getMessage() );
    }
  }




  @Test
  public void testOptionalConstructors() throws IOException, ParseException {

    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );
    sb.append( "a\tb\tc" ).append( "\n" );
    sb.append( "a\t'b\tb\tb'\tc" ).append( "\n" );
    final CSVReader c = new CSVReader( new StringReader( sb.toString() ), '\t', '\'' );

    String[] nextLine = c.readNext();
    assertEquals( 3, nextLine.length );

    nextLine = c.readNext();
    assertEquals( 3, nextLine.length );
    c.close();

  }




  @Test
  public void testDelim() throws IOException, ParseException {
    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );
    sb.append( "a\tb\tc" ).append( "\n" );

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ), '\t' );

    final String[] nextLine = c.readNext();
    assertEquals( 3, nextLine.length );
    c.close();

  }




  @Test
  public void testSkippingLines() throws IOException, ParseException {

    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );
    sb.append( "Skip this line\t with tab" ).append( "\n" );
    sb.append( "And this line too" ).append( "\n" );
    sb.append( "a\t'b\tb\tb'\tc" ).append( "\n" );
    final CSVReader c = new CSVReader( new StringReader( sb.toString() ), '\t', '\'', 2 );

    final String[] nextLine = c.readNext();
    assertEquals( 3, nextLine.length );

    assertEquals( "a", nextLine[0] );
    c.close();

  }




  @Test
  public void testDiffEsc() throws IOException, ParseException {

    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );
    sb.append( "Skip this line?t with tab" ).append( "\n" );
    sb.append( "And this line too" ).append( "\n" );
    sb.append( "a\t'b\tb\tb'\t'c'" ).append( "\n" );
    final CSVReader c = new CSVReader( new StringReader( sb.toString() ), '\t', '\'', '?', 2 );

    final String[] nextLine = c.readNext();

    assertEquals( 3, nextLine.length );

    assertEquals( "a", nextLine[0] );
    assertEquals( "c", nextLine[2] );
    c.close();
  }




  @Test
  public void testNormalParsedLine() throws IOException, ParseException {

    final StringBuilder sb = new StringBuilder( CSVParser.INITIAL_READ_SIZE );

    sb.append( "a,1234567,c" ).append( "\n" );

    final CSVReader c = new CSVReader( new StringReader( sb.toString() ) );

    final String[] nextLine = c.readNext();
    assertEquals( 3, nextLine.length );

    assertEquals( "a", nextLine[0] );
    assertEquals( "1234567", nextLine[1] );
    assertEquals( "c", nextLine[2] );
    c.close();

  }

}
