package coyote.commons.cli;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * This is a collection of tests that test real world applications command lines.
 *
 * <p>
 * The following applications are tested:
 * <ul>
 *   <li>ls</li>
 *   <li>Ant</li>
 *   <li>Groovy</li>
 *   <li>man</li>
 * </ul>
 * </p>
 */
public class ApplicationTest {

  /**
   * Ant test
   */
  @Test
  public void testAnt() throws Exception {
    // use the GNU parser
    final ArgumentParser parser = new GnuParser();
    final Options options = new Options();
    options.addOption( "help", false, "print this message" );
    options.addOption( "projecthelp", false, "print project help information" );
    options.addOption( "version", false, "print the version information and exit" );
    options.addOption( "quiet", false, "be extra quiet" );
    options.addOption( "verbose", false, "be extra verbose" );
    options.addOption( "debug", false, "print debug information" );
    options.addOption( "logfile", true, "use given file for log" );
    options.addOption( "logger", true, "the class which is to perform the logging" );
    options.addOption( "listener", true, "add an instance of a class as a project listener" );
    options.addOption( "buildfile", true, "use given buildfile" );
    options.addOption( OptionBuilder.withDescription( "use value for given property" ).hasArgs().withValueSeparator().create( 'D' ) );
    options.addOption( "find", true, "search for buildfile towards the root of the filesystem and use it" );

    final String[] args = new String[] { "-buildfile", "mybuild.xml", "-Dproperty=value", "-Dproperty1=value1", "-projecthelp" };

    final ArgumentList argList = parser.parse( options, args );

    // check multiple values
    final String[] opts = argList.getOptionValues( "D" );
    assertEquals( "property", opts[0] );
    assertEquals( "value", opts[1] );
    assertEquals( "property1", opts[2] );
    assertEquals( "value1", opts[3] );

    // check single value
    assertEquals( argList.getOptionValue( "buildfile" ), "mybuild.xml" );

    // check option
    assertTrue( argList.hasOption( "projecthelp" ) );
  }




  @Test
  public void testGroovy() throws Exception {
    final Options options = new Options();

    options.addOption( OptionBuilder.withLongOpt( "define" ).withDescription( "define a system property" ).hasArg( true ).withArgName( "name=value" ).create( 'D' ) );
    options.addOption( OptionBuilder.hasArg( false ).withDescription( "usage information" ).withLongOpt( "help" ).create( 'h' ) );
    options.addOption( OptionBuilder.hasArg( false ).withDescription( "debug mode will print out full stack traces" ).withLongOpt( "debug" ).create( 'd' ) );
    options.addOption( OptionBuilder.hasArg( false ).withDescription( "display the Groovy and JVM versions" ).withLongOpt( "version" ).create( 'v' ) );
    options.addOption( OptionBuilder.withArgName( "charset" ).hasArg().withDescription( "specify the encoding of the files" ).withLongOpt( "encoding" ).create( 'c' ) );
    options.addOption( OptionBuilder.withArgName( "script" ).hasArg().withDescription( "specify a command line script" ).create( 'e' ) );
    options.addOption( OptionBuilder.withArgName( "extension" ).hasOptionalArg().withDescription( "modify files in place; create backup if extension is given (e.g. \'.bak\')" ).create( 'i' ) );
    options.addOption( OptionBuilder.hasArg( false ).withDescription( "process files line by line using implicit 'line' variable" ).create( 'n' ) );
    options.addOption( OptionBuilder.hasArg( false ).withDescription( "process files line by line and print result (see also -n)" ).create( 'p' ) );
    options.addOption( OptionBuilder.withArgName( "port" ).hasOptionalArg().withDescription( "listen on a port and process inbound lines" ).create( 'l' ) );
    options.addOption( OptionBuilder.withArgName( "splitPattern" ).hasOptionalArg().withDescription( "split lines using splitPattern (default '\\s') using implicit 'split' variable" ).withLongOpt( "autosplit" ).create( 'a' ) );

    final Parser parser = new PosixParser();
    final ArgumentList argList = parser.parse( options, new String[] { "-e", "println 'hello'" }, true );

    assertTrue( argList.hasOption( 'e' ) );
    assertEquals( "println 'hello'", argList.getOptionValue( 'e' ) );
  }




  @Test
  public void testLs() throws Exception {
    // create the command line parser
    final ArgumentParser parser = new PosixParser();
    final Options options = new Options();
    options.addOption( "a", "all", false, "do not hide entries starting with ." );
    options.addOption( "A", "almost-all", false, "do not list implied . and .." );
    options.addOption( "b", "escape", false, "print octal escapes for nongraphic characters" );
    options.addOption( OptionBuilder.withLongOpt( "block-size" ).withDescription( "use SIZE-byte blocks" ).hasArg().withArgName( "SIZE" ).create() );
    options.addOption( "B", "ignore-backups", false, "do not list implied entried ending with ~" );
    options.addOption( "c", false, "with -lt: sort by, and show, ctime (time of last modification of file status information) with -l:show ctime and sort by name otherwise: sort by ctime" );
    options.addOption( "C", false, "list entries by columns" );

    final String[] args = new String[] { "--block-size=10" };

    final ArgumentList argList = parser.parse( options, args );
    assertTrue( argList.hasOption( "block-size" ) );
    assertEquals( argList.getOptionValue( "block-size" ), "10" );
  }




  /**
   *
   */
  @Test
  public void testMan() {
    final String cmdLine = "man [-c|-f|-k|-w|-tZT device] [-adlhu7V] [-Mpath] [-Ppager] [-Slist] " + "[-msystem] [-pstring] [-Llocale] [-eextension] [section] page ...";
    final Options options = new Options().addOption( "a", "all", false, "find all matching manual pages." ).addOption( "d", "debug", false, "emit debugging messages." ).addOption( "e", "extension", false, "limit search to extension type 'extension'." ).addOption( "f", "whatis", false, "equivalent to whatis." ).addOption( "k", "apropos", false, "equivalent to apropos." ).addOption( "w", "location", false, "print physical location of man page(s)." ).addOption( "l", "local-file", false, "interpret 'page' argument(s) as local filename(s)" ).addOption( "u", "update", false, "force a cache consistency check." ).
    //FIXME - should generate -r,--prompt string
        addOption( "r", "prompt", true, "provide 'less' pager with prompt." ).addOption( "c", "catman", false, "used by catman to reformat out of date cat pages." ).addOption( "7", "ascii", false, "display ASCII translation or certain latin1 chars." ).addOption( "t", "troff", false, "use troff format pages." ).
        //FIXME - should generate -T,--troff-device device
        addOption( "T", "troff-device", true, "use groff with selected device." ).addOption( "Z", "ditroff", false, "use groff with selected device." ).addOption( "D", "default", false, "reset all options to their default values." ).
        //FIXME - should generate -M,--manpath path
        addOption( "M", "manpath", true, "set search path for manual pages to 'path'." ).
        //FIXME - should generate -P,--pager pager
        addOption( "P", "pager", true, "use program 'pager' to display output." ).
        //FIXME - should generate -S,--sections list
        addOption( "S", "sections", true, "use colon separated section list." ).
        //FIXME - should generate -m,--systems system
        addOption( "m", "systems", true, "search for man pages from other unix system(s)." ).
        //FIXME - should generate -L,--locale locale
        addOption( "L", "locale", true, "define the locale for this particular man search." ).
        //FIXME - should generate -p,--preprocessor string
        addOption( "p", "preprocessor", true, "string indicates which preprocessor to run.\n" + " e - [n]eqn  p - pic     t - tbl\n" + " g - grap    r - refer   v - vgrind" ).addOption( "V", "version", false, "show version." ).addOption( "h", "help", false, "show this usage message." );

    final HelpFormatter hf = new HelpFormatter();
    //hf.printHelp(cmdLine, opts);
    hf.printHelp( 60, cmdLine, null, options, null );
  }

}
