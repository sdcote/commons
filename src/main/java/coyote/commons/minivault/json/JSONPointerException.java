package coyote.commons.minivault.json;

/**
 * The JSONPointerException is thrown by {@link JSONPointer} if an error occurs
 * during evaluating a pointer.
 */
public class JSONPointerException extends JSONException {
  private static final long serialVersionUID = -7557498469534532973L;

  public JSONPointerException(String message) {
    super(message);
  }

  public JSONPointerException(String message, Throwable cause) {
    super(message, cause);
  }

}
