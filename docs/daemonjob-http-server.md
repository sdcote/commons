# DaemonJob HTTP Server Guide

The `DaemonJob` can be configured to start an HTTP server that allows for remote control and monitoring via RESTful web services.

## User Guide

### Configuration

To enable the HTTP server in a `DaemonJob`, add a `Server` section to your job configuration.

```json
{
  "Job": {
    "class": "coyote.DaemonJob",
    "Server": {
      "port": 8443,
      "Secure": {
        "filename": "/keystore.jks",
        "password": "password"
      },
      "ipacl": {
        "127.0.0.1": "allow",
        "default": "deny"
      }
    }
  }
}
```

The `Server` section supports the full configuration of the Coyote HTTP Server, including ports, IP ACLs, and SSL/TLS.

### Enabling SSL/HTTPS

To enable secure connections in the `DaemonJob` HTTP server, add a `Secure` section to the `Server` configuration.

```json
"Server": {
  "port": 8443,
  "Secure": {
    "filename": "/keystore.jks",
    "password": "password"
  }
}
```

- **`filename`**: The path to the Java Keystore (JKS) file. This path is relative to the application's classpath.
- **`password`**: The passphrase for the keystore.

#### Preparing a Keystore

You can generate a self-signed certificate and keystore using the `keytool` command:

```bash
keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 360 -keysize 2048
```

Place the generated `keystore.jks` in your resources directory (e.g., `src/main/resources`) so it is available on the classpath.

### Endpoints

Once enabled, the following endpoints are available:

- `GET /api/status`: Returns the current status of the job (state, uptime, etc.). Anyone can access this endpoint.
- `POST /api/command`: Executes a control command on the job. This endpoint requires authentication.

### Authentication

You can secure the HTTP server by adding a `Users` section to the `Server` configuration. Each user must have a `Name`, `Password`, and `Groups` specification.

```json
{
  "Job": {
    "class": "coyote.DaemonJob",
    "Server": {
      "port": 8080,
      "Users": {
        "admin": {
          "Password": "secretPassword",
          "Groups": "admin,oper"
        }
      }
    }
  }
}
```

When users are configured, endpoints protected with the `@Auth` annotation (like `/api/command`) will require valid credentials. The server supports Basic Authentication.

### Group-Based Access Control

The `@Auth` annotation can also be used to restrict access to specific groups. For example, if you want to ensure only members of the "admin" group can access a responder or a specific method:

```java
@Auth(groups = "admin")
public class AdminOnlyResponder extends ServiceResponder {
    // ...
}
```

Or on a specific method:

```java
@Override
@Auth(groups = "admin")
public Response post(Resource resource, Map<String, String> urlParams, HTTPSession session) {
    // ...
}
```

In the configuration, ensure the user belongs to the required group:

```json
"Users": {
  "alice": {
    "Password": "alicePassword",
    "Groups": "admin"
  },
  "bob": {
    "Password": "bobPassword",
    "Groups": "oper"
  }
}
```

In this case, Alice can access the admin-protected endpoints, but Bob cannot.

Alternatively, you can use an `Auth` section for more advanced configuration:

```json
{
  "Server": {
    "port": 8080,
    "Auth": {
      "AllowUnsecuredConnections": "true",
      "Users": {
        "admin": { "Password": "secretPassword", "Groups": "admin" }
      }
    }
  }
}
```

### Command Protocol

To control the job, send a POST request to `/api/command` with a JSON body:

```json
{
  "command": "start"
}
```

Valid commands are:
- `start`: Starts the job if it is not already running.
- `stop`: Gracefully stops the job.
- `shutdown`: Stops the job and terminates the daemon process.

---

## Developer Guide

### Implementation Details

The `DaemonJob` incorporates an HTTP server by using the `HTTPDRouter` class. This allows for sophisticated routing and the use of specialized responders.

The `DaemonJob` instance is passed to the responders during initialization, allowing them to interact directly with the job's lifecycle and state.

### Modifying Existing Responders

Developers can extend the existing responders in the `coyote.commons.rtw.daemonjob` package to add more functionality.

#### Adding Commands to `CommandResponder`

The `CommandResponder` handles control commands via `POST` requests. To add a new command:

1.  Open `coyote.commons.rtw.daemonjob.CommandResponder`.
2.  In the `post` method, find the `switch (command.toLowerCase())` block.
3.  Add a new `case` for your command.
4.  Implement the logic using the `job` instance.
5.  Update the `results` DataFrame to provide feedback.

Example adding a "reset" command:

```java
case "reset":
    job.reset(); // Assuming DaemonJob has a reset method
    results.set("status", "reset complete");
    break;
```

#### Adding Reporting to `StatusResponder`

The `StatusResponder` provides job status via `GET` requests. To add more reporting metrics:

1.  Open `coyote.commons.rtw.daemonjob.StatusResponder`.
2.  In the `get` method, use the `job` instance to retrieve the desired information.
3.  Add the data to the `results` DataFrame (inherited from `AbstractJsonResponder`).

Example adding memory usage:

```java
long freeMem = Runtime.getRuntime().freeMemory();
results.set("freeMemory", freeMem);
```

### Adding New Responders

To add entirely new endpoints and capabilities:

1.  **Create a new Responder class**: Create a class in `coyote.commons.rtw.daemonjob` that extends `AbstractJsonResponder` (for JSON APIs) or `ServiceResponder`.
2.  **Add Authentication (Optional)**: Use the `@Auth` annotation to secure the responder. To restrict access to a specific group, use `@Auth(groups = "admin")`.
3.  **Implement Handler Methods**: Override `get`, `post`, `put`, or `delete` as needed.
4.  **Access DaemonJob**: Use `resource.initParameter(DaemonJob.class)` to get the job instance.
5.  **Register the Route**: In `coyote.DaemonJob.configure(Config)`, add your new route to the `server` instance.

Example of a group-restricted responder:

```java
@Auth(groups = "admin")
public class ShutdownResponder extends AbstractJsonResponder {
    @Override
    public Response post(Resource resource, Map<String, String> urlParams, HTTPSession session) {
        DaemonJob job = resource.initParameter(DaemonJob.class);
        job.stop();
        results.set("status", "System shutting down");
        return Response.createFixedLengthResponse(Status.OK, getMimeType(), results.toString());
    }
}
```

And registering it in `DaemonJob.configure`:

```java
server.addRoute("/api/admin/shutdown", ShutdownResponder.class, this);
```

### Initialization

The server is initialized in `DaemonJob.configure(Config)` when a `Server` configuration section is detected.

```java
server = new HTTPDRouter(port);
server.addDefaultRoutes();
server.addRoute("/api/command", CommandResponder.class, this);
server.addRoute("/api/status", StatusResponder.class, this);
```

The `this` reference (the `DaemonJob` instance) is passed as an initialization parameter. Responders retrieve it using:

```java
DaemonJob job = resource.initParameter(DaemonJob.class);
```
