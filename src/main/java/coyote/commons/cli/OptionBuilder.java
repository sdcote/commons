package coyote.commons.cli;

/**
 * OptionBuilder allows the user to create Options using descriptive methods.
 */
public final class OptionBuilder {
  /** long option */
  private static String longopt;

  /** option description */
  private static String description;

  /** argument name */
  private static String argName;

  /** is required? */
  private static boolean required;

  /** the number of arguments */
  private static int numberOfArgs = Option.UNINITIALIZED;

  /** option type */
  private static Object type;

  /** option can have an optional argument value */
  private static boolean optionalArg;

  /** value separator for argument value */
  private static char valuesep;

  /** option builder instance */
  private static OptionBuilder instance = new OptionBuilder();




  /**
   * private constructor to prevent instances being created
   */
  private OptionBuilder() {
    // hide the constructor
  }




  /**
   * Create an Option using the current settings
   *
   * @return the Option instance
   * 
   * @throws IllegalArgumentException if {@code longOpt} has not been set.
   */
  public static Option create() throws IllegalArgumentException {
    if ( longopt == null ) {
      OptionBuilder.reset();
      throw new IllegalArgumentException( "must specify longopt" );
    }

    return create( null );
  }




  /**
   * Create an Option using the current settings and with the specified Option 
   * {@code char}.
   *
   * @param opt the character representation of the Option
   * 
   * @return the Option instance
   * 
   * @throws IllegalArgumentException if {@code opt} is not a valid character.
   */
  public static Option create( final char opt ) throws IllegalArgumentException {
    return create( String.valueOf( opt ) );
  }




  /**
   * Create an Option using the current settings and with the specified Option 
   * {@code char}.
   *
   * @param opt the {@code java.lang.String} representation of the Option
   * 
   * @return the Option instance
   * 
   * @throws IllegalArgumentException if {@code opt} is not a valid character.
   */
  public static Option create( final String opt ) throws IllegalArgumentException {
    Option option = null;
    try {
      // create the option
      option = new Option( opt, description );

      // set the option properties
      option.setLongOpt( longopt );
      option.setRequired( required );
      option.setOptionalArg( optionalArg );
      option.setArgs( numberOfArgs );
      option.setType( type );
      option.setValueSeparator( valuesep );
      option.setArgName( argName );
    }
    finally {
      // reset the OptionBuilder properties
      OptionBuilder.reset();
    }

    // return the Option instance
    return option;
  }




  /**
   * The next Option created will require an argument value.
   *
   * @return the OptionBuilder instance
   */
  public static OptionBuilder hasArg() {
    OptionBuilder.numberOfArgs = 1;

    return instance;
  }




  /**
   * The next Option created will require an argument value if {@code hasArg} 
   * is true.
   *
   * @param hasArg if true then the Option has an argument value
   * 
   * @return the OptionBuilder instance
   */
  public static OptionBuilder hasArg( final boolean hasArg ) {
    OptionBuilder.numberOfArgs = hasArg ? 1 : Option.UNINITIALIZED;

    return instance;
  }




  /**
   * The next Option created can have unlimited argument values.
   *
   * @return the OptionBuilder instance
   */
  public static OptionBuilder hasArgs() {
    OptionBuilder.numberOfArgs = Option.UNLIMITED_VALUES;

    return instance;
  }




  /**
   * The next Option created can have {@code num} argument values.
   *
   * @param num the number of args that the option can have
   * 
   * @return the OptionBuilder instance
   */
  public static OptionBuilder hasArgs( final int num ) {
    OptionBuilder.numberOfArgs = num;

    return instance;
  }




  /**
   * The next Option can have an optional argument.
   *
   * @return the OptionBuilder instance
   */
  public static OptionBuilder hasOptionalArg() {
    OptionBuilder.numberOfArgs = 1;
    OptionBuilder.optionalArg = true;

    return instance;
  }




  /**
   * The next Option can have an unlimited number of optional arguments.
   *
   * @return the OptionBuilder instance
   */
  public static OptionBuilder hasOptionalArgs() {
    OptionBuilder.numberOfArgs = Option.UNLIMITED_VALUES;
    OptionBuilder.optionalArg = true;

    return instance;
  }




  /**
   * The next Option can have the specified number of optional arguments.
   *
   * @param numArgs - the maximum number of optional arguments the next Option 
   * created can have.
   * 
   * @return the OptionBuilder instance
   */
  public static OptionBuilder hasOptionalArgs( final int numArgs ) {
    OptionBuilder.numberOfArgs = numArgs;
    OptionBuilder.optionalArg = true;

    return instance;
  }




  /**
   * The next Option created will be required.
   *
   * @return the OptionBuilder instance
   */
  public static OptionBuilder isRequired() {
    OptionBuilder.required = true;

    return instance;
  }




  /**
   * The next Option created will be required if {@code required} is true.
   *
   * @param newRequired if true then the Option is required
   * 
   * @return the OptionBuilder instance
   */
  public static OptionBuilder isRequired( final boolean newRequired ) {
    OptionBuilder.required = newRequired;

    return instance;
  }




  /**
   * Resets the member variables to their default values.
   */
  private static void reset() {
    description = null;
    argName = "arg";
    longopt = null;
    type = null;
    required = false;
    numberOfArgs = Option.UNINITIALIZED;
    optionalArg = false;
    valuesep = (char)0;
  }




  /**
   * The next Option created will have the specified argument value name.
   *
   * @param name the name for the argument value
   * 
   * @return the OptionBuilder instance
   */
  public static OptionBuilder withArgName( final String name ) {
    OptionBuilder.argName = name;

    return instance;
  }




  /**
   * The next Option created will have the specified description
   *
   * @param newDescription a description of the Option's purpose
   * 
   * @return the OptionBuilder instance
   */
  public static OptionBuilder withDescription( final String newDescription ) {
    OptionBuilder.description = newDescription;

    return instance;
  }




  /**
   * The next Option created will have the following long option value.
   *
   * @param newLongopt the long option value
   * 
   * @return the OptionBuilder instance
   */
  public static OptionBuilder withLongOpt( final String newLongopt ) {
    OptionBuilder.longopt = newLongopt;

    return instance;
  }




  /**
   * The next Option created will have a value that will be an instance of 
   * {@code type}.
   *
   * @param newType the type of the Options argument value
   * 
   * @return the OptionBuilder instance
   */
  public static OptionBuilder withType( final Object newType ) {
    OptionBuilder.type = newType;

    return instance;
  }




  /**
   * The next Option created uses '{@code =}' as a means to separate argument 
   * values.
   *
   * <b>Example:</b>
   * <pre>
   * Option opt = OptionBuilder.withValueSeparator()
   *                           .create('D');
   *
   * ArgumentList systemArgs = parser.parse(args);
   * String propertyName = opt.getValue(0);
   * String propertyValue = opt.getValue(1);
   * </pre>
   *
   * @return the OptionBuilder instance
   */
  public static OptionBuilder withValueSeparator() {
    OptionBuilder.valuesep = '=';

    return instance;
  }




  /**
   * The next Option created uses {@code sep} as a means to
   * separate argument values.
   *
   * <b>Example:</b>
   * <pre>
   * Option opt = OptionBuilder.withValueSeparator(':')
   *                           .create('D');
   *
   * ArgumentList systemArgs = parser.parse(args);
   * String propertyName = opt.getValue(0);
   * String propertyValue = opt.getValue(1);
   * </pre>
   *
   * @param sep The value separator to be used for the argument values.
   *
   * @return the OptionBuilder instance
   */
  public static OptionBuilder withValueSeparator( final char sep ) {
    OptionBuilder.valuesep = sep;

    return instance;
  }

}
