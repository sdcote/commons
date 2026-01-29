package coyote.commons.minivault.ui;

import coyote.commons.minivault.util.CryptUtils;
import coyote.commons.minivault.util.SpringUtilities;
import coyote.commons.minivault.util.StringUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for displaying message dialogs.
 */
public final class MessageDialog extends JDialog implements ActionListener {

  public static final int DEFAULT_OPTION = -1;
  public static final int YES_NO_OPTION = 0;
  public static final int YES_NO_CANCEL_OPTION = 1;
  public static final int OK_CANCEL_OPTION = 2;
  public static final int YES_OPTION = 0;
  public static final int OK_OPTION = 0;
  public static final int NO_OPTION = 1;
  public static final int CANCEL_OPTION = 2;
  public static final int CLOSED_OPTION = -1;
  private static final Logger LOG = Logger.getLogger(MessageDialog.class.getName());
  private static final long serialVersionUID = -950047714192599040L;
  private int selectedOption;

  private MessageDialog(final Dialog parent, final Object message, final String title, ImageIcon icon, int optionType) {
    super(parent);
    initializeDialog(parent, message, title, icon, optionType);
  }

  private MessageDialog(final Frame parent, final Object message, final String title, ImageIcon icon, int optionType) {
    super(parent);
    initializeDialog(parent, message, title, icon, optionType);
  }

  private static void showMessageDialog(final Component parent, final Object message, final String title, ImageIcon icon) {
    showMessageDialog(parent, message, title, icon, DEFAULT_OPTION);
  }

  private static int showMessageDialog(final Component parent, final Object message, final String title, ImageIcon icon, int optionType) {
    int ret = CLOSED_OPTION;
    MessageDialog dialog = null;
    if (parent instanceof Frame) {
      dialog = new MessageDialog((Frame) parent, message, title, icon, optionType);
    } else if (parent instanceof Dialog) {
      dialog = new MessageDialog((Dialog) parent, message, title, icon, optionType);
    }
    if (dialog != null) {
      ret = dialog.getSelectedOption();
    }
    return ret;
  }

  /**
   * Shows a warning message.
   *
   * @param parent  parent component
   * @param message dialog message
   */
  public static void showWarningMessage(final Component parent, final String message) {
    showMessageDialog(parent, message, "Warning", getIcon("dialog_warning"));
  }

  /**
   * Shows an error message.
   *
   * @param parent  parent component
   * @param message dialog message
   */
  public static void showErrorMessage(final Component parent, final String message) {
    showMessageDialog(parent, message, "Error", getIcon("dialog_error"));
  }

  /**
   * Shows an information message.
   *
   * @param parent  parent component
   * @param message dialog message
   */
  public static void showInformationMessage(final Component parent, final String message) {
    showMessageDialog(parent, message, "Information", getIcon("dialog_info"));
  }

  /**
   * Shows a question dialog.
   *
   * @param parent     parent component
   * @param message    dialog message
   * @param optionType question type
   * @return selected option
   */
  public static int showQuestionMessage(final Component parent, final String message, final int optionType) {
    return showMessageDialog(parent, message, "Confirmation", getIcon("dialog_question"), optionType);
  }

  /**
   * Shows a password dialog.
   *
   * @param parent  parent component
   * @param confirm password confirmation
   * @return the password
   */
  public static byte[] showPasswordDialog(final Component parent, final boolean confirm) {
    JPanel panel = new JPanel();
    panel.add(new JLabel("Password:"));
    final JPasswordField password = TextComponentFactory.newPasswordField();
    panel.add(password);
    JPasswordField repeat = null;
    if (confirm) {
      repeat = TextComponentFactory.newPasswordField();
      panel.add(new JLabel("Repeat:"));
      panel.add(repeat);
    }
    panel.setLayout(new SpringLayout());
    SpringUtilities.makeCompactGrid(panel, confirm ? 2 : 1, 2, 5, 5, 5, 5);
    boolean notCorrect = true;

    while (notCorrect) {
      int option = showMessageDialog(parent, panel, "Enter Password", getIcon("dialog_lock"), OK_CANCEL_OPTION);
      if (option == OK_OPTION) {
        if (password.getPassword().length == 0) {
          showWarningMessage(parent, "Please enter a password.");
        } else if (confirm && !Arrays.equals(password.getPassword(), repeat.getPassword())) {
          showWarningMessage(parent, "Password and repeated password are not identical.");
        } else {
          notCorrect = false;
        }
      } else {
        return null;
      }
    }

    byte[] passwordHash = null;
    try {
      passwordHash = CryptUtils.getPKCS5Sha256Hash(password.getPassword());
    } catch (Exception e) {
      showErrorMessage(parent,
              "Cannot generate password hash:\n" + StringUtil.stripString(e.getMessage()) + "\n\nOpening and saving files are not possible!");
    }
    return passwordHash;
  }

  /**
   * Returns an image resource.
   *
   * @param name image name without path and extension
   * @return ImageIcon object
   */
  public static ImageIcon getIcon(String name) {
    try {
      return new ImageIcon(MessageDialog.class.getClassLoader().getResource("resources/images/" + name + ".png"));
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Get resource as string
   */
  private static String getResourceAsString(String name) {
    StringBuilder builder = new StringBuilder();
    BufferedReader bufferedReader = null;
    try {
      InputStream is = MessageDialog.class.getClassLoader().getResourceAsStream("resources/" + name);
      bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        builder.append(line).append('\n');
      }
    } catch (Exception e) {
      LOG.log(Level.WARNING, String.format("An error occurred during reading resource [%s]", name), e);
    } finally {
      try {
        if (bufferedReader != null) {
          bufferedReader.close();
        }
      } catch (IOException e) {
        LOG.log(Level.WARNING, String.format("An error occurred during closing reader for resource [%s]", name), e);
      }
    }
    return builder.toString();
  }

  /**
   * Shows a text file from the class path.
   *
   * @param parent   parent component
   * @param title    window title
   * @param textFile text file name
   */
  public static void showTextFile(final Component parent, final String title, final String textFile) {
    JTextArea area = TextComponentFactory.newTextArea(getResourceAsString(textFile));
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setEditable(false);
    area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    JScrollPane scrollPane = new JScrollPane(area);
    scrollPane.setPreferredSize(new Dimension(600, 400));
    showMessageDialog(parent, scrollPane, title, null, DEFAULT_OPTION);
  }

  private void initializeDialog(final Component parent, final Object message, final String title, ImageIcon icon, int optionType) {
    setModal(true);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(title);
    this.selectedOption = CLOSED_OPTION;

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    JButton defaultButton;
    switch (optionType) {
      case YES_NO_OPTION:
        defaultButton = createButton("Yes", YES_OPTION, getIcon("accept"));
        buttonPanel.add(defaultButton);
        buttonPanel.add(createButton("No", NO_OPTION, getIcon("close")));
        break;
      case YES_NO_CANCEL_OPTION:
        defaultButton = createButton("Yes", YES_OPTION, getIcon("accept"));
        buttonPanel.add(defaultButton);
        buttonPanel.add(createButton("No", NO_OPTION, getIcon("close")));
        buttonPanel.add(createButton("Cancel", CANCEL_OPTION, getIcon("cancel")));
        break;
      case OK_CANCEL_OPTION:
        defaultButton = createButton("OK", OK_OPTION, getIcon("accept"));
        buttonPanel.add(defaultButton);
        buttonPanel.add(createButton("Cancel", CANCEL_OPTION, getIcon("cancel")));
        break;
      default:
        defaultButton = createButton("OK", OK_OPTION, getIcon("accept"));
        buttonPanel.add(defaultButton);
        break;
    }
    getRootPane().setDefaultButton(defaultButton);

    JPanel mainPanel = new JPanel(new BorderLayout(5, 0));

    float widthMultiplier;
    JPanel messagePanel = new JPanel(new BorderLayout());
    if (message instanceof JScrollPane) {
      widthMultiplier = 1.0f;
      messagePanel.add((Component) message, BorderLayout.CENTER);
    } else if (message instanceof Component) {
      widthMultiplier = 1.5f;
      messagePanel.add((Component) message, BorderLayout.NORTH);
    } else {
      widthMultiplier = 1.0f;
      messagePanel.setBorder(new EmptyBorder(10, 0, 10, 10));
      messagePanel.add(new JLabel("<html>" + String.valueOf(message) .replaceAll("\\n", "<br />") + "</html>"), BorderLayout.CENTER);
    }
    mainPanel.add(messagePanel, BorderLayout.CENTER);

    if (icon != null) {
      JLabel image = new JLabel(icon);
      image.setVerticalAlignment(SwingConstants.TOP);
      image.setBorder(new EmptyBorder(10, 10, 0, 10));
      mainPanel.add(image, BorderLayout.WEST);
    }
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    getContentPane().add(mainPanel);
    setResizable(false);
    pack();
    setSize((int) (getWidth() * widthMultiplier), getHeight());
    setLocationRelativeTo(parent);
    setVisible(true);
  }

  private JButton createButton(String name, int option, ImageIcon icon) {
    JButton button = new JButton(name, icon);
    button.setMnemonic(name.charAt(0));
    button.setActionCommand(String.valueOf(option));
    button.addActionListener(this);
    return button;
  }

  private int getSelectedOption() {
    return this.selectedOption;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    this.selectedOption = Integer.parseInt(event.getActionCommand());
    dispose();
  }

}
