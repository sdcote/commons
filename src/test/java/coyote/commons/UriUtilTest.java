package coyote.commons;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;


/**
 * Class URITest
 */
public class UriUtilTest {

  String fs = System.getProperty( "file.separator" );




  /**
   * Method testGetScheme
   */
  @Test
  public void testGetScheme() {
    try {
      URI uri = new URI( "blp://linkage.bralyn.net/" );
      assertTrue( uri.getScheme().equalsIgnoreCase( "blp" ) );
    }
    catch( Exception ex ) {}
  }




  /**
   * Method testGetHost
   */
  @Test
  public void testGetHost() {
    try {
      URI uri = new URI( "blp://linkage.bralyn.net/" );
      assertTrue( uri.getHost().equalsIgnoreCase( "linkage.bralyn.net" ) );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetPort
   */
  @Test
  public void testGetPort() {
    try {
      URI uri = new URI( "blp://linkage.bralyn.net/" );
      assertTrue( uri.getPort() == -1 );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetPort2
   */
  @Test
  public void testGetPort2() {
    try {
      URI uri = new URI( "blp://linkage.bralyn.net:5529/root/home/stuff" );
      assertTrue( uri.getPort() == 5529 );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetPath
   */
  @Test
  public void testGetPath() {
    try {
      URI uri = new URI( "blp://linkage.bralyn.net:5529/root/home/stuff" );
      assertTrue( uri.getPath().equalsIgnoreCase( "/root/home/stuff" ) );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetReference
   */
  //@Test
  public void testGetReference() {
    try {
      URI uri = new URI( "blp://linkage.bralyn.net:5529/root/home/stuff#Junk" );
      //assertTrue( uri.getReference().equalsIgnoreCase( "Junk" ) );
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
  }




  /**
   * Method testGetQuery
   */
  //@Test
  public void testGetQuery() {
    try {
      URI uri = new URI( "blp://linkage.bralyn.net:5529/root/home/stuff#Junk?name=bob&job=coder" );
      //assertTrue( uri.getReference().equalsIgnoreCase( "Junk" ) );
      assertTrue( uri.getQuery().equalsIgnoreCase( "name=bob&job=coder" ) );
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
  }




  /**
   * Method testGetQueryParamenter
   */
  //@Test
  public void testGetQueryParamenter() {
    try {
      URI uri = new URI( "blp://linkage.bralyn.net:5529/root/home/stuff#MoreJunk?name=bob&job=coder" );
      assertTrue( uri.getPort() == 5529 );
      assertTrue( uri.getPath().equalsIgnoreCase( "/root/home/stuff" ) );
      //assertTrue( uri.getReference().equalsIgnoreCase( "MoreJunk" ) );
      //assertTrue( uri.getQueryParamenter( "name" ).equals( "bob" ) );
      //assertTrue( uri.getQueryParamenter( "job" ).equals( "coder" ) );
      //assertTrue( uri.getQueryParamenter( "age" ) == null );
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
  }




  /**
   * Method testEncodeString
   */
  @Test
  public void testEncodeString1() {
    try {
      String encoded = UriUtil.encodeString( "This has spaces, hyphens(-) and other punctuation." );
      assertTrue( encoded.equals( "This+has+spaces%2C+hyphens%28%2D%29+and+other+punctuation%2E" ) );
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
  }




  /**
   * Method testDecodeString
   */
  @Test
  public void testDecodeString() {
    String decoded = UriUtil.decodeString( "This+has+spaces%2C+hyphens%28%2D%29+and+other+punctuation%2E" );
    assertTrue( decoded.equals( "This has spaces, hyphens(-) and other punctuation." ) );
  }




  /**
   * Method testFileURIFour
   */
  @Test
  public void testFileURIFour() {
    try {
      URI uri = new URI( "file://config/node3.xml" );
      assertTrue( uri.getScheme().equals( "file" ) );
      assertTrue( "Not proper path", UriUtil.getFilePath( uri ).equals( "config" + fs + "node3.xml" ) );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testFileURIFive
   */
  @Test
  public void testFileURIFive() {
    try {
      URI uri = new URI( "file:///C:/Documents%20and%20Settings/CoteS/EXML-7.0b2.zip" );
      assertTrue( uri.getScheme().equals( "file" ) );
      assertTrue( "Not proper path", UriUtil.getFilePath( uri ).equals( "C:" + fs + "Documents and Settings" + fs + "CoteS" + fs + "EXML-7.0b2.zip" ) );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testFileToURI
   */
  @Test
  public void testFileToURI() {
    try {
      File homedir = new File( System.getProperty( "user.home" ) );
      System.err.println( "File to URI = " + FileUtil.getFileURI( homedir ) );
      System.err.flush();
      //System.err.println( "File to URI path = " + FileUtil.getFileURI( homedir ).getFilePath() );
      System.err.flush();

      URI home = new URI( FileUtil.getFileURI( homedir ).toString() );
    }
    catch( Exception e ) {
      e.printStackTrace();
    }
  }




  /**
   * Method testURIToFile1
   */
  @Test
  public void testURIToFile1() {
    try {
      File homedir = new File( System.getProperty( "user.home" ) );
      URI homeuri = FileUtil.getFileURI( homedir );

      File file = UriUtil.getFile( homeuri );
      assertTrue( System.getProperty( "user.home" ).equals( file.getAbsolutePath() ) );
    }
    catch( Exception e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testURIToFile2
   */
  @Test
  public void testURIToFile2() {
    try {
      File homedir = new File( System.getProperty( "user.home" ) );
      String path = "log" + fs + "test.log";
      String expected = System.getProperty( "user.home" ) + fs + path;

      URI homeuri = FileUtil.getFileURI( homedir );
      File dir = new File( homedir, path );
      URI uri = FileUtil.getFileURI( dir );
      File file = UriUtil.getFile( uri );

      assertTrue( file.getAbsolutePath().equals( expected ) );
    }
    catch( Exception e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testURIToFile3
   */
  @Test
  public void testURIToFile3() {
    try {
      File homedir = new File( System.getProperty( "user.home" ) );
      String path = "log" + fs + "test.log";
      String expected = System.getProperty( "user.home" ) + fs + path;

      File dir = new File( homedir, path );
      URI uri = FileUtil.getFileURI( dir );

      File file = UriUtil.getFile( uri );

      assertTrue( file.getAbsolutePath().equals( expected ) );
    }
    catch( Exception e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetUser1
   */
  @Test
  public void testGetUser1() {
    try {
      URI uri = new URI( "blp://sdcote:pickles@linkage.bralyn.net:5529/root/home/stuff" );
      String expected = "sdcote";
      String user = UriUtil.getUser( uri );

      assertTrue( user.equals( expected ) );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetUser2
   */
  @Test
  public void testGetUser2() {
    try {
      URI uri = new URI( "blp://sdcote@linkage.bralyn.net:5529/root/home/stuff" );
      String expected = "sdcote";
      String user = UriUtil.getUser( uri );

      assertTrue( user.equals( expected ) );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetUser3
   */
  @Test
  public void testGetUser3() {
    try {
      URI uri = new URI( "blp://:pickles@linkage.bralyn.net:5529/root/home/stuff" );
      String user = UriUtil.getUser( uri );
      // System.out.println( "User='" + user + "'" );

      assertTrue( user == null );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetPassword1
   */
  @Test
  public void testGetPassword1() {
    try {
      URI uri = new URI( "blp://sdcote:pickles@linkage.bralyn.net:5529/root/home/stuff" );
      String expected = "pickles";
      String password = UriUtil.getPassword( uri );

      assertTrue( password.equals( expected ) );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetPassword2
   */
  @Test
  public void testGetPassword2() {
    try {
      URI uri = new URI( "blp://sdcote@linkage.bralyn.net:5529/root/home/stuff" );
      String password = UriUtil.getPassword( uri );

      assertTrue( password == null );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetHostAddress1
   */
  @Test
  public void testGetHostAddress1() {
    try {
      URI uri = new URI( "blp://sdcote@linkage.bralyn.net:5529/root/home/stuff" );
      InetAddress addr = UriUtil.getHostAddress( uri );
      assertTrue( addr != null );
      assertTrue( uri.getScheme().equals( "blp" ) );
      assertTrue( uri.getRawUserInfo().equals( "sdcote" ) );
      assertTrue( uri.getHost().equals( "linkage.bralyn.net" ) );
      assertTrue( uri.getPort() == 5529 );
      assertTrue( uri.getPath().equals( "/root/home/stuff" ) );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetFilePath1
   */
  @Test
  public void testGetFilePath1() {
    try {
      URI uri = new URI( "jar:file:/c:/almanac/my.jar!/com/mycompany/MyClass.class" );
      String path = UriUtil.getFilePath( uri );
      System.out.println( "Jar filepath 1 = '" + path + "'" );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetFilePath2
   */
  @Test
  public void testGetFilePath2() {
    try {
      URI uri = new URI( "jar:file:/c:/almanac/my.jar!/" );
      String path = UriUtil.getFilePath( uri );
      System.out.println( "Jar filepath 2 = '" + path + "'" );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetFilePath3
   */
  @Test
  public void testGetFilePath3() {
    try {
      URI uri = new URI( "jar:/c:/almanac/my.jar!/" );
      String path = UriUtil.getFilePath( uri );
      System.out.println( "Jar filepath 3 = '" + path + "'" );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Method testGetFilePath4
   */
  @Test
  public void testGetFilePath4() {
    try {
      URI uri = new URI( "jar://c:/almanac/my.jar" );
      String path = UriUtil.getFilePath( uri );
      System.out.println( "Jar filepath 4 = '" + path + "'" );
    }
    catch( URISyntaxException e ) {
      fail( e.getMessage() );
    }
  }
}