# Vault Secrets Management

The `coyote.commons.vault` package provides a uniform, decoupled interface for interacting with various secrets management technologies. By using the `Vault` interface, client code remains stable even if the underlying secrets management implementation (e.g., CyberArk, MiniVault, or a local file-based store) is changed.

## The Vault Interface

The `Vault` interface represents a secrets provider implementation. It acts as a mediator between client code and vendor-specific models and API calls.

### Key Methods

- `open()`: Initializes the vault. This must be called before retrieving secrets.
- `getEntry(String key)`: Retrieves a `VaultEntry` for the given key. The key can be a simple string, a path, or an expression, depending on the provider.
- `isReadOnly()`: Indicates if the vault only supports secret retrieval.
- `save()`: Persists any changes made to the vault (if supported).
- `close()`: Closes the vault and releases any resources.

### VaultEntry

A `VaultEntry` represents a collection of data stored under a single identifier. It contains a map of key-value pairs, allowing it to hold various types of information and metadata.

```java
VaultEntry entry = vault.getEntry("mySecret");
String password = entry.get("password");
String username = entry.get("username");
```

## Building a Vault with VaultBuilder

The `VaultBuilder` is used to configure and create `Vault` instances. It allows you to specify the provider, the method of access, and any necessary properties.

### Configuration Options

- `setProvider(String name)`: Sets the name of the `Provider` to use. This can be a fully-qualified class name or a simple name (which defaults to the `coyote.commons.vault.provider` package).
- `setMethod(String method)`: Specifies the method to be used by the provider (e.g., "HTTPS", "FILE").
- `setProperty(String name, String value)`: Sets provider-specific properties (e.g., API keys, filenames, passwords).
- `build()`: Creates and initializes the `Vault` instance.

### Example

```java
Vault vault = new VaultBuilder()
    .setProvider("Local")
    .setProperty("filename", "secrets.json")
    .setProperty("password", "vaultPassword")
    .build();

vault.open();
VaultEntry entry = vault.getEntry("database");
System.out.println(entry.get("password"));
vault.close();
```

## Providers

A `Provider` is a factory for `Vault` instances. Different `Provider` implementations handle the specifics of interacting with various secrets management technologies.

### Provider Loading

When `VaultBuilder.build()` is called, it uses the `ProviderLoader` to instantiate the specified provider class. 

- If a simple name like "Local" is provided, the loader searches for `coyote.commons.vault.provider.Local`.
- If a fully-qualified name is provided, it attempts to load that class directly.

This architecture allows for easy extension. For example, a CyberArk provider could be implemented and loaded by specifying its class name:

```java
Vault vault = new VaultBuilder()
    .setProvider("com.example.CyberArkProvider")
    .setProperty("url", "https://cyberark.example.com")
    .build();
```

## Example: MiniVault Provider

The `MiniVault` implementation serves as an excellent example of how a provider and vault work together.

### Local Provider

The `Local` provider (found in `coyote.commons.vault.provider.Local`) is a built-in provider that creates `MiniVaultVault` instances. It requires two main properties:

1. `filename`: The path to the encrypted MiniVault JSON file.
2. `password`: The password used to derive the decryption key for the file.

### MiniVaultVault

The `MiniVaultVault` implements the `Vault` interface. When `open()` is called, it reads the encrypted file, decrypts it using the provided password, and loads the secrets into memory.

`VaultEntry` objects returned by `MiniVaultVault` contain standard tags defined in the `MiniVault` class:

- `name`: The identifier for the entry.
- `username`: Associated username.
- `password`: The secret password.
- `url`: Associated URL.
- `notes`: Any notes or metadata.
- `token`: A security token.

By using the `Vault` interface, your application can switch from a simple `MiniVault` file to a robust enterprise secrets manager like CyberArk by simply changing the `VaultBuilder` configuration, without modifying the code that consumes the secrets.
