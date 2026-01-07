package coyote.commons.network.http;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.*;


public class ServerSocketFactoryTest extends HTTPD {

    public static final int PORT = 8192;

    public ServerSocketFactoryTest() {
        super(PORT);

        setServerSocketFactory(new TestFactory());
    }

    @Test
    public void isCustomServerSocketFactory() {
        assertInstanceOf(TestFactory.class, getServerSocketFactory());
    }

    @Test
    public void testCreateServerSocket() {
        ServerSocket ss = null;
        try {
            ss = getServerSocketFactory().create();
        } catch (final IOException e) {
        }
        assertNotNull(ss);
    }

    @Test
    public void testSSLServerSocketFail() {
        final String[] protocols = {""};
        System.setProperty("javax.net.ssl.trustStore", new File("src/test/resources/keystore.jks").getAbsolutePath());
        final ServerSocketFactory ssFactory = new SecureServerSocketFactory(null, protocols);
        ServerSocket ss = null;
        try {
            ss = ssFactory.create();
        } catch (final Exception e) {
        }
        assertNull(ss);

    }

    private class TestFactory implements ServerSocketFactory {

        @Override
        public ServerSocket create() {
            try {
                return new ServerSocket();
            } catch (final IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
