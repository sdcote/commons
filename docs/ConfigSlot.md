# ConfigSlot Documentation

The `coyote.commons.cfg.ConfigSlot` models an attribute that is expected to be present in a configuration. It serves as a metadata description for configuration parameters, allowing components to define their configurable properties, provide default values, and support dynamic configuration generation and UI prompts.

## Overview

Within the Coyote Plugin architecture, components (such as Readers, Writers, Transformers, and custom plugins) are often dynamically instantiated and configured via JSON, XML, or other representations. `ConfigSlot` provides a standardized way for these components to declare what configuration options they support, what those options mean, and what defaults they fall back to if left unspecified.

### Properties of a ConfigSlot

A `ConfigSlot` instance holds the following properties:

* **`name`**: The exact key or property name expected in the configuration (e.g., `"Port"`, `"Target"`).
* **`description`**: A human-readable description of the attribute's purpose and usage. This is particularly useful for auto-generating documentation or displaying help text in a UI.
* **`defaultValue`**: The default string value to use if the user does not specify this attribute in their configuration.
* **`message`**: A user-defined string often used to hold validation error messages or status updates when passed back to a GUI or editing tool.
* **`type`**: An integer indicating the underlying data type.

## Basic Usage

### Defining Configuration Slots

Components typically inherit from an abstract component base (such as `coyote.commons.rtw.AbstractConfigurableComponent`) which contains an underlying `Config` object. A component can declare its expected configuration slots during initialization:

```java
import coyote.commons.cfg.ConfigSlot;
import coyote.commons.cfg.Config;
import coyote.commons.rtw.AbstractConfigurableComponent;

public class MyComponent extends AbstractConfigurableComponent {

  public MyComponent() {
    // Add expected configuration slots to this component's Config
    ConfigSlot portSlot = new ConfigSlot("Port", "The network port on which to listen.", "8080");
    configuration.addConfigSlot(portSlot);
    
    ConfigSlot hostSlot = new ConfigSlot("Host", "The IP address or hostname to bind to.", "127.0.0.1");
    configuration.addConfigSlot(hostSlot);
  }
}
```

### Applying Defaults

Once slots are defined, you can instruct the `Config` object to prime itself with the default values. This is especially useful as a starting point when a configuration has not been explicitly provided by the user:

```java
// Populate the configuration with the default values defined in the ConfigSlots
configuration.setDefaults();
```

Calling `setDefaults()` iterates over all registered `ConfigSlot` instances and inserts their `defaultValue` into the configuration data frame using the slot's `name` as the key.

## Architecture and Integration

### `Config` and `DataFrame`

The `coyote.commons.cfg.Config` class extends `coyote.commons.dataframe.DataFrame`. A `DataFrame` is essentially a collection of typed fields (key-value pairs) that represents structured data, similar to a JSON object.

Because `Config` is a `DataFrame`, configuration data can easily be marshaled to and from JSON, XML, and other formats. The `ConfigSlot` acts as the schema or template overlay for this dynamic `DataFrame`, turning an unstructured dictionary into a structured configuration with known expectations.

### UIs and Mutable Attributes

In a graphical or web-based environment where a user configures a Coyote job, `ConfigSlot` acts as a mutable Attribute instance. 
* A GUI can query a component's `Config` for its slots via `configSlotIterator()` to dynamically build a form with inputs and tooltips (`description`).
* When the user submits the form, the actual configuration values can be set. 
* If a value fails a validity check, the `ConfigSlot` can be passed back to the GUI with the invalid value placed in the `defaultValue` field and an error message set via `setMessage()`, providing immediate feedback without strict type-checking failures.

### Summary

The `ConfigSlot` strategy empowers the Coyote framework to be both highly flexible and self-documenting. By using `ConfigSlot`, developers ensure that their configurable components explicitly define their interfaces, provide fallback defaults, and integrate cleanly with higher-level tooling and configuration loaders.
