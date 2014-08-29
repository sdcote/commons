/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * 
 */
public class ClasspathUtil {
  private static volatile boolean checkedClasspathAlready = false;

  private static HashMap<String, String> shadowClasses = new HashMap<String, String>();
  private static ArrayList<String> missingLibraries = new ArrayList<String>();




  /**
   * Simple classpath checker.
   * 
   * <p>Go through each entry in the classpath and verify that it is actually 
   * on the file system, can be read, and if it is a JAR or ZIP, verify that 
   * the entries can be read.</p>
   * 
   * <p>This method will also generate a hashmap of classes that appear in more 
   * than one path entry. These shadow classes should not be the cause of 
   * errors, but some class loaders can get confused when shadow classes exist. 
   * Also, is the wrong version of the class is loaded because it appears first 
   * on the classpath, the system many not operate as expected. So it is a good 
   * idea to eliminate them if possible.<p>
   * 
   * <p>This class will also generate a list of missing path entries. These are 
   * classpath entries which do not exist on the file system. An example of 
   * this if if a JAR was removed from the disk and now the system is throwing 
   * "class not found" exceptions. Sometimes class paths get too long for the 
   * OS environment variables (Windows) and older systems have empty classpath 
   * entries which are no longer needed. This feature can be used to identify 
   * empty. useless entries so they can be removed providing more room for new 
   * entries.</p>
   * 
   * @see {@link #getShadowedClasses}
   * @see {@link #getShadowClassDetails}
   * @see {@link #getMissingClasspathEntries}
   * 
   * @return True if the classpath is clean, false if there are invalid entries.
   */
  public static boolean verifyClasspath() {
    checkedClasspathAlready = true;

    HashMap<String, String> classMap = new HashMap<String, String>();
    HashMap<String, String> shadows = new HashMap<String, String>();
    ArrayList<String> missing = new ArrayList<String>();
    
    @SuppressWarnings("unused")
    int bytesRead;

    boolean retval = true;

    StringTokenizer st = new StringTokenizer( System.getProperty( "java.class.path" ), System.getProperty( "path.separator" ) );
    while ( st.hasMoreTokens() ) {
      String entry = st.nextToken();
      File file = new File( entry );
      if ( file.exists() ) {
        if ( file.canRead() ) {
          if ( entry.endsWith( "jar" ) ) {
            JarFile jarfile = null;
            try {
              jarfile = new JarFile( file );
              // System.out.println( "checking '" + entry + "'" );
              for ( Enumeration<JarEntry> en = jarfile.entries(); en.hasMoreElements(); ) {
                JarEntry jentry = (JarEntry)en.nextElement();
                // System.out.println( "    '" + jentry.getName() + "' " + jentry.getCrc() );
                if ( jentry.getName().endsWith( ".class" ) && classMap.containsKey( jentry.getName() ) ) {
                  shadows.put( jentry.getName() + " found in '" + classMap.get( jentry.getName() ) + "'; shadowed in '" + entry + "'", jentry.getName() );
                } else {
                  classMap.put( jentry.getName(), entry );
                }

                try {
                  InputStream entryStream = jarfile.getInputStream( jentry );
                  byte[] buffer = new byte[1024];
                  while ( ( bytesRead = entryStream.read( buffer ) ) != -1 );
                } catch ( Exception ex ) {
                  Log.error( "Classpath entry '" + entry + "' is not a valid archive, problems accessing '" + jentry.getName() + "' - " + ex.getMessage() );
                  retval = false;
                  break;
                }
              }
            } catch ( IOException e ) {
              Log.error( "Classpath entry '" + entry + "' is not a valid java archive: " + e.getMessage() );
              retval = false;
            }
          } else if ( entry.endsWith( "zip" ) ) {
            ZipFile zipfile = null;
            try {
              zipfile = new ZipFile( file );
              for ( Enumeration<? extends ZipEntry> en = zipfile.entries(); en.hasMoreElements(); ) {
                ZipEntry zentry = (ZipEntry)en.nextElement();
                // System.out.println( "    '" + zentry.getName() + "' " + zentry.getCrc() );
                if ( zentry.getName().endsWith( ".class" ) && classMap.containsKey( zentry.getName() ) ) {
                  shadows.put( zentry.getName() + " found in '" + classMap.get( zentry.getName() ) + "'; shadowed in '" + entry + "'", zentry.getName() );
                } else {
                  classMap.put( zentry.getName(), entry );
                }

                try {
                  InputStream entryStream = zipfile.getInputStream( zentry );
                  byte[] buffer = new byte[1024];
                  while ( ( bytesRead = entryStream.read( buffer ) ) != -1 );
                } catch ( Exception ex ) {
                  Log.error( "Classpath entry '" + entry + "' is not a valid archive, problems accessing '" + zentry.getName() + "' - " + ex.getMessage() );
                  retval = false;
                  break;
                }
              }
            } catch ( IOException e ) {
              Log.error( "Classpath entry '" + entry + "' is not a valid zip archive: " + e.getMessage() );
              retval = false;
            }
          }
        } else {
          Log.error( "Classpath entry '" + entry + "' is not readable" );
          retval = false;
        }
      } else {
        Log.error( "Classpath entry '" + entry + "' does not appear to exist on file system" );
        missing.add( entry );
        retval = false;
      }

    } // while more path entries

    ClasspathUtil.shadowClasses = shadows;
    ClasspathUtil.missingLibraries = missing;

    return retval;
  }




  /**
   * Get a listing of libraries missing from the class path.
   * 
   * <p>This is a listing of all the files or directories specified on the 
   * classpath but could not be found or read from the file system.</p>
   *  
   * @return An array of class path entries that do not exist on the file 
   *         system.
   */
  public static String[] getMissingClasspathEntries() {
    if ( !checkedClasspathAlready )
      ClasspathUtil.verifyClasspath();

    String[] retval = new String[missingLibraries.size()];
    for ( int x = 0; x < retval.length; x++ ) {
      retval[x] = missingLibraries.get( x );
    }
    return retval;
  }




  /**
   * Get a listing of class names that appear in more than one entry in the 
   * class path.
   * 
   * <p>This not necessarily an error, but it can allow an application to 
   * determine why a version of a class is not loading as expected. This may be 
   * the case when an older version of the class appears first on the class 
   * path.</p>
   * 
   * @return an array of fully qualified class names that appear more than one 
   *         in the class path.
   */
  public static String[] getShadowedClasses() {
    if ( !checkedClasspathAlready )
      ClasspathUtil.verifyClasspath();

    HashSet<String> set = new HashSet<String>();
    for ( Iterator<String> it = shadowClasses.values().iterator(); it.hasNext(); ) {
      set.add( it.next() );
    }

    String[] retval = new String[set.size()];
    int x = 0;
    for ( Iterator<String> it = set.iterator(); it.hasNext(); retval[x++] = it.next() );

    return retval;
  }




  /**
   * Access a detailed line of text describing what classes were found in which 
   * class path entries.
   * 
   * <p>This not necessarily an error, but it can allow an application to 
   * determine why a version of a class is not loading as expected. This may be 
   * the case when an older version of the class appears first on the class 
   * path.</p>
   * 
   * The format is: <pre>
   * <tt>&lt;class&gt;</tt> found in <tt>&lsquo;&lt;initial&gt;</tt>&rsquo;;shadowed in &lsquo;<tt>&lt;secondary&gt;</tt>&rsquo;<br></pre></p>
   * 
   * <p>Where: <tt>&lt;class&gt;</tt> is the fully-qualified class name that 
   * appears more than once in the class path, <tt>&lt;initial&gt;</tt> is the 
   * class path entry where the class was first located and from where it will 
   * probably be found by the class loader, and <tt>&lt;secondary&gt;</tt> is 
   * the class path entry when a copy of the class was found.</p>
   * 
   * <p>This will show each class that occurs more than once in the class path 
   * listing; both the first class path entry from which the class will 
   * probably be loaded and the class path entry contains a shadow copy of the 
   * entry.</p>
   * 
   * @return an array of shadowed class entries.
   */
  public static String[] getShadowClassDetails() {
    if ( !checkedClasspathAlready )
      ClasspathUtil.verifyClasspath();

    ArrayList<String> list = new ArrayList<String>();
    for ( Iterator<String> it = shadowClasses.keySet().iterator(); it.hasNext(); list.add( new String( it.next() ) ) );

    String[] retval = new String[list.size()];
    for ( int x = 0; x < retval.length; retval[x] = list.get( x++ ) );

    return retval;
  }




  /**
   * @param args
   */
  public static void main( String[] args ) {
    if ( ClasspathUtil.verifyClasspath() ) {
      Log.info( "Classpath checks out O.K." );
    } else {
      Log.warn( "Classpath has some problems. Check the logs for details." );

      // Get a listing of classes that appear more than once in the class path
      String[] shadowedClasses = ClasspathUtil.getShadowedClasses();

      // print them out
      if ( shadowedClasses.length > 0 ) {
        StringBuffer buffer = new StringBuffer( "The following classes appear more than once in the class path:\n" );
        for ( int x = 0; x < shadowedClasses.length; buffer.append( shadowedClasses[x++] + "\n" ) );
        Log.warn( buffer );
      } else {
        Log.info( "No shadowed classes were found" );
      }

      // Get a listing of classes that appear more than once in the class path
      String[] shadowedDetails = ClasspathUtil.getShadowClassDetails();

      // print them out
      if ( shadowedClasses.length > 0 ) {
        StringBuffer buffer = new StringBuffer( "Details of shadowed classes:\n" );
        for ( int x = 0; x < shadowedClasses.length; buffer.append( shadowedDetails[x++] + "\n" ) );
        Log.warn( buffer );
      } else {
        Log.info( "No shadowed classes were found" );
      }
    }
  }

}
