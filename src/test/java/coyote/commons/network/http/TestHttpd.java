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
package coyote.commons.network.http;


import coyote.commons.NetUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class TestHttpd {

    private static HTTPD server = null;
    private static int port = 54321;


    /**
     * @throws Exception
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {

        port = NetUtil.getNextAvailablePort(port);
        server = new TestingServer(port);

        try {
            server.start(HTTPD.SOCKET_READ_TIMEOUT, true);
        } catch (final IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
            server.stop();
            server = null;
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
    public void test() {
        assertNotNull(server);
        assertEquals(port, server.getPort());
    }

}
