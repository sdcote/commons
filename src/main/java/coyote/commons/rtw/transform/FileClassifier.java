package coyote.commons.rtw.transform;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TransformException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The FileClassifier transform reads a filename from the data frame and, based
 * on the file's extension, moves it to a specific subdirectory or leaves it in
 * place.
 */
public class FileClassifier extends AbstractFrameTransform {

  private static final String FILENAME = "filename";
  private final Map<String, String> extensionMap = new HashMap<>();

  @Override
  public void setConfiguration(Config cfg) throws ConfigurationException {
    super.setConfiguration(cfg);
    Config section = cfg.getSection(ConfigTag.MAP);
    if (section != null) {
      for (DataField field : section.getFields()) {
        if (field.isNotNull() && !field.isFrame()) {
          String ext = field.getName();
          String relDir = field.getStringValue();
          if (StringUtil.isNotBlank(ext) && StringUtil.isNotBlank(relDir)) {
            extensionMap.put(ext.toLowerCase(), relDir);
          }
        }
      }
    }
  }

  /**
   * Subclasses should probably override {@link #performTransform(DataFrame)} instead of
   * this method to enable this class to handle conditional checks.
   */
  @Override
  public DataFrame process(DataFrame frame) throws TransformException {
    DataFrame retval = null;
    if (isEnabled()) {
        retval = performTransform(frame);
    }
    return retval;
  }


  protected DataFrame performTransform(DataFrame frame) throws TransformException {
    if (frame == null) {
      return null;
    }

    // The first thing the transform should do is clone or perform a deep copy of the DataFrame
    DataFrame retval = (DataFrame) frame.clone();

    String filename = frame.getAsString(FILENAME);
    if (StringUtil.isBlank(filename)) {
      if (Log.isLogging(Log.DEBUG_EVENTS)) {
        Log.debug("FileClassifier: No '" + FILENAME + "' field found in DataFrame");
      }
      return retval;
    }

    File file = new File(filename);
    if (!file.exists()) {
      if (Log.isLogging(Log.DEBUG_EVENTS)) {
        Log.debug("FileClassifier: File not found: " + filename);
      }
      return retval;
    }

    String ext = null;
    try {
      ext = FileUtil.getExtension(file.getName());
    } catch (IOException e) {
      if (Log.isLogging(Log.DEBUG_EVENTS)) {
        Log.debug("FileClassifier: Could not get extension for " + file.getName() + ": " + e.getMessage());
      }
      return retval;
    }

    if (StringUtil.isBlank(ext)) {
      if (Log.isLogging(Log.DEBUG_EVENTS)) {
        Log.debug("FileClassifier: File has no extension: " + filename);
      }
      return retval;
    }

    String subDir = extensionMap.get(ext.toLowerCase());
    if (subDir == null) {
      if (Log.isLogging(Log.DEBUG_EVENTS)) {
        Log.debug("FileClassifier: Extension '" + ext + "' not found in map for file: " + filename);
      }
      return retval;
    }

    try {
      File targetDir = new File(file.getParentFile(), subDir);
      if (!targetDir.exists()) {
        try {
          FileUtil.makeDirectory(targetDir);
        } catch (IOException e) {
          if (Log.isLogging(Log.DEBUG_EVENTS)) {
            Log.debug("FileClassifier: Could not create directory " + targetDir.getAbsolutePath() + ": " + e.getMessage());
          }
          return retval;
        }
      }

      String baseName = FileUtil.getBase(file.getName());
      File[] filesInDir = file.getParentFile().listFiles();
      File newFileLocation = null;

      if (filesInDir != null) {
        for (File f : filesInDir) {
          if (FileUtil.getBase(f.getName()).equals(baseName)) {
            File targetFile = new File(targetDir, f.getName());
            try {
              FileUtil.moveFile(f, targetFile);
              if (f.getAbsolutePath().equals(file.getAbsolutePath())) {
                newFileLocation = targetFile;
              }
            } catch (IOException e) {
              if (Log.isLogging(Log.DEBUG_EVENTS)) {
                Log.debug("FileClassifier: Could not move file " + f.getAbsolutePath() + " to " + targetFile.getAbsolutePath() + ": " + e.getMessage());
              }
            }
          }
        }
      }

      if (newFileLocation != null) {
        retval.put(FILENAME, newFileLocation.getAbsolutePath());
      }
    } catch (Exception e) {
      if (Log.isLogging(Log.DEBUG_EVENTS)) {
        Log.debug("FileClassifier: Error moving file " + filename + ": " + e.getMessage());
      }
    }

    return retval;
  }
}
