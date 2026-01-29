package coyote.commons.minivault.crypt;

/**
 * Exception, if the decryption fails. {@link Cbc} throws this exception, if the last block is not a legal conclusion
 * of a decryption stream.
 */
public final class DecryptException extends Exception {

    private static final long serialVersionUID = 8374524125891530363L;

    /**
     * Creates the exception.
     */
    public DecryptException() {
        super("Decryption failed.");
    }
}
