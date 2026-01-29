package coyote.commons.minivault;

import coyote.commons.minivault.ui.MiniVaultFrame;
import coyote.commons.minivault.util.UserPreferences;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class MiniVault {

  public static final String ENTRIES_TAG = "entries";
  public static final String USER_TAG = "username";
  public static final String URL_TAG = "url";
  public static final String EMAIL_TAG = "email";
  public static final String NOTES_TAG = "notes";
  public static final String PASSPHRASE_TAG = "passphrase";
  public static final String PUBLIC_KEY_TAG = "publickey";
  public static final String PRIVATE_KEY_TAG = "privatekey";
  public static final String PASSWORD_TAG = "password";
  public static final String NAME_TAG = "name";
  public static final String TOKEN_TAG = "token";
  public static final String LAST_DIRECTORY_PREF = "last.directory";
  private static final String PREFERENCE_FILE = ".jvault";
  private static final String PREFERENCE_SECTION = "vault";
  private static final Logger LOG = Logger.getLogger(MiniVault.class.getName());
  private static final String METAL_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";

  public static UserPreferences USER_PREFERENCE;

  static {
    USER_PREFERENCE = new UserPreferences(PREFERENCE_FILE, PREFERENCE_SECTION);
    System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
  }

  public static void main(final String[] args) {
    try {
      String lookAndFeel;
      if (USER_PREFERENCE.is("system.look.and.feel.enabled", true)) {
        lookAndFeel = UIManager.getSystemLookAndFeelClassName();
      } else {
        lookAndFeel = METAL_LOOK_AND_FEEL;
      }

      if (METAL_LOOK_AND_FEEL.equals(lookAndFeel)) {
        MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme() {
          private final ColorUIResource primary1 = new ColorUIResource(0x4d6781);
          private final ColorUIResource primary2 = new ColorUIResource(0x7a96b0);
          private final ColorUIResource primary3 = new ColorUIResource(0xc8d4e2);
          private final ColorUIResource secondary1 = new ColorUIResource(0x000000);
          private final ColorUIResource secondary2 = new ColorUIResource(0xaaaaaa);
          private final ColorUIResource secondary3 = new ColorUIResource(0xdfdfdf);

          @Override
          protected ColorUIResource getPrimary1() {
            return this.primary1;
          }

          @Override
          protected ColorUIResource getPrimary2() {
            return this.primary2;
          }

          @Override
          protected ColorUIResource getPrimary3() {
            return this.primary3;
          }

          @Override
          protected ColorUIResource getSecondary1() {
            return this.secondary1;
          }

          @Override
          protected ColorUIResource getSecondary2() {
            return this.secondary2;
          }

          @Override
          protected ColorUIResource getSecondary3() {
            return this.secondary3;
          }
        });

        UIManager.put("swing.boldMetal", Boolean.FALSE);
      }

      UIManager.setLookAndFeel(lookAndFeel);
    } catch (Exception e) {
      LOG.log(Level.CONFIG, "Could not set look and feel for the application", e);
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        MiniVaultFrame.getInstance((args.length > 0) ? args[0] : null);
      }
    });
  }
}
