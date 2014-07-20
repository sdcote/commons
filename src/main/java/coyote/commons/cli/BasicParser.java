package coyote.commons.cli;

/**
 * The class BasicParser provides a very simple implementation of
 * the {@link Parser#flatten(Options,String[],boolean) flatten} method.
 */
public class BasicParser extends Parser {
  /**
   * <p>A simple implementation of {@link Parser}'s abstract
   * {@link Parser#flatten(Options, String[], boolean) flatten} method.</p>
   *
   * <p><b>Note:</b> <code>options</code> and <code>stopAtNonOption</code>
   * are not used in this <code>flatten</code> method.</p>
   *
   * @param options The argument list {@link Options}
   * @param arguments The command-line arguments to be parsed
   * @param stopAtNonOption Specifies whether to stop flattening when an 
   * unrecognized option is found.
   * 
   * @return The <code>arguments</code> String array.
   */
  @Override
  protected String[] flatten( final Options options, final String[] arguments, final boolean stopAtNonOption ) {
    // just echo the arguments
    return arguments;
  }
}
