package coyote.commons.rtw;

/**
 * Exception thrown when there are problems with the configuration of the Transform Engine.
 */
public class RTWConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RTWConfigurationException(String message) {
        super(message);
    }

    public RTWConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
