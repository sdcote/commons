/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote
 *      - Initial concept and implementation
 */
package coyote.commons.network.http.responder;

//import static org.junit.Assert.*;

import coyote.commons.NetUtil;
import coyote.commons.cfg.Config;
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
 *
 */
public class ResourceResponderTest {

    private static TestRouter server = null;
    private static int port = 3232;


    /**
     * @throws Exception
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        port = NetUtil.getNextAvailablePort(port);
        server = new TestRouter(port);
        server.addDefaultRoutes();

        // try to start the server, waiting only 2 seconds before giving up
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


    /**
     * @throws Exception
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        server.stop();
    }


    @Test
    public void test200() {
        server.addRoute("/", Integer.MAX_VALUE, ResourceResponder.class, server, new Config().set(ResourceResponder.ROOT_TAG, "content"));
        server.addRoute("/(.)+", Integer.MAX_VALUE, ResourceResponder.class, server, new Config().set(ResourceResponder.ROOT_TAG, "content").set(ResourceResponder.REDIRECT_TAG, true));

        try {
            TestResponse response = TestHttpClient.sendGet("http://localhost:" + port);
            System.out.println(response.getStatus() + ":" + response.getData());
            assertEquals(200, response.getStatus());
            assertTrue(server.isAlive());

            response = TestHttpClient.sendGet("http://localhost:" + port + "/");
            System.out.println(response.getStatus() + ":" + response.getData());
            assertEquals(200, response.getStatus());
            assertTrue(server.isAlive());

        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void test404() {
        server.addRoute("/", Integer.MAX_VALUE, ResourceResponder.class, server, new Config().set(ResourceResponder.ROOT_TAG, "content"));
        server.addRoute("/(.)+", Integer.MAX_VALUE, ResourceResponder.class, server, new Config().set(ResourceResponder.ROOT_TAG, "content").set(ResourceResponder.REDIRECT_TAG, true));

        try {
            TestResponse response = TestHttpClient.sendGet("http://localhost:" + port + "/notfound.txt");
            System.out.println(response.getStatus() + ":" + response.getData());
            assertEquals(404, response.getStatus());
            assertTrue(server.isAlive());
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
}
