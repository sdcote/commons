package coyote.commons.cli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import coyote.commons.StringUtil;


/**
 * Represents list of arguments parsed against a {@link Options} descriptor.
 *
 * <p>It allows querying of a boolean {@link #hasOption(String opt)}, in 
 * addition to retrieving the {@link #getOptionValue(String opt)} for options 
 * requiring arguments.
 *
 * <p>Additionally, any left-over or unrecognized arguments, are available for 
 * further processing.
 */
public class ArgumentList implements Serializable {
  private static final long serialVersionUID = 1L;

  /** the unrecognized options/arguments */
  private final List<String> args = new LinkedList<String>();

  /** the processed options */
  private final List<Option> options = new ArrayList<Option>();




  /**
   * Creates a list of arguments.
   */
  ArgumentList() {
    // nothing to do
  }




  /**
   * Add left-over unrecognized option/argument.
   *
   * @param arg the unrecognized option/argument.
   */
  void addArg( final String arg ) {
    args.add( arg );
  }




  /**
   * Add an option to the argument list.
   *
   * @param opt the processed option
   */
  void addOption( final Option opt ) {
    options.add( opt );
  }




  /** 
   * Retrieve any left-over non-recognized options and arguments
   *
   * @return remaining items passed in but not parsed as a {@code List}.
   */
  public List<String> getArgList() {
    return args;
  }




  /** 
   * Retrieve any left-over non-recognized options and arguments
   *
   * @return remaining items passed in but not parsed as an array
   */
  public String[] getArgs() {
    final String[] answer = new String[args.size()];

    args.toArray( answer );

    return answer;
  }




  /**
   * Retrieve the map of values associated to the option. This is convenient
   * for options specifying Java properties like {@code -Dparam1=value1
   * -Dparam2=value2}. The first argument of the option is the key, and
   * the 2nd argument is the value. If the option has only one argument
   * ({@code -Xfoo}) it is considered as a boolean flag and the value is
   * {@code "true"}.
   *
   * @param opt name of the option
   * 
   * @return The Properties mapped by the option, never {@code null} even if 
   * the option doesn't exists
   */
  public Properties getOptionProperties( final String opt ) {
    final Properties props = new Properties();

    for ( final Option option : options ) {
      if ( opt.equals( option.getOpt() ) || opt.equals( option.getLongOpt() ) ) {
        final List<String> values = option.getValuesList();
        if ( values.size() >= 2 ) {
          // use the first 2 arguments as the key/value pair
          props.put( values.get( 0 ), values.get( 1 ) );
        } else if ( values.size() == 1 ) {
          // no explicit value, handle it as a boolean
          props.put( values.get( 0 ), "true" );
        }
      }
    }

    return props;
  }




  /**
   * Returns an array of the processed {@link Option}s.
   *
   * @return an array of the processed {@link Option}s.
   */
  public Option[] getOptions() {
    final Collection<Option> processed = options;

    // reinitialize array
    final Option[] optionsArray = new Option[processed.size()];

    // return the array
    return processed.toArray( optionsArray );
  }




  /** 
   * Retrieve the argument, if any, of this option.
   *
   * @param opt the character name of the option
   * 
   * @return Value of the argument if option is set, and has an argument,
   * otherwise null.
   */
  public String getOptionValue( final char opt ) {
    return getOptionValue( String.valueOf( opt ) );
  }




  /** 
   * Retrieve the argument, if any, of an option.
   *
   * @param opt character name of the option
   * @param defaultValue is the default value to be returned if the option
   * is not specified
   * 
   * @return Value of the argument if option is set, and has an argument,
   * otherwise {@code defaultValue}.
   */
  public String getOptionValue( final char opt, final String defaultValue ) {
    return getOptionValue( String.valueOf( opt ), defaultValue );
  }




  /** 
   * Retrieve the argument, if any, of this option.
   *
   * @param opt the name of the option
   * 
   * @return Value of the argument if option is set, and has an argument,
   * otherwise null.
   */
  public String getOptionValue( final String opt ) {
    final String[] values = getOptionValues( opt );

    return ( values == null ) ? null : values[0];
  }




  /** 
   * Retrieve the argument, if any, of an option.
   *
   * @param opt name of the option
   * @param defaultValue is the default value to be returned if the option
   * is not specified
   * 
   * @return Value of the argument if option is set, and has an argument,
   * otherwise {@code defaultValue}.
   */
  public String getOptionValue( final String opt, final String defaultValue ) {
    final String answer = getOptionValue( opt );

    return ( answer != null ) ? answer : defaultValue;
  }




  /** 
   * Retrieves the array of values, if any, of an option.
   *
   * @param opt character name of the option
   * 
   * @return Values of the argument if option is set, and has an argument,
   * otherwise null.
   */
  public String[] getOptionValues( final char opt ) {
    return getOptionValues( String.valueOf( opt ) );
  }




  /** 
   * Retrieves the array of values, if any, of an option.
   *
   * @param opt string name of the option
   * 
   * @return Values of the argument if option is set, and has an argument,
   * otherwise null.
   */
  public String[] getOptionValues( final String opt ) {
    final List<String> values = new ArrayList<String>();

    for ( final Option option : options ) {
      if ( opt.equals( option.getOpt() ) || opt.equals( option.getLongOpt() ) ) {
        values.addAll( option.getValuesList() );
      }
    }

    return values.isEmpty() ? null : (String[])values.toArray( new String[values.size()] );
  }




  /**
   * Return the {@code Object} type of this {@code Option}.
   *
   * @param opt the name of the option
   * 
   * @return the type of opt
   * 
   * @throws ArgumentException if there are problems turning the option value into the desired type
   */
  public Object getParsedOptionValue( final char opt ) throws ArgumentException {
    return getParsedOptionValue( String.valueOf( opt ) );
  }




  /**
   * Return a version of this {@code Option} converted to a particular type. 
   *
   * @param opt the name of the option
   * 
   * @return the value parsed into a particular object
   * 
   * @throws ArgumentException if there are problems turning the option value into the desired type
   */
  public Object getParsedOptionValue( final String opt ) throws ArgumentException {
    final String res = getOptionValue( opt );

    final Option option = resolveOption( opt );
    if ( option == null ) {
      return null;
    }

    final Object type = option.getType();

    return ( res == null ) ? null : TypeHandler.createValue( res, type );
  }




  /** 
   * Query to see if an option has been set.
   *
   * @param opt character name of the option
   * 
   * @return true if set, false if not
   */
  public boolean hasOption( final char opt ) {
    return hasOption( String.valueOf( opt ) );
  }




  /** 
   * Query to see if an option has been set.
   *
   * @param opt Short name of the option
   * 
   * @return true if set, false if not
   */
  public boolean hasOption( final String opt ) {
    return options.contains( resolveOption( opt ) );
  }




  /**
   * Returns an iterator over the Option members of ArgumentList.
   *
   * @return an {@code Iterator} over the processed {@link Option} members of 
   * this {@link ArgumentList}
   */
  public Iterator<Option> iterator() {
    return options.iterator();
  }




  /**
   * Retrieves the option object given the long or short option as a String
   * 
   * @param opt short or long name of the option
   * 
   * @return Canonicalized option
   */
  private Option resolveOption( String opt ) {
    opt = StringUtil.stripLeadingHyphens( opt );
    for ( final Option option : options ) {
      if ( opt.equals( option.getOpt() ) ) {
        return option;
      }

      if ( opt.equals( option.getLongOpt() ) ) {
        return option;
      }

    }
    return null;
  }

}
