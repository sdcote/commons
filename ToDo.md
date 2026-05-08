# Things To Do

## Snap

## Bootstrap

If it is necessary to name a snap job, use the following:
```java
if (StringUtil.isBlank(configuration.getName())) {
  configuration.setName(UriUtil.getBase(cfgUri));
}
```

## MLLP (Minimal Lower Layer Protocol)

Support the ability to test HL7 integrations using TCP/MLLP: The standard for HL7 v2.

**Channels:** These are the primary units of work. A channel consists of a Source (where data comes from), Filters (rules to decide if a message should be processed), Transformers (logic to modify data), and one or more Destinations (where data is sent).

Channels map to a Snap Job in that a Reader operates as a Source, and Writers operate as Destinations. RTW components can be used to filter and transform.

### MLLP Reader

### MLLP Writer

## Service Wrappers
To run a Java application as a stable background service on Windows and macOS, you should use the native service managers for each platform. Windows requires a wrapper to bridge the Java process with the Service Control Manager (SCM), while macOS uses launchd to manage daemons.

### Windows
Windows services must implement specific lifecycle callbacks (Start/Stop) that a standard Java main method does not provide.
**Recommended Tool: WinSW (Windows Service Wrapper)**

WinSW is an open-source, widely adopted executable that wraps any binary as a Windows service. It is more lightweight and easier to configure than Apache Commons Daemon (Procrun).

1. Download: Get the `WinSW-x64.exe` from the GitHub releases page.
1. Rename: Rename it to your service name, e.g., `MyAppService.exe`.
1. Configure: Create an XML file with the same name (`MyAppService.xml`):
```XML
    <service>
      <id>MyAppService</id>
      <name>My Java Application</name>
      <description>Runs my Java 21 background task.</description>
      <executable>java</executable>
      <arguments>-jar "C:\path\to\your\app.jar"</arguments>
      <log mode="roll"></log>
    </service>
```
4. Install: Run MyAppService.exe install from an Administrator command prompt.
5. Start: Run net start MyAppService or use services.msc.

### MacOS
On macOS, you do not need a wrapper. You use a property list (.plist) file to tell launchd how to manage your Java process.

1. Create the Plist: Create a file named `com.user.myapp.plist` in `/Library/LaunchDaemons/` (for system-wide) or `~/Library/LaunchAgents/` (for the logged-in user).
1. Define the Service:
```XML
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
    <plist version="1.0">
    <dict>
        <key>Label</key>
        <string>com.user.myapp</string>
        <key>ProgramArguments</key>
        <array>
            <string>/usr/bin/java</string>
            <string>-jar</string>
            <string>/absolute/path/to/app.jar</string>
        </array>
        <key>RunAtLoad</key>
        <true/>
        <key>KeepAlive</key>
        <true/>
        <key>StandardOutPath</key>
        <string>/tmp/myapp.stdout.log</string>
        <key>StandardErrorPath</key>
        <string>/tmp/myapp.stderr.log</string>
    </dict>
    </plist>
```
3. Load the Service: * Set permissions: `sudo chown root /Library/LaunchDaemons/com.user.myapp.plist`
4. Load: `sudo launchctl load /Library/LaunchDaemons/com.user.myapp.plist`
