package coyote.commons.network.http;


import coyote.commons.network.MimeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class MimeTest {

    @Test
    public void testExistingMimeType() throws Exception {
        assertEquals("text/html", MimeType.get("xxxx.html").get(0).getType());
    }


    @Test
    public void testManualMimeType() throws Exception {
        MimeType.add("flv", "video/manualOverwrite", true);
        // all mimetypes are normalized to lowercase as per RFC 2045 pg.13
        assertEquals("video/manualoverwrite", MimeType.get("xxxx.flv").get(0).getType());

    }


    @Test
    public void testNotExistingMimeType() throws Exception {
        assertNotNull(MimeType.get("notExistent")); // at least "unknown"
        assertEquals("application/octet-stream", MimeType.get("xxxx.notExistent").get(0).getType());
    }


    @Test
    public void testOverwritenMimeType() throws Exception {
        assertEquals("application/octet-stream", MimeType.get("xxxx.ts").get(0).getType());
    }
}
