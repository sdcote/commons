# Transform Engine (RTW Framework)

## Overview
The Read-Transform-Write (RTW) framework is a powerful and flexible component-based system designed for data processing, migration, and integration tasks. At its core is the `TransformEngine`, which orchestrates the flow of data from a source (Reader) through various processing stages (Filters, Validators, Transformers, Mappers) to one or more destinations (Writers, Aggregators).

The `coyote.commons.rtw.TransformEngine` interface defines the contract for all transform engines, while `coyote.commons.rtw.AbstractTransformEngine` provides the base implementation that handles the life cycle, configuration, and execution logic.

## Life Cycle
The `TransformEngine` follows a well-defined life cycle when the `run()` method is called:

1.  **Context Initialization**: The `TransformContext` is created and initialized. This context serves as a shared memory space for all components during the engine's execution.
2.  **Pre-processing**: All configured `PreProcess` tasks are executed in the order they were defined.
3.  **Component Initialization**: Core components are initialized in the following order:
    -   Reader
    -   Mapper
    -   Aggregators
    -   Writers
    -   Filters
    -   Validators
    -   Transformers
4.  **Pre-loading**: If a pre-loader is configured, it reads data to prime components (e.g., loading cache or historical data).
5.  **Main Read-Transform-Write Loop**: The engine enters a loop that continues until the Reader reaches EOF or a fatal error occurs:
    -   **Read**: The `Reader` retrieves a `DataFrame` from the source.
    -   **Filter**: `Filters` determine if the frame should be processed or skipped.
    -   **Validate**: `Validators` check the data against business rules.
    -   **Transform**: `Transformers` modify the data in the frame.
    -   **Map**: The `Mapper` translates the source frame into a target frame (if configured).
    -   **Write/Aggregate**: Data is sent to `Writers` or `Aggregators`.
6.  **Post-processing**: After the main loop completes, `PostProcess` tasks are executed.
7.  **Shutdown**: All components are closed, and resources are released.

## Configuration Sections
The engine is typically configured using a JSON or XML structure. Each section represents a specific component or behavior.

### Reader (`Reader`)
The Reader is responsible for pulling data into the engine. Only one Reader can be configured for a `TransformEngine`.
-   **Interface**: `coyote.commons.rtw.FrameReader`
-   **Common Implementations**: `CSVReader`, `JdbcReader`, `DirectoryChangeReader`.

### Writer (`Writer`)
Writers send the processed data to a destination. Multiple Writers can be configured.
-   **Interface**: `coyote.commons.rtw.FrameWriter`
-   **Common Implementations**: `CSVWriter`, `JdbcWriter`, `ConsoleWriter`.

### Listener (`Listener`)
Listeners observe events in the engine's life cycle (e.g., `onRead`, `onWrite`, `onError`) and can take actions based on those events.
-   **Interface**: `coyote.commons.rtw.context.ContextListener`

### Mapper (`Mapper`)
The Mapper defines how fields from the input (source) frame are mapped to the output (target) frame.
-   **Interface**: `coyote.commons.rtw.FrameMapper`
-   **Default**: `DefaultFrameMapper` which supports field renaming and basic transformations.

### Task (`PreProcess` / `PostProcess`)
Tasks are discrete operations that run before or after the main processing loop. Examples include moving files, sending emails, or clearing database tables.
-   **Interface**: `coyote.commons.rtw.TransformTask`

### Validator (`Validate`)
Validators ensure that the data meets specific criteria. If a validator fails, the engine can be configured to log the error, skip the record, or halt execution.
-   **Interface**: `coyote.commons.rtw.FrameValidator`

### Filter (`Filter`)
Filters allow for conditional processing. If a filter rejects a frame, it is dropped from the current transaction and not passed to validators, transformers, or writers.
-   **Interface**: `coyote.commons.rtw.FrameFilter`

### Transformer (`Transform`)
Transformers perform in-place modifications to the `DataFrame` during the processing loop.
-   **Interface**: `coyote.commons.rtw.FrameTransform`

### Aggregator (`Aggregator`)
Aggregators collect data over multiple frames to perform operations like summing values or grouping data before writing.
-   **Interface**: `coyote.commons.rtw.FrameAggregator`

## Use Cases

### 1. Data Migration (ETL)
Moving data from a legacy SQL database to a set of CSV files for backup or analysis.
-   **Reader**: `JdbcReader`
-   **Mapper**: Renames database columns to business-friendly headers.
-   **Writer**: `CSVWriter`

### 2. Real-time File Monitoring
Watching a directory for new log files and indexing their content into a search engine.
-   **Reader**: `DirectoryChangeReader`
-   **Transformer**: Parses log lines into structured fields.
-   **Writer**: `ElasticSearchWriter` (hypothetical)

### 3. Data Validation and Cleaning
Reading a messy data file, validating required fields, and filtering out incomplete records.
-   **Reader**: `CSVReader`
-   **Validator**: Checks for non-null email addresses.
-   **Filter**: Removes records where the "status" is "deleted".
-   **Writer**: `JdbcWriter`

## File and Directory Resolution

The `TransformEngine` uses a standardized approach for resolving relative file and directory paths. To promote isolation and portability, the **Job Directory** is used as the default parent for all relative paths.

When a component (Reader, Writer, Task, etc.) encounters a relative path in its configuration, it resolves it using the following search order:

1.  **Job Directory**: The dedicated workspace for the current job (e.g., `snap/wrk/JobName/`).
2.  **Configuration Location**: The directory where the job's configuration file is located.
3.  **Work Directory**: The general application working directory (usually `app.home/wrk`).
4.  **Current Working Directory**: The directory from which the process was started.

This hierarchy allows jobs to be "sandboxed" within their own directory while still providing access to shared resources or the current environment if needed.

## Developer Information
To implement your own component, you should extend the appropriate abstract class or implement the interface. Most components benefit from extending `coyote.commons.rtw.AbstractConfigurableComponent` to gain access to standard configuration parsing and logging.

-   **Base Interface**: `coyote.commons.rtw.TransformEngine`
-   **Base Implementation**: `coyote.commons.rtw.AbstractTransformEngine`
-   **Core Data Structure**: `coyote.commons.dataframe.DataFrame`
