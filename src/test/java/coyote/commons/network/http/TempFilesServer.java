package coyote.commons.network.http;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 */
public class TempFilesServer extends DebugServer {

  private static class ExampleManager implements TempFileManager {

    private final File tmpdir;

    private final List<TempFile> tempFiles;




    private ExampleManager() {
      tmpdir = new File( System.getProperty( "java.io.tmpdir" ) );
      tempFiles = new ArrayList<TempFile>();
    }




    @Override
    public void clear() {
      if ( !tempFiles.isEmpty() ) {
        System.out.println( "Cleaning up:" );
      }
      for ( final TempFile file : tempFiles ) {
        try {
          System.out.println( "   " + file.getName() );
          file.delete();
        } catch ( final Exception ignored ) {}
      }
      tempFiles.clear();
    }




    @Override
    public TempFile createTempFile( final String filename_hint ) throws Exception {
      final DefaultTempFile tempFile = new DefaultTempFile( tmpdir );
      tempFiles.add( tempFile );
      System.out.println( "Created tempFile: " + tempFile.getName() );
      return tempFile;
    }
  }

  private static class ExampleManagerFactory implements TempFileManagerFactory {

    @Override
    public TempFileManager create() {
      return new ExampleManager();
    }
  }




  public static void main( final String[] args ) {
    final TempFilesServer server = new TempFilesServer();
    server.setTempFileManagerFactory( new ExampleManagerFactory() );
    ServerRunner.executeInstance( server );
  }
}
