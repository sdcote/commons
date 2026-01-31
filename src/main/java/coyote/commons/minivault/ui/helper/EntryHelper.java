package coyote.commons.minivault.ui.helper;

import coyote.commons.minivault.Entry;
import coyote.commons.minivault.ui.EntryDialog;
import coyote.commons.minivault.ui.MessageDialog;
import coyote.commons.minivault.ui.MiniVaultFrame;
import coyote.commons.minivault.util.ClipboardUtils;

/**
 * Helper class for entry operations.
 */
public final class EntryHelper {

  private EntryHelper() {
    // not intended to be instantiated
  }

  /**
   * Deletes an entry.
   *
   * @param parent parent component
   */
  public static void deleteEntry(MiniVaultFrame parent) {
    if (parent.getEntryTitleList().getSelectedIndex() == -1) {
      MessageDialog.showWarningMessage(parent, "Please select an entry.");
      return;
    }
    int option = MessageDialog.showQuestionMessage(parent, "Do you really want to delete this entry?",
            MessageDialog.YES_NO_OPTION);
    if (option == MessageDialog.YES_OPTION) {
      String title = (String) parent.getEntryTitleList().getSelectedValue();
      parent.getModel().getEntries().getEntry().remove(parent.getModel().getEntryByTitle(title));
      parent.getModel().setModified(true);
      parent.refreshFrameTitle();
      parent.refreshEntryTitleList(null);
    }
  }

  /**
   * Duplicates an entry.
   *
   * @param parent parent component
   */
  public static void duplicateEntry(MiniVaultFrame parent) {
    if (parent.getEntryTitleList().getSelectedIndex() == -1) {
      MessageDialog.showWarningMessage(parent, "Please select an entry.");
      return;
    }
    String title = (String) parent.getEntryTitleList().getSelectedValue();
    Entry oldEntry = parent.getModel().getEntryByTitle(title);
    EntryDialog ed = new EntryDialog(parent, "Duplicate Entry", oldEntry, true);
    if (ed.getFormData() != null) {
      parent.getModel().getEntries().getEntry().add(ed.getFormData());
      parent.getModel().setModified(true);
      parent.refreshFrameTitle();
      parent.refreshEntryTitleList(ed.getFormData().getName());
    }
  }

  /**
   * Edits the entry.
   *
   * @param parent parent component
   */
  public static void editEntry(MiniVaultFrame parent) {
    if (parent.getEntryTitleList().getSelectedIndex() == -1) {
      MessageDialog.showWarningMessage(parent, "Please select an entry.");
      return;
    }
    String title = (String) parent.getEntryTitleList().getSelectedValue();
    Entry oldEntry = parent.getModel().getEntryByTitle(title);
    EntryDialog ed = new EntryDialog(parent, "Edit Entry", oldEntry, false);
    if (ed.getFormData() != null) {
      parent.getModel().getEntries().getEntry().remove(oldEntry);
      parent.getModel().getEntries().getEntry().add(ed.getFormData());
      parent.getModel().setModified(true);
      parent.refreshFrameTitle();
      parent.refreshEntryTitleList(ed.getFormData().getName());
    }
  }

  /**
   * Adds an entry.
   *
   * @param parent parent component
   */
  public static void addEntry(MiniVaultFrame parent) {
    EntryDialog ed = new EntryDialog(parent, "Add New Entry", null, true);
    if (ed.getFormData() != null) {
      parent.getModel().getEntries().getEntry().add(ed.getFormData());
      parent.getModel().setModified(true);
      parent.refreshFrameTitle();
      parent.refreshEntryTitleList(ed.getFormData().getName());
    }
  }

  /**
   * Gets the selected entry.
   *
   * @param parent the parent frame
   * @return the entry or null
   */
  public static Entry getSelectedEntry(MiniVaultFrame parent) {
    if (parent.getEntryTitleList().getSelectedIndex() == -1) {
      MessageDialog.showWarningMessage(parent, "Please select an entry.");
      return null;
    }
    return parent.getModel().getEntryByTitle((String) parent.getEntryTitleList().getSelectedValue());
  }

  /**
   * Copy entry field value to clipboard.
   *
   * @param parent  the parent frame
   * @param content the content to copy
   */
  public static void copyEntryField(MiniVaultFrame parent, String content) {
    try {
      ClipboardUtils.setClipboardContent(content);
    } catch (Exception e) {
      MessageDialog.showErrorMessage(parent, e.getMessage());
    }
  }

}
