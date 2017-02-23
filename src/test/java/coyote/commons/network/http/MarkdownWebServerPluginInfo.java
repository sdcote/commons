package coyote.commons.network.http;

/**
 * 
 */
public class MarkdownWebServerPluginInfo implements WebServerPluginInfo {

  @Override
  public String[] getIndexFilesForMimeType( String mime ) {
    return new String[] { "index.md" };
  }




  @Override
  public String[] getMimeTypes() {
    return new String[] { "text/markdown" };
  }




  @Override
  public WebServerPlugin getWebServerPlugin( String mimeType ) {
    return new MarkdownWebServerPlugin();
  }
}
