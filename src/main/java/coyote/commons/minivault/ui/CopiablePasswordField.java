package coyote.commons.minivault.ui;

import javax.swing.*;

/**
 * An extension of {@link JPasswordField} which holds an extra attribute which flags if the password
 * field content is allowed to copy to system clipboard.
 */
public class CopiablePasswordField extends JPasswordField {

    private static final long serialVersionUID = 1205118236056025220L;
    private final boolean copyEnabled;

    public CopiablePasswordField(boolean copyEnabled) {
        super();
        this.copyEnabled = copyEnabled;
    }

    public boolean isCopyEnabled() {
        return this.copyEnabled;
    }
}
