package coyote.commons.cli;

/**
 * Exception thrown during parsing signaling an unrecognized
 * option was seen.
 */
public class UnrecognizedOptionException extends ArgumentException {
  /**
   * 
   */
  private static final long serialVersionUID = -7243845035382751264L;
  /** The  unrecognized option */
  private String option;




  /**
   * Construct a new <code>UnrecognizedArgumentException</code>
   * with the specified detail message.
   *
   * @param message the detail message
   */
  public UnrecognizedOptionException( final String message ) {
    super( message );
  }




  /**
   * Construct a new <code>UnrecognizedArgumentException</code>
   * with the specified option and detail message.
   *
   * @param message the detail message
   * @param option  the unrecognized option
   */
  public UnrecognizedOptionException( final String message, final String option ) {
    this( message );
    this.option = option;
  }




  /**
   * Returns the unrecognized option.
   *
   * @return the related option
   */
  public String getOption() {
    return option;
  }
}
