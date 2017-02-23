package coyote.commons.network.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This is an example of creating a plugin that handles MarkDown content using
 * an open source processor (PEGdown - https://github.com/sirthias/pegdown)
 */
public class MarkdownWebServerPlugin implements WebServerPlugin {

  private static final Logger LOG = Logger.getLogger( MarkdownWebServerPlugin.class.getName() );

  // THe processoe which will handle parsing markworn into HTML
  // private final PegDownProcessor processor;




  public MarkdownWebServerPlugin() {
    // Create a new instance of the processor
    // processor = new PegDownProcessor();
  }




  @Override
  public boolean canServeUri( String uri, File rootDir ) {
    File f = new File( rootDir, uri );
    return f.exists();
  }




  @Override
  public void initialize( Map<String, String> commandLineOptions ) {}




  private String readSource( File file ) {
    FileReader fileReader = null;
    BufferedReader reader = null;
    try {
      fileReader = new FileReader( file );
      reader = new BufferedReader( fileReader );
      String line = null;
      StringBuilder sb = new StringBuilder();
      do {
        line = reader.readLine();
        if ( line != null ) {
          sb.append( line ).append( "\n" );
        }
      }
      while ( line != null );
      reader.close();
      return sb.toString();
    } catch ( Exception e ) {
      MarkdownWebServerPlugin.LOG.log( Level.SEVERE, "could not read source", e );
      return null;
    }
    finally {
      try {
        if ( fileReader != null ) {
          fileReader.close();
        }
        if ( reader != null ) {
          reader.close();
        }
      } catch ( IOException ignored ) {
        MarkdownWebServerPlugin.LOG.log( Level.FINEST, "close failed", ignored );
      }
    }
  }




  /**
   * 
   * @see coyote.commons.network.http.WebServerPlugin#serveFile(java.lang.String, java.util.Map, coyote.commons.network.http.IHTTPSession, java.io.File, java.lang.String)
   */
  @Override
  public Response serveFile( String uri, Map<String, String> headers, IHTTPSession session, File file, String mimeType ) {
    String markdownSource = readSource( file );
    byte[] bytes;
    try {
      // bytes = processor.markdownToHtml( markdownSource ).getBytes( "UTF-8" );
      bytes = "This is where the processed markdown would go".getBytes( "UTF-8" );
    } catch ( UnsupportedEncodingException e ) {
      MarkdownWebServerPlugin.LOG.log( Level.SEVERE, "encoding problem, responding nothing", e );
      bytes = new byte[0];
    }
    return markdownSource == null ? null : new Response( Status.OK, HTTPD.MIME_HTML, new ByteArrayInputStream( bytes ), bytes.length );
  }
}
