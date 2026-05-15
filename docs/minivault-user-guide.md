# MiniVault User Guide

MiniVault is a lightweight, encrypted secrets store designed for simple and secure management of credentials and other sensitive information. It provides both a programmatic API for application integration and a Swing-based graphical user interface for easy management.

## Capabilities

- **Secure Storage**: Uses AES-256 encryption to protect secrets on disk.
- **Programmatic API**: Easily integrate secrets management into your Java applications.
- **Swing GUI**: A user-friendly interface for managing vault entries.
- **Vault Abstraction**: Fully compatible with the `coyote.commons.vault.Vault` interface, allowing for implementation decoupling.
- **Flexible Entries**: Supports a wide range of fields including usernames, passwords, URLs, tokens, and cryptographic keys.

## Programmatic Usage

You can interact with MiniVault directly using the `MiniVaultVault` class or via the `Vault` interface.

### Creating and Retrieving Secrets

The following example shows how to programmatically open a MiniVault file and retrieve a secret.

```java
import coyote.commons.minivault.MiniVaultVault;
import coyote.commons.vault.VaultEntry;

public class MiniVaultExample {
    public static void main(String[] args) {
        try {
            MiniVaultVault vault = new MiniVaultVault();
            vault.setFilename("mysecrets.jvt");
            vault.setPassword("mySecurePassword");
            
            // Open and decrypt the vault
            vault.open();
            
            // Retrieve an entry by name
            VaultEntry entry = vault.getEntry("Database");
            if (entry != null) {
                System.out.println("User: " + entry.get("username"));
                System.out.println("Password: " + entry.get("password"));
            }
            
            vault.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Using the Swing GUI

MiniVault includes a Swing-based GUI for managing secrets. It can be launched by running the `coyote.commons.minivault.MiniVault` class.

### Key Features of the GUI:

- **Entry Management**: Easily add, edit, duplicate, and delete entries.
- **Clipboard Integration**: One-click copying of usernames, passwords, and URLs to the clipboard.
- **Password Generation**: Built-in tool to generate secure, random passwords.
- **Search**: Quickly find entries using the search panel.
- **Security**: Prompt for a password to open encrypted files; option to clear the clipboard on exit.

To launch the GUI from the command line:
```bash
java -cp coyote-commons.jar coyote.commons.minivault.MiniVault [optional-vault-file]
```

## Abstracting Secrets Management

MiniVault is designed to work with the `coyote.commons.vault` abstraction layer. This allows your application to remain independent of the specific secrets management technology being used.

### Using VaultBuilder

The `VaultBuilder` provides a fluid API for creating `Vault` instances. By using the "Local" provider, you can create a `MiniVaultVault` instance.

```java
import coyote.commons.vault.Vault;
import coyote.commons.vault.VaultBuilder;
import coyote.commons.vault.VaultEntry;

public class VaultAbstractionExample {
    public static void main(String[] args) {
        try {
            Vault vault = new VaultBuilder()
                .setProvider("Local")
                .setProperty("filename", "secrets.jvt")
                .setProperty("password", "vaultPassword")
                .build();

            vault.open();
            VaultEntry entry = vault.getEntry("API_KEY");
            System.out.println("Token: " + entry.get("token"));
            vault.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

By coding against the `Vault` interface, you can easily switch from MiniVault to an enterprise solution like CyberArk by simply changing the `VaultBuilder` configuration.

## Configuration Parameters

When using the `VaultBuilder` with the `Local` provider, the following properties are supported:

| Property | Description | Required |
|----------|-------------|----------|
| `filename` | The path to the MiniVault file (typically `.jvt` or `.json`). | Yes |
| `password` | The passphrase used to encrypt/decrypt the file. | Yes |

### User Preferences

MiniVault stores user preferences (like the last opened directory or UI theme) in a `.jvault` file in the user's home directory.

## JSON Configuration Examples

MiniVault stores entries in a JSON format. When not encrypted (or after decryption), the structure looks like this:

