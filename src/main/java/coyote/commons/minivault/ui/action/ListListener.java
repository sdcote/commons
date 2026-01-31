package coyote.commons.minivault.ui.action;

import coyote.commons.minivault.ui.MiniVaultFrame;
import coyote.commons.minivault.ui.helper.EntryHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Mouse listener for the entry title list.
 */
public class ListListener extends MouseAdapter {

  /**
   * Show entry on double click.
   *
   * @see MouseAdapter#mouseClicked(MouseEvent)
   */
  @Override
  public void mouseClicked(MouseEvent evt) {
    if (MiniVaultFrame.getInstance().isProcessing()) {
      return;
    }
    if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2) {
      EntryHelper.editEntry(MiniVaultFrame.getInstance());
    }
  }

  /**
   * Handle pop-up.
   *
   * @see MouseAdapter#mousePressed(MouseEvent)
   */
  @Override
  public void mousePressed(MouseEvent evt) {
    checkPopup(evt);
  }

  /**
   * Handle pop-up.
   *
   * @see MouseAdapter#mouseReleased(MouseEvent)
   */
  @Override
  public void mouseReleased(MouseEvent evt) {
    checkPopup(evt);
  }

  /**
   * Checks pop-up trigger.
   *
   * @param evt mouse event
   */
  private void checkPopup(MouseEvent evt) {
    if (MiniVaultFrame.getInstance().isProcessing()) {
      return;
    }
    if (evt.isPopupTrigger()) {
      JList list = MiniVaultFrame.getInstance().getEntryTitleList();
      if (list.isEnabled()) {
        Point point = new Point(evt.getX(), evt.getY());
        list.setSelectedIndex(list.locationToIndex(point));
        MiniVaultFrame.getInstance().getPopup().show(evt.getComponent(), evt.getX(), evt.getY());
      }
    }
  }

}
