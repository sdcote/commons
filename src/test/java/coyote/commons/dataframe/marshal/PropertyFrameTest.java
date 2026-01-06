package coyote.commons.dataframe.marshal;


import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;


public class PropertyFrameTest {

    @Test
    public void testMarshalProperties() {
        PropertyFrame marshaler = new PropertyFrame();
        DataFrame frame = marshaler.marshal(System.getProperties(), true);
        assertNotNull(frame);

        Properties props = new Properties();
        props.setProperty("java.specification.version", "1.7");
        props.setProperty("user.name", "alice");
        props.setProperty("dir", "/tmp");

        frame = marshaler.marshal(props, true);
        assertNotNull(frame);
        assertEquals(3, frame.getFieldCount());
        Object value = frame.getField("user");
        assertNotNull(value);
        assertInstanceOf(DataField.class, value);
        DataField field = (DataField) value;
        assertTrue(field.isFrame());
        DataFrame uframe = (DataFrame) field.getObjectValue();
        assertEquals(1, uframe.getFieldCount());
        value = uframe.getField("name");
        assertNotNull(value);
        field = (DataField) value;
        String val = (String) field.getObjectValue();
        assertEquals("alice", val);
    }


    @Test
    public void testMarshalFrame() {
        DataFrame frame = new DataFrame();

        DataFrame vframe = new DataFrame();
        vframe.add("version", "1.7");
        DataFrame sframe = new DataFrame();
        sframe.add("specification", vframe);
        frame.add("java", sframe);

        DataFrame nframe = new DataFrame();
        nframe.add("name", "alice");
        frame.add("user", nframe);

        PropertyFrame marshaler = new PropertyFrame();
        Properties props = marshaler.marshal(frame);
        assertNotNull(props);
        assertEquals(2, props.size());
        assertNotNull(props.getProperty("java.specification.version"));
        assertEquals("1.7", props.getProperty("java.specification.version"));
        assertNotNull(props.getProperty("user.name"));
        assertEquals("alice", props.getProperty("user.name"));
    }
}
