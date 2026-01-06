/*
 *
 */
package coyote.commons.dataframe;


import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class DataFieldTest {

    @Test
    public void testConstructor() {
        String nulltag = null;
        Object nullval = null;

        DataField field = new DataField("");
        field.getValue();

        field = new DataField(nullval);
        field = new DataField(Long.valueOf(0));

        field = new DataField("", "");
        new DataField(nulltag, nullval);

        field = new DataField(0L);
        field = new DataField("", 0L);
        field = new DataField(nulltag, 0L);

        field = new DataField(0);
        field = new DataField("", 0);
        field = new DataField(nulltag, 0);

        field = new DataField((short) 0);
        field = new DataField("", (short) 0);
        field = new DataField(nulltag, (short) 0);

        field = new DataField(new byte[0]);
        field = new DataField("", new byte[0]);
        field = new DataField(nulltag, new byte[0]);

        field = new DataField((byte[]) null);
        field = new DataField(nulltag, null);

        field = new DataField(0f);
        field = new DataField("", 0f);
        field = new DataField(nulltag, 0f);

        field = new DataField(0d);
        field = new DataField("", 0d);
        field = new DataField(nulltag, 0d);

        field = new DataField(true);
        field = new DataField("", true);
        field = new DataField(nulltag, true);

        field = new DataField(new Date());
        field = new DataField("", new Date());
        field = new DataField(null, new Date());

        try {
            field = new DataField(new URI(""));
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
        try {
            field = new DataField("", new URI(""));
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }

        try {
            field = new DataField(nulltag, new URI(""));
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }

        //field = new DataField(null); //ambiguous; DataInputStream or Object?

    }


    @Test
    public void testDataFieldBoolean() {
        DataField field = new DataField(true);
        byte[] data = field.getBytes();
        //System.out.println(ByteUtil.dump( data ));
        assertEquals(3, data.length);
        assertEquals(0, data[0]);
        assertEquals(14, data[1]);
        assertEquals(1, data[2]);

    }


    @Test
    public void testDataFieldStringBoolean() {
        DataField field = new DataField("Test", true);
        byte[] data = field.getBytes();
        //System.out.println(ByteUtil.dump( data ));
        assertEquals(7, data.length);
        assertEquals(4, data[0]);
        assertEquals(84, data[1]);
        assertEquals(101, data[2]);
        assertEquals(115, data[3]);
        assertEquals(116, data[4]);
        assertEquals(14, data[5]);
        assertEquals(1, data[6]);

        field = new DataField("Test", false);
        data = field.getBytes();
        //System.out.println(ByteUtil.dump( data ));
        assertEquals(7, data.length);
        assertEquals(4, data[0]);
        assertEquals(84, data[1]);
        assertEquals(101, data[2]);
        assertEquals(115, data[3]);
        assertEquals(116, data[4]);
        assertEquals(14, data[5]);
        assertEquals(0, data[6]);
    }


    @Test
    public void testClone() {
        DataField original = new DataField("Test", 17345);

        Object copy = original.clone();

        assertNotNull(copy);
        assertInstanceOf(DataField.class, copy);
        DataField field = (DataField) copy;
        assertEquals("Test", field.name);
        assertEquals(7, field.type);
        Object obj = field.getObjectValue();
        assertNotNull(obj);
        assertInstanceOf(Integer.class, obj);
        assertEquals(17345, ((Integer) obj).intValue());
    }


    @Test
    public void testIsNumeric() {
        DataField subject = new DataField("Test", 32767);
        assertTrue(subject.isNumeric());
        subject = new DataField("Test", "32767");
        assertFalse(subject.isNumeric());
    }


    @Test
    public void testToString() {
        DataField subject = new DataField("Test", 32767);
        String text = subject.toString();
        assertNotNull(text);
        assertEquals(48, text.length());

        // Test truncation of long values
        subject = new DataField("Test", "01234567890123456789012345678901234567890123456789");
        text = subject.toString();
        assertNotNull(text);
        assertTrue(text.length() < 170);
    }

}
