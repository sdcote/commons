# MiniVault Developer Guide

This guide provides technical details on the inner workings of MiniVault, its lifecycle, and how to extend its capabilities.

## Architecture Overview

MiniVault is built on several key components that handle data modeling, persistence, encryption, and the user interface.

### Key Classes

- `coyote.commons.minivault.MiniVaultVault`: The primary implementation of the `Vault` interface for MiniVault. It manages the loading, decryption, and retrieval of secrets.
- `coyote.commons.minivault.Entries` and `coyote.commons.minivault.Entry`: The data model for the secrets store. These classes are used by the `JsonMarshaller` to map JSON data to Java objects.
- `coyote.commons.minivault.data.DocumentHelper`: Handles the I/O operations, including GZIP compression and encryption/decryption via crypto streams.
- `coyote.commons.minivault.util.JsonMarshaller`: Responsible for converting between the `Entries` object and its JSON representation.
- `coyote.commons.minivault.crypt.Aes256`: Provides the underlying AES-256 encryption logic.

## Lifecycle of a MiniVault

1. **Initialization**: When a `MiniVaultVault` instance is created, it is in an uninitialized state. Configuration (filename and password) must be provided.
2. **Opening**: Calling `open()` triggers the `DocumentHelper` to:
    - Open a `FileInputStream`.
    - Wrap it in a `CryptInputStream` for decryption using the provided password hash.
    - Wrap it in a `GZIPInputStream` for decompression.
    - Use `JsonMarshaller` to read the JSON data and populate the `Entries` object.
3. **Operation**: Once opened, `getEntry(String key)` can be used to retrieve secrets. This operation iterates through the loaded `Entries` and returns a `VaultEntry` if a match is found.
4. **Closing**: The `close()` method is currently a no-op, but it is recommended to call it to adhere to the `Vault` interface contract. Resources are primarily managed during the `open()` and `save()` operations.

## Data Model and Serialization

The data model is a simple collection of `Entry` objects within an `Entries` wrapper. Serialization is handled by a custom `JsonMarshaller` which uses a simplified JSON library included in the package (`coyote.commons.minivault.json`).

### JSON Tags

Standard tags used for serialization are defined as constants in the `MiniVault` class:
- `ENTRIES_TAG`: "entries"
- `NAME_TAG`: "name"
- `USER_TAG`: "username"
- `PASSWORD_TAG`: "password"
- `URL_TAG`: "url"
- `TOKEN_TAG`: "token"
- `NOTES_TAG`: "notes"
- `PUBLIC_KEY_TAG`: "publickey"
- `PRIVATE_KEY_TAG`: "privatekey"
- `PASSPHRASE_TAG`: "passphrase"

## Extending MiniVault

### Adding New Fields to Entries

To add a new field to a vault entry:
1. Add a new constant tag in `MiniVault.java`.
2. Add the corresponding field and getter/setter in `Entry.java`.
3. Update `JsonMarshaller.read()` and `MiniVaultVault.getEntry()` to handle the new field.

### Custom Encryption

While MiniVault uses AES-256 by default, the encryption logic is encapsulated in the `coyote.commons.minivault.crypt` package. You can modify `Aes256.java` or `Cbc.java` to adjust the encryption parameters (e.g., iterations, salt handling) if necessary.

### Custom UI Components

The UI is built using standard Swing components. The `MiniVaultFrame` acts as the main container. Custom actions can be added by implementing the `Action` interface or extending `AbstractMenuAction` in the `coyote.commons.minivault.ui.action` package and registering them in `MenuActionType`.

## Implementation Details

### Encryption Key Derivation

MiniVault uses PKCS#5 SHA-256 to derive a 256-bit key from the user-provided password. This logic is located in `CryptUtils.getPKCS5Sha256Hash()`.

### Thread Safety

The `MiniVaultVault` class is not explicitly designed for multi-threaded access. It is recommended to use a single instance per thread or implement external synchronization if shared across threads.
