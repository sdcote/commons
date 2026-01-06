/*
 *
 */
package coyote.commons.dataframe.marshal;


import coyote.commons.dataframe.DataFrame;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 * @author Steve Cote
 */
public class MapFrameTest {

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testMarshalMap() {
        HashMap data = new HashMap();
        data.put("One", "One");
        data.put("Two", 2);

        MapFrame marshaler = new MapFrame();
        DataFrame frame = marshaler.marshal(data);
        assertNotNull(frame);
        assertEquals(2, frame.getFieldCount());
        assertTrue(frame.contains("One"));
    }


    @SuppressWarnings("rawtypes")
    @Test
    public void testMarshalDataFrame() {
        DataFrame frame = new DataFrame();

        DataFrame vframe = new DataFrame();
        vframe.add("version", "1.7");
        DataFrame sframe = new DataFrame();
        sframe.add("specification", vframe);
        frame.add("java", sframe);

        DataFrame nframe = new DataFrame();
        nframe.add("name", "alice");
        frame.add("user", nframe);

        MapFrame marshaler = new MapFrame();
        Map map = marshaler.marshal(frame);
        assertNotNull(map);
        assertEquals(2, map.size());
        Object obj = map.get("java");
        assertNotNull(obj);
        assertInstanceOf(Map.class, obj);
        Map jmap = (Map) obj;
        assertEquals(1, jmap.size());
        obj = jmap.get("specification");
        assertNotNull(obj);
        assertInstanceOf(Map.class, obj);
        Map smap = (Map) obj;
        assertEquals(1, smap.size());
        obj = smap.get("version");
        assertNotNull(obj);
        assertInstanceOf(String.class, obj);
        String ver = (String) obj;
        assertEquals("1.7", ver);
    }
}
