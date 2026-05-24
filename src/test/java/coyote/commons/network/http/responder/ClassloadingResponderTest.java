package coyote.commons.network.http.responder;

import coyote.commons.NetUtil;
import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.TestHttpClient;
import coyote.commons.network.http.TestResponse;
import coyote.commons.network.http.TestRouter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the ClassloadingResponder and its integration with HTTPDRouter.
 */
public class ClassloadingResponderTest {

    private static TestRouter server = null;
    private static int port = 3232;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        port = NetUtil.getNextAvailablePort(port);
        server = new TestRouter(port);
        server.addDefaultRoutes();
        // Add the route as it is configured in DaemonJob
        server.addRoute("/", coyote.DaemonJob.RedirectResponder.class, "/daemonjob/index.html");
        server.addRoute("/daemonjob/(.)+", ClassloadingResponder.class, "daemonjob");

        try {
            server.start(HTTPD.SOCKET_READ_TIMEOUT, true);
            long start = System.currentTimeMillis();
            Thread.sleep(100L);
            while (!server.wasStarted()) {
                Thread.sleep(100L);
                if (System.currentTimeMillis() - start > 2000) {
                    server.stop();
                    fail("could not start server");
                }
            }
        } catch (IOException ioe) {
            fail("could not start server");
        }
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testIndexHtml() {
        try {
            TestResponse response = TestHttpClient.sendGet("http://localhost:" + port + "/daemonjob/index.html");
            assertEquals(200, response.getStatus());
            assertNotNull(response.getData());
            assertTrue(response.getData().contains("<!DOCTYPE html>"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRootRedirect() {
        try {
            // Requesting / should redirect to /daemonjob/index.html
            TestResponse response = TestHttpClient.sendGet("http://localhost:" + port + "/");
            assertEquals(301, response.getStatus()); // 301 is Status.REDIRECT
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCss() {
        try {
            TestResponse response = TestHttpClient.sendGet("http://localhost:" + port + "/daemonjob/css/main.css");
            assertEquals(200, response.getStatus());
            // We can't easily check content-type with TestHttpClient currently, 
            // but we can check if it returned something.
            assertNotNull(response.getData());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testJs() {
        try {
            TestResponse response = TestHttpClient.sendGet("http://localhost:" + port + "/daemonjob/js/app.js");
            assertEquals(200, response.getStatus());
            assertNotNull(response.getData());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test404() {
        try {
            TestResponse response = TestHttpClient.sendGet("http://localhost:" + port + "/daemonjob/missing.html");
            assertEquals(404, response.getStatus());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
