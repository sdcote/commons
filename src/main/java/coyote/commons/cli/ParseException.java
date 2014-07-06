package coyote.commons.cli;

/**
 * Base for Exceptions thrown during parsing of a command-line.
 */
public class ParseException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = 4394607932276076440L;




  /**
   * Construct a new <code>ParseException</code>
   * with the specified detail message.
   *
   * @param message the detail message
   */
  public ParseException( final String message ) {
    super( message );
  }
}
