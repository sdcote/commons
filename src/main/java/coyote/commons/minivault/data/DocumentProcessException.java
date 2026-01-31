package coyote.commons.minivault.data;

/**
 * Exception if the processing of XML document fails.
 */
public class DocumentProcessException extends Exception {
  private static final long serialVersionUID = -7191451026697848490L;

  public DocumentProcessException(String message) {
    super("Cannot process document due to the following exception:\n" + message);
  }
}
