package coyote.commons.minivault;

import coyote.commons.minivault.data.DocumentHelper;
import coyote.commons.minivault.data.DocumentProcessException;
import coyote.commons.minivault.util.CryptUtils;
import coyote.commons.vault.ConfigurationException;
import coyote.commons.vault.Vault;
import coyote.commons.vault.VaultEntry;
import coyote.commons.vault.VaultException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * MiniVaultVault is an JVault {@code Vault} implementation of a MiniVault secrets store.
 *
 * <p>This is the main API into the MiniVault secrets store.</p>
 */
public class MiniVaultVault implements Vault {
    Entries entries = null;
    String filename = null;
    byte[] passwordHash = null;


    @Override
    public boolean isReadOnly() {
        return false;
    }


    @Override
    public void save() {

    }


    @Override
    public void close() {

    }


    @Override
    public void open() throws VaultException {
        try {
            entries = DocumentHelper.newInstance(filename, passwordHash).readJsonDocument();
        } catch (IOException e) {
            throw new VaultException("Problems reading file", e);
        } catch (DocumentProcessException e) {
            throw new VaultException("Problems decrypting file", e);
        }
    }


    @Override
    public VaultEntry getEntry(String key) {
        VaultEntry retval = null;
        if (key != null) {
            if (entries != null) {
                for (Entry entry : entries.getEntry()) {
                    if (key.equals(entry.getName())) {
                        retval = new VaultEntry();
                        retval.set(MiniVault.NAME_TAG, entry.getName());
                        retval.set(MiniVault.USER_TAG, entry.getUsername());
                        retval.set(MiniVault.URL_TAG, entry.getUrl());
                        retval.set(MiniVault.NOTES_TAG, entry.getNotes());
                        retval.set(MiniVault.PUBLIC_KEY_TAG, entry.getPublickey());
                        retval.set(MiniVault.PRIVATE_KEY_TAG, entry.getPrivatekey());
                        retval.set(MiniVault.PASSPHRASE_TAG, entry.getPassphrase());
                        retval.set(MiniVault.PASSWORD_TAG, entry.getPassword());
                        retval.set(MiniVault.EMAIL_TAG, entry.getEmail());
                        retval.set(MiniVault.TOKEN_TAG, entry.getToken());
                    }
                }
            }
        }
        return retval;
    }


    /**
     * This is a way to get all the entry keys (i.e. names) in the vault.
     *
     * @return a list of keys in this vault. It may be empty, but never null.
     */
    public List<String> getKeys() {
        List<String> retval = new ArrayList<>();
        if (entries != null) {
            for (Entry entry : entries.getEntry()) {
              if( entry.getName()!=null){
                retval.add(entry.getName());
              }
            }
        }
        return retval;
    }


    /**
     * @param value
     * @return an instance of this vault for method chaining.
     * @throws ConfigurationException if the file could not be found or is not readable
     */
    public MiniVaultVault setFilename(String value) throws ConfigurationException {
        File file = new File(value);
        if (!file.exists()) {
            throw new ConfigurationException("Vault file does not exist: '" + value + "' (" + file.getAbsolutePath() + ")");
        }
        if (!file.canRead()) {
            throw new ConfigurationException("Cannot read vault file: '" + value + "' (" + file.getAbsolutePath() + ")");
        }
        filename = file.getAbsolutePath();
        return this;
    }


    /**
     * Set the password hash for this vault.
     *
     * @param value the array of characters to hash in creating the key for the encrypted vault file
     * @return an instance of this vault for method chaining.
     * @throws ConfigurationException if the password is not a valid string of characters (e.g., null)
     */
    public MiniVaultVault setPassword(String value) throws ConfigurationException {
        try {
            passwordHash = CryptUtils.getPKCS5Sha256Hash(value.toCharArray());
        } catch (Exception e) {
            throw new ConfigurationException("Invalid password", e);
        }
        return this;
    }

}
