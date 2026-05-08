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
 *   <li>{@code seconds} - the number of seconds to wait between scans when no changes are found (default: 6).</li>
 *   <li>{@code recurse} - boolean flag indicating if the reader should scan subdirectories (default: true).</li>
 *   <li>{@code include} - a list of regex expressions; only matching files/dirs generate dataframes. The include list is checked first.</li>
 *   <li>{@code exclude} - a list of regex expressions; matching files/dirs are ignored. The exclude list is checked after the include list.</li>
 * </ul>
 * </p>
 */
public class DirectoryChangeReader extends AbstractFrameReader implements FrameReader {

    /** Field name for the absolute filename of the change. */
    public static final String FILENAME_FIELD = "filename";
    /** Field name for the type of change ("Created" or "Deleted"). */
    public static final String CHANGE_FIELD = "change";

    public static final String CREATED = "Created";
    public static final String DELETED = "Deleted";

    private static final int DEFAULT_INTERVAL = 6;

    private File directoryToMonitor;
    private int scanInterval = DEFAULT_INTERVAL;
    private boolean recurse = true;
    private final List<Pattern> includes = new ArrayList<>();
    private final List<Pattern> excludes = new ArrayList<>();

    private Set<String> currentState = new HashSet<>();
    private final Queue<Change> pendingChanges = new LinkedList<>();

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

        if (configuration.containsIgnoreCase(ConfigTag.SECONDS)) {
            try {
                scanInterval = (int) configuration.getLong(ConfigTag.SECONDS);
            } catch (Exception e) {
                Log.warn("DirectoryChangeReader: Invalid scan interval, using default " + DEFAULT_INTERVAL);
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

    @Override
    public DataFrame read(final TransactionContext context) {
        while (pendingChanges.isEmpty()) {
            Set<String> newState = scanDirectory(directoryToMonitor);
            
            // Detect deletions
            for (String path : currentState) {
                if (!newState.contains(path)) {
                    pendingChanges.add(new Change(path, DELETED));
                }
            }

            // Detect creations
            for (String path : newState) {
                if (!currentState.contains(path)) {
                    pendingChanges.add(new Change(path, CREATED));
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

        super.recordCounter++;
        return frame;
    }

    @Override
    public boolean eof() {
        return false; // This reader is intended to run indefinitely
    }

    private void parsePatterns(String tag, List<Pattern> list) {
        for (DataField field : configuration.getFields()) {
            if (field.getName() != null && field.getName().equalsIgnoreCase(tag)) {
                addPattern(field.getStringValue(), list);
            }
        }
    }

    private void addPattern(String regex, List<Pattern> list) {
        try {
            list.add(Pattern.compile(regex));
        } catch (Exception e) {
            Log.error("DirectoryChangeReader: Invalid regex pattern: " + regex);
        }
    }

    private Set<String> scanDirectory(File root) {
        Set<String> results = new HashSet<>();
        doScan(root, results, recurse);
        return results;
    }

    private void doScan(File dir, Set<String> results, boolean recursive) {
        File[] entries = dir.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                String path = entry.getAbsolutePath();
                if (shouldInclude(entry)) {
                    if (entry.isDirectory()) {
                        if (recursive) {
                            results.add(path);
                            doScan(entry, results, true);
                        }
                    } else {
                        results.add(path);
                    }
                }
            }
        }
    }

    private boolean shouldInclude(File entry) {
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

    @Override
    public String getSource() {
        return configuration.getAsString(ConfigTag.DIRECTORY);
    }

    private static class Change {
        final String path;
        final String type;

        Change(String path, String type) {
            this.path = path;
            this.type = type;
        }
    }
}
