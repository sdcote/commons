package coyote.commons.rtw.reader;

import coyote.commons.StringUtil;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.FrameReader;
import coyote.commons.rtw.RTW;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Reader that monitors a directory for changes (creations and deletions).
 *
 * <p>Each returned frame contains a {@code filename} field with the absolute
 * path of the changed file or directory, and a {@code change} field with the
 * type of change ("Created" or "Deleted").</p>
 *
 * <p>The reader scans the directory structure on initialization to establish a
 * baseline. Subsequent calls to {@code read()} will rescan the directory and
 * return any detected changes. If multiple changes are detected in a single
 * scan (e.g., deleting a directory containing multiple files), they are queued
 * and returned one by one in subsequent calls to {@code read()} without
 * rescanning until the queue is empty.</p>
 *
 * <p>If no changes are detected, the reader waits for a configurable interval
 * (default 6 seconds) before scanning again. It does not return until a
 * change is detected or the reader is closed. This is a blocking read, making
 * it suitable for event-driven processing where the thread should wait for
 * activity.</p>
 *
 * <p>Configuration:
 * <ul>
 *   <li>{@code directory} - the directory to monitor.</li>
 *   <li>{@code interval} - the number of seconds to wait between scans when no changes are found (default: 6).</li>
 *   <li>{@code recurse} - boolean flag indicating if the reader should scan subdirectories (default: true).</li>
 *   <li>{@code include} - a list of regex expressions; only matching files/dirs generate dataframes. The include list is checked first.</li>
 *   <li>{@code exclude} - a list of regex expressions; matching files/dirs are ignored. The exclude list is checked after the include list.</li>
 * </ul>
 * </p>
 */
public class DirectoryChangeReader extends AbstractFrameReader implements FrameReader {

    /** Field name for the absolute filename of the change. */
    public static final String FILENAME_FIELD = "filename";

    /** Field name for the type of change ("Created", "Deleted", or "Modified"). */
    public static final String CHANGE_FIELD = "change";

    /** Constant for a "Created" change type. */
    public static final String CREATED = "Created";

    /** Constant for a "Deleted" change type. */
    public static final String DELETED = "Deleted";

    /** Constant for a "Modified" change type. */
    public static final String MODIFIED = "Modified";

    /** Field name for the previous size of a file in bytes. */
    public static final String PREVIOUS_SIZE = "previousSize";

    /** Field name for the current size of a file in bytes. */
    public static final String CURRENT_SIZE = "currentSize";

    /** Default scan interval in seconds. */
    private static final int DEFAULT_INTERVAL = 6;

    /** The directory being monitored. */
    private File directoryToMonitor;

    /** Interval between scans in seconds. */
    private int scanInterval = DEFAULT_INTERVAL;

    /** Whether to scan subdirectories. */
    private boolean recurse = true;

    /** List of include patterns. */
    private final List<Pattern> includes = new ArrayList<>();

    /** List of exclude patterns. */
    private final List<Pattern> excludes = new ArrayList<>();

    /** Current state of the directory (path to size mapping). */
    private Map<String, Long> currentState = new HashMap<>();

    /** Queue of pending changes to be returned by read(). */
    private final Queue<Change> pendingChanges = new LinkedList<>();



    /**
     * Initializes the reader with configuration and performs an initial scan.
     *
     * <p>This establishes the baseline state against which future changes are detected.</p>
     *
     * @param context The transform context for this operation.
     */
    @Override
    public void open(final TransformContext context) {
        super.open(context);

        String source = getSource();
        if (StringUtil.isBlank(source)) {
            String msg = "DirectoryChangeReader: No directory specified in configuration.";
            Log.error(msg);
            context.setError(msg);
            return;
        }

        directoryToMonitor = new File(source);
        if (!directoryToMonitor.isAbsolute()) {
            directoryToMonitor = RTW.resolveFile(directoryToMonitor, getContext());
        }

        if (directoryToMonitor == null || !directoryToMonitor.exists() || !directoryToMonitor.isDirectory() || !directoryToMonitor.canRead()) {
            String msg = "DirectoryChangeReader: Could not read from source directory - " +
                    (directoryToMonitor == null ? source : directoryToMonitor.getAbsolutePath());
            Log.error(msg);
            context.setError(msg);
            return;
        }

        if (configuration.containsIgnoreCase(ConfigTag.INTERVAL)) {
            try {
                scanInterval = (int) configuration.getLong(ConfigTag.INTERVAL);
            } catch (Exception e) {
                Log.warn("DirectoryChangeReader: Invalid scan interval, using default " + DEFAULT_INTERVAL);
                scanInterval = DEFAULT_INTERVAL;
            }
        } else if (configuration.containsIgnoreCase(ConfigTag.SECONDS)) {
            try {
                scanInterval = (int) configuration.getLong(ConfigTag.SECONDS);
            } catch (Exception e) {
                Log.warn("DirectoryChangeReader: Invalid seconds value, using default " + DEFAULT_INTERVAL);
                scanInterval = DEFAULT_INTERVAL;
            }
        }

        if (configuration.containsIgnoreCase(ConfigTag.RECURSE)) {
            try {
                recurse = configuration.getBoolean(ConfigTag.RECURSE);
            } catch (Exception e) {
                Log.warn("DirectoryChangeReader: Invalid recurse value, using default true");
                recurse = true;
            }
        }

        parsePatterns(ConfigTag.INCLUDE, includes);
        parsePatterns(ConfigTag.EXCLUDE, excludes);

        // Initial scan to establish baseline
        currentState = scanDirectory(directoryToMonitor);
        Log.debug("DirectoryChangeReader: Initialized with " + currentState.size() + " entries in " + directoryToMonitor.getAbsolutePath());
    }


    /**
     * Reads the next change from the directory.
     *
     * <p>If there are pending changes in the queue, it returns the next one.
     * Otherwise, it rescans the directory. If no changes are detected, it
     * waits for the configured interval and scans again until a change is found.</p>
     *
     * @param context The transaction context for the current operation.
     * @return A DataFrame containing details about the detected change, or null if interrupted.
     */
    @Override
    public DataFrame read(final TransactionContext context) {
        while (pendingChanges.isEmpty()) {
            Map<String, Long> newState = scanDirectory(directoryToMonitor);

            // Detect deletions
            for (String path : currentState.keySet()) {
                if (!newState.containsKey(path)) {
                    pendingChanges.add(new Change(path, DELETED, currentState.get(path), null));
                }
            }

            // Detect creations and modifications
            for (Map.Entry<String, Long> entry : newState.entrySet()) {
                String path = entry.getKey();
                Long newSize = entry.getValue();
                Long oldSize = currentState.get(path);

                if (oldSize == null) {
                    pendingChanges.add(new Change(path, CREATED, null, newSize));
                } else if (!newSize.equals(oldSize)) {
                    // Check if it's a file before reporting modification based on size
                    // (directories often change size, but we usually care about files)
                    File file = new File(path);
                    if (file.isFile()) {
                        pendingChanges.add(new Change(path, MODIFIED, oldSize, newSize));
                    }
                }
            }

            if (pendingChanges.isEmpty()) {
                try {
                    Thread.sleep(scanInterval * 1000L);
                } catch (InterruptedException e) {
                    Log.info("DirectoryChangeReader: Interrupted while waiting for changes.");
                    Thread.currentThread().interrupt();
                    return null;
                }
            } else {
                currentState = newState;
            }
        }

        Change change = pendingChanges.poll();
        DataFrame frame = new DataFrame();
        frame.add(FILENAME_FIELD, change.path);
        frame.add(CHANGE_FIELD, change.type);

        if (change.oldSize != null) {
            frame.add(PREVIOUS_SIZE, change.oldSize);
        }
        if (change.newSize != null) {
            frame.add(CURRENT_SIZE, change.newSize);
        }

        super.recordCounter++;
        return frame;
    }


    /**
     * Indicates whether the end of the stream has been reached.
     *
     * @return {@code false} as this reader is designed to run indefinitely.
     */
    @Override
    public boolean eof() {
        return false; // This reader is intended to run indefinitely
    }


    /**
     * Parses include or exclude patterns from the configuration.
     *
     * @param tag  The configuration tag to search for (e.g., "include" or "exclude").
     * @param list The list to which successfully compiled patterns will be added.
     */
    private void parsePatterns(String tag, List<Pattern> list) {
        for (DataField field : configuration.getFields()) {
            if (field.getName() != null && field.getName().equalsIgnoreCase(tag)) {
                addPattern(field.getStringValue(), list);
            }
        }
    }


    /**
     * Compiles a regex string into a Pattern and adds it to the provided list.
     *
     * @param regex The regular expression string to compile.
     * @param list  The list to which the compiled Pattern will be added.
     */
    private void addPattern(String regex, List<Pattern> list) {
        try {
            list.add(Pattern.compile(regex));
        } catch (Exception e) {
            Log.error("DirectoryChangeReader: Invalid regex pattern: " + regex);
        }
    }


    /**
     * Scans the monitored directory and returns its current state.
     *
     * @param root The root directory to scan.
     * @return A map where keys are absolute file paths and values are file sizes in bytes.
     */
    private Map<String, Long> scanDirectory(File root) {
        Map<String, Long> results = new HashMap<>();
        doScan(root, results, recurse);
        return results;
    }


    /**
     * Performs a recursive or non-recursive scan of a directory.
     *
     * @param dir       The directory to scan.
     * @param results   The map to populate with scan results.
     * @param recursive Whether to scan subdirectories.
     */
    private void doScan(File dir, Map<String, Long> results, boolean recursive) {
        File[] entries = dir.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                String path = entry.getAbsolutePath();
                if (shouldInclude(entry)) {
                    if (entry.isDirectory()) {
                        if (recursive) {
                            results.put(path, entry.length());
                            doScan(entry, results, true);
                        }
                    } else {
                        results.put(path, entry.length());
                    }
                }
            }
        }
    }


    /**
     * Determines if a file or directory should be included in the scan based on filters.
     *
     * <p>The include patterns are checked first, followed by the exclude patterns.</p>
     *
     * @param entry The file or directory to check.
     * @return {@code true} if the entry should be included, {@code false} otherwise.
     */
    protected boolean shouldInclude(File entry) {
        String name = entry.getName();
        
        // Includes list checked first
        if (!includes.isEmpty()) {
            boolean matched = false;
            for (Pattern p : includes) {
                if (p.matcher(name).matches()) {
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }

        // Excludes list checked second
        for (Pattern p : excludes) {
            if (p.matcher(name).matches()) {
                return false;
            }
        }

        return true;
    }


    /**
     * Retrieves the source directory path from the configuration.
     *
     * @return The configured directory path as a string.
     */
    @Override
    public String getSource() {
        return configuration.getAsString(ConfigTag.DIRECTORY);
    }


    /**
     * Represents a detected change in the directory.
     */
    protected static class Change {
        /** The absolute path to the file or directory. */
        final String path;
        /** The type of change (Created, Deleted, or Modified). */
        final String type;
        /** The size of the file before the change, if applicable. */
        final Long oldSize;
        /** The size of the file after the change, if applicable. */
        final Long newSize;

        /**
         * Constructor for a Change object.
         *
         * @param path    The absolute path of the change.
         * @param type    The type of change detected.
         * @param oldSize The previous size of the file.
         * @param newSize The current size of the file.
         */
        Change(String path, String type, Long oldSize, Long newSize) {
            this.path = path;
            this.type = type;
            this.oldSize = oldSize;
            this.newSize = newSize;
        }
    }

}
