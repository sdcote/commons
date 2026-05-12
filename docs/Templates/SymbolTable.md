# SymbolTable

The `SymbolTable` class is a specialized data structure used primarily for managing named string values within the RTW framework and template resolution system. It extends `HashMap` and provides utility functions for handling system properties, environment variables, encrypted values, and formatted data.

## Overview

A `SymbolTable` acts as a repository of "symbols" (key-value pairs) that can be used to dynamically populate data fields in configurations or text templates. It supports:
- **String Mapping**: Storing and retrieving string values by name.
- **System Integration**: Easily importing System Properties and Environment Variables.
- **Static Literals**: Built-in symbols for common values like timestamps, line feeds, and file separators.
- **Automatic Decryption**: Decrypting values on-the-fly if they are prefixed with `ENC:`.
- **Formatting**: Applying patterns to dates and numbers during retrieval.

## Template Integration

`SymbolTable` is designed to work seamlessly with the `coyote.commons.template.Template` class. When a `Template` is resolved, it uses a `SymbolTable` to look up variables.

### Accessing Values

Templates use the `[# ... #]` syntax. To access a value from the `SymbolTable`, prefix the symbol name with a `$`.

| Syntax | Description | Example |
| :--- | :--- | :--- |
| `[#$name#]` | Basic lookup of the symbol `name`. | `[#$user.name#]` |
| `[#$name\|format#]` | Lookup with formatting applied. | `[#$run.date\|yyyy-MM-dd#]` |

### Formatting Patterns

The `SymbolTable` supports several formatting options when using the pipe (`|`) syntax:

- **Date Formatting**: If the value is a `Date` object (or a string that can be parsed as a number), any standard `SimpleDateFormat` pattern can be used.
  - Example: `[#$currentDate\|yyyy-MM-dd HH:mm:ss#]`
- **Number Formatting**: If the value is a `Number`, any standard `DecimalFormat` pattern can be used.
  - Example: `[#$item.price\|#,##0.00#]`
- **Case Conversion**:
  - `TOUPPER`: Converts the value to upper case. `[#$status\|TOUPPER#]`
  - `TOLOWER`: Converts the value to lower case. `[#$status\|TOLOWER#]`
- **URI Conversion**:
  - `TOURI`: Converts a file path into a file URI. `[#$file.path\|TOURI#]`

### Static Literals

The following symbols are always available in a `SymbolTable`, even if not explicitly set:

| Symbol | Description | Example Value |
| :--- | :--- | :--- |
| `time` | Current time in extended format | `18:15:30` |
| `currentMilliseconds` | System.currentTimeMillis() | `1715451330000` |
| `iso8601date` | Current date in ISO 8601 format | `2026-05-11T18:15:30Z` |
| `rfc822date` | Current date in RFC 822 format | `Mon, 11 May 2026 18:15:30 +0000` |
| `NL` | New Line character | `\n` |
| `CR` | Carriage Return character | `\r` |
| `CRLF` | Carriage Return + New Line | `\r\n` |
| `FS` | File Separator | `/` or `\` |
| `PS` | Path Separator | `:` or `;` |
| `HT` | Horizontal Tab | `\t` |
| `UUID` | A random UUID | `550e8400-e29b-41d4-a716-446655440000` |
| `Hostname` | The local host name | `myhost` |
| `FQDN` | The fully qualified domain name | `myhost.example.com` |
| `IPAddr` | The local IP address | `192.168.1.10` |
| `Username` | The current user name | `jdoe` |
| `UserHome` | The user's home directory | `/home/jdoe` |
| `TmpDir` | The system temporary directory | `/tmp` |
| `OSName` | The operating system name | `Linux` |
| `OSVersion` | The operating system version | `5.15.0-60-generic` |
| `OSArch` | The operating system architecture | `amd64` |
| `RandomInt` | A random integer | `12345678` |
| `RandomLong` | A random long | `1234567890123456` |
| `symbolDump` | A full dump of the table contents | (debugging info) |

### Encrypted Values

The `Template` system, via the `SymbolTable`, supports a mechanism for handling encrypted (obfuscated) values. Any symbol in the `SymbolTable` whose value starts with the `ENC:` prefix is automatically decrypted when it is resolved in a template.

**Example:**
If the `SymbolTable` contains a symbol `db.password` with the value `ENC:G67pQ9...`, using `[#$db.password#]` in a template will result in the decrypted password string.

#### Generating Encrypted Values

Encrypted values are generated using the `coyote.commons.cipher.CipherUtil` class. To generate a value that the `SymbolTable` can decrypt, you use the `CipherUtil.encryptString(String)` method.

##### Configuration Properties
The `CipherUtil` class uses two system properties to control the encryption and decryption process:

1.  `cipher.name`: The name of the cipher algorithm to use (e.g., `Blowfish`, `XTEA`). If not specified, it defaults to `Blowfish`.
2.  `cipher.key`: The key used for encryption and decryption. This should be a Base64-encoded string representing the bytes of the key. If not specified, a default internal key is used.

##### Key vs. Password
In `CipherUtil`, there is a distinction between a **key** and a **password**:
- **Password**: A human-readable string (e.g., "MySecretPassword").
- **Key**: A Base64-encoded string of bytes that the cipher uses directly as an initialization vector or private key.

You can convert a password into a key suitable for the `cipher.key` property using `CipherUtil.getKey(String password)`.

##### Java Code Examples

**Encrypting a value:**
```java
// Optional: Set custom cipher and key via system properties
System.setProperty("cipher.name", "Blowfish");
String myKey = CipherUtil.getKey("MyPrivatePassword");
System.setProperty("cipher.key", myKey);

// Encrypt the sensitive data
String secret = "SuperSecret123";
String encrypted = "ENC:" + CipherUtil.encryptString(secret);

System.out.println("Place this in your config: " + encrypted);
```

**Decrypting a value manually:**
```java
// Decrypting the value (assuming system properties are set as above)
String encryptedValue = "G67pQ9..."; // The part after "ENC:"
String decrypted = CipherUtil.decryptString(encryptedValue);
```

#### How Decryption Works
When `SymbolTable.getString(name)` is called:
1. It checks if the value associated with the name starts with `ENC:`.
2. If it does, it strips the `ENC:` prefix.
3. It calls `CipherUtil.decryptString(cipherText)`.
4. `CipherUtil.decryptString` retrieves the cipher name and key from the system properties (or defaults).
5. It Base64-decodes the `cipherText`.
6. It initializes the cipher with the key.
7. It decrypts the data, removes the 4-byte random salt, and converts the resulting bytes into a UTF-16 string.

#### Obfuscation vs. Confidentiality
It is important to understand the purpose and limitations of the `ENC:` prefix:

- **Obfuscation**: This mechanism is intended for **obfuscation**. It prevents casual observation of sensitive data (like passwords) in configuration files or log streams. It uses internal cipher utilities within the library to perform this task.
- **Not for Confidentiality**: The internal cipher utilities are not designed to provide high-level confidentiality or protection against determined attackers. They lack robust key management and other features required for true secure storage.

#### Recommendation: Use Vault Classes
For applications requiring genuine confidentiality and secure secrets management, it is strongly recommended to use the **Vault classes** located in the `coyote.commons.vault` package. 

The `Vault` system provides:
- A standardized API for secrets management.
- Support for multiple providers (including local file-based storage and potentially external secret managers).
- Better security practices for handling sensitive information.

When security is a priority, consider integrating a `Vault` instance into your application logic to retrieve secrets, rather than relying solely on `ENC:` symbols in a `SymbolTable`.

## Use Case Examples

### 1. Dynamic File Paths
Constructing a path based on the current user and date:
**Template:** `/home/[#$user.name#]/reports/[#$iso8601date\|yyyy/MM/dd#]/summary.log`
**Result:** `/home/scote/reports/2026/05/11/summary.log`

### 2. Database Connection Strings
Using environment variables and encrypted passwords:
**Template:** `jdbc:mysql://[#$DB_HOST#]:3306/[#$DB_NAME#]?user=[#$DB_USER#]&password=[#$DB_PASS#]`
*(Where `DB_PASS` starts with `ENC:`, it will be decrypted before the connection is made)*

### 3. Log Formatting
Creating a standardized log entry:
**Template:** `[#$time#] [#$JobId#] - [#$Status\|TOUPPER#]: [#$Message#][#$NL#]`

---

## Developer Documentation

### Class Operation & Lifecycle

The `SymbolTable` is essentially a `HashMap<Object, Object>`. While it is often populated with `String` keys and values, it can store any object.

1. **Instantiation**: `new SymbolTable()` creates an empty table.
2. **Population**:
    - `put(key, value)`: Add a single entry.
    - `readSystemProperties()`: Imports all current JVM system properties.
    - `readEnvironmentVariables()`: Imports all system environment variables.
3. **Retrieval**:
    - `get(key)`: Returns the raw object.
    - `getString(key)`: Returns a string representation, handling `ENC:` decryption and static literals if the key is not found in the map.
    - `getString(key, format)`: Applies the specified format to the value.

### Extending SymbolTable

You can extend `SymbolTable` to provide custom static values or specialized lookup logic. Override `getStaticValue(String symbol)` to add your own built-in constants.

### Object Placement & Method Invocation

The `Template` class can also interact with arbitrary Java objects placed in its internal cache (separate from the `SymbolTable` scalars).

#### Placing Objects
You can place objects into a `Template` instance using:
```java
Template tmplt = new Template(text);
tmplt.put("myHelper", new MyCustomHelper());
String result = tmplt.convertToString();
```

#### Accessing Methods in Templates
Once an object is placed in the cache, its methods can be called directly from the template string. Method arguments can be literal strings or symbols from the `SymbolTable`.

**Syntax:** `[#objectKey.methodName(arg1, $symbolArg)#]`

**Example:**
Suppose `myHelper` has a method `calculate(String type, String value)`.
**Template:** `Result is: [#myHelper.calculate("tax", $amount)#]`

The template engine will:
1. Locate `myHelper` in its object cache.
2. Resolve `$amount` from the `SymbolTable`.
3. Use reflection to find a method `calculate` that accepts two String arguments.
4. Invoke the method and append the result to the output string.

#### Writing Classes for Template Integration
To be effectively used within a template:
- Methods must be `public`.
- Methods used in templates must return an object (whose `toString()` will be used) or a `String`.
- Arguments passed from templates are currently handled as `String` objects by the `Template` engine. Ensure your helper methods accept `String` parameters or are prepared to parse them.
- Static logic can be encapsulated in these helper classes to provide complex transformations (e.g., currency conversion, custom masking, or complex business logic) that are not easily expressed in simple formatting strings.
