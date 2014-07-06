package coyote.commons.cli;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * <p>Main entry-point into the library.</p>
 *
 * <p>Options represents a collection of {@link Option} objects, which
 * describe the possible options for a command-line.<p>
 *
 * <p>It may flexibly parse long and short options, with or without
 * values.  Additionally, it may parse only a portion of a commandline,
 * allowing for flexible multi-stage parsing.<p>
 */
public class Options implements Serializable {
  private static final long serialVersionUID = 1L;

  /** a map of the options with the character key */
  private final Map shortOpts = new HashMap();

  /** a map of the options with the long key */
  private final Map longOpts = new HashMap();

  /** a map of the required options */
  private final List requiredOpts = new ArrayList();

  /** a map of the option groups */
  private final Map optionGroups = new HashMap();




  /**
   * Adds an option instance
   *
   * @param opt the option that is to be added
   * @return the resulting Options instance
   */
  public Options addOption( final Option opt ) {
    final String key = opt.getKey();

    // add it to the long option list
    if ( opt.hasLongOpt() ) {
      longOpts.put( opt.getLongOpt(), opt );
    }

    // if the option is required add it to the required list
    if ( opt.isRequired() ) {
      if ( requiredOpts.contains( key ) ) {
        requiredOpts.remove( requiredOpts.indexOf( key ) );
      }
      requiredOpts.add( key );
    }

    shortOpts.put( key, opt );

    return this;
  }




  /**
   * Add an option that only contains a short-name.
   * It may be specified as requiring an argument.
   *
   * @param opt Short single-character name of the option.
   * @param hasArg flag signally if an argument is required after this option
   * @param description Self-documenting description
   * @return the resulting Options instance
   */
  public Options addOption( final String opt, final boolean hasArg, final String description ) {
    addOption( opt, null, hasArg, description );

    return this;
  }




  /**
   * Add an option that contains a short-name and a long-name.
   * It may be specified as requiring an argument.
   *
   * @param opt Short single-character name of the option.
   * @param longOpt Long multi-character name of the option.
   * @param hasArg flag signally if an argument is required after this option
   * @param description Self-documenting description
   * @return the resulting Options instance
   */
  public Options addOption( final String opt, final String longOpt, final boolean hasArg, final String description ) {
    addOption( new Option( opt, longOpt, hasArg, description ) );

    return this;
  }




  /**
   * Add the specified option group.
   *
   * @param group the OptionGroup that is to be added
   * @return the resulting Options instance
   */
  public Options addOptionGroup( final OptionGroup group ) {
    final Iterator options = group.getOptions().iterator();

    if ( group.isRequired() ) {
      requiredOpts.add( group );
    }

    while ( options.hasNext() ) {
      final Option option = (Option)options.next();

      // an Option cannot be required if it is in an
      // OptionGroup, either the group is required or
      // nothing is required
      option.setRequired( false );
      addOption( option );

      optionGroups.put( option.getKey(), group );
    }

    return this;
  }




  /**
   * Retrieve the {@link Option} matching the long or short name specified.
   * The leading hyphens in the name are ignored (up to 2).
   *
   * @param opt short or long name of the {@link Option}
   * @return the option represented by opt
   */
  public Option getOption( String opt ) {
    opt = Util.stripLeadingHyphens( opt );

    if ( shortOpts.containsKey( opt ) ) {
      return (Option)shortOpts.get( opt );
    }

    return (Option)longOpts.get( opt );
  }




  /**
   * Returns the OptionGroup the <code>opt</code> belongs to.
   * @param opt the option whose OptionGroup is being queried.
   *
   * @return the OptionGroup if <code>opt</code> is part
   * of an OptionGroup, otherwise return null
   */
  public OptionGroup getOptionGroup( final Option opt ) {
    return (OptionGroup)optionGroups.get( opt.getKey() );
  }




  /**
   * Lists the OptionGroups that are members of this Options instance.
   *
   * @return a Collection of OptionGroup instances.
   */
  Collection getOptionGroups() {
    return new HashSet( optionGroups.values() );
  }




  /**
   * Retrieve a read-only list of options in this set
   *
   * @return read-only Collection of {@link Option} objects in this descriptor
   */
  public Collection getOptions() {
    return Collections.unmodifiableCollection( helpOptions() );
  }




  /**
   * Returns the required options.
   *
   * @return List of required options
   */
  public List getRequiredOptions() {
    return requiredOpts;
  }




  /**
   * Returns whether the named {@link Option} is a member of this {@link Options}.
   *
   * @param opt short or long name of the {@link Option}
   * @return true if the named {@link Option} is a member
   * of this {@link Options}
   */
  public boolean hasOption( String opt ) {
    opt = Util.stripLeadingHyphens( opt );

    return shortOpts.containsKey( opt ) || longOpts.containsKey( opt );
  }




  /**
   * Returns the Options for use by the HelpFormatter.
   *
   * @return the List of Options
   */
  List helpOptions() {
    return new ArrayList( shortOpts.values() );
  }




  /**
   * Dump state, suitable for debugging.
   *
   * @return The human readable form of this object
   */
  @Override
  public String toString() {
    final StringBuffer buf = new StringBuffer();

    buf.append( "[ Options: [ short " );
    buf.append( shortOpts.toString() );
    buf.append( " ] [ long " );
    buf.append( longOpts );
    buf.append( " ]" );

    return buf.toString();
  }
}
