# DirectoryChangeReader Documentation

## Overview
The `DirectoryChangeReader` is a component designed to monitor a specific directory structure for changes, including the creation, deletion, and modification of files and directories. It is built to be used within the RTW (Read-Transform-Write) framework.

When the reader is initialized, it performs an initial scan of the configured directory to establish a baseline state. Subsequent calls to its `read()` method will trigger a rescan to detect any differences from the previous state.

## Key Features
- **Baseline Establishment**: Scans the directory on startup so it only reports changes occurring after initialization.
- **Recursive Monitoring**: Monitors the entire directory tree from the root directory provided by default, but can be configured to scan only the top-level directory.
- **Change Detection**: Detects when files or directories are created or deleted, and when files are modified (detected by changes in file size).
- **Change Queuing**: If multiple changes are detected in a single scan (e.g., deleting a directory with multiple files), they are queued and returned one by one in subsequent `read()` calls without re-scanning until the queue is empty.
- **Configurable Polling**: If no changes are found, the reader waits for a configurable number of seconds before scanning again.
- **Blocking Read**: The `read()` method will not return until a change is detected, making it ideal for event-driven workflows.
- **Filtering**: Supports include and exclude regular expressions to filter which files or directories should trigger a change event.

## Configuration

The `DirectoryChangeReader` is configured via a JSON structure (or a `DataFrame` in code).

### Parameters

| Parameter | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `directory` | String | (Required) | The absolute or relative path of the directory to monitor. |
| `interval` | Integer | 6 | The number of seconds to wait between scans when no changes are detected. (Deprecated: `seconds`) |
| `recurse` | Boolean | true | Whether to scan subdirectories. If false, only the top-level directory is monitored. |
| `include` | String / Array | None | One or more regular expressions. If provided, only files/directories matching at least one expression are reported. |
| `exclude` | String / Array | None | One or more regular expressions. Files/directories matching any of these expressions are ignored. |

**Note**: The `include` list is checked first. If a file matches an include pattern (or if no include patterns are specified), it is then checked against the `exclude` list.

## Data Format

Each `read()` call returns a `DataFrame` containing the following fields:

- `filename`: The absolute path of the file or directory that changed.
- `change`: The type of change: `"Created"`, `"Deleted"`, or `"Modified"`.
- `previousSize`: The size in bytes before modification (Modified only).
- `currentSize`: The size in bytes after creation or modification.

---

## Use Cases and Examples

### 1. Monitoring a Landing Zone for New Data Files
In many ETL processes, a "landing zone" directory is used where external systems drop files. You may want to process only `.csv` files as they arrive.

**Configuration:**
```json
{
  "Reader": {
    "class": "coyote.commons.rtw.reader.DirectoryChangeReader",
    "directory": "/data/landing_zone",
    "interval": 5,
    "include": [ ".*\\.csv" ]
  }
}
```

### 2. Synchronizing Directory Deletions
If you are maintaining a mirror of a directory, you need to know when items are removed from the source to remove them from the target.

**Configuration:**
```json
{
  "Reader": {
    "class": "coyote.commons.rtw.reader.DirectoryChangeReader",
    "directory": "/shared/assets",
    "interval": 10
  }
}
```

### 3. Ignoring Temporary or System Files
When monitoring a directory, you often want to ignore hidden files (like `.DS_Store` on macOS) or temporary files created by editors (like `~filename`).

**Configuration:**
```json
{
  "Reader": {
    "class": "coyote.commons.rtw.reader.DirectoryChangeReader",
    "directory": "/home/user/documents",
    "exclude": [
      "^\\..*",
      ".*~$"
    ]
  }
}
```

### 4. Comprehensive Monitoring with Specific Exclusions
Monitor all changes in a project directory but ignore the `build` and `.git` directories to avoid noise.

**Configuration:**
```json
{
  "Reader": {
    "class": "coyote.commons.rtw.reader.DirectoryChangeReader",
    "directory": "/projects/my_app",
    "interval": 2,
    "exclude": [
      ".*[\\\\/]build[\\\\/].*",
      ".*[\\\\/]\\.git[\\\\/].*"
    ]
  }
}
```

### 5. Non-Recursive Monitoring
Monitor only the top-level directory for new or removed files, ignoring any changes inside subdirectories. If a directory is added, it is NOT reported. If the top-level directory itself is removed or added, it is reported.

**Configuration:**
```json
{
  "Reader": {
    "class": "coyote.commons.rtw.reader.DirectoryChangeReader",
    "directory": "/data/incoming",
    "recurse": false
  }
}
```

A common approach is to add Listeners to perform specific operations when reads are performed, such as logging, triggering workflows, or sending notifications. Some listeners trigger `onEnd(TransformContext)` and make use of any transforms that have been applied. A `FileListener` may perform some custom activity when a file or directory has meen created or deleted.


---

## Developer Information

### Implementation Details
The reader maintains an internal `Map<String, Long>` representing the absolute paths and current sizes of all files and directories in the monitored structure.

1. **`open()`**: Calls `scanDirectory()` to populate the initial `currentState`.
2. **`read()`**:
    - If `pendingChanges` queue is empty:
        - Calls `scanDirectory()` to get `newState`.
        - Compares `currentState` and `newState` to find deletions, creations, and modifications (size changes).
        - If no changes, sleeps for `scanInterval` and repeats.
        - If changes found, updates `currentState = newState`.
    - Polls the next `Change` from `pendingChanges`.
    - Returns a `DataFrame` representing the change, including `previousSize` and `currentSize` metadata.

### Class Reference
- **FQN**: `coyote.commons.rtw.reader.DirectoryChangeReader`
- **Extends**: `coyote.commons.rtw.reader.AbstractFrameReader`
- **Fields**:
    - `FILENAME_FIELD`: "filename"
    - `CHANGE_FIELD`: "change"
    - `CREATED`: "Created"
    - `DELETED`: "Deleted"
    - `MODIFIED`: "Modified"
    - `PREVIOUS_SIZE`: "previousSize"
    - `CURRENT_SIZE`: "currentSize"
