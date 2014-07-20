package coyote.commons.cli;

/**
 * Base for Exceptions thrown during parsing of command-line arguments.
 */
public class ArgumentException extends Exception {

  private static final long serialVersionUID = 4394607932276076440L;




  /**
   * Construct a new {@code ArgumentException} with the specified message.
   *
   * @param msg the message
   */
  public ArgumentException( final String msg ) {
    super( msg );
  }
}
