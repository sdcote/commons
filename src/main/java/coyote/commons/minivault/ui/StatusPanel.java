package coyote.commons.minivault.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Class for representing a status bar.
 */
public class StatusPanel extends JPanel {

  private static final long serialVersionUID = -4183115153931668242L;

  private final JLabel label;
  private final JProgressBar progressBar;

  public StatusPanel() {
    super(new BorderLayout());
    setBorder(new EmptyBorder(2, 2, 2, 2));
    this.label = new JLabel();
    this.progressBar = new JProgressBar();
    add(this.label, BorderLayout.CENTER);
    add(this.progressBar, BorderLayout.EAST);
    setProcessing(false);
  }

  public String getText() {
    return this.label.getText();
  }

  public void setText(final String text) {
    this.label.setText(text);
  }

  public void setProcessing(boolean processing) {
    this.progressBar.setVisible(processing);
    this.progressBar.setIndeterminate(processing);
    setText(processing ? "Processing..." : " ");
  }
}
