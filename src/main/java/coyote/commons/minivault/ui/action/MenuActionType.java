package coyote.commons.minivault.ui.action;

import coyote.commons.minivault.Entry;
import coyote.commons.minivault.ui.GeneratePasswordDialog;
import coyote.commons.minivault.ui.MessageDialog;
import coyote.commons.minivault.ui.MiniVaultFrame;
import coyote.commons.minivault.ui.helper.EntryHelper;
import coyote.commons.minivault.ui.helper.FileHelper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static coyote.commons.minivault.ui.MessageDialog.getIcon;
import static java.awt.event.InputEvent.ALT_MASK;
import static java.awt.event.InputEvent.CTRL_MASK;
import static javax.swing.KeyStroke.getKeyStroke;

/**
 * Enumeration which holds menu actions and related data.
 */
public enum MenuActionType {
  NEW_FILE(new AbstractMenuAction("New", getIcon("new"), getKeyStroke(KeyEvent.VK_N, CTRL_MASK)) {
    private static final long serialVersionUID = 6798968691756693113L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      FileHelper.createNew(MiniVaultFrame.getInstance());
    }
  }),

  OPEN_FILE(new AbstractMenuAction("Open File...", getIcon("open"), getKeyStroke(KeyEvent.VK_O, CTRL_MASK)) {
    private static final long serialVersionUID = -2607867190987446747L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      FileHelper.openFile(MiniVaultFrame.getInstance());
    }
  }),

  SAVE_FILE(new AbstractMenuAction("Save", getIcon("save"), getKeyStroke(KeyEvent.VK_S, CTRL_MASK)) {
    private static final long serialVersionUID = 6550002748470376623L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      FileHelper.saveFile(MiniVaultFrame.getInstance(), false);
    }
  }),

  SAVE_AS_FILE(new AbstractMenuAction("Save As...", getIcon("save_as"), null) {
    private static final long serialVersionUID = -8983765836213821794L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      FileHelper.saveFile(MiniVaultFrame.getInstance(), true);
    }
  }),

  CHANGE_PASSWORD(new AbstractMenuAction("Change Password...", getIcon("lock"), null) {
    private static final long serialVersionUID = 7102875777744975755L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      MiniVaultFrame parent = MiniVaultFrame.getInstance();
      byte[] password = MessageDialog.showPasswordDialog(parent, true);
      if (password == null) {
        MessageDialog.showInformationMessage(parent, "Password has not been modified.");
      } else {
        parent.getModel().setPassword(password);
        parent.getModel().setModified(true);
        parent.refreshFrameTitle();
        MessageDialog.showInformationMessage(parent,
                "Password has been successfully modified.\n\nSave the file now in order to\nget the new password applied.");
      }
    }
  }),

  GENERATE_PASSWORD(new AbstractMenuAction("Generate Password...", getIcon("generate"), getKeyStroke(KeyEvent.VK_Z, CTRL_MASK)) {
    private static final long serialVersionUID = 5510778062356764754L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      new GeneratePasswordDialog(MiniVaultFrame.getInstance());
    }
  }),

  EXIT(new AbstractMenuAction("Exit", getIcon("exit"), getKeyStroke(KeyEvent.VK_F4, ALT_MASK)) {
    private static final long serialVersionUID = 2536756965788796058L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      MiniVaultFrame.getInstance().exitFrame();
    }
  }),

  ABOUT(new AbstractMenuAction("About", getIcon("info"), getKeyStroke(KeyEvent.VK_F1, 0)) {
    private static final long serialVersionUID = -8805074855103908839L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      StringBuilder sb = new StringBuilder();
      sb.append("<b>" + MiniVaultFrame.PROGRAM_NAME + "</b>\n");
      sb.append("version: " + MiniVaultFrame.PROGRAM_VERSION + "\n");
      sb.append("\n");
      sb.append("Java version: ").append(System.getProperties().getProperty("java.version")).append("\n");
      sb.append(System.getProperties().getProperty("java.vendor"));
      MessageDialog.showInformationMessage(MiniVaultFrame.getInstance(), sb.toString());
    }
  }),

  ADD_ENTRY(new AbstractMenuAction("Add Entry...", getIcon("entry_new"), getKeyStroke(KeyEvent.VK_Y, CTRL_MASK)) {
    private static final long serialVersionUID = 1077683429043469432L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      EntryHelper.addEntry(MiniVaultFrame.getInstance());
    }
  }),

  EDIT_ENTRY(new AbstractMenuAction("Edit Entry...", getIcon("entry_edit"), getKeyStroke(KeyEvent.VK_E, CTRL_MASK)) {
    private static final long serialVersionUID = -8531358656886786384L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      EntryHelper.editEntry(MiniVaultFrame.getInstance());
    }
  }),

  DUPLICATE_ENTRY(new AbstractMenuAction("Duplicate Entry...", getIcon("entry_duplicate"), getKeyStroke(KeyEvent.VK_K, CTRL_MASK)) {
    private static final long serialVersionUID = 3707448174681781747L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      EntryHelper.duplicateEntry(MiniVaultFrame.getInstance());
    }
  }),

  DELETE_ENTRY(new AbstractMenuAction("Delete Entry...", getIcon("entry_delete"), getKeyStroke(KeyEvent.VK_D, CTRL_MASK)) {
    private static final long serialVersionUID = 6286328867418402965L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      EntryHelper.deleteEntry(MiniVaultFrame.getInstance());
    }
  }),

  COPY_URL(new AbstractMenuAction("Copy URL", getIcon("url"), getKeyStroke(KeyEvent.VK_U, CTRL_MASK)) {
    private static final long serialVersionUID = 7879840950345008835L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      MiniVaultFrame parent = MiniVaultFrame.getInstance();
      Entry entry = EntryHelper.getSelectedEntry(parent);
      if (entry != null) {
        EntryHelper.copyEntryField(parent, entry.getUrl());
      }
    }
  }),

  COPY_USER(new AbstractMenuAction("Copy User Name", getIcon("user"), getKeyStroke(KeyEvent.VK_B, CTRL_MASK)) {
    private static final long serialVersionUID = 5156534927048067411L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      MiniVaultFrame parent = MiniVaultFrame.getInstance();
      Entry entry = EntryHelper.getSelectedEntry(parent);
      if (entry != null) {
        EntryHelper.copyEntryField(parent, entry.getUsername());
      }
    }
  }),

  COPY_PASSWORD(new AbstractMenuAction("Copy Password", getIcon("keyring"), getKeyStroke(KeyEvent.VK_C, CTRL_MASK)) {
    private static final long serialVersionUID = -587306354455769670L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      MiniVaultFrame parent = MiniVaultFrame.getInstance();
      Entry entry = EntryHelper.getSelectedEntry(parent);
      if (entry != null) {
        EntryHelper.copyEntryField(parent, entry.getPassword());
      }
    }
  }),

  CLEAR_CLIPBOARD(new AbstractMenuAction("Clear Clipboard", getIcon("clear"), getKeyStroke(KeyEvent.VK_X, CTRL_MASK)) {
    private static final long serialVersionUID = -4309229892713364997L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      EntryHelper.copyEntryField(MiniVaultFrame.getInstance(), null);
    }
  }),

  EXPORT_JSON(new AbstractMenuAction("Export to JSON...", getIcon("export"), null) {
    private static final long serialVersionUID = -6830696193299436558L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      FileHelper.exportJsonFile(MiniVaultFrame.getInstance());
    }
  }),

  IMPORT_JSON(new AbstractMenuAction("Import from JSON...", getIcon("import"), null) {
    private static final long serialVersionUID = -5564393740561143067L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      FileHelper.importJsonFile(MiniVaultFrame.getInstance());
    }
  }),

  FIND_ENTRY(new AbstractMenuAction("Find Entry", getIcon("find"), getKeyStroke(KeyEvent.VK_F, CTRL_MASK)) {
    private static final long serialVersionUID = 5112161847931597231L;

    @Override
    public void actionPerformed(ActionEvent ev) {
      MiniVaultFrame.getInstance().getSearchPanel().setVisible(true);
    }
  });

  private final String name;
  private final AbstractMenuAction action;

  MenuActionType(AbstractMenuAction action) {
    this.name = String.format("vault.menu.%s_action", this.name().toLowerCase());
    this.action = action;
  }

  public static final void bindAllActions(JComponent component) {
    ActionMap actionMap = component.getActionMap();
    InputMap inputMap = component.getInputMap();
    for (MenuActionType type : values()) {
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

  public AbstractMenuAction getAction() {
    return this.action;
  }

  public KeyStroke getAccelerator() {
    return (KeyStroke) this.action.getValue(Action.ACCELERATOR_KEY);
  }

}
