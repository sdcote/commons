package coyote.commons.network.http;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class BadRequestTest extends HttpServerTest {

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
    }

    @AfterEach
    @Override
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testEmptyRequest() throws IOException {
        final ByteArrayOutputStream outputStream = invokeServer("\n\n");
        final String[] expected = new String[]{"HTTP/1.1 400 Bad Request"};
        assertResponse(outputStream, expected);
    }


    @Test
    public void testInvalidMethod() throws IOException {
        final ByteArrayOutputStream outputStream = invokeServer("GETT http://example.com");
        final String[] expected = new String[]{"HTTP/1.1 400 Bad Request"};
        assertResponse(outputStream, expected);
    }


    @Test
    public void testMissingURI() throws IOException {
        final ByteArrayOutputStream outputStream = invokeServer("GET");
        final String[] expected = new String[]{"HTTP/1.1 400 Bad Request"};
        assertResponse(outputStream, expected);
    }

}
