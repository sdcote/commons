# Snap Job Developer Guide

Snap jobs are modular components designed to perform specific tasks, such as data transformation, system monitoring, or background processing. Implementing a new Snap job involves extending the `AbstractSnapJob` class and implementing the `SnapJob` interface.

## Core Interface: SnapJob

The `coyote.commons.snap.SnapJob` interface defines the basic lifecycle methods:

- `configure(Config cfg)`: Configures the job with the provided parameters.
- `start()`: Begins the execution of the job.

## Base Class: AbstractSnapJob

The `coyote.commons.snap.AbstractSnapJob` class provides a robust foundation for most jobs. It handles directory resolution (`app.home`, `app.work`), symbol table initialization, and logging setup.

### Lifecycle Pattern

`AbstractSnapJob` implements `configure(Config)` using the Template Method pattern. Subclasses should override the following protected methods instead of `configure`:

1.  **`preConfigure()`**: (Optional) Called before the main configuration logic. Use this to prepare resources or initialize fields.
2.  **`doConfigure()`**: (Required) Performs the bulk of the configuration. Use this to read parameters from the `configuration` field.
3.  **`postConfigure()`**: (Optional) Called after configuration and logging are initialized.

### Example Implementation

```java
public class MyCustomJob extends AbstractSnapJob {

    private String targetFile;

    @Override
    protected void doConfigure() throws ConfigurationException {
        // Retrieve parameters from the configuration object
        targetFile = configuration.getString("target");
        
        if (StringUtil.isBlank(targetFile)) {
            throw new ConfigurationException("Target file must be specified");
        }
    }

    @Override
    public void start() {
        Log.info("Starting MyCustomJob, writing to: " + targetFile);
        
        // Perform job logic here
        
        Log.info("MyCustomJob completed.");
    }
}
```

## Job Loading with JobLoader

The `JobLoader` utility is responsible for instantiating and configuring jobs from a `Config` object. It supports both standard and legacy JSON formats, as well as multiple job configurations and scheduling.

```java
Config myConfig = ...;
List<ScheduledJob> jobs = JobLoader.loadJobs(myConfig);
for (ScheduledJob job : jobs) {
    job.run();
}
```

## Best Practices

- **Use the Symbol Table**: Use `symbols.getString(key)` or `Template.preProcess(value, symbols)` to allow configuration values to use environment variables and system properties.
- **Respect Directories**: Always resolve output paths against `System.getProperty("app.work")` and input paths against `System.getProperty("app.home")`.
- **Informative Logging**: Use the `Log` utility (`Log.debug`, `Log.info`, `Log.error`) to provide visibility into the job's progress and any issues encountered.
- **Fail Fast**: Throw a `ConfigurationException` in `doConfigure()` if required parameters are missing or invalid to prevent the job from starting in an inconsistent state.
