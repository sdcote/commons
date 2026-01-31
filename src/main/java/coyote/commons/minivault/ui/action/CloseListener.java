package coyote.commons.minivault.ui.action;

import coyote.commons.minivault.ui.MiniVaultFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Listener for widow close.
 */
public class CloseListener extends WindowAdapter {

  /**
   * Calls the {@code exitFrame} method of main frame.
   *
   * @see WindowAdapter#windowClosing(WindowEvent)
   */
  @Override
  public void windowClosing(WindowEvent event) {
    if (event.getSource() instanceof MiniVaultFrame) {
      ((MiniVaultFrame) event.getSource()).exitFrame();
    }
  }

}
