package coyote.commons.network.http;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class HttpHeadRequestTest extends HttpServerTest {

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        final String responseBody = "Success!";
        testServer.response = Response.createFixedLengthResponse(responseBody);
    }

    @AfterEach
    @Override
    public void tearDown(){
        super.tearDown();
    }

    @Test
    public void testDecodingFieldWithEmptyValueAndFieldWithMissingValueGiveDifferentResults() {
        invokeServer("HEAD " + HttpServerTest.URI + "?foo&bar= HTTP/1.1");
        assertInstanceOf(List.class, testServer.decodedParamters.get("foo"));
        assertEquals(0, testServer.decodedParamters.get("foo").size());
        assertInstanceOf(List.class, testServer.decodedParamters.get("bar"));
        assertEquals(1, testServer.decodedParamters.get("bar").size());
        assertEquals("", testServer.decodedParamters.get("bar").get(0));
    }


    @Test
    public void testDecodingMixtureOfParameters() {
        invokeServer("HEAD " + HttpServerTest.URI + "?foo=bar&foo=baz&zot&zim= HTTP/1.1");
        assertInstanceOf(List.class, testServer.decodedParamters.get("foo"));
        assertEquals(2, testServer.decodedParamters.get("foo").size());
        assertEquals("bar", testServer.decodedParamters.get("foo").get(0));
        assertEquals("baz", testServer.decodedParamters.get("foo").get(1));
        assertInstanceOf(List.class, testServer.decodedParamters.get("zot"));
        assertEquals(0, testServer.decodedParamters.get("zot").size());
        assertInstanceOf(List.class, testServer.decodedParamters.get("zim"));
        assertEquals(1, testServer.decodedParamters.get("zim").size());
        assertEquals("", testServer.decodedParamters.get("zim").get(0));
    }


    @Test
    public void testDecodingParametersFromParameterMap() {
        invokeServer("HEAD " + HttpServerTest.URI + "?foo=bar&foo=baz&zot&zim= HTTP/1.1");
        assertEquals(testServer.decodedParamters, testServer.decodedParamtersFromParameter);
    }


    @Test
    public void testDecodingParametersWithSingleValue() {
        invokeServer("HEAD " + HttpServerTest.URI + "?foo=bar&baz=zot HTTP/1.1");
        assertEquals("foo=bar&baz=zot", testServer.queryParameterString);
        assertInstanceOf(List.class, testServer.decodedParamters.get("foo"));
        assertEquals(1, testServer.decodedParamters.get("foo").size());
        assertEquals("bar", testServer.decodedParamters.get("foo").get(0));
        assertInstanceOf(List.class, testServer.decodedParamters.get("baz"));
        assertEquals(1, testServer.decodedParamters.get("baz").size());
        assertEquals("zot", testServer.decodedParamters.get("baz").get(0));
    }


    @Test
    public void testDecodingParametersWithSingleValueAndMissingValue() {
        invokeServer("HEAD " + HttpServerTest.URI + "?foo&baz=zot HTTP/1.1");
        assertEquals("foo&baz=zot", testServer.queryParameterString);
        assertInstanceOf(List.class, testServer.decodedParamters.get("foo"));
        assertEquals(0, testServer.decodedParamters.get("foo").size());
        assertInstanceOf(List.class, testServer.decodedParamters.get("baz"));
        assertEquals(1, testServer.decodedParamters.get("baz").size());
        assertEquals("zot", testServer.decodedParamters.get("baz").get(0));
    }


    @Test
    public void testDecodingSingleFieldRepeated() {
        invokeServer("HEAD " + HttpServerTest.URI + "?foo=bar&foo=baz HTTP/1.1");
        assertInstanceOf(List.class, testServer.decodedParamters.get("foo"));
        assertEquals(2, testServer.decodedParamters.get("foo").size());
        assertEquals("bar", testServer.decodedParamters.get("foo").get(0));
        assertEquals("baz", testServer.decodedParamters.get("foo").get(1));
    }


    @Test
    public void testEmptyHeadersSuppliedToServeMethodFromSimpleWorkingGetRequest() {
        invokeServer("HEAD " + HttpServerTest.URI + " HTTP/1.1");
        assertNotNull(testServer.parms);
        assertNotNull(testServer.header);
        assertNotNull(testServer.body);
        assertNotNull(testServer.uri);
    }


    @Test
    public void testHeadRequestDoesntSendBackResponseBody() throws Exception {
        final ByteArrayOutputStream outputStream = invokeServer("HEAD " + HttpServerTest.URI + " HTTP/1.1");
        final String[] expected = {"HTTP/1.1 200 OK", "Content-Type: text/html", "Date: .*", "Connection: keep-alive", "Content-Length: 8", ""};
        assertResponse(outputStream, expected);
    }


    @Test
    public void testMultipleGetParameters() {
        invokeServer("HEAD " + HttpServerTest.URI + "?foo=bar&baz=zot HTTP/1.1");
        assertEquals("bar", testServer.parms.get("foo"));
        assertEquals("zot", testServer.parms.get("baz"));
    }


    @Test
    public void testMultipleGetParametersWithMissingValue() {
        invokeServer("HEAD " + HttpServerTest.URI + "?foo=&baz=zot HTTP/1.1");
        assertEquals("", testServer.parms.get("foo"));
        assertEquals("zot", testServer.parms.get("baz"));
    }


    @Test
    public void testMultipleGetParametersWithMissingValueAndRequestHeaders() {
        invokeServer("HEAD " + HttpServerTest.URI + "?foo=&baz=zot HTTP/1.1\nAccept: text/html");
        assertEquals("", testServer.parms.get("foo"));
        assertEquals("zot", testServer.parms.get("baz"));
        assertEquals("text/html", testServer.header.get("accept"));
    }


    @Test
    public void testMultipleHeaderSuppliedToServeMethodFromSimpleWorkingGetRequest() {
        final String userAgent = "jUnit Test";
        final String accept = "text/html";
        invokeServer("HEAD " + HttpServerTest.URI + " HTTP/1.1\nUser-Agent: " + userAgent + "\nAccept: " + accept);
        assertEquals(userAgent, testServer.header.get("user-agent"));
        assertEquals(accept, testServer.header.get("accept"));
    }


    @Test
    public void testSingleGetParameter() {
        invokeServer("HEAD " + HttpServerTest.URI + "?foo=bar HTTP/1.1");
        assertEquals("bar", testServer.parms.get("foo"));
    }


    @Test
    public void testSingleGetParameterWithNoValue() {
        invokeServer("HEAD " + HttpServerTest.URI + "?foo HTTP/1.1");
        assertEquals("", testServer.parms.get("foo"));
    }


    @Test
    public void testSingleUserAgentHeaderSuppliedToServeMethodFromSimpleWorkingGetRequest() {
        final String userAgent = "jUnit Test";
        invokeServer("HEAD " + HttpServerTest.URI + " HTTP/1.1\nUser-Agent: " + userAgent + "\n");
        assertEquals(userAgent, testServer.header.get("user-agent"));
        assertEquals(Method.HEAD, testServer.method);
        assertEquals(HttpServerTest.URI, testServer.uri);
    }

}
