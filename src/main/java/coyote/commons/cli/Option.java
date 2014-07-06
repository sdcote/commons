package coyote.commons.cli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Describes a single command-line option.  It maintains
 * information regarding the short-name of the option, the long-name,
 * if any exists, a flag indicating if an argument is required for
 * this option, and a self-documenting description of the option.</p>
 *
 * <p>An Option is not created independently, but is created through
 * an instance of {@link Options}.<p>
 */
public class Option implements Cloneable, Serializable {
  private static final long serialVersionUID = 1L;

  /** constant that specifies the number of argument values has not been specified */
  public static final int UNINITIALIZED = -1;

  /** constant that specifies the number of argument values is infinite */
  public static final int UNLIMITED_VALUES = -2;

  /** the name of the option */
  private final String opt;

  /** the long representation of the option */
  private String longOpt;

  /** the name of the argument for this option */
  private String argName = "arg";

  /** description of the option */
  private String description;

  /** specifies whether this option is required to be present */
  private boolean required;

  /** specifies whether the argument value of this Option is optional */
  private boolean optionalArg;

  /** the number of argument values this option can have */
  private int numberOfArgs = UNINITIALIZED;

  /** the type of this Option */
  private Object type;

  /** the list of argument values **/
  private List values = new ArrayList();

  /** the character that is the value separator */
  private char valuesep;




  /**
   * Creates an Option using the specified parameters.
   *
   * @param opt short representation of the option
   * @param hasArg specifies whether the Option takes an argument or not
   * @param description describes the function of the option
   *
   * @throws IllegalArgumentException if there are any non valid
   * Option characters in <code>opt</code>.
   */
  public Option( final String opt, final boolean hasArg, final String description ) throws IllegalArgumentException {
    this( opt, null, hasArg, description );
  }




  /**
   * Creates an Option using the specified parameters.
   *
   * @param opt short representation of the option
   * @param description describes the function of the option
   *
   * @throws IllegalArgumentException if there are any non valid
   * Option characters in <code>opt</code>.
   */
  public Option( final String opt, final String description ) throws IllegalArgumentException {
    this( opt, null, false, description );
  }




  /**
   * Creates an Option using the specified parameters.
   *
   * @param opt short representation of the option
   * @param longOpt the long representation of the option
   * @param hasArg specifies whether the Option takes an argument or not
   * @param description describes the function of the option
   *
   * @throws IllegalArgumentException if there are any non valid
   * Option characters in <code>opt</code>.
   */
  public Option( final String opt, final String longOpt, final boolean hasArg, final String description ) throws IllegalArgumentException {
    // ensure that the option is valid
    OptionValidator.validateOption( opt );

    this.opt = opt;
    this.longOpt = longOpt;

    // if hasArg is set then the number of arguments is 1
    if ( hasArg ) {
      numberOfArgs = 1;
    }

    this.description = description;
  }




  /**
   * Add the value to this Option.  If the number of arguments
   * is greater than zero and there is enough space in the list then
   * add the value.  Otherwise, throw a runtime exception.
   *
   * @param value The value to be added to this Option
   */
  private void add( final String value ) {
    if ( ( numberOfArgs > 0 ) && ( values.size() > ( numberOfArgs - 1 ) ) ) {
      throw new RuntimeException( "Cannot add value, list full." );
    }

    // store value
    values.add( value );
  }




  /**
   * Adds the specified value to this Option.
   * 
   * @param value is a/the value of this Option
   */
  void addValueForProcessing( final String value ) {
    switch ( numberOfArgs ) {
      case UNINITIALIZED:
        throw new RuntimeException( "NO_ARGS_ALLOWED" );

      default:
        processValue( value );
    }
  }




  /**
   * Clear the Option values. After a parse is complete, these are left with
   * data in them and they need clearing if another parse is done.
   */
  void clearValues() {
    values.clear();
  }




  /**
   * A rather odd clone method - due to incorrect code in 1.0 it is public 
   * and in 1.1 rather than throwing a CloneNotSupportedException it throws 
   * a RuntimeException so as to maintain backwards compat at the API level. 
   *
   * After calling this method, it is very likely you will want to call 
   * clearValues(). 
   *
   * @throws RuntimeException
   */
  @Override
  public Object clone() {
    try {
      final Option option = (Option)super.clone();
      option.values = new ArrayList( values );
      return option;
    } catch ( final CloneNotSupportedException cnse ) {
      throw new RuntimeException( "A CloneNotSupportedException was thrown: " + cnse.getMessage() );
    }
  }




  @Override
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( ( o == null ) || ( getClass() != o.getClass() ) ) {
      return false;
    }

    final Option option = (Option)o;

    if ( opt != null ? !opt.equals( option.opt ) : option.opt != null ) {
      return false;
    }
    if ( longOpt != null ? !longOpt.equals( option.longOpt ) : option.longOpt != null ) {
      return false;
    }

    return true;
  }




  /**
   * Gets the display name for the argument value.
   *
   * @return the display name for the argument value.
   */
  public String getArgName() {
    return argName;
  }




  /** 
   * Returns the number of argument values this Option can take.
   *
   * @return num the number of argument values
   */
  public int getArgs() {
    return numberOfArgs;
  }




  /** 
   * Retrieve the self-documenting description of this Option
   *
   * @return The string description of this option
   */
  public String getDescription() {
    return description;
  }




  /**
   * Returns the id of this Option.  This is only set when the
   * Option shortOpt is a single character.  This is used for switch
   * statements.
   *
   * @return the id of this Option
   */
  public int getId() {
    return getKey().charAt( 0 );
  }




  /**
   * Returns the 'unique' Option identifier.
   * 
   * @return the 'unique' Option identifier
   */
  String getKey() {
    // if 'opt' is null, then it is a 'long' option
    if ( opt == null ) {
      return longOpt;
    }

    return opt;
  }




  /** 
   * Retrieve the long name of this Option.
   *
   * @return Long name of this option, or null, if there is no long name
   */
  public String getLongOpt() {
    return longOpt;
  }




  /** 
   * Retrieve the name of this Option.
   *
   * It is this String which can be used with
   * {@link CommandLine#hasOption(String opt)} and
   * {@link CommandLine#getOptionValue(String opt)} to check
   * for existence and argument.
   *
   * @return The name of this option
   */
  public String getOpt() {
    return opt;
  }




  /**
   * Retrieve the type of this Option.
   * 
   * @return The type of this option
   */
  public Object getType() {
    return type;
  }




  /**
   * Returns the specified value of this Option or 
   * <code>null</code> if there is no value.
   *
   * @return the value/first value of this Option or 
   * <code>null</code> if there is no value.
   */
  public String getValue() {
    return hasNoValues() ? null : (String)values.get( 0 );
  }




  /**
   * Returns the specified value of this Option or 
   * <code>null</code> if there is no value.
   *
   * @param index The index of the value to be returned.
   *
   * @return the specified value of this Option or 
   * <code>null</code> if there is no value.
   *
   * @throws IndexOutOfBoundsException if index is less than 1
   * or greater than the number of the values for this Option.
   */
  public String getValue( final int index ) throws IndexOutOfBoundsException {
    return hasNoValues() ? null : (String)values.get( index );
  }




  /**
   * Returns the value/first value of this Option or the 
   * <code>defaultValue</code> if there is no value.
   *
   * @param defaultValue The value to be returned if ther
   * is no value.
   *
   * @return the value/first value of this Option or the 
   * <code>defaultValue</code> if there are no values.
   */
  public String getValue( final String defaultValue ) {
    final String value = getValue();

    return ( value != null ) ? value : defaultValue;
  }




  /**
   * Return the values of this Option as a String array 
   * or null if there are no values
   *
   * @return the values of this Option as a String array 
   * or null if there are no values
   */
  public String[] getValues() {
    return hasNoValues() ? null : (String[])values.toArray( new String[values.size()] );
  }




  /**
   * Returns the value separator character.
   *
   * @return the value separator character.
   */
  public char getValueSeparator() {
    return valuesep;
  }




  /**
   * @return the values of this Option as a List
   * or null if there are no values
   */
  public List getValuesList() {
    return values;
  }




  /** 
   * Query to see if this Option requires an argument
   *
   * @return boolean flag indicating if an argument is required
   */
  public boolean hasArg() {
    return ( numberOfArgs > 0 ) || ( numberOfArgs == UNLIMITED_VALUES );
  }




  /**
   * Returns whether the display name for the argument value
   * has been set.
   *
   * @return if the display name for the argument value has been
   * set.
   */
  public boolean hasArgName() {
    return ( argName != null ) && ( argName.length() > 0 );
  }




  /** 
   * Query to see if this Option can take many values.
   *
   * @return boolean flag indicating if multiple values are allowed
   */
  public boolean hasArgs() {
    return ( numberOfArgs > 1 ) || ( numberOfArgs == UNLIMITED_VALUES );
  }




  @Override
  public int hashCode() {
    int result;
    result = ( opt != null ? opt.hashCode() : 0 );
    result = ( 31 * result ) + ( longOpt != null ? longOpt.hashCode() : 0 );
    return result;
  }




  /** 
   * Query to see if this Option has a long name
   *
   * @return boolean flag indicating existence of a long name
   */
  public boolean hasLongOpt() {
    return longOpt != null;
  }




  /**
   * Returns whether this Option has any values.
   *
   * @return whether this Option has any values.
   */
  private boolean hasNoValues() {
    return values.isEmpty();
  }




  /**
   * @return whether this Option can have an optional argument
   */
  public boolean hasOptionalArg() {
    return optionalArg;
  }




  /**
   * Return whether this Option has specified a value separator.
   * 
   * @return whether this Option has specified a value separator.
   */
  public boolean hasValueSeparator() {
    return valuesep > 0;
  }




  /** 
   * Query to see if this Option requires an argument
   *
   * @return boolean flag indicating if an argument is required
   */
  public boolean isRequired() {
    return required;
  }




  /**
   * Processes the value.  If this Option has a value separator
   * the value will have to be parsed into individual tokens.  When
   * n-1 tokens have been processed and there are more value separators
   * in the value, parsing is ceased and the remaining characters are
   * added as a single token.
   *
   * @param value The String to be processed.
   */
  private void processValue( String value ) {
    // this Option has a separator character
    if ( hasValueSeparator() ) {
      // get the separator character
      final char sep = getValueSeparator();

      // store the index for the value separator
      int index = value.indexOf( sep );

      // while there are more value separators
      while ( index != -1 ) {
        // next value to be added 
        if ( values.size() == ( numberOfArgs - 1 ) ) {
          break;
        }

        // store
        add( value.substring( 0, index ) );

        // parse
        value = value.substring( index + 1 );

        // get new index
        index = value.indexOf( sep );
      }
    }

    // store the actual value or the last value that has been parsed
    add( value );
  }




  /**
   * Sets the display name for the argument value.
   *
   * @param argName the display name for the argument value.
   */
  public void setArgName( final String argName ) {
    this.argName = argName;
  }




  /** 
   * Sets the number of argument values this Option can take.
   *
   * @param num the number of argument values
   */
  public void setArgs( final int num ) {
    numberOfArgs = num;
  }




  /**
   * Sets the self-documenting description of this Option
   *
   * @param description The description of this option
   */
  public void setDescription( final String description ) {
    this.description = description;
  }




  /**
   * Sets the long name of this Option.
   *
   * @param longOpt the long name of this Option
   */
  public void setLongOpt( final String longOpt ) {
    this.longOpt = longOpt;
  }




  /**
   * Sets whether this Option can have an optional argument.
   *
   * @param optionalArg specifies whether the Option can have
   * an optional argument.
   */
  public void setOptionalArg( final boolean optionalArg ) {
    this.optionalArg = optionalArg;
  }




  /**
   * Sets whether this Option is mandatory.
   *
   * @param required specifies whether this Option is mandatory
   */
  public void setRequired( final boolean required ) {
    this.required = required;
  }




  /**
   * Sets the type of this Option.
   *
   * @param type the type of this Option
   */
  public void setType( final Object type ) {
    this.type = type;
  }




  /**
   * Sets the value separator.  For example if the argument value
   * was a Java property, the value separator would be '='.
   *
   * @param sep The value separator.
   */
  public void setValueSeparator( final char sep ) {
    valuesep = sep;
  }




  /** 
   * Dump state, suitable for debugging.
   *
   * @return human readable format of this object
   */
  @Override
  public String toString() {
    final StringBuffer buf = new StringBuffer().append( "[ option: " );

    buf.append( opt );

    if ( longOpt != null ) {
      buf.append( " " ).append( longOpt );
    }

    buf.append( " " );

    if ( hasArgs() ) {
      buf.append( "[ARG...]" );
    } else if ( hasArg() ) {
      buf.append( " [ARG]" );
    }

    buf.append( " :: " ).append( description );

    if ( type != null ) {
      buf.append( " :: " ).append( type );
    }

    buf.append( " ]" );

    return buf.toString();
  }

}
