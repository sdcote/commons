package coyote.commons.minivault.ui.helper;

import coyote.commons.minivault.Entry;
import coyote.commons.minivault.MiniVault;
import coyote.commons.minivault.data.DocumentHelper;
import coyote.commons.minivault.ui.MessageDialog;
import coyote.commons.minivault.ui.MiniVaultFrame;
import coyote.commons.minivault.ui.action.Callback;
import coyote.commons.minivault.ui.action.Worker;
import coyote.commons.minivault.util.IconStorage;
import coyote.commons.minivault.util.StringUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Helper utils for file operations.
 */
public final class FileHelper {

  private static final String EXTENSION = "vault";

  private FileHelper() {
    // not intended to be instantiated
  }

  /**
   * Creates a new entries document.
   *
   * @param parent parent component
   */
  public static void createNew(final MiniVaultFrame parent) {
    if (parent.getModel().isModified()) {
      int option = MessageDialog.showQuestionMessage(parent,
              "The current file has been modified.\nDo you want to save the changes before closing?",
              MessageDialog.YES_NO_CANCEL_OPTION);
      if (option == MessageDialog.YES_OPTION) {
        saveFile(parent, false, new Callback() {
          @Override
          public void call(boolean result) {
            if (result) {
              parent.clearModel();
              parent.getSearchPanel().setVisible(false);
              parent.refreshAll();
            }
          }
        });
        return;
      } else if (option != MessageDialog.NO_OPTION) {
        return;
      }
    }
    parent.clearModel();
    parent.getSearchPanel().setVisible(false);
    parent.refreshAll();
  }


  /**
   * Shows a file chooser dialog and saves a file.
   *
   * @param parent parent component
   * @param saveAs normal 'Save' dialog or 'Save as'
   */
  public static void saveFile(final MiniVaultFrame parent, final boolean saveAs) {
    saveFile(parent, saveAs, new Callback() {
      @Override
      public void call(boolean result) {
        //default empty call
      }
    });
  }

  /**
   * Shows a file chooser dialog and saves a file.
   *
   * @param parent   parent component
   * @param saveAs   normal 'Save' dialog or 'Save as'
   * @param callback callback function with the result; the result is {@code true} if the file successfully saved;
   *                 otherwise {@code false}
   */
  public static void saveFile(final MiniVaultFrame parent, final boolean saveAs, final Callback callback) {
    final String fileName;
    if (saveAs || parent.getModel().getFileName() == null) {
      File file = showFileChooser(parent, "Save", EXTENSION, "Vault Files (*.vault)");
      if (file == null) {
        callback.call(false);
        return;
      }
      fileName = checkExtension(file.getPath(), "vault");
      if (!checkFileOverwrite(fileName, parent)) {
        callback.call(false);
        return;
      }
    } else {
      fileName = parent.getModel().getFileName();
    }

    final byte[] password;
    if (parent.getModel().getPassword() == null) {
      password = MessageDialog.showPasswordDialog(parent, true);
      if (password == null) {
        callback.call(false);
        return;
      }
    } else {
      password = parent.getModel().getPassword();
    }
    Worker worker = new Worker(parent) {
      @Override
      protected Void doInBackground() throws Exception {
        try {
          DocumentHelper.newInstance(fileName, password).writeJsonDocument(parent.getModel().getEntries(), false);
          parent.getModel().setFileName(fileName);
          parent.getModel().setPassword(password);
          parent.getModel().setModified(false);
        } catch (Throwable e) {
          throw new Exception("An error occurred during the save operation:\n" + e.getMessage());
        }
        return null;
      }

      @Override
      protected void done() {
        stopProcessing();
        boolean result = true;
        try {
          get();
        } catch (Exception e) {
          result = false;
          showErrorMessage(e);
        }
        callback.call(result);
      }
    };
    worker.execute();
    MiniVault.USER_PREFERENCE.set(MiniVault.LAST_DIRECTORY_PREF, new File(fileName).getParent());
  }

  /**
   * Shows a file chooser dialog and opens a file.
   *
   * @param parent parent component
   */
  public static void openFile(final MiniVaultFrame parent) {
    final File file = showFileChooser(parent, "Open", EXTENSION, "Vault Files (*.vault)");
    if (file == null) {
      return;
    }
    if (parent.getModel().isModified()) {
      int option = MessageDialog.showQuestionMessage(
              parent,
              "The current file has been modified.\nDo you want to save the changes before closing?",
              MessageDialog.YES_NO_CANCEL_OPTION);
      if (option == MessageDialog.YES_OPTION) {
        saveFile(parent, false, new Callback() {
          @Override
          public void call(boolean result) {
            if (result) {
              MiniVault.USER_PREFERENCE.set(MiniVault.LAST_DIRECTORY_PREF, file.getParent());
              doOpenFile(file.getPath(), parent);
            }
          }
        });
        return;
      } else if (option != MessageDialog.NO_OPTION) {
        return;
      }
    }
    MiniVault.USER_PREFERENCE.set(MiniVault.LAST_DIRECTORY_PREF, file.getParent());
    doOpenFile(file.getPath(), parent);
  }

  /**
   * Loads a file and fills the data model.
   *
   * @param fileName file name
   * @param parent   parent component
   */
  public static void doOpenFile(final String fileName, final MiniVaultFrame parent) {
    parent.clearModel();
    if (fileName == null) {
      return;
    }
    final byte[] password = MessageDialog.showPasswordDialog(parent, false);
    if (password == null) {
      return;
    }
    Worker worker = new Worker(parent) {
      @Override
      protected Void doInBackground() throws Exception {
        try {
          parent.getModel().setEntries(DocumentHelper.newInstance(fileName, password).readJsonDocument());
          parent.getModel().setFileName(fileName);
          parent.getModel().setPassword(password);
          parent.getSearchPanel().setVisible(false);
          preloadDomainIcons(parent.getModel().getEntries().getEntry());
        } catch (FileNotFoundException e) {
          throw e;
        } catch (IOException e) {
          throw new Exception("An error occurred during the open operation.\nPlease check your password.");
        } catch (Throwable e) {
          throw new Exception("An error occurred during the open operation:\n" + e.getMessage());
        }
        return null;
      }

      @Override
      protected void done() {
        stopProcessing();
        try {
          get();
        } catch (Exception e) {
          if (e.getCause() != null && e.getCause() instanceof FileNotFoundException) {
            handleFileNotFound(parent, fileName, password);
          } else {
            showErrorMessage(e);
          }
        }
      }
    };
    worker.execute();
  }

  /**
   * Handles file not found exception.
   *
   * @param parent   parent frame
   * @param fileName file name
   * @param password password to create a new file
   */
  static void handleFileNotFound(final MiniVaultFrame parent, final String fileName, final byte[] password) {
    int option = MessageDialog.showQuestionMessage(parent, "File not found:\n" + StringUtil.stripString(fileName)
            + "\n\nDo you want to create the file?", MessageDialog.YES_NO_OPTION);
    if (option == MessageDialog.YES_OPTION) {
      Worker fileNotFoundWorker = new Worker(parent) {
        @Override
        protected Void doInBackground() throws Exception {
          try {
            DocumentHelper.newInstance(fileName, password).writeJsonDocument(parent.getModel().getEntries(), false);
            parent.getModel().setFileName(fileName);
            parent.getModel().setPassword(password);
          } catch (Exception ex) {
            throw new Exception("An error occurred during the open operation:\n" + ex.getMessage());
          }
          return null;
        }

      };
      fileNotFoundWorker.execute();
    }
  }

  /**
   * Shows a file chooser dialog.
   *
   * @param parent      parent component
   * @param taskName    name of the task
   * @param extension   accepted file extension
   * @param description file extension description
   * @return a file object
   */
  private static File showFileChooser(final MiniVaultFrame parent, final String taskName, final String extension, final String description) {
    File ret = null;
    String lastDirectory = MiniVault.USER_PREFERENCE.get(MiniVault.LAST_DIRECTORY_PREF, "./");
    JFileChooser fc = new JFileChooser(lastDirectory);
    fc.setFileFilter(new FileFilter() {
      @Override
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith("." + extension);
      }

      @Override
      public String getDescription() {
        return description;
      }
    });
    int returnVal = fc.showDialog(parent, taskName);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      ret = fc.getSelectedFile();
    }
    return ret;
  }

  /**
   * Checks if overwrite is accepted.
   *
   * @param fileName file name
   * @param parent   parent component
   * @return {@code true} if overwrite is accepted; otherwise {@code false}
   */
  private static boolean checkFileOverwrite(String fileName, MiniVaultFrame parent) {
    boolean overwriteAccepted = true;
    File file = new File(fileName);
    if (file.exists()) {
      int option = MessageDialog.showQuestionMessage(parent, "File is already exists:\n" + StringUtil.stripString(fileName) + "\n\nDo you want to overwrite?", MessageDialog.YES_NO_OPTION);
      if (option != MessageDialog.YES_OPTION) {
        overwriteAccepted = false;
      }
    }
    return overwriteAccepted;
  }

  /**
   * Checks if the file name has the given extension
   *
   * @param fileName  file name
   * @param extension extension
   * @return file name ending with the given extension
   */
  private static String checkExtension(final String fileName, final String extension) {
    String separator = fileName.endsWith(".") ? "" : ".";
    if (!fileName.toLowerCase().endsWith(separator + extension)) {
      return fileName + separator + extension;
    }
    return fileName;
  }

  /**
   * Preload favicon image icons for domains.
   *
   * @param entries the entries
   */
  private static void preloadDomainIcons(List<Entry> entries) {
    IconStorage iconStorage = IconStorage.newInstance();
    for (Entry entry : entries) {
      iconStorage.getIcon(entry.getUrl());
    }
  }

  public static void exportJsonFile(final MiniVaultFrame parent) {
    MessageDialog.showWarningMessage(parent, "Please note that all data will be stored unencrypted.\nMake sure you keep the exported file in a secure location.");
    File file = showFileChooser(parent, "Export", "json", "JSON Files (*.json)");
    if (file == null) {
      return;
    }
    final String fileName = checkExtension(file.getPath(), "json");
    if (!checkFileOverwrite(fileName, parent)) {
      return;
    }
    Worker worker = new Worker(parent) {
      @Override
      protected Void doInBackground() throws Exception {
        try {
          DocumentHelper.newInstance(fileName).writeJsonDocument(parent.getModel().getEntries(), false);
        } catch (Throwable e) {
          throw new Exception("An error occurred during the JSON export operation:\n" + e.getMessage());
        }
        return null;
      }
    };
    worker.execute();
  }

  /**
   * Shows a file chooser dialog and imports the file.
   *
   * @param parent parent component
   */
  public static void importJsonFile(final MiniVaultFrame parent) {
    File file = showFileChooser(parent, "Import", "json", "JSON Files (*.json)");
    if (file == null) {
      return;
    }
    final String fileName = file.getPath();
    if (parent.getModel().isModified()) {
      int option = MessageDialog.showQuestionMessage(parent, "The current file has been modified.\nDo you want to save the changes before closing?", MessageDialog.YES_NO_CANCEL_OPTION);
      if (option == MessageDialog.YES_OPTION) {
        saveFile(parent, false, new Callback() {
          @Override
          public void call(boolean result) {
            if (result) {
              doImportJsonFile(fileName, parent);
            }
          }
        });
        return;
      } else if (option != MessageDialog.NO_OPTION) {
        return;
      }
    }
    doImportJsonFile(fileName, parent);
  }

  /**
   * Imports the given file.
   *
   * @param fileName file name
   * @param parent   parent component
   */
  static void doImportJsonFile(final String fileName, final MiniVaultFrame parent) {
    Worker worker = new Worker(parent) {
      @Override
      protected Void doInBackground() throws Exception {
        try {
          parent.getModel().setEntries(DocumentHelper.newInstance(fileName).readJsonDocument());
          parent.getModel().setModified(true);
          parent.getModel().setFileName(null);
          parent.getModel().setPassword(null);
          parent.getSearchPanel().setVisible(false);
          preloadDomainIcons(parent.getModel().getEntries().getEntry());
        } catch (Throwable e) {
          throw new Exception("An error occurred during the import operation:\n" + e.getMessage());
        }
        return null;
      }
    };
    worker.execute();
  }

}
