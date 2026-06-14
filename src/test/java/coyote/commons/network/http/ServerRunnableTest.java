package coyote.commons.network.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerRunnableTest {

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testInvalidPort() {
        System.out.println("Running testInvalidPort");
        HTTPD server = new HTTPD(-1) {
            @Override
            public Response serve(HTTPSession session) {
                return Response.createFixedLengthResponse("OK");
            }
        };
        
        assertThrows(IOException.class, () -> {
            server.start();
        });
        server.stop();
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testPortOutOfRange() {
        System.out.println("Running testPortOutOfRange");
        HTTPD server = new HTTPD(70000) {
            @Override
            public Response serve(HTTPSession session) {
                return Response.createFixedLengthResponse("OK");
            }
        };

        assertThrows(IOException.class, () -> {
            server.start();
        });
        server.stop();
    }
}
