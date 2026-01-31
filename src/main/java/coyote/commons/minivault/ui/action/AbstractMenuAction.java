package coyote.commons.minivault.ui.action;

import javax.swing.*;

/**
 * Class for handling menu actions.
 */
public abstract class AbstractMenuAction extends AbstractAction {

  private static final long serialVersionUID = 5470805628583386182L;

  /**
   * Creates a new menu action.
   *
   * @param text        title of the action that appears on UI
   * @param icon        icon of action
   * @param accelerator accelerator key
   */
  public AbstractMenuAction(String text, Icon icon, KeyStroke accelerator) {
    super(text, icon);
    putValue(SHORT_DESCRIPTION, text);
    if (accelerator != null) {
      putValue(ACCELERATOR_KEY, accelerator);
    }
  }
}
