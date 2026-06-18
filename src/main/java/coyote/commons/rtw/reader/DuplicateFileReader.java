/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.reader;

import coyote.commons.Glob;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.FrameReader;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.CRC32C;
import java.util.zip.CheckedInputStream;

/**
 * Reader that finds duplicate files based on their CRC32C checksum.
 *
 * <p>This reader scans one or more directories for files, calculates their
 * CRC32C checksum, and groups them by checksum. Any groups containing more than
 * one file are considered duplicates and are returned by the {@code read()}
 * method.</p>
 *
 * <p>Configuration:
 * <ul>
 *   <li>{@code directory} - a single directory name or an array of directory
 *     names to scan. Directory names can be regular expressions or GLOB
 *     patterns.</li>
 * </ul>
 * </p>
 */
public class DuplicateFileReader extends AbstractFrameReader implements FrameReader {

    /** The field name for the array of duplicate filenames. */
    public static final String DUPLICATES_FIELD = "Duplicates";

    /** The field name for the number of duplicates in the group. */
    public static final String COUNT_FIELD = "Count";

    /** The field name for the checksum of the duplicate group. */
    public static final String CHECKSUM_FIELD = "Checksum";

    private static final int PROGRESS_INTERVAL = 1000;
    private static final int STREAM_BUFFER_LENGTH = 1024 * 8;

    private final List<String> directorySpecs = new ArrayList<>();
    private final Queue<DataFrame> duplicateGroups = new LinkedList<>();
    private boolean eof = false;


    /**
     * @see coyote.commons.rtw.reader.AbstractFrameReader#open(coyote.commons.rtw.context.TransformContext)
     */
    @Override
    public void open(TransformContext context) {
        super.open(context);

        // Get the directory or directories to scan
        Object dirObj = getConfiguration().getObject(ConfigTag.DIRECTORY);
        if (dirObj instanceof String) {
            directorySpecs.add((String) dirObj);
        } else if (dirObj instanceof List) {
            for (Object item : (List<?>) dirObj) {
                if (item != null) {
                    directorySpecs.add(item.toString());
                }
            }
        } else if (dirObj instanceof DataFrame) {
            DataFrame df = (DataFrame) dirObj;
            for (int i = 0; i < df.getFieldCount(); i++) {
                directorySpecs.add(df.getField(i).getStringValue());
            }
        }

        if (directorySpecs.isEmpty()) {
            context.setError("DuplicateFileReader: No directory specified in configuration.");
            return;
        }

        scanForDuplicates();
    }


    /**
     * Scan the configured directories and find duplicates.
     */
    private void scanForDuplicates() {
        Log.info("DuplicateFileReader: Scanning for duplicates...");
        Map<String, List<String>> checksumMap = new HashMap<>();
        long fileCount = 0;

        Set<File> filesToProcess = discoverFiles();

        for (File file : filesToProcess) {
            String checksum = getCRC32CChecksum(file);
            if (checksum != null) {
                checksumMap.computeIfAbsent(checksum, k -> new ArrayList<>()).add(file.getAbsolutePath());
            }

            fileCount++;
            if (fileCount % PROGRESS_INTERVAL == 0) {
                Log.info("DuplicateFileReader: Scanned " + fileCount + " files...");
            }
        }

        Log.info("DuplicateFileReader: Scan complete. Total files scanned: " + fileCount);

        int duplicateCount = 0;
        int groupCount = 0;

        for (Map.Entry<String, List<String>> entry : checksumMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                groupCount++;
                duplicateCount += entry.getValue().size();

                DataFrame frame = new DataFrame();
                frame.add(CHECKSUM_FIELD, entry.getKey());
                frame.add(COUNT_FIELD, entry.getValue().size());
                
                DataFrame dups = new DataFrame();
                for (String filename : entry.getValue()) {
                    dups.add(filename);
                }
                frame.add(DUPLICATES_FIELD, dups);
                
                duplicateGroups.add(frame);
            }
        }

        Log.info("DuplicateFileReader: Detected " + duplicateCount + " duplicate files in " + groupCount + " groups.");
        
        if (duplicateGroups.isEmpty()) {
            eof = true;
        }
    }


    /**
     * Discover all files that match the configured directory specs.
     *
     * @return a set of unique files found.
     */
    private Set<File> discoverFiles() {
        Set<File> discoveredFiles = new HashSet<>();
        for (String spec : directorySpecs) {
            if (spec == null || spec.trim().isEmpty()) continue;

            // Handle relative paths by making them absolute relative to the current working directory
            File searchDir;
            String pattern = null;

            // Simple heuristic to separate path from pattern if it looks like a glob or regex
            if (spec.contains("*") || spec.contains("?") || spec.contains("[") || spec.contains("{") || spec.contains("(")) {
                // Find the last separator before the first wildcard
                int firstWildcard = -1;
                String[] wildcards = {"*", "?", "[", "{", "("};
                for (String w : wildcards) {
                    int idx = spec.indexOf(w);
                    if (idx != -1 && (firstWildcard == -1 || idx < firstWildcard)) {
                        firstWildcard = idx;
                    }
                }

                int lastSeparator = -1;
                if (firstWildcard != -1) {
                    lastSeparator = spec.lastIndexOf(File.separatorChar, firstWildcard);
                    if (lastSeparator == -1 && File.separatorChar != '/') {
                        lastSeparator = spec.lastIndexOf('/', firstWildcard);
                    }
                }

                if (lastSeparator != -1) {
                    searchDir = new File(spec.substring(0, lastSeparator));
                    pattern = spec.substring(lastSeparator + 1);
                } else {
                    searchDir = new File(".");
                    pattern = spec;
                }
            } else {
                searchDir = new File(spec);
            }

            if (searchDir.exists() && searchDir.isDirectory()) {
                scanDir(searchDir, pattern, discoveredFiles);
            } else if (pattern != null) {
                // Maybe the whole spec is a pattern or it's just not found as a dir
                Log.debug("DuplicateFileReader: Search directory not found: " + searchDir.getAbsolutePath() + " (spec: " + spec + ")");
            }
        }
        return discoveredFiles;
    }


    /**
     * Recursively scan a directory for files matching a pattern.
     *
     * @param dir     The directory to scan.
     * @param pattern The pattern to match filenames against.
     * @param results The set to populate with discovered files.
     */
    private void scanDir(File dir, String pattern, Set<File> results) {
        File[] entries = dir.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                if (entry.isDirectory()) {
                    scanDir(entry, pattern, results);
                } else {
                    if (pattern == null || matches(entry.getName(), pattern)) {
                        results.add(entry);
                    }
                }
            }
        }
    }


    /**
     * Check if a filename matches a pattern (GLOB or Regex).
     *
     * @param name    The filename to check.
     * @param pattern The pattern (GLOB or Regex).
     * @return true if it matches, false otherwise.
     */
    private boolean matches(String name, String pattern) {
        // Try GLOB first
        if (new Glob(pattern).isFileMatched(name)) {
            return true;
        }
        // Try Regex
        try {
            if (name.matches(pattern)) {
                return true;
            }
        } catch (Exception ignored) {
            // Not a valid regex or other issue
        }
        return false;
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
                // just read to update the checksum
            }
            return Long.toHexString(cis.getChecksum().getValue());
        } catch (IOException e) {
            Log.warn("DuplicateFileReader: Could not calculate checksum for " + file.getAbsolutePath() + " - " + e.getMessage());
            return null;
        }
    }


    /**
     * @see coyote.commons.rtw.reader.AbstractFrameReader#read(coyote.commons.rtw.context.TransactionContext)
     */
    @Override
    public DataFrame read(TransactionContext context) {
        DataFrame result = duplicateGroups.poll();
        if (result != null) {
            recordCounter++;
            if (duplicateGroups.isEmpty()) {
                eof = true;
            }
        } else {
            eof = true;
        }
        return result;
    }


    /**
     * @see coyote.commons.rtw.FrameReader#eof()
     */
    @Override
    public boolean eof() {
        return eof;
    }

}
