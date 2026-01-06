package coyote.commons;

//import static org.junit.Assert.*;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Class FileUtilTest
 */
public class FileUtilTest {
  private static final String S = File.separator;




  /**
   * Method testFileToStringOne
   */
  @Test
  public void testFileToStringOne() {
    assertTrue(  FileUtil.stringToFile( "This is test named testFileToStringOne", "test.txt" ),"testFileToStringOne" );

    if ( FileUtil.fileToString( "test.txt" ) == null ) {
      fail( "File not found" );
    }

    try {
      FileUtil.deleteFile( "test.txt" );
    } catch ( Exception ex ) {}
  }




  /**
   * Method testFileToStringTwo
   */
  @Test
  public void testFileToStringTwo() {
    assertEquals( "", FileUtil.fileToString( "Does-Not-Exist.txt" ), "Nothing should have been read." ) ;
  }




  /**
   * Method testStringToFileOne
   */
  @Test
  public void testStringToFileOne() {
    String filename = "StringToFile.txt";
    try {
      assertTrue(  FileUtil.stringToFile( "This is a test", filename ),"StringToFile" );
    }
    finally {
      new File( filename ).delete();
    }
  }




  /**
   * Method testGetAllFiles
   */
  @Test
  public void testGetAllFiles() {

    // Should return no files in the current directory with .JAVA extension
    try {
      List<File> list = FileUtil.getAllFiles( ".", "java", false );
      assertTrue( list.size() == 0 );
    } catch ( Exception e ) {
      fail( "getAllFiles: " + e.getMessage() );
    }

    // Should return all the files in the current directory
    try {
      List<File> list = FileUtil.getAllFiles( ".", null, false );
      assertTrue( list.size() > 0 );
    } catch ( Exception e ) {
      fail( "getAllFiles: " + e.getMessage() );
    }

    // Should return many files in the current directory with .JAVA extension as recurse is set to true
    try {
      List<File> list = FileUtil.getAllFiles( ".", "java", true );
      assertTrue( list.size() > 0 );
      //for( File file : list ) System.out.println( ">" + file.getAbsolutePath() );
    } catch ( Exception e ) {
      fail( "getAllFiles: " + e.getMessage() );
    }

  }




  /**
   * Method testTouch
   */
  @Test
  public void testTouch() {
    try {
      String filename = "C:\\WINNT\\Profiles\\cotes.000\\pub\\data\\comments.txt";
      File subject = new File( filename );
      FileUtil.touch( subject );
    } catch ( Exception e ) {
      fail( "testTouch: " + e.getMessage() );
    }
  }




  /**
   * Method testFinals
   */
  @Test
  public void testFinals() {
    System.out.println( "Home: " + FileUtil.HOME );
    assertTrue( FileUtil.HOME.equalsIgnoreCase( System.getProperty( "user.home" ) ) );
    System.out.println( "Home Directory: " + FileUtil.HOME_DIR );
    System.out.println( "Home URI: " + FileUtil.HOME_DIR_URI );

    System.out.println( "Current: " + FileUtil.CURRENT );
    assertTrue( FileUtil.CURRENT.equalsIgnoreCase( System.getProperty( "user.dir" ) ) );
    System.out.println( "Current Directory: " + FileUtil.CURRENT_DIR );
    System.out.println( "Current URI: " + FileUtil.CURRENT_DIR_URI );
  }




  /**
   * Method testGetBase
   */
  @Test
  public void testGetBase() {
    System.out.println( "GetBase directory: " + FileUtil.CURRENT );
    System.out.println( "GetBase   results: " + FileUtil.getBase( FileUtil.CURRENT ) );
  }




  /**
   * Method testGetPath1
   */
  @Test
  public void testGetPath1() {
    String filename = FileUtil.CURRENT + FileUtil.FILE_SEPARATOR + "README.TXT";
    assertTrue( FileUtil.getPath( filename ).equals( FileUtil.CURRENT + FileUtil.FILE_SEPARATOR ) );
  }




  /**
   * Method testGetPath2
   */
  @Test
  public void testGetPath2() {
    String filename = "/export/home/sdcote/find.txt";
    String path = FileUtil.FILE_SEPARATOR + "export" + FileUtil.FILE_SEPARATOR + "home" + FileUtil.FILE_SEPARATOR + "sdcote" + FileUtil.FILE_SEPARATOR;
    assertTrue(  FileUtil.getPath( filename ).equals( path ) );
  }




  /**
   * Method testGetPath3
   */
  @Test
  public void testGetPath3() {
    String filename = "export/home/sdcote/find.txt";
    String path = "export" + FileUtil.FILE_SEPARATOR + "home" + FileUtil.FILE_SEPARATOR + "sdcote" + FileUtil.FILE_SEPARATOR;
    assertTrue( FileUtil.getPath( filename ).equals( path ) );
  }




  /**
   * Method testGetPath4
   */
  @Test
  public void testGetPath4() {
    String filename = "clean//extra//delimiters////from//this/path.txt";
    String path = "clean" + FileUtil.FILE_SEPARATOR + "extra" + FileUtil.FILE_SEPARATOR + "delimiters" + FileUtil.FILE_SEPARATOR + "from" + FileUtil.FILE_SEPARATOR + "this" + FileUtil.FILE_SEPARATOR;
    assertTrue( FileUtil.getPath( filename ).equals( path ) );
  }




  /**
   * Method testGetPath5
   */
  @Test
  public void testGetPath5() {
    String filename = "clean//mixed\\delimiters//\\//\\from\\this/path.txt";
    String path = "clean" + FileUtil.FILE_SEPARATOR + "mixed" + FileUtil.FILE_SEPARATOR + "delimiters" + FileUtil.FILE_SEPARATOR + "from" + FileUtil.FILE_SEPARATOR + "this" + FileUtil.FILE_SEPARATOR;
    assertTrue( FileUtil.getPath( filename ).equals( path ) );
  }




  /**
   * Method testGetFilename
   */
  @Test
  public void testGetFilename() {
    String filename = "clean//mixed\\delimiters//\\//\\from\\this/path.txt";
    String file = "path.txt";
    assertTrue( FileUtil.getFile( filename ).equals( file ) );
  }






  /**
   * Method testGetJavaFile1
   */
  @Test
  public void testGetJavaFile1() {
    String classname = "coyote.commons.util.FileUtil.class";
    assertTrue(  FileUtil.getJavaFile( classname ).equals( "FileUtil.class" ) );
  }




  /**
   * Method testGetJavaFile2
   */
  @Test
  public void testGetJavaFile2() {
    String classname = "coyote.commons.util.FileUtil";
    assertTrue(  FileUtil.getJavaFile( classname ).equals( "FileUtil.class" ) );
  }




  /**
   * Method testRemoveRelations1
   */
  @Test
  public void testRemoveRelations1() {
    String path = "/export/home/sdcote/projects/BusStress/bin/../cfg/busconnector.xml";
    String expected = S + "export" + S + "home" + S + "sdcote" + S + "projects" + S + "BusStress" + S + "cfg" + S + "busconnector.xml";
    String normal = FileUtil.removeRelations( path );

    assertTrue( normal.equals( expected ) );
  }




  /**
   * Method testRemoveRelations2
   */
  @Test
  public void testRemoveRelations2() {
    String path = "C:\\sdcote\\eclipse\\workspace\\BusStress\\bin\\..";
    String expected = "C:" + S + "sdcote" + S + "eclipse" + S + "workspace" + S + "BusStress";
    String normal = FileUtil.removeRelations( path );
    System.out.println( "PATH1=" + path );
    System.out.println( "PATH2=" + normal );

    assertTrue( normal.equals( expected ) );
  }




  /**
   * Method testRemoveRelations3
   */
  @Test
  public void testRemoveRelations3() {
    String path = "C:\\sdcote\\eclipse\\workspace\\BusStress\\..\\..";
    String expected = "C:" + S + "sdcote" + S + "eclipse";
    String normal = FileUtil.removeRelations( path );

    assertTrue( normal.equals( expected ) );
  }




  /**
   * Method testRemoveRelations4
   */
  @Test
  public void testRemoveRelations4() {
    String path = "C:\\sdcote\\eclipse\\.\\workspace\\BusStress\\..\\..";
    String expected = "C:" + S + "sdcote" + S + "eclipse";
    String normal = FileUtil.removeRelations( path );

    assertTrue( normal.equals( expected ) );
  }




  /**
   * Make sure we preserve the trailing separator.
   * <p>
   * We only remove the relations, not any extra file separators that the user
   * may want or expect. Makse sure the trailing separator remains.
   * 
   */
  @Test
  public void testRemoveRelations5() {
    String path = "C:\\sdcote\\eclipse\\.\\workspace\\BusStress\\..\\..\\";
    String expected = "C:" + S + "sdcote" + S + "eclipse" + S;
    String normal = FileUtil.removeRelations( path );

    // System.out.println("PATH1="+path);
    // System.out.println("PATH2="+normal);
    assertTrue( normal.equals( expected ) );
  }




  /**
   * Method testRemoveRelations6
   */
  @Test
  public void testRemoveRelations6() {
    String path = "/../../../../..";
    String expected = S;
    String normal = FileUtil.removeRelations( path );

    System.out.println( "PATH1=" + path );
    System.out.println( "PATH2=" + normal );
    assertTrue( normal.equals( expected ) );
  }

}