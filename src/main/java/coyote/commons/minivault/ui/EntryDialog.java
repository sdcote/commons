package coyote.commons.minivault.ui;

import coyote.commons.minivault.Entry;
import coyote.commons.minivault.ui.helper.EntryHelper;
import coyote.commons.minivault.util.SpringUtilities;
import coyote.commons.minivault.util.StringUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * A dialog with the entry data.
 */
public class EntryDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 175220948895406617L;
    private static final char NULL_ECHO = '\0';
    private final JTextField nameField;
    private final JTextField userField;
    private final JTextField emailField;
    private final JTextField tokenField;
    private final JPasswordField passwordField;
    private final JPasswordField repeatField;
    private final JTextField urlField;
    private final JTextArea notesField;
    private final JToggleButton showButton;
    private final JToggleButton revealButton;
    private final char ORIGINAL_ECHO;
    private final boolean newEntry;
    private final JTextArea publickeyField;
    private final JTextArea privatekeyField;
    private final JPasswordField passphraseField;
    private final JPasswordField confirmField;
    private Entry formData;
    private String originalName;




    private JPanel keyPanel;
    private JPanel publicPanel;
    private JLabel publicKey;
    private JTextPane publicKeyField;
    private JPanel privatePanel;
    private JLabel privateKey;
    private JTextPane privateKeyField;




    /**
     * Creates a new EntryDialog instance.
     *
     * @param parent   parent component
     * @param title    dialog title
     * @param entry    the entry
     * @param newEntry new entry marker
     */
    public EntryDialog(final MiniVaultFrame parent, final String title, final Entry entry, final boolean newEntry) {
        super(parent, title, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.newEntry = newEntry;

        this.formData = null;

        JTabbedPane tabbedPane = new JTabbedPane();

        // General Tab / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
        JPanel generalPanel = new JPanel(false);

        JPanel fieldPanel = new JPanel();

        fieldPanel.add(new JLabel("Name:"));
        this.nameField = TextComponentFactory.newTextField();
        fieldPanel.add(this.nameField);

        fieldPanel.add(new JLabel("URL:"));
        this.urlField = TextComponentFactory.newTextField();
        fieldPanel.add(this.urlField);

        fieldPanel.add(new JLabel("eMail:"));
        this.emailField = TextComponentFactory.newTextField();
        fieldPanel.add(this.emailField);

        fieldPanel.add(new JLabel("Token:"));
        this.tokenField = TextComponentFactory.newTextField();
        fieldPanel.add(this.tokenField);

        fieldPanel.add(new JLabel("Username:"));
        this.userField = TextComponentFactory.newTextField();
        fieldPanel.add(this.userField);

        fieldPanel.add(new JLabel("Password:"));
        this.passwordField = TextComponentFactory.newPasswordField(true);
        this.ORIGINAL_ECHO = this.passwordField.getEchoChar();
        fieldPanel.add(this.passwordField);

        fieldPanel.add(new JLabel("Repeat:"));
        this.repeatField = TextComponentFactory.newPasswordField(true);
        fieldPanel.add(this.repeatField);

        fieldPanel.add(new JLabel(""));
        JPanel passwordButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.showButton = new JToggleButton("Show", MessageDialog.getIcon("show"));
        this.showButton.setActionCommand("show_button");
        this.showButton.setMnemonic(KeyEvent.VK_S);
        this.showButton.addActionListener(this);
        passwordButtonPanel.add(this.showButton);
        JButton generateButton = new JButton("Generate", MessageDialog.getIcon("generate"));
        generateButton.setActionCommand("generate_button");
        generateButton.setMnemonic(KeyEvent.VK_G);
        generateButton.addActionListener(this);
        passwordButtonPanel.add(generateButton);
        JButton copyButton = new JButton("Copy", MessageDialog.getIcon("keyring"));
        copyButton.setActionCommand("copy_button");
        copyButton.setMnemonic(KeyEvent.VK_C);
        copyButton.addActionListener(this);
        passwordButtonPanel.add(copyButton);
        fieldPanel.add(passwordButtonPanel);

        fieldPanel.setLayout(new SpringLayout());
        SpringUtilities.makeCompactGrid(fieldPanel, 8, 2, 5, 5, 5, 5);

        JPanel notesPanel = new JPanel(new BorderLayout(5, 5));
        notesPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        notesPanel.add(new JLabel("Notes:"), BorderLayout.NORTH);

        this.notesField = TextComponentFactory.newTextArea();
        this.notesField.setFont(TextComponentFactory.newTextField().getFont());
        this.notesField.setLineWrap(true);
        this.notesField.setWrapStyleWord(true);
        notesPanel.add(new JScrollPane(this.notesField), BorderLayout.CENTER);

        // Add the field panel and the notes panel to the general panel
        generalPanel.setLayout(new GridLayout(2, 1));
        generalPanel.add(fieldPanel, BorderLayout.NORTH);
        generalPanel.add(notesPanel, BorderLayout.CENTER);

        // add the general panel to the tabbed pane
        tabbedPane.addTab("General", null, generalPanel, "Basic fields");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        // Keypair Tab / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
        keyPanel = new JPanel();
        keyPanel.setLayout(new GridBagLayout());

        publicPanel = new JPanel();
        publicPanel.setLayout(new GridBagLayout());

        publicKey = new JLabel();
        publicKey.setText("Public Key");
        publicPanel.add(publicKey, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        this.publickeyField = TextComponentFactory.newTextArea();
        this.publickeyField.setFont(TextComponentFactory.newTextField().getFont());
        this.publickeyField.setLineWrap(false);
        this.publickeyField.setWrapStyleWord(true);
        publicPanel.add(new JScrollPane(this.publickeyField), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        keyPanel.add(publicPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        privatePanel = new JPanel();
        privatePanel.setLayout(new GridBagLayout());

        privateKey = new JLabel();
        privateKey.setText("Private Key");
        privatePanel.add(privateKey, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        this.privatekeyField = TextComponentFactory.newTextArea();
        this.privatekeyField.setFont(TextComponentFactory.newTextField().getFont());
        this.privatekeyField.setLineWrap(false);
        this.privatekeyField.setWrapStyleWord(true);
        privatePanel.add(new JScrollPane(this.privatekeyField), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        keyPanel.add(privatePanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        JPanel passphrasePanel = new JPanel();
        passphrasePanel.add(new JLabel("Passphrase:"));
        this.passphraseField = TextComponentFactory.newPasswordField(true);
        passphrasePanel.add(this.passphraseField);

        passphrasePanel.add(new JLabel("Repeat:"));
        this.confirmField = TextComponentFactory.newPasswordField(true);
        passphrasePanel.add(this.confirmField);

        passphrasePanel.add(new JLabel(""));

        JPanel passphraseButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.revealButton = new JToggleButton("Reveal", MessageDialog.getIcon("show"));
        this.revealButton.setActionCommand("reveal_button");
        this.revealButton.setMnemonic(KeyEvent.VK_R);
        this.revealButton.addActionListener(this);
        passphraseButtonPanel.add(this.revealButton);

        JButton copyPassphraseButton = new JButton("Copy", MessageDialog.getIcon("keyring"));
        copyPassphraseButton.setActionCommand("copy_passphrase_button");
        copyPassphraseButton.setMnemonic(KeyEvent.VK_P);
        copyPassphraseButton.addActionListener(this);
        passphraseButtonPanel.add(copyPassphraseButton);

        passphrasePanel.add(passphraseButtonPanel);

        passphrasePanel.setLayout(new SpringLayout());
        SpringUtilities.makeCompactGrid(passphrasePanel, 3, 2, 5, 5, 5, 5);
        keyPanel.add(passphrasePanel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));



        // add the general panel to the tabbed pane
        tabbedPane.addTab("Keys", null, keyPanel, "Keypair fields");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton okButton = new JButton("OK", MessageDialog.getIcon("accept"));
        okButton.setActionCommand("ok_button");
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.addActionListener(this);
        buttonPanel.add(okButton);

        JButton cancelButton = new JButton("Cancel", MessageDialog.getIcon("cancel"));
        cancelButton.setActionCommand("cancel_button");
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);

        // Add the tabbed pane to the  content pane of the dialog
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        // Add the button panel to the bottom
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        fillDialogData(entry);
        setSize(420, 520);
        setMinimumSize(new Dimension(370, 300));
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("show_button".equals(command)) {
            this.passwordField.setEchoChar(this.showButton.isSelected() ? NULL_ECHO : this.ORIGINAL_ECHO);
            this.repeatField.setEchoChar(this.showButton.isSelected() ? NULL_ECHO : this.ORIGINAL_ECHO);
        } else if ("ok_button".equals(command)) {
            if (this.nameField.getText().trim().isEmpty()) {
                MessageDialog.showWarningMessage(this, "Please fill the name field.");
                return;
            } else if (!checkEntryName()) {
                MessageDialog.showWarningMessage(this, "Name already exists,\nplease enter a different name.");
                return;
            } else if (!Arrays.equals(this.passwordField.getPassword(), this.repeatField.getPassword())) {
                MessageDialog.showWarningMessage(this, "Password and repeated password are not identical.");
                return;
            } else if (!Arrays.equals(this.passphraseField.getPassword(), this.confirmField.getPassword())) {
                MessageDialog.showWarningMessage(this, "Passphrase and repeated passphrase are not identical.");
                return;
            }
            setFormData(fetchDialogData());
            dispose();
        } else if ("cancel_button".equals(command)) {
            dispose();
        } else if ("generate_button".equals(command)) {
            GeneratePasswordDialog gpd = new GeneratePasswordDialog(this);
            String generatedPassword = gpd.getGeneratedPassword();
            if (generatedPassword != null && !generatedPassword.isEmpty()) {
                this.passwordField.setText(generatedPassword);
                this.repeatField.setText(generatedPassword);
            }
        } else if ("copy_button".equals(command)) {
            EntryHelper.copyEntryField(MiniVaultFrame.getInstance(), String.valueOf(this.passwordField.getPassword()));
        } else if ("reveal_button".equals(command)) {
            this.passphraseField.setEchoChar(this.revealButton.isSelected() ? NULL_ECHO : this.ORIGINAL_ECHO);
            this.confirmField.setEchoChar(this.revealButton.isSelected() ? NULL_ECHO : this.ORIGINAL_ECHO);
        } else if ("copy_passphrase_button".equals(command)) {
            EntryHelper.copyEntryField(MiniVaultFrame.getInstance(), String.valueOf(this.passphraseField.getPassword()));
        }
    }

    /**
     * Fills the form with the data of given entry.
     *
     * @param entry an entry
     */
    private void fillDialogData(Entry entry) {
        if (entry == null) {
            return;
        }
        this.originalName = entry.getName() == null ? "" : entry.getName();
        this.nameField.setText(this.originalName + (this.newEntry ? " (copy)" : ""));
        this.userField.setText(entry.getUsername() == null ? "" : entry.getUsername());
        this.passwordField.setText(entry.getPassword() == null ? "" : entry.getPassword());
        this.repeatField.setText(entry.getPassword() == null ? "" : entry.getPassword());
        this.urlField.setText(entry.getUrl() == null ? "" : entry.getUrl());
        this.emailField.setText(entry.getEmail() == null ? "" : entry.getEmail());
        this.tokenField.setText(entry.getToken() == null ? "" : entry.getToken());
        this.notesField.setText(entry.getNotes() == null ? "" : entry.getNotes());
        this.notesField.setCaretPosition(0);
        this.publickeyField.setText(entry.getPublickey() == null ? "" : entry.getPublickey());
        this.publickeyField.setCaretPosition(0);
        this.privatekeyField.setText(entry.getPrivatekey() == null ? "" : entry.getPrivatekey());
        this.notesField.setCaretPosition(0);
        this.passphraseField.setText(entry.getPassphrase() == null ? "" : entry.getPassphrase());
    }

    /**
     * Retrieves the form data.
     *
     * @return an entry
     */
    private Entry fetchDialogData() {
        Entry entry = new Entry();

        String title = StringUtil.stripNonValidXMLCharacters(this.nameField.getText());
        String user = StringUtil.stripNonValidXMLCharacters(this.userField.getText());
        String password = StringUtil.stripNonValidXMLCharacters(String.valueOf(this.passwordField.getPassword()));
        String url = StringUtil.stripNonValidXMLCharacters(this.urlField.getText());
        String email = StringUtil.stripNonValidXMLCharacters(this.emailField.getText());
        String token = StringUtil.stripNonValidXMLCharacters(this.tokenField.getText());
        String notes = StringUtil.stripNonValidXMLCharacters(this.notesField.getText());
        String publicKey = StringUtil.stripNonValidXMLCharacters(this.publickeyField.getText());
        String privateKey = StringUtil.stripNonValidXMLCharacters(this.privatekeyField.getText());
        String passphrase = StringUtil.stripNonValidXMLCharacters(String.valueOf(this.passphraseField.getPassword()));

        entry.setName(title == null || title.isEmpty() ? null : title);
        entry.setUsername(user == null || user.isEmpty() ? null : user);
        entry.setPassword(password == null || password.isEmpty() ? null : password);
        entry.setUrl(url == null || url.isEmpty() ? null : url);
        entry.setEmail(email == null || email.isEmpty() ? null : email);
        entry.setToken(token == null || token.isEmpty() ? null : token);
        entry.setNotes(notes == null || notes.isEmpty() ? null : notes);
        entry.setPublickey(publicKey == null || publicKey.isEmpty() ? null : publicKey);
        entry.setPrivatekey(privateKey == null || privateKey.isEmpty() ? null : privateKey);
        entry.setPassphrase(passphrase == null || passphrase.isEmpty() ? null : passphrase);

        return entry;
    }

    /**
     * Gets the form data (entry) of this dialog.
     *
     * @return nonempty form data if the 'OK1 button is pressed, otherwise an empty data
     */
    public Entry getFormData() {
        return this.formData;
    }

    /**
     * Sets the form data.
     *
     * @param formData form data
     */
    private void setFormData(Entry formData) {
        this.formData = formData;
    }

    /**
     * Checks the entry name.
     *
     * @return if the entry name is already exists in the data model than returns {@code false},
     * otherwise {@code true}
     */
    private boolean checkEntryName() {
        boolean nameIsOk = true;
        MiniVaultFrame parent = MiniVaultFrame.getInstance();
        String currentNameText = StringUtil.stripNonValidXMLCharacters(this.nameField.getText());
        if (currentNameText == null) {
            currentNameText = "";
        }
        if (this.newEntry || !currentNameText.equalsIgnoreCase(this.originalName)) {
            for (Entry entry : parent.getModel().getEntries().getEntry()) {
                if (currentNameText.equalsIgnoreCase(entry.getName())) {
                    nameIsOk = false;
                    break;
                }
            }
        }
        return nameIsOk;
    }
}
