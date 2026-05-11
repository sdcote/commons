# StaticReader

The `StaticReader` is a component for the RTW (Read-Transform-Write) framework designed to generate a fixed set of data records. It is primarily used for testing, seeding databases, and publishing control messages.

## Overview

The `StaticReader` produces a single `DataFrame` (data record) based on its configuration and repeats it for a specified number of times. It supports template resolution, allowing fields to be dynamically populated from the symbol table at runtime.

### Key Features

- **Repeatable Output**: Generate a single record once or multiple times using the `limit` parameter.
- **Dynamic Templates**: Field values can contain templates (e.g., `[#$cmd.arg.1#]`) that are resolved against the current symbol table for each read.
- **Type Preservation**: Numeric and boolean values in the configuration are preserved as their respective types in the output `DataFrame`.
- **Testing Utility**: Ideal for simulating source data when developing or debugging transformation logic.

## Configuration

The `StaticReader` is configured with a `fields` section to define the record content and an optional `limit` to control the number of records produced.

| Tag | Type | Description | Default |
| :--- | :--- | :--- | :--- |
| `fields` | Object | A map of field names and values to be included in the generated record. | Empty record |
| `limit` | Integer | The number of times the record should be returned before reaching EOF. | 1 |

### Configuration Examples

#### 1. Basic Static Message
Generate a single record with fixed values.
```json
{
  "Reader": {
    "class": "StaticReader",
    "fields": {
      "Status": "OK",
      "Code": 200,
      "Internal": true
    }
  }
}
```

#### 2. Multiple Identical Records
Generate five copies of the same record, useful for load testing or batch processing simulations.
```json
{
  "Reader": {
    "class": "StaticReader",
    "limit": 5,
    "fields": {
      "Type": "Heartbeat",
      "Source": "InternalMonitor"
    }
  }
}
```

#### 3. Dynamic Values using Templates
Use templates to pull data from the environment or command-line arguments. Templates are resolved every time `read()` is called.
```json
{
  "Reader": {
    "class": "StaticReader",
    "fields": {
      "JobId": "[#$UUID#]",
      "RunBy": "[#$user.name#]",
      "InputFile": "[#$cmd.arg.1#]"
    }
  }
}
```

## Operation

When the reader is opened, it parses the `fields` section of its configuration and stores it as a template `DataFrame`. 

Each call to `read()` performs the following:
1. Increments an internal counter.
2. Checks if the counter has reached the `limit`. If so, it marks the transaction context as the last frame.
3. Iterates through the fields of the template `DataFrame`.
4. If a field is a String, it attempts to resolve any templates within it using the current symbol table.
5. Returns a new `DataFrame` containing the resolved values.

## Developer Notes

### Lifecycle

1. **Configuration**: The engine passes the configuration `DataFrame` to the reader.
2. **Open**: The `open(TransformContext)` method is called. This is where the `limit` is parsed and the template `DataFrame` is constructed from the `fields` configuration section.
3. **Read**: The `read(TransactionContext)` method is called repeatedly by the engine. It returns a resolved version of the template `DataFrame` until the `limit` is reached.
4. **EOF**: After the `limit` is reached, `eof()` returns `true`, signaling the engine to stop reading.

### Extending StaticReader

The `StaticReader` extends `AbstractFrameReader`. To extend its capabilities, you can override:

- `resolve(DataFrame)`: If you need custom logic for how templates or values are processed before being returned.
- `read(TransactionContext)`: If you want to change the logic of when the reader terminates or how it interacts with the transaction context.

Since it uses `coyote.commons.template.Template` for resolution, any symbols added to the `TransformContext` symbols will be available for use in the `fields` configuration.
