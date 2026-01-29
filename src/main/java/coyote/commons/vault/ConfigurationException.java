package coyote.commons.vault;

public class ConfigurationException extends Exception {
  private static final long serialVersionUID = -4400938310573761017L;

  /**
   * Constructs a ConfigurationException with no detail message. A detail message is a String that describes this particular
   * exception.
   */
  public ConfigurationException() {
    super();
  }

  /**
   * Constructs a ConfigurationException with the specified detail message. A detail message is a String that describes this
   * particular exception.
   *
   * @param s the detail message.
   */
  public ConfigurationException(String s) {
    super(s);
  }

  /**
   * Creates a ConfigurationException with the specified detail message and cause.
   *
   * @param message the detail message (which is saved for later retrieval by the getMessage method).
   * @param cause the cause (which is saved for later retrieval by the getCause method). Null is permitted, and
   *              indicates that the cause is nonexistent or unknown.)
   */
  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a ConfigurationException with the specified cause and a detail message of (cause==null ? null : cause.toString())
   * (which typically contains the class and detail message of cause).
   *
   * @param cause the cause (which is saved for later retrieval by the getCause() method). Null is permitted, and
   *              indicates that the cause is nonexistent or unknown.)
   */
  public ConfigurationException(Throwable cause) {
    super(cause);
  }
}
