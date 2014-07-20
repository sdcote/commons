package coyote.commons.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import coyote.commons.StringUtil;


/**
 * {@code Parser} creates {@link ArgumentList}s.
 */
public abstract class Parser implements CommandLineParser {
  /** command-line instance */
  protected ArgumentList cmd;

  /** current Options */
  private Options options;

  /** list of required options strings */
  private List<String> requiredOptions;




  /**
   * Throws a {@link MissingOptionException} if all of the required options
   * are not present.
   *
   * @throws MissingOptionException if any of the required Options are not 
   * present.
   */
  protected void checkRequiredOptions() throws MissingOptionException {
    // if there are required options that have not been processed
    if ( !getRequiredOptions().isEmpty() ) {
      throw new MissingOptionException( getRequiredOptions() );
    }
  }




  /**
   * Subclasses must implement this method to reduce the {@code arguments} that 
   * have been passed to the parse method.
   *
   * @param opts The Options to parse the arguments by.
   * @param arguments The arguments that have to be flattened.
   * @param stopAtNonOption specifies whether to stop flattening when a non 
   * option has been encountered
   * 
   * @return a String array of the flattened arguments
   */
  protected abstract String[] flatten( Options opts, String[] arguments, boolean stopAtNonOption );




  /**
   * @return the options used by this parser
   */
  protected Options getOptions() {
    return options;
  }




  /**
   * @return The keys to the required options.
   */
  protected List<String> getRequiredOptions() {
    return requiredOptions;
  }




  /**
   * Parses the specified {@code arguments} based on the specified 
   * {@link Options}.
   *
   * @param options the {@code Options}
   * @param arguments the {@code arguments}
   * 
   * @return the {@code ArgumentList}
   * 
   * @throws ArgumentException if an error occurs when parsing the arguments.
   */
  @Override
  public ArgumentList parse( final Options options, final String[] arguments ) throws ArgumentException {
    return parse( options, arguments, null, false );
  }




  /**
   * Parses the specified {@code arguments} based on the specified 
   * {@link Options}.
   *
   * @param options the {@code Options}
   * @param arguments the arguments to parse
   * @param stopAtNonOption specifies whether to stop interpreting the 
   * arguments when a non option has been encountered and to add them to the 
   * ArgumentList args list.
   * 
   * @return the {@code ArgumentList}
   * 
   * @throws ArgumentException if an error occurs when parsing the arguments.
   */
  @Override
  public ArgumentList parse( final Options options, final String[] arguments, final boolean stopAtNonOption ) throws ArgumentException {
    return parse( options, arguments, null, stopAtNonOption );
  }




  /**
   * Parse the arguments according to the specified options and properties.
   *
   * @param options the specified {@code Options}
   * @param arguments  the command line arguments
   * @param properties command line option name-value pairs
   * 
   * @return the list of atomic option and value tokens
   * 
   * @throws ArgumentException if there are any problems encountered while 
   * parsing the command line tokens.
   */
  public ArgumentList parse( final Options options, final String[] arguments, final Properties properties ) throws ArgumentException {
    return parse( options, arguments, properties, false );
  }




  /**
   * Parse the arguments according to the specified options and properties.
   *
   * @param options the specified Options
   * @param arguments the command line arguments
   * @param properties command line option name-value pairs
   * @param stopAtNonOption stop parsing the arguments when the first 
   * non-option is encountered.
   *
   * @return the list of atomic option and value tokens
   *
   * @throws ArgumentException if there are any problems encountered while 
   * parsing the command line tokens.
   */
  public ArgumentList parse( final Options options, String[] arguments, final Properties properties, final boolean stopAtNonOption ) throws ArgumentException {
    // clear out the data in options in case it's been used before (CLI-71)
    for ( Option opt : options.helpOptions() ) {
      opt.clearValues();
    }

    // initialize members
    setOptions( options );

    cmd = new ArgumentList();

    boolean eatTheRest = false;

    if ( arguments == null ) {
      arguments = new String[0];
    }

    final List<String> tokenList = Arrays.asList( flatten( getOptions(), arguments, stopAtNonOption ) );

    final ListIterator<String> iterator = tokenList.listIterator();

    // process each flattened token
    while ( iterator.hasNext() ) {
      final String t = iterator.next();

      // the value is the double-dash
      if ( "--".equals( t ) ) {
        eatTheRest = true;
      }

      // the value is a single dash
      else if ( "-".equals( t ) ) {
        if ( stopAtNonOption ) {
          eatTheRest = true;
        } else {
          cmd.addArg( t );
        }
      }

      // the value is an option
      else if ( t.startsWith( "-" ) ) {
        if ( stopAtNonOption && !getOptions().hasOption( t ) ) {
          eatTheRest = true;
          cmd.addArg( t );
        } else {
          processOption( t, iterator );
        }
      }

      // the value is an argument
      else {
        cmd.addArg( t );

        if ( stopAtNonOption ) {
          eatTheRest = true;
        }
      }

      // eat the remaining tokens
      if ( eatTheRest ) {
        while ( iterator.hasNext() ) {
          final String str = iterator.next();

          // ensure only one double-dash is added
          if ( !"--".equals( str ) ) {
            cmd.addArg( str );
          }
        }
      }
    }

    processProperties( properties );
    checkRequiredOptions();

    return cmd;
  }




  /**
   * Process the argument values for the specified Option using the values 
   * retrieved from the specified iterator.
   *
   * @param opt The current Option
   * @param iter The iterator over the flattened command line Options.
   *
   * @throws ArgumentException if an argument value is required and it is has not been found.
   */
  public void processArgs( final Option opt, final ListIterator<String> iter ) throws ArgumentException {
    // loop until an option is found
    while ( iter.hasNext() ) {
      final String str = iter.next();

      // found an Option, not an argument
      if ( getOptions().hasOption( str ) && str.startsWith( "-" ) ) {
        iter.previous();
        break;
      }

      // found a value
      try {
        opt.addValueForProcessing( StringUtil.stripLeadingAndTrailingQuotes( str ) );
      } catch ( final RuntimeException exp ) {
        iter.previous();
        break;
      }
    }

    if ( ( opt.getValues() == null ) && !opt.hasOptionalArg() ) {
      throw new MissingArgumentException( opt );
    }
  }




  /**
   * Process the Option specified by {@code arg} using the values retrieved 
   * from the specified iterator {@code iter}.
   *
   * @param arg The String value representing an Option
   * @param iter The iterator over the flattened command line arguments.
   *
   * @throws ArgumentException if {@code arg} does not represent an Option
   */
  protected void processOption( final String arg, final ListIterator<String> iter ) throws ArgumentException {
    final boolean hasOption = getOptions().hasOption( arg );

    // if there is no option throw an UnrecognisedOptionException
    if ( !hasOption ) {
      throw new UnrecognizedOptionException( "Unrecognized option: " + arg, arg );
    }

    // get the option represented by arg
    final Option opt = (Option)getOptions().getOption( arg ).clone();

    // if the option is a required option remove the option from
    // the requiredOptions list
    if ( opt.isRequired() ) {
      getRequiredOptions().remove( opt.getKey() );
    }

    // if the option is in an OptionGroup make that option the selected option 
    // of the group
    if ( getOptions().getOptionGroup( opt ) != null ) {
      final OptionGroup group = getOptions().getOptionGroup( opt );

      if ( group.isRequired() ) {
        getRequiredOptions().remove( group );
      }

      group.setSelected( opt );
    }

    // if the option takes an argument value
    if ( opt.hasArg() ) {
      processArgs( opt, iter );
    }

    // set the option on the command line
    cmd.addOption( opt );
  }




  /**
   * Sets the values of Options using the values in {@code properties}.
   *
   * @param properties The value properties to be processed.
   */
  protected void processProperties( final Properties properties ) {
    if ( properties == null ) {
      return;
    }

    for ( final Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
      final String option = e.nextElement().toString();

      if ( !cmd.hasOption( option ) ) {
        final Option opt = getOptions().getOption( option );

        // get the value from the properties instance
        final String value = properties.getProperty( option );

        if ( opt.hasArg() ) {
          if ( ( opt.getValues() == null ) || ( opt.getValues().length == 0 ) ) {
            try {
              opt.addValueForProcessing( value );
            } catch ( final RuntimeException exp ) {
              // if we cannot add the value don't worry about it
            }
          }
        } else if ( !( "yes".equalsIgnoreCase( value ) || "true".equalsIgnoreCase( value ) || "1".equalsIgnoreCase( value ) ) ) {
          // if the value is not yes, true or 1 then don't add the option to 
          // the ArgumentList
          break;
        }

        cmd.addOption( opt );
      }
    }
  }




  /**
   * Set options this parser should use to parse command line argument tokens.
   * 
   * @param options the options against which the tokens are parsed.
   */
  protected void setOptions( final Options options ) {
    this.options = options;
    requiredOptions = new ArrayList<String>( options.getRequiredOptions() );
  }
  
}
