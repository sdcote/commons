package coyote.commons.rtw.transform;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TransformException;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.dataframe.DataField;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The FileDateClassifier transform reads a filename from the data frame and,
 * based on the file's last modified date, moves it to a subdirectory named with
 * that date in "YYYY-MM-DD" format or leaves it in place if it cannot determine
 * the file's last modified date.
 */
public class FileDateClassifier extends AbstractFrameTransform {

  private static final String FILENAME = "filename";
  private static final String DATE_FORMAT = "yyyy-MM-dd";

  private List<Pattern> patterns = new ArrayList<>();

  @Override
  public void open(final TransformContext context) {
    super.open(context);

    DataField field = configuration.getFieldIgnoreCase(ConfigTag.PATTERN);
    if (field != null) {
      if (field.isFrame()) {
        DataFrame patternFrame = (DataFrame) field.getObjectValue();
        for (DataField pField : patternFrame.getFields()) {
          String pattern = pField.getStringValue();
          if (StringUtil.isNotBlank(pattern)) {
            try {
              patterns.add(Pattern.compile(pattern));
            } catch (Exception e) {
              Log.error("Invalid filename regex pattern: " + pattern + " - " + e.getMessage());
            }
          }
        }
      } else {
        String pattern = field.getStringValue();
        if (StringUtil.isNotBlank(pattern)) {
          try {
            patterns.add(Pattern.compile(pattern));
          } catch (Exception e) {
            Log.error("Invalid filename regex pattern: " + pattern + " - " + e.getMessage());
          }
        }
      }
    }
  }

  /**
   * @see coyote.commons.rtw.FrameTransform#process(coyote.commons.dataframe.DataFrame)
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
        Log.debug("FileDateClassifier: No '" + FILENAME + "' field found in DataFrame");
      }
      return retval;
    }

    File file = new File(filename);

    if (patterns.size() > 0) {
      boolean match = false;
      for (Pattern p : patterns) {
        if (p.matcher(file.getName()).matches()) {
          match = true;
          break;
        }
      }
      if (!match) {
        return retval;
      }
    }

    if (!file.exists()) {
      if (Log.isLogging(Log.DEBUG_EVENTS)) {
        Log.debug("FileDateClassifier: File not found: " + filename);
      }
      return retval;
    }

    long lastModified = file.lastModified();
    if (lastModified == 0L) {
      if (Log.isLogging(Log.DEBUG_EVENTS)) {
        Log.debug("FileDateClassifier: Could not determine last modified date for: " + filename);
      }
      return retval;
    }

    String subDir;
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
      subDir = sdf.format(new Date(lastModified));
    } catch (Exception e) {
      if (Log.isLogging(Log.DEBUG_EVENTS)) {
        Log.debug("FileDateClassifier: Error formatting date for " + filename + ": " + e.getMessage());
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
            Log.debug("FileDateClassifier: Could not create directory " + targetDir.getAbsolutePath() + ": " + e.getMessage());
          }
          return retval;
        }
      }

      String baseName = FileUtil.getBase(file.getName());
      File[] filesInDir = file.getParentFile().listFiles();
      File newFileLocation = null;

      if (filesInDir != null) {
        for (File f : filesInDir) {
          if (f.isFile() && FileUtil.getBase(f.getName()).equals(baseName)) {
            File targetFile = new File(targetDir, f.getName());
            try {
              FileUtil.moveFile(f, targetFile);
              if (f.getAbsolutePath().equals(file.getAbsolutePath())) {
                newFileLocation = targetFile;
              }
            } catch (IOException e) {
              if (Log.isLogging(Log.DEBUG_EVENTS)) {
                Log.debug("FileDateClassifier: Could not move file " + f.getAbsolutePath() + " to " + targetFile.getAbsolutePath() + ": " + e.getMessage());
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
        Log.debug("FileDateClassifier: Error moving file " + filename + ": " + e.getMessage());
      }
    }

    return retval;
  }
}
