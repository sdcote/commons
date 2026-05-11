# Template

The `Template` class is a powerful string manipulation utility that allows for dynamic text generation using variables, static literals, and even direct method calls on Java objects. It is the core of the RTW framework's dynamic configuration system.

## Overview

A `Template` is a string containing special tags in the format `[# ... #]`. When processed, these tags are replaced with values retrieved from a `SymbolTable` or results of method calls on objects stored in an internal cache.

### Key Features

- **Variable Substitution**: Replace `[#$var#]` with values from a `SymbolTable`.
- **Formatting**: Support for date, number, and string formatting within tags.
- **Method Invocation**: Call methods on Java objects directly from the template string.
- **Pre-processing**: Partially resolve templates while keeping unresolved tags for later processing.
- **Static Cache**: Register objects globally to be available in all template instances.

## Template Syntax

The general syntax for a template tag is `[# ... #]`. Within these tags, different prefixes and delimiters determine how the content is resolved.

| Syntax | Type | Description |
| :--- | :--- | :--- |
| `[#$var#]` | Symbol | Resolves the symbol `var` from the `SymbolTable`. |
| `[#$var\|format#]` | Formatted Symbol | Resolves `var` and applies the specified `format`. |
| `[#obj#]` | Object | Calls `toString()` on the object named `obj` in the cache. |
| `[#obj.method()#]` | Method | Calls `method()` on the cached object `obj`. |
| `[#obj.method($var)#]` | Method with Arg | Calls `method()` using the resolved value of `$var` as an argument. |

### Encrypted Values (`ENC:`)

The `Template` class supports the automatic decryption of symbols that are prefixed with `ENC:`. This is handled during the symbol resolution phase by the `SymbolTable`.

- **Purpose**: This feature is designed for **obfuscation**, intended to hide sensitive information like passwords from casual view in configuration files.
- **Implementation**: It uses `coyote.commons.CipherUtil` for encryption and decryption.
- **System Properties**: It relies on `cipher.name` and `cipher.key` for selecting the algorithm and key.
- **Security Warning**: Because it uses internal utilities with limited key management, it should **not** be used for data requiring high confidentiality. It is an obfuscation tool, not a robust security solution.
- **Better Alternative**: For true secrets management and confidentiality, use the classes in the `coyote.commons.vault` package.

For detailed instructions on generating encrypted values and configuring ciphers, see the [SymbolTable documentation](SymbolTable.md#encrypted-values).

> **Note**: For details on available formatting patterns and static literals (like `[#$iso8601date#]`), see the [SymbolTable documentation](SymbolTable.md).

## Usage Examples

### 1. Simple Variable Substitution
```java
SymbolTable symbols = new SymbolTable();
symbols.put("name", "Alice");
String text = "Hello, [#$name#]!";
String result = Template.resolve(text, symbols);
// Result: "Hello, Alice!"
```

### 2. File Path Generation
Combining system properties and date literals for dynamic logging paths.
**Template:** `[#$user.home#]/logs/app_[#$iso8601date|yyyyMMdd#].log`

### 3. Dynamic SQL Construction
Using templates to build queries based on runtime symbols.
**Template:** `SELECT * FROM [#$table.name#] WHERE id = [#$record.id#]`

### 4. Method Calls on Helper Objects
You can register a helper class to perform complex logic within a template.
```java
public class TextUtils {
    public String mask(String input) {
        return "****" + input.substring(input.length() - 4);
    }
}

Template tmplt = new Template("Card: [#utils.mask($cardNum)#]");
tmplt.put("utils", new TextUtils());
tmplt.addSymbol("cardNum", "1234567890123456");
String result = tmplt.convertToString();
// Result: "Card: ****3456"
```

## Pre-processing vs. String Generation

The `Template` class distinguishes between "Pre-processing" and "Full Resolution".

### `convertToString()` (Normal Generation)
This is the standard way to resolve a template. 
- It attempts to resolve all tags.
- **Unresolved tags** are replaced with an empty string (or the original tag if certain internal flags are set).
- It is intended for the **final step** of text generation.

### `preProcess()`
This allows for multi-pass template resolution.
- Only tags that match keys in the provided `SymbolTable` are resolved.
- **Unresolved tags** are left exactly as they are in the output (e.g., `[#$futureVar#]` remains in the string).
- This is useful for pipelines where different components add symbols to a template at different stages of a lifecycle.

## Developer Documentation

### Class Operation
`Template` extends `StringParser`, which provides the underlying mechanics for scanning the template string and identifying tags. 

1. **Parsing**: When `convertToString()` or `preProcess()` is called, the string is scanned for `OPEN` (`[#`) and `CLOSE` (`#]`) delimiters.
2. **Tokenization**: The content between delimiters is extracted as a tag.
3. **Resolution Logic**: 
    - If the tag starts with `VAR_PREFIX` (`$`), it queries the `SymbolTable`.
    - If the tag contains a dot (`.`) and parentheses `()`, it attempts reflection-based method invocation.
    - Otherwise, it looks for the object in the `classCache` (instance-level) or `staticCache` (global-level) and calls `toString()`.

### Lifecycle
1. **Instantiation**: A `Template` is created with a template string and optionally an initial `SymbolTable`.
2. **Configuration**: Objects are added to the cache via `put()`, and symbols are added via `addSymbol()`.
3. **Execution**: The resolution method (`convertToString` or `preProcess`) is called.
4. **Cleanup**: Instances are lightweight and usually garbage collected after use. The `staticCache` persists for the life of the JVM.

### Extending Template
- **Custom Objects**: The easiest way to extend `Template` is to register custom Java objects in the cache. Any public method that returns an object can be utilized.
- **SymbolTable Extension**: By extending `SymbolTable`, you can introduce new static literals or custom formatting logic that becomes available to all templates using that table.
- **Subclassing**: While `Template` can be subclassed, most extension needs are met by the object cache and symbol table mechanisms.
