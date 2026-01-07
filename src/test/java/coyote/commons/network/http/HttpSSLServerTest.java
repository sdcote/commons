package coyote.commons.network.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class HttpSSLServerTest extends HttpServerTest {

    @Override
    @BeforeEach
    public void setUp() {
        System.setProperty("javax.net.ssl.trustStore", new File("src/test/resources/keystore.jks").getAbsolutePath());
        testServer = new TestServer(9043);
        try {
            testServer.makeSecure(HTTPD.makeSSLSocketFactory("/keystore.jks", "password".toCharArray()), null);
            tempFileManager = new TestTempFileManager();
            testServer.start();
            final long start = System.currentTimeMillis();
            Thread.sleep(100L);
            while (!testServer.wasStarted()) {
                Thread.sleep(100L);
                if ((System.currentTimeMillis() - start) > 2000) {
                    fail("could not start server");
                }
            }
        } catch (final InterruptedException e) {
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }


    @Override
    @AfterEach
    public void tearDown() {
        testServer.stop();
    }


    /**
     * using http to connect to https.
     *
     * @throws ClientProtocolException
     * @throws IOException
     */
    @Test
    public void testHttpOnSSLConnection() throws ClientProtocolException, IOException {
        final DefaultHttpClient httpclient = new DefaultHttpClient();
        final HttpTrace httphead = new HttpTrace("http://localhost:9043/index.html");

        Assertions.assertThrows(ClientProtocolException.class, () -> {
            httpclient.execute(httphead);
        });
    }


    @Test
    public void testSSLConnection() throws IOException {
        final DefaultHttpClient httpclient = new DefaultHttpClient();
        final HttpTrace httphead = new HttpTrace("https://localhost:9043/index.html");
        final HttpResponse response = httpclient.execute(httphead);
        response.getEntity();
        assertEquals(200, response.getStatusLine().getStatusCode());

        assertEquals(9043, testServer.getListeningPort());
        assertTrue(testServer.isAlive());
    }
}
