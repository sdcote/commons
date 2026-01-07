package coyote.commons.network.http;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.fail;


public class ShutdownTest {

    //@Ignore
    public void connectionsAreClosedWhenServerStops() throws IOException {
        final TestServer server = new TestServer();
        server.start();
        makeRequest();
        server.stop();
        try {
            makeRequest();
            fail("Connection should be closed!");
        } catch (final IOException e) {
            // Expected exception
        }
    }

    private void makeRequest() throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8092/").openConnection();
        // Keep-alive seems to be on by default, but just in case that changes.
        connection.addRequestProperty("Connection", "keep-alive");
        final InputStream in = connection.getInputStream();
        while (in.available() > 0) {
            in.read();
        }
        in.close();
    }

    private class TestServer extends HTTPD {

        public TestServer() {
            super(8092);
        }


        @Override
        public Response serve(final HTTPSession session) {
            return Response.createFixedLengthResponse("Whatever");
        }
    }

}
