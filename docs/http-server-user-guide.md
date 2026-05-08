# HTTP Server User Guide

The Coyote HTTP Server is a lightweight, embeddable HTTP/1.1 server designed for simplicity and flexibility. It supports static file serving, RESTful web services, and role-based access control.

## Capabilities

- **Embeddable**: Easily integrate an HTTP server into any Java application.
- **Routing**: Sophisticated URI routing to different responders.
- **Responders**: Extensible architecture for handling requests (GET, POST, PUT, DELETE, etc.).
- **Static File Serving**: Serve files from the local file system or classpath.
- **Web Services**: Easily create RESTful endpoints by extending base responder classes.
- **Security**: Role-based access control using annotations and custom authentication providers.
- **IP ACL**: Access Control Lists based on IP addresses.
- **DoS Protection**: Basic Denial of Service protection based on request frequency.
- **SSL/HTTPS**: Support for secure connections using SSL/TLS.
- **Remote Control**: The `DaemonJob` can be controlled remotely via an HTTP server (see [DaemonJob HTTP Server Guide](daemonjob-http-server.md)).

## Creating an HTTP File Server

To create a fully-functioning HTTP file server, you can use the `HTTPDRouter` and the `FileResponder`.

### Programmatic Example

```java
import coyote.commons.network.http.responder.HTTPDRouter;
import coyote.commons.network.http.responder.FileResponder;
import java.io.File;

public class FileServer {
    public static void main(String[] args) {
        // Create a router on port 8080
        HTTPDRouter server = new HTTPDRouter(8080);
        
        // Add a route to serve files from the 'public_html' directory
        File rootDir = new File("public_html");
        server.addRoute("/", FileResponder.class, rootDir);
        
        try {
            server.start();
            System.out.println("Server started on port 8080");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

In this example, any request to `/` or its sub-paths will be handled by the `FileResponder`, which will look for files in the `public_html` directory.

## Creating Simple Web Services

Web services can be created by implementing the `Responder` interface or extending `DefaultResponder` (for text-based responses) or `AbstractJsonResponder`.

### JSON Web Service Example

```java
import coyote.commons.network.http.responder.AbstractJsonResponder;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;
import coyote.commons.network.http.responder.Resource;
import java.util.Map;

public class HelloResponder extends AbstractJsonResponder {
    @Override
    public Response get(Resource resource, Map<String, String> urlParams, HTTPSession session) {
        return Response.createFixedLengthResponse(Status.OK, getMimeType(), "{\"message\":\"Hello World\"}");
    }
}
```

Register the responder with the server:

```java
server.addRoute("/api/hello", HelloResponder.class);
```

## Protecting Paths with Auth Annotation

You can protect specific responders or methods using the `@Auth` annotation. This requires an `AuthProvider` to be configured on the server.

### Example with Auth Annotation

```java
import coyote.commons.network.http.auth.Auth;
import coyote.commons.network.http.responder.DefaultResponder;
import coyote.commons.network.http.Status;

@Auth(groups = "admin", required = true)
public class AdminResponder extends DefaultResponder {
    @Override
    public Status getStatus() {
        return Status.OK;
    }

    @Override
    public String getText() {
        return "Welcome, Admin!";
    }
}
```

The `@Auth` annotation can specify:
- `groups`: A comma-separated list of groups allowed to access the resource.
- `required`: Whether authentication is mandatory (default is true).
- `requireSSL`: Whether an SSL connection is required (default is false).

To make this work, you must provide an `AuthProvider` implementation to the server:

```java
server.setAuthProvider(new MyCustomAuthProvider());
```

## Denial of Service (DoS) Protection

The Coyote HTTP Server includes a basic Denial of Service (DoS) protection mechanism that tracks the frequency of requests from individual IP addresses and networks.

### How it Works

When a new connection is established, the server checks the remote IP address against its internal DoS tracker. If the address (or the network it belongs to) has sent too many requests within a specific time window, the connection is immediately closed.

By default, the server allows up to **24 requests every 500 milliseconds** per IP address.

### Key Features

- **Automatic Tracking**: The server automatically begins tracking request frequency for every new IP address that connects.
- **Early Rejection**: Connections are rejected before the HTTP request is even parsed, saving CPU and memory resources.
- **Configurable Limits**: Developers can adjust the global frequency limits or set specific overrides for known IP addresses or networks.

```
server.dosTable.expire(3600000);
```

## Enabling SSL/HTTPS

The Coyote HTTP Server supports secure connections using SSL/TLS. To enable HTTPS, you must provide an `SSLServerSocketFactory` to the server before starting it.

### Preparing a Keystore

To use SSL, you need a Java Keystore (JKS) file containing your server certificate. You can create a self-signed certificate for testing purposes using the `keytool` command:

```bash
keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 360 -keysize 2048
```

This command will prompt you for details and create a file named `keystore.jks` with the password `password`.

### HTTPS with HTTPD Classes

Once you have a keystore, you can enable HTTPS by calling the `makeSecure` method on your server instance.

#### Programmatic Example

```java
import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.responder.HTTPDRouter;

public class SecureServer {
    public static void main(String[] args) {
        HTTPDRouter server = new HTTPDRouter(8443);
        
        try {
            // Load the keystore from the classpath and make the server secure
            // The null parameter uses default SSL protocols
            server.makeSecure(HTTPD.makeSSLSocketFactory("/keystore.jks", "password".toCharArray()), null);
            
            server.start();
            System.out.println("Secure server started on port 8443");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

Make sure the `keystore.jks` file is available in your application's classpath.
