package coyote.commons.minivault.ui.action;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A listener which adds context menu capability to text components.
 */
public class TextComponentPopupListener extends MouseAdapter {

  private final JPopupMenu popup;
  private final JMenuItem cutItem;
  private final JMenuItem copyItem;
  private final JMenuItem pasteItem;
  private final JMenuItem deleteItem;
  private final JMenuItem clearAllItem;
  private final JMenuItem selectAllItem;

  public TextComponentPopupListener() {
    this.cutItem = new JMenuItem(TextComponentActionType.CUT.getAction());
    this.copyItem = new JMenuItem(TextComponentActionType.COPY.getAction());
    this.pasteItem = new JMenuItem(TextComponentActionType.PASTE.getAction());
    this.deleteItem = new JMenuItem(TextComponentActionType.DELETE.getAction());
    this.clearAllItem = new JMenuItem(TextComponentActionType.CLEAR_ALL.getAction());
    this.selectAllItem = new JMenuItem(TextComponentActionType.SELECT_ALL.getAction());

    this.popup = new JPopupMenu();
    this.popup.add(this.cutItem);
    this.popup.add(this.copyItem);
    this.popup.add(this.pasteItem);
    this.popup.add(this.deleteItem);
    this.popup.addSeparator();
    this.popup.add(this.clearAllItem);
    this.popup.add(this.selectAllItem);
  }

  private void showPopupMenu(MouseEvent e) {
    if (e.isPopupTrigger() && e.getSource() instanceof JTextComponent) {
      JTextComponent textComponent = (JTextComponent) e.getSource();
      if (textComponent.isEnabled() && (textComponent.hasFocus() || textComponent.requestFocusInWindow())) {
        this.cutItem.setEnabled(TextComponentActionType.CUT.getAction().isEnabled(textComponent));
        this.copyItem.setEnabled(TextComponentActionType.COPY.getAction().isEnabled(textComponent));
        this.pasteItem.setEnabled(TextComponentActionType.PASTE.getAction().isEnabled(textComponent));
        this.deleteItem.setEnabled(TextComponentActionType.DELETE.getAction().isEnabled(textComponent));
        this.clearAllItem.setEnabled(TextComponentActionType.CLEAR_ALL.getAction().isEnabled(textComponent));
        this.selectAllItem.setEnabled(TextComponentActionType.SELECT_ALL.getAction().isEnabled(textComponent));
        this.popup.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    showPopupMenu(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    showPopupMenu(e);
  }

}
