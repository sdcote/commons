package coyote.commons.minivault.ui;

import coyote.commons.minivault.MiniVault;
import coyote.commons.minivault.data.DataModel;
import coyote.commons.minivault.ui.action.Callback;
import coyote.commons.minivault.ui.action.CloseListener;
import coyote.commons.minivault.ui.action.ListListener;
import coyote.commons.minivault.ui.action.MenuActionType;
import coyote.commons.minivault.ui.helper.EntryHelper;
import coyote.commons.minivault.ui.helper.FileHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static coyote.commons.minivault.ui.MessageDialog.*;

/**
 * The main frame for the MiniVault.
 */
public final class MiniVaultFrame extends JFrame {

  public static final String PROGRAM_NAME = "JVault";
  public static final String PROGRAM_VERSION = "1.1.0";
  private static final Logger LOG = Logger.getLogger(MiniVaultFrame.class.getName());
  private static final long serialVersionUID = 1882514949062181443L;
  private static volatile MiniVaultFrame INSTANCE;
  private final JPopupMenu popup;
  private final JPanel topContainerPanel;
  private final JMenuBar menuBar;
  private final SearchPanel searchPanel;
  private final JMenu fileMenu;
  private final JMenu editMenu;
  private final JMenu toolsMenu;
  private final JMenu helpMenu;
  private final JToolBar toolBar;
  private final JScrollPane scrollPane;
  private final JList entryTitleList;
  private final DefaultListModel entryTitleListModel;
  private final DataModel model = DataModel.getInstance();
  private final StatusPanel statusPanel;
  private volatile boolean processing = false;

  private MiniVaultFrame(String fileName) {
    try {
      List<Image> icons = new ArrayList<Image>(5);
      icons.add(getIcon("jvault16x16").getImage());
      icons.add(getIcon("jvault24x24").getImage());
      icons.add(getIcon("jvault32x32").getImage());
      icons.add(getIcon("jvault64x64").getImage());
      icons.add(getIcon("jvault128x128").getImage());
      setIconImages(icons);
    } catch (Exception e) {
      LOG.log(Level.CONFIG, "Could not set application icon.", e);
    }

    this.toolBar = new JToolBar();
    this.toolBar.setFloatable(false);
    this.toolBar.add(MenuActionType.NEW_FILE.getAction());
    this.toolBar.add(MenuActionType.OPEN_FILE.getAction());
    this.toolBar.add(MenuActionType.SAVE_FILE.getAction());
    this.toolBar.addSeparator();
    this.toolBar.add(MenuActionType.ADD_ENTRY.getAction());
    this.toolBar.add(MenuActionType.EDIT_ENTRY.getAction());
    this.toolBar.add(MenuActionType.DUPLICATE_ENTRY.getAction());
    this.toolBar.add(MenuActionType.DELETE_ENTRY.getAction());
    this.toolBar.addSeparator();
    this.toolBar.add(MenuActionType.COPY_URL.getAction());
    this.toolBar.add(MenuActionType.COPY_USER.getAction());
    this.toolBar.add(MenuActionType.COPY_PASSWORD.getAction());
    this.toolBar.add(MenuActionType.CLEAR_CLIPBOARD.getAction());
    this.toolBar.addSeparator();
    this.toolBar.add(MenuActionType.ABOUT.getAction());
    this.toolBar.add(MenuActionType.EXIT.getAction());

    this.searchPanel = new SearchPanel(new Callback() {
      @Override
      public void call(boolean enabled) {
        if (enabled) {
          refreshEntryTitleList(null);
        }
      }
    });

    this.topContainerPanel = new JPanel(new BorderLayout());
    this.topContainerPanel.add(this.toolBar, BorderLayout.NORTH);
    this.topContainerPanel.add(this.searchPanel, BorderLayout.SOUTH);

    this.menuBar = new JMenuBar();

    this.fileMenu = new JMenu("File");
    this.fileMenu.setMnemonic(KeyEvent.VK_F);
    this.fileMenu.add(MenuActionType.NEW_FILE.getAction());
    this.fileMenu.add(MenuActionType.OPEN_FILE.getAction());
    this.fileMenu.add(MenuActionType.SAVE_FILE.getAction());
    this.fileMenu.add(MenuActionType.SAVE_AS_FILE.getAction());
    this.fileMenu.addSeparator();
    this.fileMenu.add(MenuActionType.EXPORT_JSON.getAction());
    this.fileMenu.add(MenuActionType.IMPORT_JSON.getAction());
    this.fileMenu.addSeparator();
    this.fileMenu.add(MenuActionType.CHANGE_PASSWORD.getAction());
    this.fileMenu.addSeparator();
    this.fileMenu.add(MenuActionType.EXIT.getAction());
    this.menuBar.add(this.fileMenu);

    this.editMenu = new JMenu("Edit");
    this.editMenu.setMnemonic(KeyEvent.VK_E);
    this.editMenu.add(MenuActionType.ADD_ENTRY.getAction());
    this.editMenu.add(MenuActionType.EDIT_ENTRY.getAction());
    this.editMenu.add(MenuActionType.DUPLICATE_ENTRY.getAction());
    this.editMenu.add(MenuActionType.DELETE_ENTRY.getAction());
    this.editMenu.addSeparator();
    this.editMenu.add(MenuActionType.COPY_URL.getAction());
    this.editMenu.add(MenuActionType.COPY_USER.getAction());
    this.editMenu.add(MenuActionType.COPY_PASSWORD.getAction());
    this.editMenu.addSeparator();
    this.editMenu.add(MenuActionType.FIND_ENTRY.getAction());
    this.menuBar.add(this.editMenu);

    this.toolsMenu = new JMenu("Tools");
    this.toolsMenu.setMnemonic(KeyEvent.VK_T);
    this.toolsMenu.add(MenuActionType.GENERATE_PASSWORD.getAction());
    this.toolsMenu.add(MenuActionType.CLEAR_CLIPBOARD.getAction());
    this.menuBar.add(this.toolsMenu);

    this.helpMenu = new JMenu("Help");
    this.helpMenu.setMnemonic(KeyEvent.VK_H);
    this.helpMenu.add(MenuActionType.ABOUT.getAction());
    this.menuBar.add(this.helpMenu);

    this.popup = new JPopupMenu();
    this.popup.add(MenuActionType.ADD_ENTRY.getAction());
    this.popup.add(MenuActionType.EDIT_ENTRY.getAction());
    this.popup.add(MenuActionType.DUPLICATE_ENTRY.getAction());
    this.popup.add(MenuActionType.DELETE_ENTRY.getAction());
    this.popup.addSeparator();
    this.popup.add(MenuActionType.COPY_URL.getAction());
    this.popup.add(MenuActionType.COPY_USER.getAction());
    this.popup.add(MenuActionType.COPY_PASSWORD.getAction());
    this.popup.addSeparator();
    this.popup.add(MenuActionType.FIND_ENTRY.getAction());

    this.entryTitleListModel = new DefaultListModel();
    this.entryTitleList = new JList(this.entryTitleListModel);
    this.entryTitleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.entryTitleList.addMouseListener(new ListListener());
    this.entryTitleList.setCellRenderer(new IconedListCellRenderer());

    this.scrollPane = new JScrollPane(this.entryTitleList);
    MenuActionType.bindAllActions(this.entryTitleList);

    this.statusPanel = new StatusPanel();

    refreshAll();

    getContentPane().add(this.topContainerPanel, BorderLayout.NORTH);
    getContentPane().add(this.scrollPane, BorderLayout.CENTER);
    getContentPane().add(this.statusPanel, BorderLayout.SOUTH);

    setJMenuBar(this.menuBar);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setSize(420, 400);
    setMinimumSize(new Dimension(420, 200));
    addWindowListener(new CloseListener());
    setLocationRelativeTo(null);
    setVisible(true);
    FileHelper.doOpenFile(fileName, this);

    // set focus to the list for easier keyboard navigation
    this.entryTitleList.requestFocusInWindow();
  }

  public static MiniVaultFrame getInstance() {
    return getInstance(null);
  }

  public static MiniVaultFrame getInstance(String fileName) {
    if (INSTANCE == null) {
      synchronized (MiniVaultFrame.class) {
        if (INSTANCE == null) {
          INSTANCE = new MiniVaultFrame(fileName);
        }
      }
    }
    return INSTANCE;
  }

  /**
   * Gets the entry title list.
   *
   * @return entry title list
   */
  public JList getEntryTitleList() {
    return this.entryTitleList;
  }

  /**
   * Gets the data model of this frame.
   *
   * @return data model
   */
  public DataModel getModel() {
    return this.model;
  }

  /**
   * Clears data model.
   */
  public void clearModel() {
    this.model.clear();
    this.entryTitleListModel.clear();
  }

  /**
   * Refresh frame title based on data model.
   */
  public void refreshFrameTitle() {
    setTitle((getModel().isModified() ? "*" : "")
            + (getModel().getFileName() == null ? "Untitled" : getModel().getFileName()) + " - "
            + PROGRAM_NAME);
  }

  /**
   * Refresh the entry titles based on data model.
   *
   * @param selectTitle title to select, or {@code null} if nothing to select
   */
  public void refreshEntryTitleList(String selectTitle) {
    this.entryTitleListModel.clear();
    List<String> titles = this.model.getTitles();
    Collections.sort(titles, String.CASE_INSENSITIVE_ORDER);

    String searchCriteria = this.searchPanel.getSearchCriteria();
    for (String title : titles) {
      if (searchCriteria.isEmpty() || title.toLowerCase().contains(searchCriteria.toLowerCase())) {
        this.entryTitleListModel.addElement(title);
      }
    }

    if (selectTitle != null) {
      this.entryTitleList.setSelectedValue(selectTitle, true);
    }

    if (searchCriteria.isEmpty()) {
      this.statusPanel.setText("Entries count: " + titles.size());
    } else {
      this.statusPanel.setText("Entries found: " + this.entryTitleListModel.size() + " / " + titles.size());
    }
  }

  /**
   * Refresh frame title and entry list.
   */
  public void refreshAll() {
    refreshFrameTitle();
    refreshEntryTitleList(null);
  }

  /**
   * Exits the application.
   */
  public void exitFrame() {
    if (MiniVault.USER_PREFERENCE.is("clear.clipboard.on.exit.enabled", false)) {
      EntryHelper.copyEntryField(this, null);
    }

    if (this.processing) {
      return;
    }
    if (this.model.isModified()) {
      int option = showQuestionMessage(this,
              "The current file has been modified.\nDo you want to save the changes before closing?", YES_NO_CANCEL_OPTION);
      if (option == YES_OPTION) {
        FileHelper.saveFile(this, false, new Callback() {
          @Override
          public void call(boolean result) {
            if (result) {
              System.exit(0);
            }
          }
        });
        return;
      } else if (option != NO_OPTION) {
        return;
      }
    }
    System.exit(0);
  }

  public JPopupMenu getPopup() {
    return this.popup;
  }

  /**
   * Gets the processing state of this frame.
   *
   * @return processing state
   */
  public boolean isProcessing() {
    return this.processing;
  }

  /**
   * Sets the processing state of this frame.
   *
   * @param processing processing state
   */
  public void setProcessing(boolean processing) {
    this.processing = processing;
    for (MenuActionType actionType : MenuActionType.values()) {
      actionType.getAction().setEnabled(!processing);
    }
    this.searchPanel.setEnabled(!processing);
    this.entryTitleList.setEnabled(!processing);
    this.statusPanel.setProcessing(processing);
  }

  /**
   * Get search panel.
   *
   * @return the search panel
   */
  public SearchPanel getSearchPanel() {
    return searchPanel;
  }

}
