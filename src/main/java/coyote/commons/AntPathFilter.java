package coyote.commons;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * PathFilter implementation for Ant-style path patterns.
 *
 * <p>Part of this mapping code has been borrowed from 
 * <a href="http://ant.apache.org">Apache Ant</a>.
 *
 * <p>The mapping matches URLs using the following rules:<br><ul>
 * <li>? matches one character</li>
 * <li>* matches zero or more characters</li>
 * <li>** matches zero or more nodes (directories) in a path</li></ul></p>
 *
 * <p>Some examples:<br><ul>
 * <li>{@code net/t?st.jsp} - matches {@code net/test.jsp} but also {@code net/tast.jsp} or {@code net/txst.jsp}</li>
 * <li>{@code net/*.jsp} - matches all {@code .jsp} files in the {@code net} directory</li> 
 * <li>{@code net/&#42;&#42;/test.jsp} - matches all {@code test.jsp} files underneath the {@code net} path</li>
 * <li>{@code net/bralyn/&#42;&#42;/*.jsp} - matches all {@code .jsp} files underneath the {@code net/bralynk} path</li>
 * <li>{@code net/&#42;&#42;/servlet/bla.jsp} - matches {@code net/bralyn/servlet/bla.jsp} but also {@code net/bralyn/testing/servlet/bla.jsp} and {@code org/servlet/bla.jsp}</li> </ul>
 */
public class AntPathFilter implements PathFilter {

  /**
   * Tests whether or not a string matches against a pattern via a 
   * {@link Pattern}.
   * 
   * <p>The pattern may contain special characters: '*' means zero or more 
   * characters; '?' means one and only one character; '{' and '}' indicate a 
   * URI template pattern. For example <tt>/users/{user}</tt>.
   */
  protected static class AntPathStringMatcher {

    private static final Pattern GLOB_PATTERN = Pattern.compile( "\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}" );

    private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";

    private final Pattern pattern;

    private final List<String> variableNames = new LinkedList<String>();




    /**
     * Constructor
     * 
     * @param pattern the pattern for this matcher
     */
    public AntPathStringMatcher( final String pattern ) {
      final StringBuilder patternBuilder = new StringBuilder();
      final Matcher m = GLOB_PATTERN.matcher( pattern );
      int end = 0;
      while ( m.find() ) {
        patternBuilder.append( quote( pattern, end, m.start() ) );
        final String match = m.group();
        if ( "?".equals( match ) ) {
          patternBuilder.append( '.' );
        } else if ( "*".equals( match ) ) {
          patternBuilder.append( ".*" );
        } else if ( match.startsWith( "{" ) && match.endsWith( "}" ) ) {
          final int colonIdx = match.indexOf( ':' );
          if ( colonIdx == -1 ) {
            patternBuilder.append( DEFAULT_VARIABLE_PATTERN );
            variableNames.add( m.group( 1 ) );
          } else {
            final String variablePattern = match.substring( colonIdx + 1, match.length() - 1 );
            patternBuilder.append( '(' );
            patternBuilder.append( variablePattern );
            patternBuilder.append( ')' );
            final String variableName = match.substring( 1, colonIdx );
            variableNames.add( variableName );
          }
        }
        end = m.end();
      }
      patternBuilder.append( quote( pattern, end, pattern.length() ) );
      this.pattern = Pattern.compile( patternBuilder.toString() );
    }




    /**
     * Check to see if the string matches against the pattern.
     * 
     * @return {@code true} if the string matches against the pattern, or {@code false} otherwise.
     */
    public boolean matchStrings( final String str, final Map<String, String> uriTemplateVariables ) {
      final Matcher matcher = pattern.matcher( str );
      if ( matcher.matches() ) {
        if ( uriTemplateVariables != null ) {
          // SPR-8455
          Assert.isTrue( variableNames.size() == matcher.groupCount(), "The number of capturing groups in the pattern segment " + pattern + " does not match the number of URI template variables it defines, which can occur if " + " capturing groups are used in a URI template regex. Use non-capturing groups instead." );
          for ( int i = 1; i <= matcher.groupCount(); i++ ) {
            final String name = variableNames.get( i - 1 );
            final String value = matcher.group( i );
            uriTemplateVariables.put( name, value );
          }
        }
        return true;
      } else {
        return false;
      }
    }




    private String quote( final String s, final int start, final int end ) {
      if ( start == end ) {
        return "";
      }
      return Pattern.quote( s.substring( start, end ) );
    }
  }

  /**
   * The default {@link Comparator} implementation returned by {@link #getPatternComparator(String)}.
   */
  protected static class AntPatternComparator implements Comparator<String> {

    private final String path;




    /**
     * Constructor 
     * @param path the path to use
     */
    public AntPatternComparator( final String path ) {
      this.path = path;
    }




    /**
     * Compare the given patterns with the path set in this comparator
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare( final String pattern1, final String pattern2 ) {
      if ( isNullOrCaptureAllPattern( pattern1 ) && isNullOrCaptureAllPattern( pattern2 ) ) {
        return 0;
      } else if ( isNullOrCaptureAllPattern( pattern1 ) ) {
        return 1;
      } else if ( isNullOrCaptureAllPattern( pattern2 ) ) {
        return -1;
      }

      final boolean pattern1EqualsPath = pattern1.equals( path );
      final boolean pattern2EqualsPath = pattern2.equals( path );
      if ( pattern1EqualsPath && pattern2EqualsPath ) {
        return 0;
      } else if ( pattern1EqualsPath ) {
        return -1;
      } else if ( pattern2EqualsPath ) {
        return 1;
      }

      final int wildCardCount1 = getWildCardCount( pattern1 );
      final int wildCardCount2 = getWildCardCount( pattern2 );

      final int bracketCount1 = StringUtil.countOccurrencesOf( pattern1, "{" );
      final int bracketCount2 = StringUtil.countOccurrencesOf( pattern2, "{" );

      final int totalCount1 = wildCardCount1 + bracketCount1;
      final int totalCount2 = wildCardCount2 + bracketCount2;

      if ( totalCount1 != totalCount2 ) {
        return totalCount1 - totalCount2;
      }

      final int pattern1Length = getPatternLength( pattern1 );
      final int pattern2Length = getPatternLength( pattern2 );

      if ( pattern1Length != pattern2Length ) {
        return pattern2Length - pattern1Length;
      }

      if ( wildCardCount1 < wildCardCount2 ) {
        return -1;
      } else if ( wildCardCount2 < wildCardCount1 ) {
        return 1;
      }

      if ( bracketCount1 < bracketCount2 ) {
        return -1;
      } else if ( bracketCount2 < bracketCount1 ) {
        return 1;
      }

      return 0;
    }




    /**
     * @return the length of the given pattern, where template variables are considered to be 1 long.
     */
    private int getPatternLength( final String pattern ) {
      return VARIABLE_PATTERN.matcher( pattern ).replaceAll( "#" ).length();
    }




    /**
     * Return the number of wildcard occurrences in the given string.
     *  
     * @param pattern the string in which to to search for wildcards.
     * 
     * @return the number of wildcard occurrences 
     */
    private int getWildCardCount( String pattern ) {
      if ( pattern.endsWith( ".*" ) ) {
        pattern = pattern.substring( 0, pattern.length() - 2 );
      }
      return StringUtil.countOccurrencesOf( pattern, "*" );
    }




    /**
     * Chec to see if the pgiven pattern is null or the capture all (/**) 
     * pattern.
     * 
     * @param pattern the pattern to check
     * 
     * @return true if null or the capture all pattern
     */
    private boolean isNullOrCaptureAllPattern( final String pattern ) {
      return ( pattern == null ) || "/**".equals( pattern );
    }
  }

  /** Default path separator: "/" */
  public static final String DEFAULT_PATH_SEPARATOR = "/";

  private static final int CACHE_DEACTIVATION_THRESHOLD = 65536;

  private static final Pattern VARIABLE_PATTERN = Pattern.compile( "\\{[^/]+?\\}" );

  private String pathSeparator = DEFAULT_PATH_SEPARATOR;

  private boolean trimTokens = true;

  private volatile Boolean cachePatterns;

  private final Map<String, String[]> tokenizedPatternCache = new ConcurrentHashMap<String, String[]>( 256 );

  final Map<String, AntPathStringMatcher> stringMatcherCache = new ConcurrentHashMap<String, AntPathStringMatcher>( 256 );




  /**
   * Combines two patterns into a new pattern that is returned.
   * 
   * <p>This implementation simply concatenates the two patterns, unless the 
   * first pattern contains a file extension match (such as {@code *.html}. In 
   * that case, the second pattern should be included in the first, or an 
   * {@code IllegalArgumentException} is thrown.</p>
   * 
   * <p>For example:<table>
   * <tr><th>Pattern 1</th><th>Pattern 2</th><th>Result</th></tr>
   * <tr><td>/orders</td><td>{@code null}</td><td>/orders</td></tr>
   * <tr><td>{@code null}</td><td>/orders</td><td>/orders</td></tr>
   * <tr><td>/orders</td><td>/bookings</td><td>/orders/bookings</td></tr>
   * <tr><td>/orders</td><td>bookings</td><td>/orders/bookings</td></tr>
   * <tr><td>/orders/*</td><td>/bookings</td><td>/orders/bookings</td></tr>
   * <tr><td>/orders/&#42;&#42;</td><td>/bookings</td><td>/orders/&#42;&#42;/bookings</td></tr>
   * <tr><td>/orders</td><td>{order}</td><td>/orders/{order}</td></tr>
   * <tr><td>/orders/*</td><td>{order}</td><td>/orders/{order}</td></tr>
   * <tr><td>/orders/&#42;&#42;</td><td>{order}</td><td>/orders/&#42;&#42;/{order}</td></tr>
   * <tr><td>/*.html</td><td>/orders.html</td><td>/orders.html</td></tr> <tr><td>/*.html</td><td>/orders</td><td>/orders.html</td></tr>
   * <tr><td>/*.html</td><td>/*.txt</td><td>IllegalArgumentException</td></tr> </table>
   * 
   * @param pattern1 the first pattern
   * @param pattern2 the second pattern
   * 
   * @return the combination of the two patterns
   * 
   * @throws IllegalArgumentException when the two patterns cannot be combined
   */
  @Override
  public String combine( final String pattern1, final String pattern2 ) {
    if ( !StringUtil.hasText( pattern1 ) && !StringUtil.hasText( pattern2 ) ) {
      return "";
    }
    if ( !StringUtil.hasText( pattern1 ) ) {
      return pattern2;
    }
    if ( !StringUtil.hasText( pattern2 ) ) {
      return pattern1;
    }

    final boolean pattern1ContainsUriVar = pattern1.indexOf( '{' ) != -1;
    if ( !pattern1.equals( pattern2 ) && !pattern1ContainsUriVar && match( pattern1, pattern2 ) ) {
      // /* + /order -> /order ; "/*.*" + "/*.html" -> /*.html
      // However /user + /user -> /usr/user ; /{foo} + /bar -> /{foo}/bar
      return pattern2;
    }

    // /orders/* + /booking -> /orders/booking
    // /orders/* + booking -> /orders/booking
    if ( pattern1.endsWith( "/*" ) ) {
      return slashConcat( pattern1.substring( 0, pattern1.length() - 2 ), pattern2 );
    }

    // /orders/** + /booking -> /orders/**/booking
    // /orders/** + booking -> /orders/**/booking
    if ( pattern1.endsWith( "/**" ) ) {
      return slashConcat( pattern1, pattern2 );
    }

    final int starDotPos1 = pattern1.indexOf( "*." );
    if ( pattern1ContainsUriVar || ( starDotPos1 == -1 ) ) {
      // simply concatenate the two patterns
      return slashConcat( pattern1, pattern2 );
    }
    final String extension1 = pattern1.substring( starDotPos1 + 1 );
    final int dotPos2 = pattern2.indexOf( '.' );
    final String fileName2 = ( dotPos2 == -1 ? pattern2 : pattern2.substring( 0, dotPos2 ) );
    final String extension2 = ( dotPos2 == -1 ? "" : pattern2.substring( dotPos2 ) );
    final String extension = extension1.startsWith( "*" ) ? extension2 : extension1;
    return fileName2 + extension;
  }




  /**
   * Turn off pattern caching and clear the cache.
   */
  private void deactivatePatternCache() {
    cachePatterns = false;
    tokenizedPatternCache.clear();
    stringMatcherCache.clear();
  }




  /**
   * Actually match the given {@code path} against the given {@code pattern}.
   * 
   * @param pattern the pattern to match against
   * @param path the path String to test
   * @param fullMatch whether a full pattern match is required (else a pattern 
   * match as far as the given base path goes is sufficient)
   * 
   * @return {@code true} if the supplied {@code path} matched, {@code false} if it didn't
   */
  protected boolean doMatch( final String pattern, final String path, final boolean fullMatch, final Map<String, String> uriTemplateVariables ) {
    if ( path.startsWith( pathSeparator ) != pattern.startsWith( pathSeparator ) ) {
      return false;
    }

    final String[] pattDirs = tokenizePattern( pattern );
    final String[] pathDirs = tokenizePath( path );

    int pattIdxStart = 0;
    int pattIdxEnd = pattDirs.length - 1;
    int pathIdxStart = 0;
    int pathIdxEnd = pathDirs.length - 1;

    // Match all elements up to the first **
    while ( ( pattIdxStart <= pattIdxEnd ) && ( pathIdxStart <= pathIdxEnd ) ) {
      final String pattDir = pattDirs[pattIdxStart];
      if ( "**".equals( pattDir ) ) {
        break;
      }
      if ( !matchStrings( pattDir, pathDirs[pathIdxStart], uriTemplateVariables ) ) {
        return false;
      }
      pattIdxStart++;
      pathIdxStart++;
    }

    if ( pathIdxStart > pathIdxEnd ) {
      // Path is exhausted, only match if rest of pattern is * or **'s
      if ( pattIdxStart > pattIdxEnd ) {
        return ( pattern.endsWith( pathSeparator ) ? path.endsWith( pathSeparator ) : !path.endsWith( pathSeparator ) );
      }
      if ( !fullMatch ) {
        return true;
      }
      if ( ( pattIdxStart == pattIdxEnd ) && pattDirs[pattIdxStart].equals( "*" ) && path.endsWith( pathSeparator ) ) {
        return true;
      }
      for ( int i = pattIdxStart; i <= pattIdxEnd; i++ ) {
        if ( !pattDirs[i].equals( "**" ) ) {
          return false;
        }
      }
      return true;
    } else if ( pattIdxStart > pattIdxEnd ) {
      // String not exhausted, but pattern is. Failure.
      return false;
    } else if ( !fullMatch && "**".equals( pattDirs[pattIdxStart] ) ) {
      // Path start definitely matches due to "**" part in pattern.
      return true;
    }

    // up to last '**'
    while ( ( pattIdxStart <= pattIdxEnd ) && ( pathIdxStart <= pathIdxEnd ) ) {
      final String pattDir = pattDirs[pattIdxEnd];
      if ( pattDir.equals( "**" ) ) {
        break;
      }
      if ( !matchStrings( pattDir, pathDirs[pathIdxEnd], uriTemplateVariables ) ) {
        return false;
      }
      pattIdxEnd--;
      pathIdxEnd--;
    }
    if ( pathIdxStart > pathIdxEnd ) {
      // String is exhausted
      for ( int i = pattIdxStart; i <= pattIdxEnd; i++ ) {
        if ( !pattDirs[i].equals( "**" ) ) {
          return false;
        }
      }
      return true;
    }

    while ( ( pattIdxStart != pattIdxEnd ) && ( pathIdxStart <= pathIdxEnd ) ) {
      int patIdxTmp = -1;
      for ( int i = pattIdxStart + 1; i <= pattIdxEnd; i++ ) {
        if ( pattDirs[i].equals( "**" ) ) {
          patIdxTmp = i;
          break;
        }
      }
      if ( patIdxTmp == ( pattIdxStart + 1 ) ) {
        // '**/**' situation, so skip one
        pattIdxStart++;
        continue;
      }
      // Find the pattern between padIdxStart & padIdxTmp in str between
      // strIdxStart & strIdxEnd
      final int patLength = ( patIdxTmp - pattIdxStart - 1 );
      final int strLength = ( ( pathIdxEnd - pathIdxStart ) + 1 );
      int foundIdx = -1;

      strLoop: for ( int i = 0; i <= ( strLength - patLength ); i++ ) {
        for ( int j = 0; j < patLength; j++ ) {
          final String subPat = pattDirs[pattIdxStart + j + 1];
          final String subStr = pathDirs[pathIdxStart + i + j];
          if ( !matchStrings( subPat, subStr, uriTemplateVariables ) ) {
            continue strLoop;
          }
        }
        foundIdx = pathIdxStart + i;
        break;
      }

      if ( foundIdx == -1 ) {
        return false;
      }

      pattIdxStart = patIdxTmp;
      pathIdxStart = foundIdx + patLength;
    }

    for ( int i = pattIdxStart; i <= pattIdxEnd; i++ ) {
      if ( !pattDirs[i].equals( "**" ) ) {
        return false;
      }
    }

    return true;
  }




  /**
   * Given a pattern and a full path, determine the pattern-mapped part. 
   * 
   * <p>For example: <ul>
   * <li>'{@code /docs/cvs/commit.html}' and '{@code /docs/cvs/commit.html} -> ''</li>
   * <li>'{@code /docs/*}' and '{@code /docs/cvs/commit} -> '{@code cvs/commit}'</li>
   * <li>'{@code /docs/cvs/*.html}' and '{@code /docs/cvs/commit.html} -> '{@code commit.html}'</li>
   * <li>'{@code /docs/**}' and '{@code /docs/cvs/commit} -> '{@code cvs/commit}'</li>
   * <li>'{@code /docs/**\/*.html}' and '{@code /docs/cvs/commit.html} -> '{@code cvs/commit.html}'</li>
   * <li>'{@code /*.html}' and '{@code /docs/cvs/commit.html} -> '{@code docs/cvs/commit.html}'</li>
   * <li>'{@code *.html}' and '{@code /docs/cvs/commit.html} -> '{@code /docs/cvs/commit.html}'</li>
   * <li>'{@code *}' and '{@code /docs/cvs/commit.html} -> '{@code /docs/cvs/commit.html}'</li> </ul></p>
   * 
   * <p>Assumes that {@link #match} returns {@code true} for '{@code pattern}' 
   * and '{@code path}', but does <strong>not</strong> enforce this.</p>
   */
  @Override
  public String extractPathWithinPattern( final String pattern, final String path ) {
    final String[] patternParts = StringUtil.tokenizeToStringArray( pattern, pathSeparator, trimTokens, true );
    final String[] pathParts = StringUtil.tokenizeToStringArray( path, pathSeparator, trimTokens, true );

    final StringBuilder builder = new StringBuilder();

    // Add any path parts that have a wildcarded pattern part.
    int puts = 0;
    for ( int i = 0; i < patternParts.length; i++ ) {
      final String patternPart = patternParts[i];
      if ( ( ( patternPart.indexOf( '*' ) > -1 ) || ( patternPart.indexOf( '?' ) > -1 ) ) && ( pathParts.length >= ( i + 1 ) ) ) {
        if ( ( puts > 0 ) || ( ( i == 0 ) && !pattern.startsWith( pathSeparator ) ) ) {
          builder.append( pathSeparator );
        }
        builder.append( pathParts[i] );
        puts++;
      }
    }

    // Append any trailing path parts.
    for ( int i = patternParts.length; i < pathParts.length; i++ ) {
      if ( ( puts > 0 ) || ( i > 0 ) ) {
        builder.append( pathSeparator );
      }
      builder.append( pathParts[i] );
    }

    return builder.toString();
  }




  /**
   * @see coyote.commons.PathFilter#extractUriTemplateVariables(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> extractUriTemplateVariables( final String pattern, final String path ) {
    final Map<String, String> variables = new LinkedHashMap<String, String>();
    final boolean result = doMatch( pattern, path, true, variables );
    Assert.state( result, "Pattern \"" + pattern + "\" is not a match for \"" + path + "\"" );
    return variables;
  }




  /**
   * Given a full path, returns a {@link Comparator} suitable for sorting 
   * patterns in order of explicitness.
   * 
   * <p>The returned {@code Comparator} will 
   * {@linkplain java.util.Collections#sort(java.util.List, 
   * java.util.Comparator) sort} a list so that more specific patterns (without 
   * uri templates or wild cards) come before generic patterns. So given a list 
   * with the following patterns: 
   * <ol><li>{@code /orders/new}</li>
   * <li>{@code /orders/{order}}</li>
   * <li>{@code /orders/*}</li> </ol> 
   * the returned comparator will sort this list so that the order will be as 
   * indicated.</p>
   * 
   * <p>The full path given as parameter is used to test for exact matches. So 
   * when the given path is {@code /orders/2}, the pattern {@code /orders/2} 
   * will be sorted before {@code /orders/1}.</p>
   * 
   * @param path the full path to use for comparison
   * 
   * @return a comparator capable of sorting patterns in order of explicitness
   */
  @Override
  public Comparator<String> getPatternComparator( final String path ) {
    return new AntPatternComparator( path );
  }




  /**
   * Build or retrieve an {@link AntPathStringMatcher} for the given pattern.
   * 
   * <p>The default implementation checks this AntPathFilter's internal cache
   * (see {@link #setCachePatterns}), creating a new AntPathStringMatcher 
   * instance if no cached copy is found. When encountering too many patterns 
   * to cache at runtime (the threshold is 65536), it turns the default cache 
   * off, assuming that arbitrary permutations of patterns are coming in, with 
   * little chance for encountering a recurring pattern.</p>
   * 
   * <p>This method may get overridden to implement a custom cache strategy.</p>
   * 
   * @param pattern the pattern to match against (never {@code null})
   * 
   * @return a corresponding AntPathStringMatcher (never {@code null})
   * 
   * @see #setCachePatterns
   */
  protected AntPathStringMatcher getStringMatcher( final String pattern ) {
    AntPathStringMatcher matcher = null;
    final Boolean cachePatterns = this.cachePatterns;
    if ( ( cachePatterns == null ) || cachePatterns.booleanValue() ) {
      matcher = stringMatcherCache.get( pattern );
    }
    if ( matcher == null ) {
      matcher = new AntPathStringMatcher( pattern );
      if ( ( cachePatterns == null ) && ( stringMatcherCache.size() >= CACHE_DEACTIVATION_THRESHOLD ) ) {
        // Try to adapt to the runtime situation that we're encountering:
        // There are obviously too many different patterns coming in here...
        // So let's turn off the cache since the patterns are unlikely to be reoccurring.
        deactivatePatternCache();
        return matcher;
      }
      if ( ( cachePatterns == null ) || cachePatterns.booleanValue() ) {
        stringMatcherCache.put( pattern, matcher );
      }
    }
    return matcher;
  }




  /**
   * @see coyote.commons.PathFilter#isPattern(java.lang.String)
   */
  @Override
  public boolean isPattern( final String path ) {
    return ( ( path.indexOf( '*' ) != -1 ) || ( path.indexOf( '?' ) != -1 ) );
  }




  /**
   * @see coyote.commons.PathFilter#match(java.lang.String, java.lang.String)
   */
  @Override
  public boolean match( final String pattern, final String path ) {
    return doMatch( pattern, path, true, null );
  }




  /**
   * @see coyote.commons.PathFilter#matchStart(java.lang.String, java.lang.String)
   */
  @Override
  public boolean matchStart( final String pattern, final String path ) {
    return doMatch( pattern, path, false, null );
  }




  /**
   * Tests whether or not a string matches against a pattern.
   * 
   * @param pattern the pattern to match against (never {@code null})
   * @param str the String which must be matched against the pattern (never 
   * {@code null})
   * 
   * @return {@code true} if the string matches against the pattern, or 
   * {@code false} otherwise
   */
  private boolean matchStrings( final String pattern, final String str, final Map<String, String> uriTemplateVariables ) {
    return getStringMatcher( pattern ).matchStrings( str, uriTemplateVariables );
  }




  /**
   * Specify whether to cache parsed pattern metadata for patterns passed
   * into this matcher's {@link #match} method. 
   * 
   * <p>A value of {@code true} activates an unlimited pattern cache; a value 
   * of {@code false} turns the pattern cache off completely.</p>
   * 
   * <p>Default is for the cache to be on, but with the variant to automatically
   * turn it off when encountering too many patterns to cache at runtime
   * (the threshold is 65536), assuming that arbitrary permutations of patterns
   * are coming in, with little chance for encountering a recurring pattern.</p>
   * @see #getStringMatcher(String)
   */
  public void setCachePatterns( final boolean cachePatterns ) {
    this.cachePatterns = cachePatterns;
  }




  /**
   * Set the path separator to use for pattern parsing.
   * 
   * Default is "/", as in Ant.
   */
  public void setPathSeparator( final String pathSeparator ) {
    this.pathSeparator = ( pathSeparator != null ? pathSeparator : DEFAULT_PATH_SEPARATOR );
  }




  /**
   * Specify whether to trim tokenized paths and patterns.
   * 
   * Default is {@code true}.
   */
  public void setTrimTokens( final boolean trimTokens ) {
    this.trimTokens = trimTokens;
  }




  /**
   * Concatenate the two paths while attempting to avoid duplicate path delimiters.
   * 
   * @param path1
   * @param path2
   * 
   * @return The concatenated path
   */
  private String slashConcat( final String path1, final String path2 ) {
    if ( path1.endsWith( "/" ) || path2.startsWith( "/" ) ) {
      return path1 + path2;
    }
    return path1 + "/" + path2;
  }




  /**
   * Tokenize the given path String into parts, based on this matcher's settings.
   * 
   * @param path the path to tokenize
   * 
   * @return the tokenized path parts
   */
  protected String[] tokenizePath( final String path ) {
    return StringUtil.tokenizeToStringArray( path, pathSeparator, trimTokens, true );
  }




  /**
   * Tokenize the given path pattern into parts, based on this matcher's 
   * settings.
   * 
   * <p>Performs caching based on {@link #setCachePatterns}, delegating to
   * {@link #tokenizePath(String)} for the actual tokenization algorithm.</p>
   * 
   * @param pattern the pattern to tokenize
   * 
   * @return the tokenized pattern parts
   */
  protected String[] tokenizePattern( final String pattern ) {
    String[] tokenized = null;
    final Boolean cachePatterns = this.cachePatterns;
    if ( ( cachePatterns == null ) || cachePatterns.booleanValue() ) {
      tokenized = tokenizedPatternCache.get( pattern );
    }
    if ( tokenized == null ) {
      tokenized = tokenizePath( pattern );
      if ( ( cachePatterns == null ) && ( tokenizedPatternCache.size() >= CACHE_DEACTIVATION_THRESHOLD ) ) {
        // Since we have reached our threshold, turn of caching because this 
        // many patterns suggest dynamic operation, not a simple configuration 
        // scenario
        deactivatePatternCache();
        return tokenized;
      }
      if ( ( cachePatterns == null ) || cachePatterns.booleanValue() ) {
        tokenizedPatternCache.put( pattern, tokenized );
      }
    }
    return tokenized;
  }

}
