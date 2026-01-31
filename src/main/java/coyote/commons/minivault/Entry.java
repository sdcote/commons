package coyote.commons.minivault;

/**
 * A secrets entry.
 */
public class Entry {
    protected String name;
    protected String url;
    protected String email;
    protected String token;
    protected String user;
    protected String password;
    protected String notes;
    protected String publicKey;
    protected String privateKey;
    protected String passPhrase;

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String}
     */
    public Entry setName(String value) {
        this.name = value;
        return this;
    }

    /**
     * Gets the value of the url property.
     *
     * @return possible object is {@link String}
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     *
     * @param value allowed object is {@link String}
     */
    public Entry setUrl(String value) {
        this.url = value;
        return this;
    }

    /**
     * Gets the value of the user property.
     *
     * @return possible object is {@link String}
     */
    public String getUsername() {
        return user;
    }

    /**
     * Sets the value of the user property.
     *
     * @param value allowed object is {@link String}
     */
    public Entry setUsername(String value) {
        this.user = value;
        return this;
    }

    /**
     * Gets the value of the password property.
     *
     * @return possible object is {@link String}
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     *
     * @param value allowed object is {@link String}
     */
    public Entry setPassword(String value) {
        this.password = value;
        return this;
    }

    /**
     * Gets the value of the notes property.
     *
     * @return possible object is {@link String}
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     *
     * @param value allowed object is {@link String}
     */
    public Entry setNotes(String value) {
        this.notes = value;
        return this;
    }

    /**
     * Gets the value of the public key property.
     *
     * @return possible object is {@link String}
     */
    public String getPublickey() {
        return publicKey;
    }


    /**
     * Sets the value of the public key property.
     *
     * @param value allowed object is {@link String}
     */
    public Entry setPublickey(String value) {
        this.publicKey = value;
        return this;
    }

    /**
     * Gets the value of the private key property.
     *
     * @return possible object is {@link String}
     */
    public String getPrivatekey() {
        return privateKey;
    }


    /**
     * Sets the value of the private key property.
     *
     * @param value allowed object is {@link String}
     */
    public Entry setPrivatekey(String value) {
        this.privateKey = value;
        return this;
    }

    /**
     * Gets the value of the passphrase for the private key.
     *
     * @return possible object is {@link String}
     */
    public String getPassphrase() {
        return passPhrase;
    }


    /**
     * Sets the value of the passphrase for the private key.
     *
     * @param value allowed object is {@link String}
     */
    public Entry setPassphrase(String value) {
        this.passPhrase = value;
        return this;
    }

    /**
     * Gets the value of the email property.
     *
     * @return possible object is {@link String}
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     *
     * @param value allowed object is {@link String}
     */
    public Entry setEmail(String value) {
        this.email = value;
        return this;
    }

    /**
     * Gets the value of the API token property.
     *
     * @return possible object is {@link String}
     */
    public String getToken() {
        return token;
    }


    /**
     * Sets the value of the API token property.
     *
     * @param value allowed object is {@link String}
     */
    public Entry setToken(String value) {
        this.token = value;
        return this;
    }


}
