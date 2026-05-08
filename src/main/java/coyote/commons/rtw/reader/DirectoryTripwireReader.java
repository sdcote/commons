/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.reader;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.rtw.ConfigTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.CRC32C;
import java.util.zip.CheckedInputStream;

/**
 * Reader that monitors a directory for changes, including file modifications.
 *
 * <p>In addition to "Created" and "Deleted" changes, this reader also detects
 * "Modified" changes by comparing file sizes and CRC32C checksums.</p>
 *
 * <p>Comparing checksums is a resource-intensive process. For large directory
 * structures, it is best to increase the check interval to give the reader
 * time to scan and calculate CRC32C checksums for all the files. Additionally,
 * it is best if this reader is configured to point to a single directory and
 * not one with multiple subdirectories or many files, if possible.</p>
 *
 * <p>Each returned frame contains:
 * <ul>
 *   <li>{@code filename} - the absolute path of the changed file or directory</li>
 *   <li>{@code change} - the type of change ("Created", "Deleted", or "Modified")</li>
 *   <li>{@code previousChecksum} - the hexadecimal CRC32C checksum before the change (for "Modified")</li>
 *   <li>{@code currentChecksum} - the hexadecimal CRC32C checksum after the change (for "Modified" or "Created")</li>
 *   <li>{@code previousSize} - the file size in bytes before the change (for "Modified")</li>
 *   <li>{@code currentSize} - the file size in bytes after the change (for "Modified" or "Created")</li>
 * </ul>
 * </p>
 */
public class DirectoryTripwireReader extends DirectoryChangeReader {

    public static final String MODIFIED = "Modified";
    public static final String PREVIOUS_CHECKSUM = "previousChecksum";
    public static final String CURRENT_CHECKSUM = "currentChecksum";
    public static final String PREVIOUS_SIZE = "previousSize";
    public static final String CURRENT_SIZE = "currentSize";
    private static final int STREAM_BUFFER_LENGTH = 1024;
    private Map<String, FileInfo> currentStateMap = new HashMap<>();
    private final Queue<TripwireChange> tripwirePendingChanges = new LinkedList<>();



    /**
     * @see coyote.commons.rtw.reader.DirectoryChangeReader#open(coyote.commons.rtw.context.TransformContext)
     */
    @Override
    public void open(final TransformContext context) {
        super.open(context);
        if (context.isInError()) return;

        // Initialize our state map from the super class's initial scan if possible,
        // but we need more info (size/checksum) than just paths.
        // So we perform our own initial scan.
        currentStateMap = scanDirectoryWithInfo(getDirectoryToMonitor());
        Log.debug("DirectoryTripwireReader: Initialized with " + currentStateMap.size() + " entries.");
    }


    /**
     * Resolves the directory to monitor from the configuration.
     *
     * @return the File object representing the directory to monitor.
     */
    private File getDirectoryToMonitor() {
        String source = getSource();
        File dir = new File(source);
        if (!dir.isAbsolute()) {
            dir = coyote.commons.rtw.RTW.resolveFile(dir, getContext());
        }
        return dir;
    }


    /**
     * @see coyote.commons.rtw.reader.DirectoryChangeReader#read(coyote.commons.rtw.context.TransactionContext)
     */
    @Override
    public DataFrame read(final TransactionContext context) {
        while (tripwirePendingChanges.isEmpty()) {
            Map<String, FileInfo> newStateMap = scanDirectoryWithInfo(getDirectoryToMonitor());

            // Detect deletions
            for (String path : currentStateMap.keySet()) {
                if (!newStateMap.containsKey(path)) {
                    tripwirePendingChanges.add(new TripwireChange(path, DELETED, currentStateMap.get(path), null));
                }
            }

            // Detect creations and modifications
            for (Map.Entry<String, FileInfo> entry : newStateMap.entrySet()) {
                String path = entry.getKey();
                FileInfo newInfo = entry.getValue();
                FileInfo oldInfo = currentStateMap.get(path);

                if (oldInfo == null) {
                    tripwirePendingChanges.add(new TripwireChange(path, CREATED, null, newInfo));
                } else if (!newInfo.isDirectory && (newInfo.size != oldInfo.size || !Objects.equals(newInfo.checksum, oldInfo.checksum))) {
                    tripwirePendingChanges.add(new TripwireChange(path, MODIFIED, oldInfo, newInfo));
                }
            }

            if (tripwirePendingChanges.isEmpty()) {
                try {
                    Thread.sleep(getScanInterval() * 1000L);
                } catch (InterruptedException e) {
                    Log.info("DirectoryTripwireReader: Interrupted while waiting for changes.");
                    Thread.currentThread().interrupt();
                    return null;
                }
            } else {
                currentStateMap = newStateMap;
            }
        }

        TripwireChange change = tripwirePendingChanges.poll();
        DataFrame frame = new DataFrame();
        frame.add(FILENAME_FIELD, change.path);
        frame.add(CHANGE_FIELD, change.type);

        if (change.oldInfo != null) {
            if (change.oldInfo.checksum != null) frame.add(PREVIOUS_CHECKSUM, change.oldInfo.checksum);
            frame.add(PREVIOUS_SIZE, change.oldSize());
        }

        if (change.newInfo != null) {
            if (change.newInfo.checksum != null) frame.add(CURRENT_CHECKSUM, change.newInfo.checksum);
            frame.add(CURRENT_SIZE, change.newSize());
        }

        super.recordCounter++;
        return frame;
    }


    /**
     * Gets the scan interval in seconds from the configuration.
     *
     * @return the scan interval in seconds.
     */
    private int getScanInterval() {
        if (configuration.containsIgnoreCase(ConfigTag.SECONDS)) {
            return (int) configuration.getLong(ConfigTag.SECONDS);
        }
        return 6;
    }


    /**
     * Scans the given directory and returns a map of file paths to FileInfo objects.
     *
     * @param root the directory to scan.
     * @return a map of absolute file paths to FileInfo objects.
     */
    private Map<String, FileInfo> scanDirectoryWithInfo(File root) {
        Map<String, FileInfo> results = new HashMap<>();
        doScanWithInfo(root, results, isRecursive());
        return results;
    }


    /**
     * Checks if the scan should be recursive.
     *
     * @return true if the scan should be recursive, false otherwise.
     */
    private boolean isRecursive() {
        if (configuration.containsIgnoreCase(ConfigTag.RECURSE)) {
            return configuration.getBoolean(ConfigTag.RECURSE);
        }
        return true;
    }


    /**
     * Recursively scans the directory and populates the results map.
     *
     * @param dir       the directory to scan.
     * @param results   the map to populate with results.
     * @param recursive true to scan subdirectories, false otherwise.
     */
    private void doScanWithInfo(File dir, Map<String, FileInfo> results, boolean recursive) {
        File[] entries = dir.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                if (shouldInclude(entry)) {
                    String path = entry.getAbsolutePath();
                    boolean isDir = entry.isDirectory();
                    long size = entry.length();
                    String checksum = null;
                    if (!isDir) {
                        checksum = getCRC32CChecksum(entry);
                    }
                    results.put(path, new FileInfo(path, isDir, size, checksum));

                    if (isDir && recursive) {
                        doScanWithInfo(entry, results, true);
                    }
                }
            }
        }
    }


    /**
     * Calculates the CRC32C checksum of the given file.
     *
     * @param file the file for which to calculate the checksum.
     * @return the hexadecimal representation of the CRC32C checksum, or null if an error occurred.
     */
    private String getCRC32CChecksum(File file) {
        try (InputStream fis = new FileInputStream(file); CheckedInputStream cis = new CheckedInputStream(fis, new CRC32C())) {
            byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
            while (cis.read(buffer) >= 0) {
            }
            return Long.toHexString(cis.getChecksum().getValue());
        } catch (IOException e) {
            Log.warn("DirectoryTripwireReader: Could not calculate checksum for " + file.getAbsolutePath() + " - " + e.getMessage());
            return null;
        }
    }


    /**
     * @see coyote.commons.rtw.reader.DirectoryChangeReader#eof()
     */
    @Override
    public boolean eof() {
        return false;
    }


    /**
     * Internal class to hold information about a file at a specific point in time.
     */
    private static class FileInfo {
        final String path;
        final boolean isDirectory;
        final long size;
        final String checksum;

        /**
         * Constructor for FileInfo.
         *
         * @param path        the absolute path of the file.
         * @param isDirectory true if the entry is a directory.
         * @param size        the size of the file in bytes.
         * @param checksum    the CRC32C checksum of the file.
         */
        FileInfo(String path, boolean isDirectory, long size, String checksum) {
            this.path = path;
            this.isDirectory = isDirectory;
            this.size = size;
            this.checksum = checksum;
        }
    }


    /**
     * Internal class to represent a detected change.
     */
    private static class TripwireChange {
        final String path;
        final String type;
        final FileInfo oldInfo;
        final FileInfo newInfo;

        /**
         * Constructor for TripwireChange.
         *
         * @param path    the absolute path of the changed file.
         * @param type    the type of change.
         * @param oldInfo the FileInfo before the change (may be null for creations).
         * @param newInfo the FileInfo after the change (may be null for deletions).
         */
        TripwireChange(String path, String type, FileInfo oldInfo, FileInfo newInfo) {
            this.path = path;
            this.type = type;
            this.oldInfo = oldInfo;
            this.newInfo = newInfo;
        }

        /**
         * @return the size of the file before the change, or null if not applicable.
         */
        Long oldSize() {
            return oldInfo != null ? oldInfo.size : null;
        }

        /**
         * @return the size of the file after the change, or null if not applicable.
         */
        Long newSize() {
            return newInfo != null ? newInfo.size : null;
        }
    }
}
