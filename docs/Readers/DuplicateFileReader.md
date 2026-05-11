# DuplicateFileReader

The `DuplicateFileReader` is a component for the RTW (Read-Transform-Write) framework designed to identify duplicate files across one or more directories using CRC32C checksums.

## Overview

Scanning occurs when the reader is opened. It traverses the specified directories, computes a CRC32C checksum for each file found, and groups files with identical checksums. These groups are then returned as individual DataFrames through the `read()` method.

### Key Features

- **Multi-directory Support**: Scan a single directory or an array of directories.
- **Pattern Matching**: Supports both GLOB patterns and Regular Expressions for directory and file selection.
- **Efficient Progress Logging**: Generates INFO logs every 1000 files to keep the operator informed of progress.
- **Summary Reporting**: Provides a final count of files scanned and duplicates detected.

## Configuration

The `DuplicateFileReader` is configured using the `directory` tag. This can be a single string or an array of strings.

| Tag | Type | Description |
| :--- | :--- | :--- |
| `directory` | String or Array | The directory path(s) to scan. Can include GLOB or Regex patterns. |

### Configuration Examples

#### Single Directory
```json
{
  "Reader": {
    "class": "DuplicateFileReader",
    "directory": "/home/user/documents"
  }
}
```

#### Multiple Directories
```json
{
  "Reader": {
    "class": "DuplicateFileReader",
    "directory": [
      "/var/data/archive",
      "/mnt/backup/files"
    ]
  }
}
```

#### Using GLOB Patterns
Search for all PDF files in any subdirectory of `reports`:
```json
{
  "Reader": {
    "class": "DuplicateFileReader",
    "directory": "reports/**/*.pdf"
  }
}
```

#### Using Regular Expressions
Match directories starting with `temp` followed by digits:
```json
{
  "Reader": {
    "class": "DuplicateFileReader",
    "directory": "temp[0-9]+/"
  }
}
```

## Output Format

Each record returned by `read()` represents a group of duplicate files and contains the following fields:

- `Checksum`: The hexadecimal CRC32C checksum common to all files in the group.
- `Count`: The number of files in this duplicate group.
- `Duplicates`: A child DataFrame (array-like) containing the absolute paths of all duplicate files.

## Use Cases

### 1. Data Deduplication
Clean up redundant backups by identifying identical files across multiple storage volumes.

**Configuration:**
```json
{
  "Reader": {
    "class": "DuplicateFileReader",
    "directory": [ "/vol1/backups", "/vol2/backups" ]
  }
}
```

### 2. Selective Media Cleanup
Find duplicate images across various "temp" and "tmp" directories using a GLOB pattern.

**Configuration:**
```json
{
  "Reader": {
    "class": "DuplicateFileReader",
    "directory": [ "**/temp/*.jpg", "**/tmp/*.jpg" ]
  }
}
```

### 3. Log Analysis
Identify duplicate log segments that might have been copied to multiple investigation folders.

**Configuration:**
```json
{
  "Reader": {
    "class": "DuplicateFileReader",
    "directory": "investigations/case_*/logs/*.log"
  }
}
```

## Developer Notes

The `DuplicateFileReader` extends `AbstractFrameReader`. It uses `java.util.zip.CRC32C` for checksum calculation, which is faster and provides better error detection than standard CRC32.

The reader performs all scanning in the `open()` method to ensure all duplicates are identified before the first call to `read()`. If many files are scanned, ensure the Java Heap has enough memory to store the file paths and checksum map.
