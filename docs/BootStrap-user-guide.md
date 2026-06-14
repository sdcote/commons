# BootStrap User Guide

The `coyote.BootStrap` class is the primary entry point for running Snap jobs from the command line. It acts as a thin wrapper that loads a job configuration, instantiates the appropriate job class, and manages its lifecycle.

## Basic Usage

To run a Snap job, invoke the `BootStrap` class with the path to a configuration file as the first argument:

```bash
java -cp commons.jar coyote.BootStrap myjob.json
```

### Arguments

- **Configuration URI**: The first argument must be the path or URI to a job configuration file (JSON format).
- **-owd**: (Optional) Overrides the Application Work directory (`app.work`). If present, the work directory is set to the directory containing the configuration file or the current working directory.
- **Other Arguments**: Any additional arguments are passed to the job and stored in the job's symbol table with the prefix `arg_` followed by their index (e.g., `arg_0`, `arg_1`).

## Application Directories

Snap jobs use two primary directory properties for organizing artifacts:

1.  **app.home**: The base directory for the application. By default, this is the directory where the configuration file is located. It is used as the root for log files (`/log`) and other resources. `app.home` is determined by the `coyote.commons.CoyoteEnvironment` class.
2.  **app.work**: The directory where the job performs its work (e.g., writing output files). By default, this is a `wrk` directory under `app.home`.

These can be overridden via system properties:

```bash
java -Dapp.home=/opt/myjob -Dapp.work=/tmp/output -cp commons.jar coyote.BootStrap myjob.json
```

## Logging

`BootStrap` uses the configuration provided in the job file to set up logging. If no logging is specified, it defaults to standard output. Log files are typically placed in the `log` subdirectory of `app.home`.

## Multi-Job Support

`BootStrap` can load and execute multiple jobs from a single configuration file. If the configuration contains multiple job definitions, `BootStrap` will use an internal `Scheduler` to manage their execution.

## Scheduling and Repeatable Jobs

A job can be scheduled to run at regular intervals by adding a `schedule` section to its configuration. Additionally, a top-level `repeat: true` flag can be used to indicate that a single job should be run repeatedly in the scheduler.

If multiple jobs are configured, or if any job is repeatable, or if a `Server` is configured, `BootStrap` will automatically start the scheduler.

## Integrated HTTP Server

`BootStrap` can start an integrated HTTP server for monitoring and managing running jobs. This is enabled by adding a `Server` section to the top-level configuration. When enabled, it provides endpoints for checking job status and sending control commands.

## Exit Codes

- **0**: All jobs completed successfully.
- **2**: No jobs were created from the configuration.
- **6**: A configuration error occurred.
