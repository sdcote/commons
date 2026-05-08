# HTTP Server Developer Guide

This guide explains how to programmatically create and extend the Coyote HTTP Server.

## Programmatic Server Creation

The core of the HTTP server is the `HTTPD` class. For routing capabilities, use the `HTTPDRouter` class.

### Simple HTTP Server

```java
import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;

public class MyServer extends HTTPD {
    public MyServer(int port) {
        super(port);
    }

    @Override
    public Response serve(HTTPSession session) {
        return Response.createFixedLengthResponse("Hello World");
    }

    public static void main(String[] args) throws Exception {
        MyServer server = new MyServer(8080);
        server.start();
    }
}
```

### Server with Routing

The `HTTPDRouter` allows you to map URI patterns to `Responder` classes.

```java
import coyote.commons.network.http.responder.HTTPDRouter;
import coyote.commons.network.http.responder.StaticPageResponder;

HTTPDRouter server = new HTTPDRouter(8080);
server.addRoute("/index.html", StaticPageResponder.class);
server.start();
```

## Responder Architecture

Responders are short-lived objects instantiated for each request. They must implement the `Responder` interface or extend one of the base classes:

- `DefaultResponder`: Base class for text/HTML responses.
- `DefaultStreamResponder`: Base class for binary/streamed responses.
- `AbstractJsonResponder`: Base class for JSON web services.

### Custom Responder Example

```java
import coyote.commons.network.http.responder.DefaultResponder;
import coyote.commons.network.http.Status;
import coyote.commons.network.http.responder.Resource;
import coyote.commons.network.http.HTTPSession;
import java.util.Map;

public class MyResponder extends DefaultResponder {
    @Override
    public Status getStatus() {
        return Status.OK;
    }

    @Override
    public String getText() {
        return "Custom Response Content";
    }
    
    @Override
    public String getMimeType() {
        return "text/plain";
    }
}
```

### JSON Responder Example

When extending `AbstractJsonResponder`, you typically populate the `results` DataFrame.

```java
import coyote.commons.network.http.responder.AbstractJsonResponder;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.responder.Resource;
import coyote.commons.network.http.HTTPSession;
import java.util.Map;

public class MyJsonService extends AbstractJsonResponder {
    @Override
    public Response get(Resource resource, Map<String, String> urlParams, HTTPSession session) {
        results.add("status", "success");
        results.add("data", "some value");
        return super.get(resource, urlParams, session); // Returns fixed length response with results as JSON
    }
}
```

## Security Model

The security model is based on the `AuthProvider` interface and the `@Auth` annotation.

### AuthProvider Interface

The `AuthProvider` is responsible for:
1. `isAuthenticated(session)`: Checking if the session is authenticated.
2. `isAuthorized(session, groups)`: Checking if the authenticated user belongs to the required groups.
3. `isSecureConnection(session)`: Checking if the connection is via SSL.

### Implementing a Custom AuthProvider

To create a custom `AuthProvider`, implement the `AuthProvider` interface. Below is an example of a simple `BasicAuthProvider` that checks for a static username and password in the request parameters (note: this is for illustration purposes; use more secure methods for production).

```java
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.auth.AuthProvider;
import java.util.Map;
import java.util.Arrays;

public class MyAuthProvider implements AuthProvider {

    @Override
    public boolean isAuthenticated(HTTPSession session) {
        // Check if the session already has a username associated with it
        return session.getUserName() != null;
    }

    @Override
    public boolean isAuthorized(HTTPSession session, String groups) {
        if (!isAuthenticated(session)) return false;
        
        // If no groups are required, then being authenticated is enough
        if (groups == null || groups.trim().isEmpty()) return true;

        // Check if the user is in any of the required groups
        String[] requiredGroups = groups.split(",");
        for (String group : requiredGroups) {
            if (session.getUserGroups().contains(group.trim())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSecureConnection(HTTPSession session) {
        return session.isSecure();
    }

    @Override
    public boolean authenticate(HTTPSession session, Map<String, String> credentials) {
        String username = credentials.get(AuthProvider.USERNAME);
        String password = credentials.get(AuthProvider.PASSWORD);

        // Simple check (in production, use a database or LDAP)
        if ("admin".equals(username) && "secret".equals(password)) {
            session.setUserName(username);
            session.setUserGroups(Arrays.asList("admin", "users"));
            return true;
        }
        return false;
    }
}
```

### Registering an AuthProvider

```java
server.setAuthProvider(new MyAuthProvider());
```

### Using @Auth Annotation

The `HTTPDRouter` checks for the `@Auth` annotation on the responder class or the specific method being called. If present, it delegates the check to the registered `AuthProvider`.

```java
@Auth(groups = "developers")
public class DevResponder extends DefaultResponder {
    // ...
}
```

## Advanced Configuration

### IP Access Control Lists (ACL)

You can restrict access based on IP address:

```java
server.addToACL(new IpNetwork("192.168.1.0/24"), true); // Allow local network
server.setDefaultAllow(false); // Deny everything else
```

### SSL/TLS

To enable HTTPS, you must provide an `SSLServerSocketFactory`:

```java
server.makeSecure(HTTPD.makeSSLSocketFactory("/keystore.jks", "password".toCharArray()), null);
```

## Denial of Service (DoS) Configuration

The DoS capability is managed by the `OperationFrequency` class, accessible via the `dosTable` protected field in the `HTTPD` class.

### Global Limits

You can adjust the global frequency limits that apply to all new connections.

```java
// Set the global limit to 50 requests every 1000 milliseconds
server.dosTable.setLimit((short)50);
server.dosTable.setDuration(1000);
```

### Specific Overrides

You can define specific limits for certain IP addresses or networks. This is useful for allowing higher traffic from trusted sources or more strictly limiting suspicious ones.

```java
import coyote.commons.network.IpAddress;
import coyote.commons.network.IpNetwork;

// Allow a trusted IP to make 100 requests every 500ms
server.dosTable.addAddress(new IpAddress("192.168.1.50"), (short)100, 500);

// Limit an entire network to 10 requests every 1000ms
server.dosTable.addNetwork(new IpNetwork("203.0.113.0/24"), (short)10, 1000);
```

### Expiring Inactive Entries

To prevent the `dosTable` from growing indefinitely, you can periodically expire entries that haven't been seen for a while.

```java
// Remove any entries that haven't made a request in the last hour
server.dosTable.expire(3600000);
```

For details on how the HTTP server is implemented and used within the `DaemonJob`, see the [DaemonJob HTTP Server Guide](daemonjob-http-server.md).
