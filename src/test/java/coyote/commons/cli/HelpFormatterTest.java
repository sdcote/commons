package coyote.commons.cli;

//import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;

import junit.framework.TestCase;

import org.junit.Test;


/** 
 * Test case for the HelpFormatter class 
 */
public class HelpFormatterTest extends TestCase {
  private static final String EOL = System.getProperty( "line.separator" );




  @Test
  public void testAccessors() {
    final HelpFormatter formatter = new HelpFormatter();

    formatter.setArgName( "argname" );
    assertEquals( "arg name", "argname", formatter.getArgName() );

    formatter.setDescPadding( 3 );
    assertEquals( "desc padding", 3, formatter.getDescPadding() );

    formatter.setLeftPadding( 7 );
    assertEquals( "left padding", 7, formatter.getLeftPadding() );

    formatter.setLongOptPrefix( "~~" );
    assertEquals( "long opt prefix", "~~", formatter.getLongOptPrefix() );

    formatter.setNewLine( "\n" );
    assertEquals( "new line", "\n", formatter.getNewLine() );

    formatter.setOptPrefix( "~" );
    assertEquals( "opt prefix", "~", formatter.getOptPrefix() );

    formatter.setSyntaxPrefix( "-> " );
    assertEquals( "syntax prefix", "-> ", formatter.getSyntaxPrefix() );

    formatter.setWidth( 80 );
    assertEquals( "width", 80, formatter.getWidth() );
  }




  @Test
  public void testAutomaticUsage() throws Exception {
    final HelpFormatter hf = new HelpFormatter();
    Options options = null;
    String expected = "usage: app [-a]";
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final PrintWriter pw = new PrintWriter( out );

    options = new Options().addOption( "a", false, "aaaa aaaa aaaa aaaa aaaa" );
    hf.printUsage( pw, 60, "app", options );
    pw.flush();
    assertEquals( "simple auto usage", expected, out.toString().trim() );
    out.reset();

    expected = "usage: app [-a] [-b]";
    options = new Options().addOption( "a", false, "aaaa aaaa aaaa aaaa aaaa" ).addOption( "b", false, "bbb" );
    hf.printUsage( pw, 60, "app", options );
    pw.flush();
    assertEquals( "simple auto usage", expected, out.toString().trim() );
    out.reset();
  }




  @Test
  public void testFindWrapPos() throws Exception {
    final HelpFormatter hf = new HelpFormatter();

    String text = "This is a test.";
    //text width should be max 8; the wrap position is 7
    assertEquals( "wrap position", 7, hf.findWrapPos( text, 8, 0 ) );
    //starting from 8 must give -1 - the wrap pos is after end
    assertEquals( "wrap position 2", -1, hf.findWrapPos( text, 8, 8 ) );
    //if there is no a good position before width to make a wrapping look for the next one
    text = "aaaa aa";
    assertEquals( "wrap position 3", 4, hf.findWrapPos( text, 3, 0 ) );
  }




  @Test
  public void testHeaderStartingWithLineSeparator() {
    final Options options = new Options();
    final HelpFormatter formatter = new HelpFormatter();
    final String header = EOL + "Header";
    final String footer = "Footer";
    final StringWriter out = new StringWriter();
    formatter.printHelp( new PrintWriter( out ), 80, "foobar", header, options, 2, 2, footer, true );
    assertEquals( "usage: foobar" + EOL + "" + EOL + "Header" + EOL + "" + EOL + "Footer" + EOL, out.toString() );
  }




  @Test
  public void testOptionWithoutShortFormat() {
    final Options options = new Options();
    options.addOption( new Option( "a", "aaa", false, "aaaaaaa" ) );
    options.addOption( new Option( null, "bbb", false, "bbbbbbb" ) );
    options.addOption( new Option( "c", null, false, "ccccccc" ) );

    final HelpFormatter formatter = new HelpFormatter();
    final StringWriter out = new StringWriter();
    formatter.printHelp( new PrintWriter( out ), 80, "foobar", "", options, 2, 2, "", true );
    assertEquals( "usage: foobar [-a] [--bbb] [-c]" + EOL + "  -a,--aaa  aaaaaaa" + EOL + "     --bbb  bbbbbbb" + EOL + "  -c        ccccccc" + EOL, out.toString() );
  }




  @Test
  public void testOptionWithoutShortFormat2() {
    // related to Bugzilla #27635 (CLI-26)
    final Option help = new Option( "h", "help", false, "print this message" );
    final Option version = new Option( "v", "version", false, "print version information" );
    final Option newRun = new Option( "n", "new", false, "Create NLT cache entries only for new items" );
    final Option trackerRun = new Option( "t", "tracker", false, "Create NLT cache entries only for tracker items" );

    OptionBuilder.withLongOpt( "limit" );
    OptionBuilder.hasArg();
    OptionBuilder.withValueSeparator();
    OptionBuilder.withDescription( "Set time limit for execution, in mintues" );
    final Option timeLimit = OptionBuilder.create( "l" );

    OptionBuilder.withLongOpt( "age" );
    OptionBuilder.hasArg();
    OptionBuilder.withValueSeparator();
    OptionBuilder.withDescription( "Age (in days) of cache item before being recomputed" );
    final Option age = OptionBuilder.create( "a" );

    OptionBuilder.withLongOpt( "server" );
    OptionBuilder.hasArg();
    OptionBuilder.withValueSeparator();
    OptionBuilder.withDescription( "The NLT server address" );
    final Option server = OptionBuilder.create( "s" );

    OptionBuilder.withLongOpt( "results" );
    OptionBuilder.hasArg();
    OptionBuilder.withValueSeparator();
    OptionBuilder.withDescription( "Number of results per item" );
    final Option numResults = OptionBuilder.create( "r" );

    OptionBuilder.withLongOpt( "config" );
    OptionBuilder.hasArg();
    OptionBuilder.withValueSeparator();
    OptionBuilder.withDescription( "Use the specified configuration file" );
    final Option configFile = OptionBuilder.create();

    final Options mOptions = new Options();
    mOptions.addOption( help );
    mOptions.addOption( version );
    mOptions.addOption( newRun );
    mOptions.addOption( trackerRun );
    mOptions.addOption( timeLimit );
    mOptions.addOption( age );
    mOptions.addOption( server );
    mOptions.addOption( numResults );
    mOptions.addOption( configFile );

    final HelpFormatter formatter = new HelpFormatter();
    final String EOL = System.getProperty( "line.separator" );
    final StringWriter out = new StringWriter();
    formatter.printHelp( new PrintWriter( out ), 80, "commandline", "header", mOptions, 2, 2, "footer", true );
    assertEquals( "usage: commandline [-a <arg>] [--config <arg>] [-h] [-l <arg>] [-n] [-r <arg>]" + EOL + "       [-s <arg>] [-t] [-v]" + EOL + "header" + EOL + "  -a,--age <arg>      Age (in days) of cache item before being recomputed" + EOL + "     --config <arg>   Use the specified configuration file" + EOL + "  -h,--help           print this message" + EOL + "  -l,--limit <arg>    Set time limit for execution, in mintues" + EOL + "  -n,--new            Create NLT cache entries only for new items" + EOL + "  -r,--results <arg>  Number of results per item" + EOL + "  -s,--server <arg>   The NLT server address" + EOL + "  -t,--tracker        Create NLT cache entries only for tracker items" + EOL + "  -v,--version        print version information" + EOL + "footer" + EOL, out.toString() );
  }




  @Test
  public void testPrintHelpWithEmptySyntax() {
    final HelpFormatter formatter = new HelpFormatter();
    try {
      formatter.printHelp( null, new Options() );
      fail( "null command line syntax should be rejected" );
    } catch ( final IllegalArgumentException e ) {
      // expected
    }

    try {
      formatter.printHelp( "", new Options() );
      fail( "empty command line syntax should be rejected" );
    } catch ( final IllegalArgumentException e ) {
      // expected
    }
  }




  @Test
  public void testPrintOptionGroupUsage() {
    final OptionGroup group = new OptionGroup();
    group.addOption( OptionBuilder.create( "a" ) );
    group.addOption( OptionBuilder.create( "b" ) );
    group.addOption( OptionBuilder.create( "c" ) );

    final Options options = new Options();
    options.addOptionGroup( group );

    final StringWriter out = new StringWriter();

    final HelpFormatter formatter = new HelpFormatter();
    formatter.printUsage( new PrintWriter( out ), 80, "app", options );

    assertEquals( "usage: app [-a | -b | -c]" + EOL, out.toString() );
  }




  @Test
  public void testPrintOptions() throws Exception {
    final StringBuffer sb = new StringBuffer();
    final HelpFormatter hf = new HelpFormatter();
    final int leftPad = 1;
    final int descPad = 3;
    final String lpad = hf.createPadding( leftPad );
    final String dpad = hf.createPadding( descPad );
    Options options = null;
    String expected = null;

    options = new Options().addOption( "a", false, "aaaa aaaa aaaa aaaa aaaa" );
    expected = lpad + "-a" + dpad + "aaaa aaaa aaaa aaaa aaaa";
    hf.renderOptions( sb, 60, options, leftPad, descPad );
    assertEquals( "simple non-wrapped option", expected, sb.toString() );

    int nextLineTabStop = leftPad + descPad + "-a".length();
    expected = lpad + "-a" + dpad + "aaaa aaaa aaaa" + hf.getNewLine() + hf.createPadding( nextLineTabStop ) + "aaaa aaaa";
    sb.setLength( 0 );
    hf.renderOptions( sb, nextLineTabStop + 17, options, leftPad, descPad );
    assertEquals( "simple wrapped option", expected, sb.toString() );

    options = new Options().addOption( "a", "aaa", false, "dddd dddd dddd dddd" );
    expected = lpad + "-a,--aaa" + dpad + "dddd dddd dddd dddd";
    sb.setLength( 0 );
    hf.renderOptions( sb, 60, options, leftPad, descPad );
    assertEquals( "long non-wrapped option", expected, sb.toString() );

    nextLineTabStop = leftPad + descPad + "-a,--aaa".length();
    expected = lpad + "-a,--aaa" + dpad + "dddd dddd" + hf.getNewLine() + hf.createPadding( nextLineTabStop ) + "dddd dddd";
    sb.setLength( 0 );
    hf.renderOptions( sb, 25, options, leftPad, descPad );
    assertEquals( "long wrapped option", expected, sb.toString() );

    options = new Options().addOption( "a", "aaa", false, "dddd dddd dddd dddd" ).addOption( "b", false, "feeee eeee eeee eeee" );
    expected = lpad + "-a,--aaa" + dpad + "dddd dddd" + hf.getNewLine() + hf.createPadding( nextLineTabStop ) + "dddd dddd" + hf.getNewLine() + lpad + "-b      " + dpad + "feeee eeee" + hf.getNewLine() + hf.createPadding( nextLineTabStop ) + "eeee eeee";
    sb.setLength( 0 );
    hf.renderOptions( sb, 25, options, leftPad, descPad );
    assertEquals( "multiple wrapped options", expected, sb.toString() );
  }




  @Test
  public void testPrintOptionWithEmptyArgNameUsage() {
    final Option option = new Option( "f", true, null );
    option.setArgName( "" );
    option.setRequired( true );

    final Options options = new Options();
    options.addOption( option );

    final StringWriter out = new StringWriter();

    final HelpFormatter formatter = new HelpFormatter();
    formatter.printUsage( new PrintWriter( out ), 80, "app", options );

    assertEquals( "usage: app -f" + EOL, out.toString() );
  }




  @Test
  public void testPrintRequiredOptionGroupUsage() {
    final OptionGroup group = new OptionGroup();
    group.addOption( OptionBuilder.create( "a" ) );
    group.addOption( OptionBuilder.create( "b" ) );
    group.addOption( OptionBuilder.create( "c" ) );
    group.setRequired( true );

    final Options options = new Options();
    options.addOptionGroup( group );

    final StringWriter out = new StringWriter();

    final HelpFormatter formatter = new HelpFormatter();
    formatter.printUsage( new PrintWriter( out ), 80, "app", options );

    assertEquals( "usage: app -a | -b | -c" + EOL, out.toString() );
  }




  @Test
  public void testPrintSortedUsage() {
    final Options opts = new Options();
    opts.addOption( new Option( "a", "first" ) );
    opts.addOption( new Option( "b", "second" ) );
    opts.addOption( new Option( "c", "third" ) );

    final HelpFormatter helpFormatter = new HelpFormatter();
    helpFormatter.setOptionComparator( new Comparator() {
      @Override
      public int compare( final Object o1, final Object o2 ) {
        // reverses the fuctionality of the default comparator
        final Option opt1 = (Option)o1;
        final Option opt2 = (Option)o2;
        return opt2.getKey().compareToIgnoreCase( opt1.getKey() );
      }
    } );

    final StringWriter out = new StringWriter();
    helpFormatter.printUsage( new PrintWriter( out ), 80, "app", opts );

    assertEquals( "usage: app [-c] [-b] [-a]" + EOL, out.toString() );
  }




  @Test
  public void testPrintSortedUsageWithNullComparator() {
    final Options opts = new Options();
    opts.addOption( new Option( "a", "first" ) );
    opts.addOption( new Option( "b", "second" ) );
    opts.addOption( new Option( "c", "third" ) );

    final HelpFormatter helpFormatter = new HelpFormatter();
    helpFormatter.setOptionComparator( null );

    final StringWriter out = new StringWriter();
    helpFormatter.printUsage( new PrintWriter( out ), 80, "app", opts );

    assertEquals( "usage: app [-a] [-b] [-c]" + EOL, out.toString() );
  }




  // This test ensures the options are properly sorted
  @Test
  public void testPrintUsage() {
    final Option optionA = new Option( "a", "first" );
    final Option optionB = new Option( "b", "second" );
    final Option optionC = new Option( "c", "third" );
    final Options opts = new Options();
    opts.addOption( optionA );
    opts.addOption( optionB );
    opts.addOption( optionC );
    final HelpFormatter helpFormatter = new HelpFormatter();
    final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    final PrintWriter printWriter = new PrintWriter( bytesOut );
    helpFormatter.printUsage( printWriter, 80, "app", opts );
    printWriter.close();
    assertEquals( "usage: app [-a] [-b] [-c]" + EOL, bytesOut.toString() );
  }




  @Test
  public void testPrintWrapped() throws Exception {
    final StringBuffer sb = new StringBuffer();
    final HelpFormatter hf = new HelpFormatter();

    String text = "This is a test.";

    String expected = "This is a" + hf.getNewLine() + "test.";
    hf.renderWrappedText( sb, 12, 0, text );
    assertEquals( "single line text", expected, sb.toString() );

    sb.setLength( 0 );
    expected = "This is a" + hf.getNewLine() + "    test.";
    hf.renderWrappedText( sb, 12, 4, text );
    assertEquals( "single line padded text", expected, sb.toString() );

    text = "  -p,--period <PERIOD>  PERIOD is time duration of form " + "DATE[-DATE] where DATE has form YYYY[MM[DD]]";

    sb.setLength( 0 );
    expected = "  -p,--period <PERIOD>  PERIOD is time duration of" + hf.getNewLine() + "                        form DATE[-DATE] where DATE" + hf.getNewLine() + "                        has form YYYY[MM[DD]]";
    hf.renderWrappedText( sb, 53, 24, text );
    assertEquals( "single line padded text 2", expected, sb.toString() );

    text = "aaaa aaaa aaaa" + hf.getNewLine() + "aaaaaa" + hf.getNewLine() + "aaaaa";

    expected = text;
    sb.setLength( 0 );
    hf.renderWrappedText( sb, 16, 0, text );
    assertEquals( "multi line text", expected, sb.toString() );

    expected = "aaaa aaaa aaaa" + hf.getNewLine() + "    aaaaaa" + hf.getNewLine() + "    aaaaa";
    sb.setLength( 0 );
    hf.renderWrappedText( sb, 16, 4, text );
    assertEquals( "multi-line padded text", expected, sb.toString() );
  }




  @Test
  public void testRtrim() {
    final HelpFormatter formatter = new HelpFormatter();

    assertEquals( null, formatter.rtrim( null ) );
    assertEquals( "", formatter.rtrim( "" ) );
    assertEquals( "  foo", formatter.rtrim( "  foo  " ) );
  }
}
