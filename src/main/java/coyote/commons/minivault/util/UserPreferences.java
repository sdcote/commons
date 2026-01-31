package coyote.commons.minivault.util;

import coyote.commons.minivault.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a utility class which allows a component to save user-related configuration settings in the users home
 * directory.
 *
 * <p>It is possible that multiple components will use the same file for user preferences. This class allows each
 * component to specify its own section in the configuration to avoid key collisions.</p>
 */
public class UserPreferences {
  private static final Logger LOG = Logger.getLogger(UserPreferences.class.getName());
  private static final Map<String, JSONObject> fileMap;

  static {
    fileMap = new HashMap<>();
    try {
      Runtime.getRuntime().addShutdownHook(new Thread("UserPreferencesSave") {
        public void run() {
          savePreferences();
        }
      });
    } catch (final Throwable e) {
      // should not happen
    }
  }

  /**
   * The name of the file this instance uses to store its configuration
   */
  private final String fileName;
  /**
   * The name of the section in the file in which our configuration is stored.
   */
  private final String sectionName;


  /**
   * Constructor for an instance of a user preferences object.
   *
   * @param filename the name of the file in which to store user preferences
   * @param section  the section within which this instances preferences are stored
   */
  public UserPreferences(String filename, String section) {
    if (StringUtil.isNotBlank(filename)) {
      if (StringUtil.isNotBlank(section)) {
        fileName = filename;
        sectionName = section;
        putConfig(fileName, readConfigurationFromFile());
      } else {
        throw new IllegalArgumentException("Section name cannot be blank");
      }
    } else {
      throw new IllegalArgumentException("File name cannot be blank");
    }
  }

  /**
   * Retrieve the configuration object stored at the given filename.
   *
   * @param filename the relative name of the file in which all configuration sections are stored.
   * @return the configuration stored in that file or none if there is no configuration found.
   */
  private static JSONObject getConfiguration(String filename) {
    JSONObject retval = null;
    synchronized (fileMap) {
      retval = fileMap.get(filename);
    }
    return retval;
  }


  /**
   * This is the method called by the shutdown hook to save all our configuration files.
   */
  private static void savePreferences() {
    synchronized (fileMap) {
      for (Map.Entry<String, JSONObject> entry : fileMap.entrySet()) {
        File filePath = new File(System.getProperty("user.home") + File.separator + entry.getKey());
        JSONObject config = entry.getValue();
        if (config != null) {
          try (FileWriter file = new FileWriter(filePath)) {
            file.write(config.toString(2));
            System.out.println("Successfully saved user preferences " + filePath.getAbsolutePath());
          } catch (Throwable t) {
            System.err.println("Could not save preferences to '" + filePath.getAbsolutePath() + "' Reason: " + t.getLocalizedMessage());
          }
        }
      }
    }
  }


  private void putConfig(String fileName, JSONObject configuration) {
    synchronized (fileMap) {
      fileMap.put(fileName, configuration);
    }
  }


  /**
   * Read the user preferences into a configuration object.
   *
   * @return the configuration object if the file exists and contains a valid configuration, null if the file does not
   * exist or is improperly formatted.
   */
  private JSONObject readConfigurationFromFile() {
    JSONObject retval = null;
    try {
      File filePath = new File(System.getProperty("user.home") + File.separator + getFileName());
      if (filePath.exists() && filePath.isFile()) {
        retval = new JSONObject(FileUtil.fileToString(filePath));
      }
    } catch (Exception e) {
      LOG.log(Level.WARNING, "An error occurred during loading user preferences.", e);
    }
    return retval;
  }


  /**
   * @return the root filename of our user preferences
   */
  private String getFileName() {
    return fileName;
  }


  /**
   * Return the user preference with the given key
   *
   * @param key the key of the user preference
   * @return the value in the preference configuration or null if not found.
   */
  public String get(String key) {
    String retval = null;
    JSONObject section = getSection();
    if (section != null) {
      try {
        retval = section.getString(key);
      } catch (Throwable t){
        // expected when not found
      }
    }
    return retval;
  }


  /**
   * Return the user preference with the given key or the given default if it does not exist
   *
   * @param key          the key of the user preference
   * @param defaultValue the value to return if it does not exist
   * @return the value in the preference configuration or the default value if not found.
   */
  public String get(String key, String defaultValue) {
    String retval = defaultValue;
    JSONObject section = getSection();
    try {
      if (section != null && StringUtil.isNotBlank(section.getString(key))) {
        retval = section.getString(key);
      }
    } catch (Throwable t) {
      // expected for not found conditions
    }
    return retval;
  }


  private JSONObject getSection() {
    JSONObject retval = null;
    JSONObject configuration = getConfiguration();
    if (configuration != null) {
      try {
        retval = configuration.getJSONObject(getSectionName());
      } catch (Throwable t) {
        // expected for not found conditions
      }
    }
    return retval;
  }


  private String getSectionName() {
    return sectionName;
  }


  private JSONObject getConfiguration() {
    return getConfiguration(getFileName());
  }


  /**
   * Set the given value in this user preferences section mapped to the given key.
   *
   * @param key   the key of the value to save
   * @param value the value to save
   * @return a reference to this object for fluent method chaining.
   */
  public UserPreferences set(String key, String value) {
    JSONObject section = getOrCreateSection();
    section.put(key, value);
    return this;
  }


  private JSONObject getOrCreateSection() {
    JSONObject retval;
    JSONObject configuration = getOrCreateConfiguration();
    try {
      retval = configuration.getJSONObject(getSectionName());
    } catch (Throwable t) {
      retval = new JSONObject();
      configuration.put(getSectionName(), retval);
    }
    return retval;
  }


  private JSONObject getOrCreateConfiguration() {
    JSONObject retval;
    synchronized (fileMap) {
      retval = fileMap.get(getFileName());
      if (retval == null) {
        retval = new JSONObject();
        fileMap.put(getFileName(), retval);
      }
    }
    return retval;
  }


  private <T> T getValue(String key, T defaultValue, Class<T> type) {
    T value = defaultValue;
    String prop = get(key);
    if (prop != null) {
      try {
        value = type.getConstructor(String.class).newInstance(prop);
      } catch (Exception e) {
        LOG.log(Level.WARNING, String.format("Could not parse value as [%s] for key [%s]", type.getName(), key));
      }
    }
    return value;
  }


  public Boolean is(String key, Boolean defaultValue) {
    return getValue(key, defaultValue, Boolean.class);
  }


  public Integer getInteger(String key, Integer defaultValue) {
    return getValue(key, defaultValue, Integer.class);
  }

}
