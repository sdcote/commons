package coyote.commons.minivault.ui.action;

import coyote.commons.minivault.ui.CopiablePasswordField;
import coyote.commons.minivault.util.ClipboardUtils;

import javax.swing.*;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static java.awt.event.InputEvent.CTRL_MASK;
import static javax.swing.KeyStroke.getKeyStroke;

/**
 * Enumeration which holds text actions and related data.
 */
public enum TextComponentActionType {
  CUT(new TextComponentAction("Cut", getKeyStroke(KeyEvent.VK_X, CTRL_MASK), KeyEvent.VK_T) {
    private static final long serialVersionUID = 14475462253299992L;

    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent component = getTextComponent(e);
      if (isEnabled(component)) {
        try {
          ClipboardUtils.setClipboardContent(component.getSelectedText());
        } catch (Exception ex) {
          // ignore
        }
        component.replaceSelection("");
      }
    }

    @Override
    public boolean isEnabled(JTextComponent component) {
      boolean copyEnabled = true;
      if (component instanceof CopiablePasswordField) {
        copyEnabled = ((CopiablePasswordField) component).isCopyEnabled();
      }
      return component != null && copyEnabled && component.isEnabled() && component.isEditable()
              && component.getSelectedText() != null;
    }
  }),
  COPY(new TextComponentAction("Copy", getKeyStroke(KeyEvent.VK_C, CTRL_MASK), KeyEvent.VK_C) {
    private static final long serialVersionUID = -7210010689471121227L;

    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent component = getTextComponent(e);
      if (isEnabled(component)) {
        try {
          ClipboardUtils.setClipboardContent(component.getSelectedText());
        } catch (Exception ex) {
          // ignore
        }
      }
    }

    @Override
    public boolean isEnabled(JTextComponent component) {
      boolean copyEnabled = true;
      if (component instanceof CopiablePasswordField) {
        copyEnabled = ((CopiablePasswordField) component).isCopyEnabled();
      }
      return component != null && copyEnabled && component.isEnabled() && component.getSelectedText() != null;
    }
  }),
  PASTE(new TextComponentAction("Paste", getKeyStroke(KeyEvent.VK_V, CTRL_MASK), KeyEvent.VK_P) {
    private static final long serialVersionUID = -1993708421550306334L;

    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent component = getTextComponent(e);
      if (isEnabled(component)) {
        component.replaceSelection(ClipboardUtils.getClipboardContent());
      }
    }

    @Override
    public boolean isEnabled(JTextComponent component) {
      return component != null && component.isEnabled() && component.isEditable()
              && ClipboardUtils.getClipboardContent() != null;
    }
  }),
  DELETE(new TextComponentAction("Delete", getKeyStroke(KeyEvent.VK_DELETE, 0), KeyEvent.VK_D) {
    private static final long serialVersionUID = 4014133071306571362L;

    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent component = getTextComponent(e);
      if (component != null && component.isEnabled() && component.isEditable()) {
        try {
          Document doc = component.getDocument();
          Caret caret = component.getCaret();
          int dot = caret.getDot();
          int mark = caret.getMark();
          if (dot != mark) {
            doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
          } else if (dot < doc.getLength()) {
            int delChars = 1;
            if (dot < doc.getLength() - 1) {
              String dotChars = doc.getText(dot, 2);
              char c0 = dotChars.charAt(0);
              char c1 = dotChars.charAt(1);
              if (c0 >= '\uD800' && c0 <= '\uDBFF' && c1 >= '\uDC00' && c1 <= '\uDFFF') {
                delChars = 2;
              }
            }
            doc.remove(dot, delChars);
          }
        } catch (Exception bl) {
          // ignore
        }
      }
    }

    @Override
    public boolean isEnabled(JTextComponent component) {
      return component != null && component.isEnabled() && component.isEditable()
              && component.getSelectedText() != null;
    }
  }),
  CLEAR_ALL(new TextComponentAction("Clear All", null, KeyEvent.VK_L) {
    private static final long serialVersionUID = -7954090627842449276L;

    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent component = getTextComponent(e);
      if (isEnabled(component)) {
        component.selectAll();
        component.replaceSelection("");
      }
    }

    @Override
    public boolean isEnabled(JTextComponent component) {
      boolean result;
      if (component instanceof CopiablePasswordField) {
        result = component.isEnabled() && component.isEditable()
                && ((CopiablePasswordField) component).getPassword() != null
                && ((CopiablePasswordField) component).getPassword().length > 0;
      } else {
        result = component != null && component.isEnabled() && component.isEditable()
                && component.getText() != null && !component.getText().isEmpty();
      }
      return result;
    }
  }),
  SELECT_ALL(new TextComponentAction("Select All", getKeyStroke(KeyEvent.VK_A, CTRL_MASK), KeyEvent.VK_A) {
    private static final long serialVersionUID = -1989627518563736756L;

    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent component = getTextComponent(e);
      if (isEnabled(component)) {
        component.selectAll();
      }
    }

    @Override
    public boolean isEnabled(JTextComponent component) {
      boolean result;
      if (component instanceof CopiablePasswordField) {
        result = component.isEnabled() && ((CopiablePasswordField) component).getPassword() != null
                && ((CopiablePasswordField) component).getPassword().length > 0;
      } else {
        result = component != null && component.isEnabled() && component.getText() != null
                && !component.getText().isEmpty();
      }
      return result;
    }
  });

  private final String name;
  private final TextComponentAction action;

  TextComponentActionType(TextComponentAction action) {
    this.name = String.format("vault.text.%s_action", this.name().toLowerCase());
    this.action = action;
  }

  public static final void bindAllActions(JTextComponent component) {
    ActionMap actionMap = component.getActionMap();
    InputMap inputMap = component.getInputMap();
    for (TextComponentActionType type : values()) {
      actionMap.put(type.getName(), type.getAction());
      KeyStroke acc = type.getAccelerator();
      if (acc != null) {
        inputMap.put(type.getAccelerator(), type.getName());
      }
    }
  }

  public String getName() {
    return this.name;
  }

  public TextComponentAction getAction() {
    return this.action;
  }

  public KeyStroke getAccelerator() {
    return (KeyStroke) this.action.getValue(Action.ACCELERATOR_KEY);
  }

}
