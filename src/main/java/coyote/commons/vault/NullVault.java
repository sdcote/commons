package coyote.commons.vault;

/**
 * This is a no-op implementation of a vault.
 *
 * <p>It is useful for testing and development. It is also useful when a vault
 * is not found or can't otherwise be opened and returning a null reference is
 * not desired. (Who wants nulls?)</p>
 */
public class NullVault implements Vault {
    /**
     * @return true if the vault can only retrieve secrets, false if it can also store them
     */
    @Override
    public boolean isReadOnly() {
        return true;
    }

    /**
     * Save the current state of the vault
     */
    @Override
    public void save() {    }

    /**
     * Close the vault
     */
    @Override
    public void close() {    }

    /**
     * Open / initialize the vault.
     *
     * @throws VaultException if the vault provider could not be opened.
     */
    @Override
    public void open() throws VaultException {    }

    /**
     * Get a VaultEntry with the given identifier.
     *
     * <p>Vaults store many types of information. A {@code VaultEntry} holds that variable data structure as a Map. Each
     * structure can be different depending on the {@code Provider}, but each provider should have a way to retrieve that
     * data structure based on some key. This key may be a simple string, a path, or some expression which allows the
     * secrets to be retrieved. This method retrieves a unique set of secrets from the vault provider.</p>
     *
     * @param key the identifier for the entry in the vault
     * @return An entry with the identifier or null if no entry with that identifier is found.
     */
    @Override
    public VaultEntry getEntry(String key) {
        return null;
    }
}
