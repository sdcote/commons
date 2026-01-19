package coyote.commons.uml.marshal;

import java.text.SimpleDateFormat;

public abstract class AbstractMarshaler {
    protected static final String CRLF = "\r\n";
    protected static final String EMPTY_STRING = "";
    protected static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private MarshalerExtension extension = new NullExtension();

    private String name = "Coyote UML";
    private String version = "1.0.2";
    private String identifier = "DOC_5529";

    /**
     * The padding for the given level.
     *
     * <p>
     * There are two spaces per level of padding. Zero or less levels result
     * in an empty string being returned. This will never return null.
     * </p>
     *
     * @param level the level of padding required
     * @return a string to indent to the given level
     */
    protected static String getPadding(int level) {
        if (level < 1) {
            return EMPTY_STRING;
        } else {
            int length = level * 2;
            StringBuilder outputBuffer = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                outputBuffer.append(" ");
            }
            return outputBuffer.toString();
        }
    }

    /**
     * Returns either CRFL or EMPTY_STRING depending on the level
     *
     * @param level the level of indenting
     * @return CRFL if level is zero or grater, an empty string "" otherwise.
     */
    protected static String lineEnd(int level) {
        if (level > -1) {
            return CRLF;
        } else {
            return EMPTY_STRING;
        }
    }


    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }


    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return the version
     */
    public String getVersion() {
        return this.version;
    }


    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the identifier for this exporter
     */
    public String getId() {
        return this.identifier;
    }

    /**
     * @param id the identifier to set
     */
    public void setId(String id) {
        this.identifier = id;
    }

    public MarshalerExtension getExtension() {
        return this.extension;
    }

    public void setExtension(MarshalerExtension extn) {
        this.extension = extn;
    }
}
