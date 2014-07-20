package coyote.commons.cli;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Date;


/**
 * Allows Options to be created from a single String.
 * 
 * <p>The pattern contains various single character flags and via an optional 
 * punctuation character, their expected type.</p>
 *
 * <table border="1">
 * <tr><td>a</td><td>-a flag</td></tr>
 * <tr><td>b@</td><td>-b [classname]</td></tr>
 * <tr><td>c&gt;</td><td>-c [filename]</td></tr>
 * <tr><td>d+</td><td>-d [classname] (creates object via empty constructor)</td></tr>
 * <tr><td>e%</td><td>-e [number] (creates Double/Long instance depending on existing of a '.')</td></tr>
 * <tr><td>f/</td><td>-f [url]</td></tr>
 * <tr><td>g:</td><td>-g [string]</td></tr>
 * </table>
 *
 * <p>For example, the following allows command line flags of '-v -p 
 * string-value -f /dir/file'. The exclamation mark precede a mandatory 
 * option.</p>
 * 
 * <pre>Options options = PatternOptionBuilder.parsePattern("vp:!f/");</pre>
 */
public class PatternOptionBuilder {
  /** String class */
  public static final Class<String> STRING_VALUE = String.class;

  /** Object class */
  public static final Class<Object> OBJECT_VALUE = Object.class;

  /** Number class */
  public static final Class<Number> NUMBER_VALUE = Number.class;

  /** Date class */
  public static final Class<Date> DATE_VALUE = Date.class;

  /** Class class */
  public static final Class<Class> CLASS_VALUE = Class.class;

  /** FileInputStream class */
  public static final Class<FileInputStream> EXISTING_FILE_VALUE = FileInputStream.class;

  /** File class */
  public static final Class<File> FILE_VALUE = File.class;

  /** File array class */
  public static final Class<File[]> FILES_VALUE = File[].class;

  /** URL class */
  public static final Class<URL> URL_VALUE = URL.class;




  /**
   * Retrieve the class that {@code ch} represents.
   *
   * @param ch the specified character
   * 
   * @return The class that {@code ch} represents
   */
  public static Object getValueClass( final char ch ) {
    switch ( ch ) {
      case '@':
        return PatternOptionBuilder.OBJECT_VALUE;
      case ':':
        return PatternOptionBuilder.STRING_VALUE;
      case '%':
        return PatternOptionBuilder.NUMBER_VALUE;
      case '+':
        return PatternOptionBuilder.CLASS_VALUE;
      case '#':
        return PatternOptionBuilder.DATE_VALUE;
      case '<':
        return PatternOptionBuilder.EXISTING_FILE_VALUE;
      case '>':
        return PatternOptionBuilder.FILE_VALUE;
      case '*':
        return PatternOptionBuilder.FILES_VALUE;
      case '/':
        return PatternOptionBuilder.URL_VALUE;
    }

    return null;
  }




  /**
   * Returns whether {@code ch} is a value code, i.e. whether it represents a 
   * class in a pattern.
   *
   * @param ch the specified character
   * 
   * @return true if {@code ch} is a value code, otherwise false.
   */
  public static boolean isValueCode( final char ch ) {
    return ( ch == '@' ) || ( ch == ':' ) || ( ch == '%' ) || ( ch == '+' ) || ( ch == '#' ) || ( ch == '<' ) || ( ch == '>' ) || ( ch == '*' ) || ( ch == '/' ) || ( ch == '!' );
  }




  /**
   * Returns the {@link Options} instance represented by {@code pattern}.
   *
   * @param pattern the pattern string
   * 
   * @return The {@link Options} instance
   */
  public static Options parsePattern( final String pattern ) {
    char opt = ' ';
    boolean required = false;
    Object type = null;

    final Options options = new Options();

    for ( int i = 0; i < pattern.length(); i++ ) {
      final char ch = pattern.charAt( i );

      // a value code comes after an option and specifies details about it
      if ( !isValueCode( ch ) ) {
        if ( opt != ' ' ) {
          OptionBuilder.hasArg( type != null );
          OptionBuilder.isRequired( required );
          OptionBuilder.withType( type );

          // we have a previous one to deal with
          options.addOption( OptionBuilder.create( opt ) );
          required = false;
          type = null;
          opt = ' ';
        }

        opt = ch;
      } else if ( ch == '!' ) {
        required = true;
      } else {
        type = getValueClass( ch );
      }
    }

    if ( opt != ' ' ) {
      OptionBuilder.hasArg( type != null );
      OptionBuilder.isRequired( required );
      OptionBuilder.withType( type );

      // we have a final one to deal with
      options.addOption( OptionBuilder.create( opt ) );
    }

    return options;
  }
}
