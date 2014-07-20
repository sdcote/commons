package coyote.commons.cli;

/**
 * Thrown when an option requiring an argument is not provided with an argument.
 */
public class MissingArgumentException extends ArgumentException {
  private static final long serialVersionUID = 3409045243535594244L;

  /** The option requiring additional arguments */
  private Option option;




  /**
   * Construct a new {@code MissingArgumentException} with the specified message.
   *
   * @param option the option requiring an argument
   */
  public MissingArgumentException( final Option option ) {
    this( "Missing argument for option: " + option.getKey() );
    this.option = option;
  }




  /**
   * Construct a new {@code MissingArgumentException} with the specified message.
   *
   * @param msg the detail message
   */
  public MissingArgumentException( final String msg ) {
    super( msg );
  }




  /**
   * Return the option requiring an argument that wasn't provided
   * on the command line.
   *
   * @return the related option
   */
  public Option getOption() {
    return option;
  }
}
