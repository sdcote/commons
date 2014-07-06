package coyote.commons.cli;

import java.util.Iterator;
import java.util.List;


/**
 * Thrown when a required option has not been provided.
 */
public class MissingOptionException extends ParseException {
  /**
   * 
   */
  private static final long serialVersionUID = -4276238738584163474L;




  /**
   * Build the exception message from the specified list of options.
   *
   * @param missingOptions
   */
  private static String createMessage( final List missingOptions ) {
    final StringBuffer buff = new StringBuffer( "Missing required option" );
    buff.append( missingOptions.size() == 1 ? "" : "s" );
    buff.append( ": " );

    final Iterator it = missingOptions.iterator();
    while ( it.hasNext() ) {
      buff.append( it.next() );
      if ( it.hasNext() ) {
        buff.append( ", " );
      }
    }

    return buff.toString();
  }

  /** The list of missing options */
  private List missingOptions;




  /**
   * Constructs a new <code>MissingSelectedException</code> with the
   * specified list of missing options.
   *
   * @param missingOptions the list of missing options
   */
  public MissingOptionException( final List missingOptions ) {
    this( createMessage( missingOptions ) );
    this.missingOptions = missingOptions;
  }




  /**
   * Construct a new <code>MissingSelectedException</code>
   * with the specified detail message.
   *
   * @param message the detail message
   */
  public MissingOptionException( final String message ) {
    super( message );
  }




  /**
   * Return the list of options (as strings) missing in the command line parsed.
   *
   * @return the missing options
   */
  public List getMissingOptions() {
    return missingOptions;
  }
}
