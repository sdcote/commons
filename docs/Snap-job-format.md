# Snap Job Configuration Format

Snap jobs use a JSON-based configuration format. `BootStrap` supports loading one or more jobs from a single configuration file.

## Multiple Jobs Support

A single configuration file can define multiple jobs. This is done by placing them in a `Job` array or by providing multiple top-level job definitions.

```json
{
  "Job": [
    {
      "name": "Job1",
      "class": "coyote.RtwJob",
      "configuration": { "source": "input1.txt" }
    },
    {
      "name": "Job2",
      "class": "coyote.RtwJob",
      "configuration": { "source": "input2.txt" }
    }
  ]
}
```

## Scheduling and Repeatability

Jobs can be scheduled to run repeatedly.

### Schedule Section

Adding a `schedule` section to a job configuration allows for interval-based execution.

```json
{
  "class": "coyote.RtwJob",
  "schedule": {
    "interval": "5m"
  }
}
```

### Repeat Flag

A top-level `repeat: true` flag can be used to indicate that a job should be immediately rescheduled for execution after it completes.

```json
{
  "class": "coyote.RtwJob",
  "repeat": true
}
```

## Standard Format

The Standard Format clearly separates the job metadata from its specific configuration.

```json
{
  "name": "MyJob",
  "class": "coyote.RtwJob",
  "configuration": {
    "target": "output.csv",
    "source": "input.txt"
  },
  "logging": [
    {
      "ConsoleAppender": {
        "target": "stdout",
        "categories": "trace, debug, info, warn, error, fatal"
      }
    },
    {
      "FileAppender": {
        "target": "myjob.log",
        "categories": "info, warn, error, fatal"
      }
    }
  ]
}
```

### Root Elements

- **`name`**: (Optional) A descriptive name for the job.
- **`class`**: (Required) The fully qualified name of the job class (e.g., `coyote.RtwJob`). If the class is not fully qualified, `JobLoader` will attempt to resolve it.
- **`configuration`**: (Optional) An object containing job-specific configuration parameters.
- **`logging`**: (Optional) An array of logging configurations.
- **`schedule`**: (Optional) A section defining the execution interval for the job.
- **`repeat`**: (Optional) A boolean flag indicating the job should be run repeatedly in the scheduler.

## Legacy Format

The Legacy Format uses the name of the first field as the class name.

```json
{
  "RtwJob": {
    "target": "output.csv",
    "source": "input.txt"
  }
}
```

In this format, the contents of the object associated with the class name are treated as the job's configuration.

## Class Resolution

When a class name is provided without a package (e.g., `"class": "RtwJob"`), the `JobLoader` attempts to resolve it by:
1.  Searching the classpath for classes matching the name.
2.  Defaulting to the `coyote` package (e.g., `coyote.RtwJob`).

## Logging Configuration

The `logging` section allows for multiple loggers. Each logger is specified by its class name (short or fully qualified) and its properties.

- **`target`**: The destination for logs (e.g., `stdout`, `stderr`, or a file path). Relative file paths are resolved against `app.home/log`.
- **`categories`**: A comma-separated list of log levels to capture.

## Relative File Resolution

Snap jobs promote a sandboxed environment where each job has its own dedicated directory. As such, the **Job Directory** is the default parent for all relative file paths.

When a relative path is encountered in the configuration (e.g., `"source": "input.txt"`), the system attempts to resolve it by searching in the following order:

1.  **Job Directory**: (e.g., `snap/wrk/JobName/`) This is the primary location for job-specific data.
2.  **Configuration Location**: The directory containing the job's configuration file.
3.  **Work Directory**: The general `wrk` directory under `app.home`.
4.  **Current Working Directory**: The directory from which the application was launched.

This ensures that jobs are self-contained and portable, while still allowing for fallback to global locations if necessary.

## Symbol Substitution

Configuration values can include symbols in the format `${symbol_name}`. These are resolved at runtime using the job's symbol table, which includes environment variables, system properties, and command-line arguments.

Example:
```json
{
  "target": "${app.work}/results_${current_date}.csv"
}
```
