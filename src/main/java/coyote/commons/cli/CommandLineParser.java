package coyote.commons.cli;

/**
 * A class that implements the <code>CommandLineParser</code> interface
 * can parse a String array according to the {@link Options} specified
 * and return a {@link CommandLine}.
 */
public interface CommandLineParser {
  /**
   * Parse the arguments according to the specified options.
   *
   * @param options the specified Options
   * @param arguments the command line arguments
   * @return the list of atomic option and value tokens
   *
   * @throws ParseException if there are any problems encountered
   * while parsing the command line tokens.
   */
  CommandLine parse( Options options, String[] arguments ) throws ParseException;




  /**
   * Parse the arguments according to the specified options.
   *
   * @param options the specified Options
   * @param arguments the command line arguments
   * @param stopAtNonOption specifies whether to continue parsing the
   * arguments if a non option is encountered.
   *
   * @return the list of atomic option and value tokens
   * @throws ParseException if there are any problems encountered
   * while parsing the command line tokens.
   */
  CommandLine parse( Options options, String[] arguments, boolean stopAtNonOption ) throws ParseException;

}
