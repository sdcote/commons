package coyote.commons.cli;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


/** 
 * A formatter of help messages for the current command line options
 */
public class HelpFormatter {

  /**
   * This class implements the <code>Comparator</code> interface
   * for comparing Options.
   */
  private static class OptionComparator implements Comparator {

    /**
     * Compares its two arguments for order. Returns a negative
     * integer, zero, or a positive integer as the first argument
     * is less than, equal to, or greater than the second.
     *
     * @param o1 The first Option to be compared.
     * @param o2 The second Option to be compared.
     * @return a negative integer, zero, or a positive integer as
     *         the first argument is less than, equal to, or greater than the
     *         second.
     */
    @Override
    public int compare( final Object o1, final Object o2 ) {
      final Option opt1 = (Option)o1;
      final Option opt2 = (Option)o2;

      return opt1.getKey().compareToIgnoreCase( opt2.getKey() );
    }
  }

  /** default number of characters per line */
  public static final int DEFAULT_WIDTH = 74;

  /** default padding to the left of each line */
  public static final int DEFAULT_LEFT_PAD = 1;

  /**
   * the number of characters of padding to be prefixed
   * to each description line
   */
  public static final int DEFAULT_DESC_PAD = 3;

  /** the string to display at the beginning of the usage statement */
  public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

  /** default prefix for shortOpts */
  public static final String DEFAULT_OPT_PREFIX = "-";

  /** default prefix for long Option */
  public static final String DEFAULT_LONG_OPT_PREFIX = "--";

  /** default name for an argument */
  public static final String DEFAULT_ARG_NAME = "arg";




  /**
   * Appends the usage clause for an Option to a StringBuffer.  
   *
   * @param buff the StringBuffer to append to
   * @param option the Option to append
   * @param required whether the Option is required or not
   */
  private static void appendOption( final StringBuffer buff, final Option option, final boolean required ) {
    if ( !required ) {
      buff.append( "[" );
    }

    if ( option.getOpt() != null ) {
      buff.append( "-" ).append( option.getOpt() );
    } else {
      buff.append( "--" ).append( option.getLongOpt() );
    }

    // if the Option has a value
    if ( option.hasArg() && option.hasArgName() ) {
      buff.append( " <" ).append( option.getArgName() ).append( ">" );
    }

    // if the Option is not a required option
    if ( !required ) {
      buff.append( "]" );
    }
  }

  /**
   * number of characters per line
   */
  private int defaultWidth = DEFAULT_WIDTH;

  /**
   * amount of padding to the left of each line
   */
  private int defaultLeftPad = DEFAULT_LEFT_PAD;

  /**
   * the number of characters of padding to be prefixed
   * to each description line
   */
  private int defaultDescPad = DEFAULT_DESC_PAD;

  /**
   * the string to display at the beginning of the usage statement
   */
  private String defaultSyntaxPrefix = DEFAULT_SYNTAX_PREFIX;

  /**
   * the new line string
   */
  private String defaultNewLine = System.getProperty( "line.separator" );

  /**
   * the shortOpt prefix
   */
  private String defaultOptPrefix = DEFAULT_OPT_PREFIX;

  /**
   * the long Opt prefix
   */
  private String defaultLongOptPrefix = DEFAULT_LONG_OPT_PREFIX;

  /**
   * the name of the argument
   */
  private String defaultArgName = DEFAULT_ARG_NAME;

  /**
   * Comparator used to sort the options when they output in help text
   * 
   * Defaults to case-insensitive alphabetical sorting by option key
   */
  protected Comparator optionComparator = new OptionComparator();




  /**
   * Appends the usage clause for an OptionGroup to a StringBuffer.  
   * The clause is wrapped in square brackets if the group is required.
   * The display of the options is handled by appendOption
   * @param buff the StringBuffer to append to
   * @param group the group to append
   * @see #appendOption(StringBuffer,Option,boolean)
   */
  private void appendOptionGroup( final StringBuffer buff, final OptionGroup group ) {
    if ( !group.isRequired() ) {
      buff.append( "[" );
    }

    final List optList = new ArrayList( group.getOptions() );
    Collections.sort( optList, getOptionComparator() );
    // for each option in the OptionGroup
    for ( final Iterator i = optList.iterator(); i.hasNext(); ) {
      // whether the option is required or not is handled at group level
      appendOption( buff, (Option)i.next(), true );

      if ( i.hasNext() ) {
        buff.append( " | " );
      }
    }

    if ( !group.isRequired() ) {
      buff.append( "]" );
    }
  }




  /**
   * Return a String of padding of length <code>len</code>.
   *
   * @param len The length of the String of padding to create.
   *
   * @return The String of padding
   */
  protected String createPadding( final int len ) {
    final StringBuffer sb = new StringBuffer( len );

    for ( int i = 0; i < len; ++i ) {
      sb.append( ' ' );
    }

    return sb.toString();
  }




  /**
   * Finds the next text wrap position after <code>startPos</code> for the
   * text in <code>text</code> with the column width <code>width</code>.
   * The wrap point is the last position before startPos+width having a 
   * whitespace character (space, \n, \r).
   *
   * @param text The text being searched for the wrap position
   * @param width width of the wrapped text
   * @param startPos position from which to start the lookup whitespace
   * character
   * @return position on which the text must be wrapped or -1 if the wrap
   * position is at the end of the text
   */
  protected int findWrapPos( final String text, final int width, final int startPos ) {
    int pos = -1;

    // the line ends before the max wrap pos or a new line char found
    if ( ( ( ( pos = text.indexOf( '\n', startPos ) ) != -1 ) && ( pos <= width ) ) || ( ( ( pos = text.indexOf( '\t', startPos ) ) != -1 ) && ( pos <= width ) ) ) {
      return pos + 1;
    } else if ( ( startPos + width ) >= text.length() ) {
      return -1;
    }

    // look for the last whitespace character before startPos+width
    pos = startPos + width;

    char c;

    while ( ( pos >= startPos ) && ( ( c = text.charAt( pos ) ) != ' ' ) && ( c != '\n' ) && ( c != '\r' ) ) {
      --pos;
    }

    // if we found it - just return
    if ( pos > startPos ) {
      return pos;
    }

    // must look for the first whitespace chearacter after startPos 
    // + width
    pos = startPos + width;

    while ( ( pos <= text.length() ) && ( ( c = text.charAt( pos ) ) != ' ' ) && ( c != '\n' ) && ( c != '\r' ) ) {
      ++pos;
    }

    return ( pos == text.length() ) ? ( -1 ) : pos;
  }




  /**
   * Returns the 'argName'.
   *
   * @return the 'argName'
   */
  public String getArgName() {
    return defaultArgName;
  }




  /**
   * Returns the 'descPadding'.
   *
   * @return the 'descPadding'
   */
  public int getDescPadding() {
    return defaultDescPad;
  }




  /**
   * Returns the 'leftPadding'.
   *
   * @return the 'leftPadding'
   */
  public int getLeftPadding() {
    return defaultLeftPad;
  }




  /**
   * Returns the 'longOptPrefix'.
   *
   * @return the 'longOptPrefix'
   */
  public String getLongOptPrefix() {
    return defaultLongOptPrefix;
  }




  /**
   * Returns the 'newLine'.
   *
   * @return the 'newLine'
   */
  public String getNewLine() {
    return defaultNewLine;
  }




  /**
   * Comparator used to sort the options when they output in help text
   * 
   * Defaults to case-insensitive alphabetical sorting by option key
   */
  public Comparator getOptionComparator() {
    return optionComparator;
  }




  /**
   * Returns the 'optPrefix'.
   *
   * @return the 'optPrefix'
   */
  public String getOptPrefix() {
    return defaultOptPrefix;
  }




  /**
   * Returns the 'syntaxPrefix'.
   *
   * @return the 'syntaxPrefix'
   */
  public String getSyntaxPrefix() {
    return defaultSyntaxPrefix;
  }




  /**
   * Returns the 'width'.
   *
   * @return the 'width'
   */
  public int getWidth() {
    return defaultWidth;
  }




  /**
   * Print the help for <code>options</code> with the specified
   * command line syntax.  This method prints help information to
   * System.out.
   *
   * @param width the number of characters to be displayed on each line
   * @param cmdLineSyntax the syntax for this application
   * @param header the banner to display at the beginning of the help
   * @param options the Options instance
   * @param footer the banner to display at the end of the help
   */
  public void printHelp( final int width, final String cmdLineSyntax, final String header, final Options options, final String footer ) {
    printHelp( width, cmdLineSyntax, header, options, footer, false );
  }




  /**
   * Print the help for <code>options</code> with the specified
   * command line syntax.  This method prints help information to
   * System.out.
   *
   * @param width the number of characters to be displayed on each line
   * @param cmdLineSyntax the syntax for this application
   * @param header the banner to display at the beginning of the help
   * @param options the Options instance
   * @param footer the banner to display at the end of the help
   * @param autoUsage whether to print an automatically generated 
   * usage statement
   */
  public void printHelp( final int width, final String cmdLineSyntax, final String header, final Options options, final String footer, final boolean autoUsage ) {
    final PrintWriter pw = new PrintWriter( System.out );

    printHelp( pw, width, cmdLineSyntax, header, options, defaultLeftPad, defaultDescPad, footer, autoUsage );
    pw.flush();
  }




  /**
   * Print the help for <code>options</code> with the specified
   * command line syntax.
   *
   * @param pw the writer to which the help will be written
   * @param width the number of characters to be displayed on each line
   * @param cmdLineSyntax the syntax for this application
   * @param header the banner to display at the beginning of the help
   * @param options the Options instance
   * @param leftPad the number of characters of padding to be prefixed
   * to each line
   * @param descPad the number of characters of padding to be prefixed
   * to each description line
   * @param footer the banner to display at the end of the help
   *
   * @throws IllegalStateException if there is no room to print a line
   */
  public void printHelp( final PrintWriter pw, final int width, final String cmdLineSyntax, final String header, final Options options, final int leftPad, final int descPad, final String footer ) {
    printHelp( pw, width, cmdLineSyntax, header, options, leftPad, descPad, footer, false );
  }




  /**
   * Print the help for <code>options</code> with the specified
   * command line syntax.
   *
   * @param pw the writer to which the help will be written
   * @param width the number of characters to be displayed on each line
   * @param cmdLineSyntax the syntax for this application
   * @param header the banner to display at the beginning of the help
   * @param options the Options instance
   * @param leftPad the number of characters of padding to be prefixed
   * to each line
   * @param descPad the number of characters of padding to be prefixed
   * to each description line
   * @param footer the banner to display at the end of the help
   * @param autoUsage whether to print an automatically generated
   * usage statement
   *
   * @throws IllegalStateException if there is no room to print a line
   */
  public void printHelp( final PrintWriter pw, final int width, final String cmdLineSyntax, final String header, final Options options, final int leftPad, final int descPad, final String footer, final boolean autoUsage ) {
    if ( ( cmdLineSyntax == null ) || ( cmdLineSyntax.length() == 0 ) ) {
      throw new IllegalArgumentException( "cmdLineSyntax not provided" );
    }

    if ( autoUsage ) {
      printUsage( pw, width, cmdLineSyntax, options );
    } else {
      printUsage( pw, width, cmdLineSyntax );
    }

    if ( ( header != null ) && ( header.trim().length() > 0 ) ) {
      printWrapped( pw, width, header );
    }

    printOptions( pw, width, options, leftPad, descPad );

    if ( ( footer != null ) && ( footer.trim().length() > 0 ) ) {
      printWrapped( pw, width, footer );
    }
  }




  /**
   * Print the help for <code>options</code> with the specified
   * command line syntax.  This method prints help information to
   * System.out.
   *
   * @param cmdLineSyntax the syntax for this application
   * @param options the Options instance
   */
  public void printHelp( final String cmdLineSyntax, final Options options ) {
    printHelp( defaultWidth, cmdLineSyntax, null, options, null, false );
  }




  /**
   * Print the help for <code>options</code> with the specified
   * command line syntax.  This method prints help information to 
   * System.out.
   *
   * @param cmdLineSyntax the syntax for this application
   * @param options the Options instance
   * @param autoUsage whether to print an automatically generated
   * usage statement
   */
  public void printHelp( final String cmdLineSyntax, final Options options, final boolean autoUsage ) {
    printHelp( defaultWidth, cmdLineSyntax, null, options, null, autoUsage );
  }




  /**
   * Print the help for <code>options</code> with the specified
   * command line syntax.  This method prints help information to
   * System.out.
   *
   * @param cmdLineSyntax the syntax for this application
   * @param header the banner to display at the begining of the help
   * @param options the Options instance
   * @param footer the banner to display at the end of the help
   */
  public void printHelp( final String cmdLineSyntax, final String header, final Options options, final String footer ) {
    printHelp( cmdLineSyntax, header, options, footer, false );
  }




  /**
   * Print the help for <code>options</code> with the specified
   * command line syntax.  This method prints help information to 
   * System.out.
   *
   * @param cmdLineSyntax the syntax for this application
   * @param header the banner to display at the begining of the help
   * @param options the Options instance
   * @param footer the banner to display at the end of the help
   * @param autoUsage whether to print an automatically generated
   * usage statement
   */
  public void printHelp( final String cmdLineSyntax, final String header, final Options options, final String footer, final boolean autoUsage ) {
    printHelp( defaultWidth, cmdLineSyntax, header, options, footer, autoUsage );
  }




  /**
   * <p>Print the help for the specified Options to the specified writer, 
   * using the specified width, left padding and description padding.</p>
   *
   * @param pw The printWriter to write the help to
   * @param width The number of characters to display per line
   * @param options The command line Options
   * @param leftPad the number of characters of padding to be prefixed
   * to each line
   * @param descPad the number of characters of padding to be prefixed
   * to each description line
   */
  public void printOptions( final PrintWriter pw, final int width, final Options options, final int leftPad, final int descPad ) {
    final StringBuffer sb = new StringBuffer();

    renderOptions( sb, width, options, leftPad, descPad );
    pw.println( sb.toString() );
  }




  /**
   * Print the cmdLineSyntax to the specified writer, using the
   * specified width.
   *
   * @param pw The printWriter to write the help to
   * @param width The number of characters per line for the usage statement.
   * @param cmdLineSyntax The usage statement.
   */
  public void printUsage( final PrintWriter pw, final int width, final String cmdLineSyntax ) {
    final int argPos = cmdLineSyntax.indexOf( ' ' ) + 1;

    printWrapped( pw, width, defaultSyntaxPrefix.length() + argPos, defaultSyntaxPrefix + cmdLineSyntax );
  }




  /**
   * <p>Prints the usage statement for the specified application.</p>
   *
   * @param pw The PrintWriter to print the usage statement 
   * @param width The number of characters to display per line
   * @param app The application name
   * @param options The command line Options
   *
   */
  public void printUsage( final PrintWriter pw, final int width, final String app, final Options options ) {
    // Initialize the string buffer
    final StringBuffer buff = new StringBuffer( defaultSyntaxPrefix ).append( app ).append( " " );

    // create a list for processed option groups
    final Collection processedGroups = new ArrayList();

    // temp variable
    Option option;

    final List optList = new ArrayList( options.getOptions() );
    Collections.sort( optList, getOptionComparator() );
    // iterate over the options
    for ( final Iterator i = optList.iterator(); i.hasNext(); ) {
      // get the next Option
      option = (Option)i.next();

      // check if the option is part of an OptionGroup
      final OptionGroup group = options.getOptionGroup( option );

      // if the option is part of a group 
      if ( group != null ) {
        // and if the group has not already been processed
        if ( !processedGroups.contains( group ) ) {
          // add the group to the processed list
          processedGroups.add( group );

          // add the usage clause
          appendOptionGroup( buff, group );
        }

        // otherwise the option was displayed in the group
        // previously so ignore it.
      }

      // if the Option is not part of an OptionGroup
      else {
        appendOption( buff, option, option.isRequired() );
      }

      if ( i.hasNext() ) {
        buff.append( " " );
      }
    }

    // call printWrapped
    printWrapped( pw, width, buff.toString().indexOf( ' ' ) + 1, buff.toString() );
  }




  /**
   * Print the specified text to the specified PrintWriter.
   *
   * @param pw The printWriter to write the help to
   * @param width The number of characters to display per line
   * @param nextLineTabStop The position on the next line for the first tab.
   * @param text The text to be written to the PrintWriter
   */
  public void printWrapped( final PrintWriter pw, final int width, final int nextLineTabStop, final String text ) {
    final StringBuffer sb = new StringBuffer( text.length() );

    renderWrappedText( sb, width, nextLineTabStop, text );
    pw.println( sb.toString() );
  }




  /**
   * Print the specified text to the specified PrintWriter.
   *
   * @param pw The printWriter to write the help to
   * @param width The number of characters to display per line
   * @param text The text to be written to the PrintWriter
   */
  public void printWrapped( final PrintWriter pw, final int width, final String text ) {
    printWrapped( pw, width, 0, text );
  }




  /**
   * Render the specified Options and return the rendered Options
   * in a StringBuffer.
   *
   * @param sb The StringBuffer to place the rendered Options into.
   * @param width The number of characters to display per line
   * @param options The command line Options
   * @param leftPad the number of characters of padding to be prefixed
   * to each line
   * @param descPad the number of characters of padding to be prefixed
   * to each description line
   *
   * @return the StringBuffer with the rendered Options contents.
   */
  protected StringBuffer renderOptions( final StringBuffer sb, final int width, final Options options, final int leftPad, final int descPad ) {
    final String lpad = createPadding( leftPad );
    final String dpad = createPadding( descPad );

    // first create list containing only <lpad>-a,--aaa where 
    // -a is opt and --aaa is long opt; in parallel look for 
    // the longest opt string this list will be then used to 
    // sort options ascending
    int max = 0;
    StringBuffer optBuf;
    final List prefixList = new ArrayList();

    final List optList = options.helpOptions();

    Collections.sort( optList, getOptionComparator() );

    for ( final Iterator i = optList.iterator(); i.hasNext(); ) {
      final Option option = (Option)i.next();
      optBuf = new StringBuffer( 8 );

      if ( option.getOpt() == null ) {
        optBuf.append( lpad ).append( "   " + defaultLongOptPrefix ).append( option.getLongOpt() );
      } else {
        optBuf.append( lpad ).append( defaultOptPrefix ).append( option.getOpt() );

        if ( option.hasLongOpt() ) {
          optBuf.append( ',' ).append( defaultLongOptPrefix ).append( option.getLongOpt() );
        }
      }

      if ( option.hasArg() ) {
        if ( option.hasArgName() ) {
          optBuf.append( " <" ).append( option.getArgName() ).append( ">" );
        } else {
          optBuf.append( ' ' );
        }
      }

      prefixList.add( optBuf );
      max = ( optBuf.length() > max ) ? optBuf.length() : max;
    }

    int x = 0;

    for ( final Iterator i = optList.iterator(); i.hasNext(); ) {
      final Option option = (Option)i.next();
      optBuf = new StringBuffer( prefixList.get( x++ ).toString() );

      if ( optBuf.length() < max ) {
        optBuf.append( createPadding( max - optBuf.length() ) );
      }

      optBuf.append( dpad );

      final int nextLineTabStop = max + descPad;

      if ( option.getDescription() != null ) {
        optBuf.append( option.getDescription() );
      }

      renderWrappedText( sb, width, nextLineTabStop, optBuf.toString() );

      if ( i.hasNext() ) {
        sb.append( defaultNewLine );
      }
    }

    return sb;
  }




  /**
   * Render the specified text and return the rendered Options
   * in a StringBuffer.
   *
   * @param sb The StringBuffer to place the rendered text into.
   * @param width The number of characters to display per line
   * @param nextLineTabStop The position on the next line for the first tab.
   * @param text The text to be rendered.
   *
   * @return the StringBuffer with the rendered Options contents.
   */
  protected StringBuffer renderWrappedText( final StringBuffer sb, final int width, int nextLineTabStop, String text ) {
    int pos = findWrapPos( text, width, 0 );

    if ( pos == -1 ) {
      sb.append( rtrim( text ) );

      return sb;
    }
    sb.append( rtrim( text.substring( 0, pos ) ) ).append( defaultNewLine );

    if ( nextLineTabStop >= width ) {
      // stops infinite loop happening
      nextLineTabStop = 1;
    }

    // all following lines must be padded with nextLineTabStop space 
    // characters
    final String padding = createPadding( nextLineTabStop );

    while ( true ) {
      text = padding + text.substring( pos ).trim();
      pos = findWrapPos( text, width, 0 );

      if ( pos == -1 ) {
        sb.append( text );

        return sb;
      }

      if ( ( text.length() > width ) && ( pos == ( nextLineTabStop - 1 ) ) ) {
        pos = width;
      }

      sb.append( rtrim( text.substring( 0, pos ) ) ).append( defaultNewLine );
    }
  }




  /**
   * Remove the trailing whitespace from the specified String.
   *
   * @param s The String to remove the trailing padding from.
   *
   * @return The String of without the trailing padding
   */
  protected String rtrim( final String s ) {
    if ( ( s == null ) || ( s.length() == 0 ) ) {
      return s;
    }

    int pos = s.length();

    while ( ( pos > 0 ) && Character.isWhitespace( s.charAt( pos - 1 ) ) ) {
      --pos;
    }

    return s.substring( 0, pos );
  }




  /**
   * Sets the 'argName'.
   *
   * @param name the new value of 'argName'
   */
  public void setArgName( final String name ) {
    defaultArgName = name;
  }




  /**
   * Sets the 'descPadding'.
   *
   * @param padding the new value of 'descPadding'
   */
  public void setDescPadding( final int padding ) {
    defaultDescPad = padding;
  }




  /**
   * Sets the 'leftPadding'.
   *
   * @param padding the new value of 'leftPadding'
   */
  public void setLeftPadding( final int padding ) {
    defaultLeftPad = padding;
  }




  /**
   * Sets the 'longOptPrefix'.
   *
   * @param prefix the new value of 'longOptPrefix'
   */
  public void setLongOptPrefix( final String prefix ) {
    defaultLongOptPrefix = prefix;
  }




  /**
   * Sets the 'newLine'.
   *
   * @param newline the new value of 'newLine'
   */
  public void setNewLine( final String newline ) {
    defaultNewLine = newline;
  }




  /**
   * Set the comparator used to sort the options when they output in help text
   * 
   * Passing in a null parameter will set the ordering to the default mode
   */
  public void setOptionComparator( final Comparator comparator ) {
    if ( comparator == null ) {
      optionComparator = new OptionComparator();
    } else {
      optionComparator = comparator;
    }
  }




  /**
   * Sets the 'optPrefix'.
   *
   * @param prefix the new value of 'optPrefix'
   */
  public void setOptPrefix( final String prefix ) {
    defaultOptPrefix = prefix;
  }




  /**
   * Sets the 'syntaxPrefix'.
   *
   * @param prefix the new value of 'syntaxPrefix'
   */
  public void setSyntaxPrefix( final String prefix ) {
    defaultSyntaxPrefix = prefix;
  }




  /**
   * Sets the 'width'.
   *
   * @param width the new value of 'width'
   */
  public void setWidth( final int width ) {
    defaultWidth = width;
  }
}
