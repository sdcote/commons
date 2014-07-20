package coyote.commons.cli;

/**
 * Thrown when more than one option in an option group has been provided.
 */
public class AlreadySelectedException extends ArgumentException {

  private static final long serialVersionUID = 4871582029230652777L;

  /** The option group selected. */
  private OptionGroup group;

  /** The option that triggered the exception. */
  private Option option;




  /**
   * Construct a new {@code AlreadySelectedException} for the specified option 
   * group.
   *
   * @param group  the option group already selected
   * @param option the option that triggered the exception
   */
  public AlreadySelectedException( final OptionGroup group, final Option option ) {
    this( "The option '" + option.getKey() + "' was specified but an option from this group has already been selected: '" + group.getSelected() + "'" );
    this.group = group;
    this.option = option;
  }




  /**
   * Construct a new {@code AlreadySelectedException} with the specified message.
   *
   * @param message the detail message
   */
  public AlreadySelectedException( final String message ) {
    super( message );
  }




  /**
   * Returns the option that was added to the group and triggered the exception.
   *
   * @return the related option
   */
  public Option getOption() {
    return option;
  }




  /**
   * Returns the option group where another option has been selected.
   *
   * @return the related option group
   */
  public OptionGroup getOptionGroup() {
    return group;
  }
}
