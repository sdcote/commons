### Refactoring Analysis Report: Coyote Snap Jobs

This report evaluates the current architecture of `coyote.BootStrap`, `coyote.RtwJob`, `coyote.DaemonJob`, and the `coyote.commons.snap` package, focusing on identifying bottlenecks, poor reuse, and providing structural recommendations for a uniform job pattern.

---

### 1. Potential Bottlenecks

*   **Synchronous Initialization**: Both `BootStrap` and `DaemonJob` perform configuration and initialization (including logging setup and directory creation) synchronously on the main thread. While acceptable for single jobs, in `DaemonJob` with many sub-jobs, this can delay the startup of the management interface (HTTP server).
*   **Brittle Configuration Parsing**: The current mechanism uses the *name* of the first field in a JSON object as the class name (e.g., `{"RtwJob":{...}}`). If the configuration file is re-ordered or contains metadata fields before the class field, the loader fails. This also makes programmatic configuration difficult.
*   **Reflection Overhead in Loops**: `DaemonJob` reinstantiates or re-evaluates class names frequently if configurations are reloaded, using `Class.forName` and `Constructor.newInstance` repeatedly without caching.
*   **Blocking Scheduler**: `DaemonJob.start()` blocks the calling thread by running the scheduler. This forces the use of external threads or OS-level management if multiple Daemons or other processes need to run in the same JVM.

### 2. Poor Reuse & Redundancy

*   **Duplicated Loading Logic**: `BootStrap.loadJob` and `DaemonJob.loadJob` share ~80% of the same logic: resolving class names, checking for fully qualified names, instantiating via reflection, and calling `configure`. However, they are implemented separately, leading to subtle differences (e.g., `DaemonJob` uses `ClasspathUtil.resolve`, while `BootStrap` assumes the local package).
*   **Inconsistent Directory Management**: `RtwJob` contains extensive logic for `determineHomeDirectory` and `determineWorkDirectory`. While `AbstractSnapJob` provides `getAppHome()`, it does not enforce the determination logic, leading to `DaemonJob` and other potential jobs missing standardized directory resolution.
*   **Redundant Symbol Table Handling**: Both `AbstractSnapJob` and `RtwJob` manually populate symbol tables with environment variables and system properties. This logic should be centralized in the base class or a configuration utility.
*   **Logging Coupling**: `AbstractSnapJob.initLogging` is complex and tightly coupled to the `app.home` system property. It contains hardcoded paths like `/snap/log`, which might not be appropriate for all job types.

### 3. Structural Recommendations

To achieve a clear, simple, and reusable pattern for configuring and running any number of jobs, the following structural changes are recommended:

#### A. Centralized Job Factory
Create a `JobFactory` (or `JobLoader`) that encapsulates the reflection and initialization logic.
*   **Standardize Config**: Use a consistent field (e.g., `"class": "coyote.RtwJob"`) instead of relying on field names.
*   **Unified Resolution**: Move the class resolution logic from `DaemonJob` (using `ClasspathUtil`) into this factory so all jobs benefit from it.

#### B. Enhance `AbstractSnapJob` Lifecycle
Formalize the lifecycle in `AbstractSnapJob` to reduce boilerplate in subclasses:
*   **Template Method Pattern**: Implement `configure` in the base class as a `final` method that calls `preConfigure()`, `doConfigure()`, and `postConfigure()`.
*   **Centralize Directories**: Move `RtwJob`'s directory determination logic into `AbstractSnapJob`. Every job should have a predictable home and work directory by default.

#### C. Decouple BootStrap from Logic
`BootStrap` should be a thin CLI wrapper.
*   It should simply locate the config URI, pass it to the `JobFactory`, and call `start()` on the returned `SnapJob`.
*   Remove the "first field name is class name" logic from `BootStrap`.

#### D. Uniform Configuration Pattern
Adopt a "Universal Job Configuration" schema:
```json
{
  "name": "MyJob",
  "class": "coyote.RtwJob",
  "schedule": { "millis": 60000 },
  "configuration": {
    "target": "output.csv",
    "logging": { ... }
  }
}
```
This allows `DaemonJob`, `BootStrap`, and even unit tests to treat every job identically regardless of whether it's a standalone task or a scheduled service.

#### E. Service-Oriented Lifecycle
Add an `initialize()` method to the `SnapJob` interface between `configure()` and `start()`. This allows jobs to validate resources (files, network) before the execution thread starts, improving error reporting.