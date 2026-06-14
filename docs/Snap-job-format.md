# Snap Job Configuration Format

Snap jobs use a JSON-based configuration format. There are two supported formats: the **Standard Format** (recommended) and the **Legacy Format**.

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

## Symbol Substitution

Configuration values can include symbols in the format `${symbol_name}`. These are resolved at runtime using the job's symbol table, which includes environment variables, system properties, and command-line arguments.

Example:
```json
{
  "target": "${app.work}/results_${current_date}.csv"
}
```
