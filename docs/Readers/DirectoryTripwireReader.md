# DirectoryTripwireReader Documentation

## Overview
The `DirectoryTripwireReader` is an advanced directory monitoring component for the RTW (Read-Transform-Write) framework. It extends the `DirectoryChangeReader` by adding the ability to detect file modifications via checksums, in addition to the file size detection provided by the base class.

When a file is modified, the reader detects the change by comparing the file size (inherited from `DirectoryChangeReader`) and a CRC32C (Castagnoli) checksum. This makes it ideal for security auditing, integrity checking, or processing files that may be updated in place without changing their size.

## Key Features
- **All DirectoryChangeReader Features**: Inherits recursive monitoring, filtering (include/exclude), and configurable polling intervals.
- **Modification Detection**: Detects "Modified" events when a file's content or size changes.
- **Rich Metadata**: Provides previous and current checksums and file sizes for modified files.
- **CRC32C Checksums**: Uses the CRC32C (Castagnoli) algorithm to efficiently detect content changes. This algorithm is faster and less prone to collisions than standard CRC32.

## Configuration

The `DirectoryTripwireReader` uses the same configuration parameters as `DirectoryChangeReader`.

### Parameters

| Parameter | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `directory` | String | (Required) | The absolute or relative path of the directory to monitor. |
| `interval` | Integer | 6 | The number of seconds to wait between scans when no changes are detected. (Deprecated: `seconds`) |
| `recurse` | Boolean | true | Whether to scan subdirectories. |
| `include` | String / Array | None | Regular expressions for files/directories to monitor. |
| `exclude` | String / Array | None | Regular expressions for files/directories to ignore. |

## Data Format

Each `read()` call returns a `DataFrame` containing:

- `filename`: The absolute path of the file or directory.
- `change`: The type of change: `"Created"`, `"Deleted"`, or `"Modified"`.
- `previousChecksum`: The hex CRC32C checksum before modification (Modified only).
- `currentChecksum`: The hex CRC32C checksum after creation or modification.
- `previousSize`: The size in bytes before modification (Modified only).
- `currentSize`: The size in bytes after creation or modification.

---

## Use Cases and Examples

### 1. Security Auditing of Configuration Files
Monitor critical system configuration files for any unauthorized changes.

**Configuration:**
```json
{
  "Reader": {
    "class": "coyote.commons.rtw.reader.DirectoryTripwireReader",
    "directory": "/etc",
    "interval": 60,
    "include": [ ".*\\.conf", ".*\\.ini" ]
  }
}
```

### 2. Processing Incremental Updates
If a system appends data to a log file or updates a status file, the `DirectoryTripwireReader` can trigger a process to handle the new data.

**Configuration:**
```json
{
  "Reader": {
    "class": "coyote.commons.rtw.reader.DirectoryTripwireReader",
    "directory": "/var/log/app",
    "include": [ "transaction\\.log" ]
  }
}
```

### 3. Data Integrity Monitoring
Monitor a data repository to ensure that once files are written, they are not tampered with. Any "Modified" event can trigger an alert or a recovery process.

**Configuration:**
```json
{
  "Reader": {
    "class": "coyote.commons.rtw.reader.DirectoryTripwireReader",
    "directory": "/data/archive",
    "recurse": true
  },
  "Listener": {
    "class": "coyote.commons.rtw.listener.AlertListener",
    "condition": "working.change == 'Modified'"
  }
}
```

---

## Developer Information

### Implementation Details
The `DirectoryTripwireReader` maintains a map of file paths to `FileInfo` objects. Each `FileInfo` contains the file's path, whether it's a directory, its size, and its CRC32C checksum.

1. **`open()`**: Performs an initial scan to populate the baseline state map.
2. **`read()`**:
    - Scans the directory and compares the new state with the baseline.
    - Files not in the new state are queued as `Deleted`.
    - Files not in the baseline are queued as `Created`.
    - Files in both but with different sizes or checksums are queued as `Modified`.
    - Updates the baseline after all changes in a scan are detected.

### Class Reference
- **FQN**: `coyote.commons.rtw.reader.DirectoryTripwireReader`
- **Extends**: `coyote.commons.rtw.reader.DirectoryChangeReader`
- **Fields**:
    - `MODIFIED`: "Modified"
    - `PREVIOUS_CHECKSUM`: "previousChecksum"
    - `CURRENT_CHECKSUM`: "currentChecksum"
    - `PREVIOUS_SIZE`: "previousSize"
    - `CURRENT_SIZE`: "currentSize"
