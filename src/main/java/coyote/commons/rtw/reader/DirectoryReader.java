package coyote.commons.rtw.reader;

import coyote.commons.StringUtil;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.FrameReader;
import coyote.commons.rtw.RTW;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reader that scans a directory and returns one filename per read().
 *
 * <p>Each returned frame contains a single String field named {@code filename}
 * with the absolute path of a matching file.</p>
 *
 * <p>The {@code directory} configuration element should point to a directory.
 * Optional filtering is controlled by the {@code pattern} configuration value,
 * which is treated as a regular expression matched against the file name.</p>
 */
public class DirectoryReader extends AbstractFrameReader implements FrameReader {

    /**
     * Field name returned in each frame
     */
    public static final String FILENAME_FIELD = "filename";

    /**
     * Default pattern that matches all files
     */
    private static final String DEFAULT_PATTERN = ".*";

    /**
     * Files discovered during open()
     */
    private final List<File> files = new ArrayList<File>();

    /**
     * Index of the next file to return
     */
    private int index = 0;

    /**
     * Compiled filename filter
     */
    private Pattern filenamePattern = Pattern.compile(DEFAULT_PATTERN);

    /**
     * Initialize the reader and scan the configured directory.
     *
     * @param context the transform context
     */
    @Override
    public void open(final TransformContext context) {
        super.open(context);

        files.clear();
        index = 0;

        String source = getSource();
        if (StringUtil.isBlank(source)) {
            String msg = "Reader.no_source_directory_specified for " + getClass().getName();
            Log.error(msg);
            context.setError(msg);
            return;
        }

        File sourceDir = new File(source);
        if (!sourceDir.isAbsolute()) {
            sourceDir = RTW.resolveFile(sourceDir, getContext());
        }

        if (sourceDir == null || !sourceDir.exists() || !sourceDir.isDirectory() || !sourceDir.canRead()) {
            String msg = "Reader.could_not_read_from_source " + getClass().getName() + " - " +
                    (sourceDir == null ? source : sourceDir.getAbsolutePath());
            Log.error(msg);
            context.setError(msg);
            return;
        }

        String pattern = configuration.containsIgnoreCase(ConfigTag.PATTERN)
                ? configuration.getAsString(ConfigTag.PATTERN)
                : DEFAULT_PATTERN;

        if (StringUtil.isBlank(pattern)) {
            pattern = DEFAULT_PATTERN;
        }

        try {
            filenamePattern = Pattern.compile(pattern);
        } catch (Exception ex) {
            String msg = "Invalid filename regex pattern: " + pattern + " - " + ex.getMessage();
            Log.error(msg);
            context.setError(msg);
            return;
        }

        boolean recurse = false;
        if (configuration.containsIgnoreCase(ConfigTag.RECURSE)) {
            try {
                recurse = configuration.getAsBoolean(ConfigTag.RECURSE);
            } catch (Exception ignore) {
                recurse = false;
            }
        }

        scanDirectory(sourceDir, recurse);

        Log.debug("DirectoryReader discovered " + files.size() + " matching file(s)");
    }

    /**
     * Read one filename at a time.
     *
     * @param context the transaction context
     * @return a frame containing the absolute filename or null when exhausted
     */
    @Override
    public DataFrame read(final TransactionContext context) {
        if (eof()) {
            return null;
        }

        File file = files.get(index++);
        DataFrame frame = new DataFrame();
        frame.add(FILENAME_FIELD, file.getAbsolutePath());

        super.recordCounter++;
        if (super.readLimit > 0 && super.recordCounter >= super.readLimit) {
            index = files.size();
        }

        if (eof()) {
            context.setLastFrame(true);
        }

        return frame;
    }

    /**
     * @return true when all matching files have been consumed
     */
    @Override
    public boolean eof() {
        return index >= files.size();
    }

    /**
     * @return the source directory
     */
    @Override
    public String getSource() {
        return configuration.getAsString(ConfigTag.DIRECTORY);
    }

    /**
     * Set the source directory.
     *
     * @param value directory to scan
     */
    public void setDirectory(final String value) {
        configuration.put(ConfigTag.DIRECTORY, value);
    }

    /**
     * @return the filename regex pattern
     */
    public String getPattern() {
        return configuration.getAsString(ConfigTag.PATTERN);
    }

    /**
     * Set the filename regex pattern.
     *
     * @param value regex used to match file names
     */
    public void setPattern(final String value) {
        configuration.put(ConfigTag.PATTERN, value);
    }

    private void scanDirectory(final File directory, final boolean recurse) {
        File[] entries = directory.listFiles();
        if (entries == null) {
            return;
        }

        for (File entry : entries) {
            if (entry.isDirectory()) {
                if (recurse) {
                    scanDirectory(entry, true);
                }
                continue;
            }

            if (filenamePattern.matcher(entry.getName()).matches()) {
                files.add(entry);
            }
        }
    }
}
