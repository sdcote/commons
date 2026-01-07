package coyote.commons.network.http;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class LoadKeyStoreTest {


    @Test
    public void loadKeyStoreFromResources() throws Exception {
        final String keyStorePath = "/keystore.jks";
        final InputStream resourceAsStream = this.getClass().getResourceAsStream(keyStorePath);
        assertNotNull(resourceAsStream);

        final SSLServerSocketFactory sslServerSocketFactory = HTTPD.makeSSLSocketFactory(keyStorePath, "password".toCharArray());
        assertNotNull(sslServerSocketFactory);
    }


    @Test
    public void loadKeyStoreFromResourcesWrongPassword() throws Exception {
        final String keyStorePath = "/keystore.jks";
        final InputStream resourceAsStream = this.getClass().getResourceAsStream(keyStorePath);
        assertNotNull(resourceAsStream);

        Assertions.assertThrows(IOException.class, () -> {
            HTTPD.makeSSLSocketFactory(keyStorePath, "wrongpassword".toCharArray());
        });
    }


    @Test
    public void loadNonExistentKeyStoreFromResources() throws Exception {
        final String nonExistentPath = "/nokeystorehere.jks";
        final InputStream resourceAsStream = this.getClass().getResourceAsStream(nonExistentPath);
        assertNull(resourceAsStream);

        Assertions.assertThrows(IOException.class, () -> {
            HTTPD.makeSSLSocketFactory(nonExistentPath, "".toCharArray());
        });
    }

}
