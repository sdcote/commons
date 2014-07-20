package coyote.commons.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * The class PosixParser provides an implementation of the
 * {@link Parser#flatten(Options,String[],boolean) flatten} method.
 */
public class PosixParser extends Parser {
  /** holder for flattened tokens */
  private final List<String> tokens = new ArrayList<String>();

  /** specifies if bursting should continue */
  private boolean eatTheRest;

  /** holder for the current option */
  private Option currentOption;

  /** the command line Options */
  private Options options;




  /**
   * Breaks {@code token} into its constituent parts using the following 
   * algorithm:<ul>
   * <li>ignore the first character ("<b>-</b>")</li>
   * <li>foreach remaining character check if an {@link Option}  exists with 
   * that id.</li>
   * <li>if an {@link Option} does exist then, add that character prepended 
   * with "<b>-</b>" to the list of processed tokens.</li>
   * <li>if the {@link Option} can have an argument value and there are 
   * remaining characters in the token, then add the remaining characters as a 
   * token to the list of processed tokens.</li>
   * <li>if an {@link Option} does <b>NOT</b> exist <b>AND</b> 
   * {@code stopAtNonOption} <b>is</b> set, then add the special token 
   * "<b>--</b>" followed by the remaining characters and also the remaining 
   * tokens directly to the processed tokens list.</li>
   * <li>if an {@link Option} does <b>NOT</b> exist <b>AND</b> 
   * {@code stopAtNonOption} <b>is not</b> set then add that character 
   * prepended with "<b>-</b>".</li></ul>
   *
   * @param token The current token to be <b>burst</b>
   * @param stopAtNonOption Specifies whether to stop processing at the first 
   * non-option encountered.
   */
  protected void burstToken( final String token, final boolean stopAtNonOption ) {
    for ( int i = 1; i < token.length(); i++ ) {
      final String ch = String.valueOf( token.charAt( i ) );

      if ( options.hasOption( ch ) ) {
        tokens.add( "-" + ch );
        currentOption = options.getOption( ch );

        if ( currentOption.hasArg() && ( token.length() != ( i + 1 ) ) ) {
          tokens.add( token.substring( i + 1 ) );

          break;
        }
      } else if ( stopAtNonOption ) {
        processNonOptionToken( token.substring( i ), true );
        break;
      } else {
        tokens.add( token );
        break;
      }
    }
  }




  /**
   * <p>An implementation of {@link Parser}'s abstract
   * {@link Parser#flatten(Options,String[],boolean) flatten} method.</p>
   *
   * <p>The following are the rules used by this flatten method:<ol>
   * <li>if {@code stopAtNonOption} is <b>true</b> then do not burst anymore of
   * {@code arguments} entries, just add each successive entry without further 
   * processing. Otherwise, ignore {@code stopAtNonOption}.</li>
   * <li>if the current {@code arguments} entry is "<b>--</b>" just add the 
   * entry to the list of processed tokens</li>
   * <li>if the current {@code arguments} entry is "<b>-</b>" just add the 
   * entry to the list of processed tokens</li>
   * <li>if the current {@code arguments} entry is two characters in length and
   * the first character is "<b>-</b>" then check if this is a valid 
   * {@link Option} id. If it is a valid id, then add the entry to the list of 
   * processed tokens and set the current {@link Option} member. If it is not a 
   * valid id and {@code stopAtNonOption} is true, then the remaining entries 
   * are copied to the list of processed tokens. Otherwise, the current entry 
   * is ignored.</li>
   * <li>if the current {@code arguments} entry is more than two characters in 
   * length and the first character is "<b>-</b>" then we need to burst the 
   * entry to determine its constituents.</li>
   * <li>if the current {@code arguments} entry is not handled by any of the 
   * previous rules, then the entry is added to the list of processed 
   * tokens.</li></ol></p>
   *
   * @param options The command line {@link Options}
   * @param arguments The command line arguments to be parsed
   * @param stopAtNonOption Specifies whether to stop flattening when a 
   * non-option is found.
   * 
   * @return The flattened {@code arguments} String array.
   */
  @Override
  protected String[] flatten( final Options options, final String[] arguments, final boolean stopAtNonOption ) {
    init();
    this.options = options;

    // an iterator for the command line tokens
    final Iterator<String> iter = Arrays.asList( arguments ).iterator();

    // process each command line token
    while ( iter.hasNext() ) {
      // get the next command line token
      final String token = iter.next();

      // handle long option --foo or --foo=bar
      if ( token.startsWith( "--" ) ) {
        final int pos = token.indexOf( '=' );
        final String opt = pos == -1 ? token : token.substring( 0, pos ); // --foo

        if ( !options.hasOption( opt ) ) {
          processNonOptionToken( token, stopAtNonOption );
        } else {
          currentOption = options.getOption( opt );

          tokens.add( opt );
          if ( pos != -1 ) {
            tokens.add( token.substring( pos + 1 ) );
          }
        }
      }

      // single hyphen
      else if ( "-".equals( token ) ) {
        tokens.add( token );
      } else if ( token.startsWith( "-" ) ) {
        if ( ( token.length() == 2 ) || options.hasOption( token ) ) {
          processOptionToken( token, stopAtNonOption );
        }
        // requires bursting
        else {
          burstToken( token, stopAtNonOption );
        }
      } else {
        processNonOptionToken( token, stopAtNonOption );
      }

      gobble( iter );
    }

    return tokens.toArray( new String[tokens.size()] );
  }




  /**
   * Adds the remaining tokens to the processed tokens list.
   *
   * @param iter An iterator over the remaining tokens
   */
  private void gobble( final Iterator<String> iter ) {
    if ( eatTheRest ) {
      while ( iter.hasNext() ) {
        tokens.add( iter.next() );
      }
    }
  }




  /**
   * Resets the members to their original state i.e. remove
   * all of {@code tokens} entries and set {@code eatTheRest}
   * to false.
   */
  private void init() {
    eatTheRest = false;
    tokens.clear();
  }




  /**
   * Add the special token "<b>--</b>" and the current {@code value}
   * to the processed tokens list. Then add all the remaining
   * {@code argument} values to the processed tokens list.
   *
   * @param value The current token
   */
  private void processNonOptionToken( final String value, final boolean stopAtNonOption ) {
    if ( stopAtNonOption && ( ( currentOption == null ) || !currentOption.hasArg() ) ) {
      eatTheRest = true;
      tokens.add( "--" );
    }

    tokens.add( value );
  }




  /**
   * <p>If an {@link Option} exists for {@code token} then
   * add the token to the processed list.</p>
   *
   * <p>If an {@link Option} does not exist and {@code stopAtNonOption}
   * is set then add the remaining tokens to the processed tokens list
   * directly.</p>
   *
   * @param token The current option token
   * @param stopAtNonOption Specifies whether flattening should halt
   * at the first non option.
   */
  private void processOptionToken( final String token, final boolean stopAtNonOption ) {
    if ( stopAtNonOption && !options.hasOption( token ) ) {
      eatTheRest = true;
    }

    if ( options.hasOption( token ) ) {
      currentOption = options.getOption( token );
    }

    tokens.add( token );
  }
  
}
