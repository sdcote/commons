package coyote.commons.network.http;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvalidRequestTest extends HttpServerTest {


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
    public void testGetRequestWithoutProtocol() {
        invokeServer("GET " + HttpServerTest.URI + "\r\nX-Important-Header: foo");

        assertNotNull(testServer.parms);
        assertTrue(testServer.header.size() > 0);
        assertNotNull(testServer.body);
        assertNotNull(testServer.uri);
    }


    @Test
    public void testGetRequestWithProtocol() {
        invokeServer("GET " + HttpServerTest.URI + " HTTP/1.1\r\nX-Important-Header: foo");

        assertNotNull(testServer.parms);
        assertTrue(testServer.header.size() > 0);
        assertNotNull(testServer.body);
        assertNotNull(testServer.uri);
    }


    @Test
    public void testPostRequestWithoutProtocol() {
        invokeServer("POST " + HttpServerTest.URI + "\r\nContent-Length: 123");
        assertNotNull(testServer.parms);
        assertTrue(testServer.header.size() > 0);
        assertNotNull(testServer.body);
        assertNotNull(testServer.uri);
    }


    @Test
    public void testPostRequestWithProtocol() {
        invokeServer("POST " + HttpServerTest.URI + " HTTP/1.1\r\nContent-Length: 123");
        assertNotNull(testServer.parms);
        assertTrue(testServer.header.size() > 0);
        assertNotNull(testServer.body);
        assertNotNull(testServer.uri);
    }

}
