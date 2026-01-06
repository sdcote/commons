/*
 *
 */
package coyote.commons;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class ByteUtilTest {


    @Test
    public void testRenderShortByte() {
        short value = 255;
        byte[] data = new byte[1];
        data[0] = ByteUtil.renderShortByte(value);

        assertEquals(-1, ByteUtil.retrieveShortByte(data, 0));
        assertEquals(255, ByteUtil.retrieveUnsignedShortByte(data, 0));
    }


    @Test
    public void testRenderShort() {
        short value = Short.MIN_VALUE;
        byte[] data = null;
        data = ByteUtil.renderShort(value);

        assertEquals(Short.MIN_VALUE, ByteUtil.retrieveShort(data, 0));
        assertEquals(32768, ByteUtil.retrieveUnsignedShort(data, 0));

        value = Short.MAX_VALUE;
        data = ByteUtil.renderShort(value);

        assertEquals(Short.MAX_VALUE, ByteUtil.retrieveShort(data, 0));
        assertEquals(32767, ByteUtil.retrieveUnsignedShort(data, 0));

        value = -1;
        data = ByteUtil.renderShort(value);

        assertEquals(-1, ByteUtil.retrieveShort(data, 0));
        assertEquals(65535, ByteUtil.retrieveUnsignedShort(data, 0));

        // Test the maximum unsigned value in the unsigned renderer
        int intvalue = 65535;
        data = ByteUtil.renderUnsignedShort(intvalue);

        assertEquals(-1, ByteUtil.retrieveShort(data, 0));
        assertEquals(65535, ByteUtil.retrieveUnsignedShort(data, 0));
    }


    @Test
    public void testRenderInt() {
        int value = Integer.MIN_VALUE;
        byte[] data = null;
        data = ByteUtil.renderInt(value);

        assertEquals(Integer.MIN_VALUE, ByteUtil.retrieveInt(data, 0));
        assertEquals(2147483648L, ByteUtil.retrieveUnsignedInt(data, 0));

        value = Integer.MAX_VALUE;
        data = ByteUtil.renderInt(value);

        assertEquals(Integer.MAX_VALUE, ByteUtil.retrieveInt(data, 0));
        assertEquals(2147483647L, ByteUtil.retrieveUnsignedInt(data, 0));

        value = -1;
        data = ByteUtil.renderInt(value);

        assertEquals(-1, ByteUtil.retrieveInt(data, 0));
        assertEquals(4294967295L, ByteUtil.retrieveUnsignedInt(data, 0));

        // Test the maximum unsigned value in the unsigned renderer
        long longvalue = 4294967295L;
        data = ByteUtil.renderUnsignedInt(longvalue);

        assertEquals(-1, ByteUtil.retrieveInt(data, 0));
        assertEquals(4294967295L, ByteUtil.retrieveUnsignedInt(data, 0));
    }


    @Test
    public void testRenderLong() {
        long value = Long.MIN_VALUE;
        byte[] data = null;
        data = ByteUtil.renderLong(value);

        assertEquals(Long.MIN_VALUE, ByteUtil.retrieveLong(data, 0));

        // assertTrue(ByteUtil.retrieveUnsignedLong(data,0) == 2147483648L);

        value = Long.MAX_VALUE;
        data = ByteUtil.renderLong(value);

        assertEquals(Long.MAX_VALUE, ByteUtil.retrieveLong(data, 0));

        // assertTrue(ByteUtil.retrieveUnsignedLong(data,0) == 2147483647L);

        value = -1;
        data = ByteUtil.renderLong(value);

        assertEquals(-1, ByteUtil.retrieveLong(data, 0));
        // assertTrue(ByteUtil.retrieveUnsignedLong(data,0) == 4294967295L);

        // Test the maximum unsigned value in the unsigned renderer
        // long longvalue = 4294967295L;
        // data = ByteUtil.renderUnsignedILong(longvalue);
        // assertTrue(ByteUtil.retrieveLong(data,0) == -1);
        // assertTrue(ByteUtil.retrieveUnsignedLong(data,0) == 4294967295L);
    }


    @Test
    public void testRenderFloat() {
        float value = Float.MIN_VALUE;
        byte[] data = null;
        data = ByteUtil.renderFloat(value);

        assertEquals(Float.MIN_VALUE, ByteUtil.retrieveFloat(data, 0));

        value = Float.MAX_VALUE;
        data = ByteUtil.renderFloat(value);

        assertEquals(Float.MAX_VALUE, ByteUtil.retrieveFloat(data, 0));

        value = -1;
        data = ByteUtil.renderFloat(value);

        assertEquals(-1, ByteUtil.retrieveFloat(data, 0));
    }


    @Test
    public void testRenderDouble() {
        double value = Double.MIN_VALUE;
        byte[] data = null;
        data = ByteUtil.renderDouble(value);

        assertEquals(Double.MIN_VALUE, ByteUtil.retrieveDouble(data, 0));

        value = Double.MAX_VALUE;
        data = ByteUtil.renderDouble(value);

        assertEquals(Double.MAX_VALUE, ByteUtil.retrieveDouble(data, 0));

        value = -1;
        data = ByteUtil.renderDouble(value);

        assertEquals(-1, ByteUtil.retrieveDouble(data, 0));
        // System.out.println( "renderDouble(" + value + ")\r\n" +
        // ByteUtil.dump(
        // data ) );
        // System.out.println( "retrieveDouble=" + ByteUtil.retrieveDouble(
        // data, 0
        // ) );
        // System.out.println();
    }


    /**
     * Test method for {@link coyote.commons.ByteUtil#renderBoolean(boolean)}.
     */
    @Test
    public void testRenderBoolean() {
        boolean value = true;
        byte[] data = null;
        data = ByteUtil.renderBoolean(value);

        assertEquals(1, data.length);
        assertEquals(1, data[0], "Byte0=" + data[0]);

        value = false;
        data = ByteUtil.renderBoolean(value);

        assertEquals(1, data.length);
        assertEquals(0, data[0], "Byte0=" + data[0]);

        assertFalse(ByteUtil.retrieveBoolean(data, 0));
    }


    /**
     * Test method for
     * {@link coyote.commons.ByteUtil#retrieveBoolean(byte[], int)}.
     */
    @Test
    public void testRetrieveBoolean() {
        byte[] data = new byte[1];
        data[0] = 0;
        assertFalse(ByteUtil.retrieveBoolean(data, 0));

        data[0] = 1;
        assertTrue(ByteUtil.retrieveBoolean(data, 0));
    }


    @Test
    public void testRenderDate() {
        // Wed Aug 11 10:26:04 EDT 2004
        // Time: 1092234364465
        // +000:00--+001:01--+002:02--+003:03--+004:04--+005:05--+006:06--+007:07--+
        // |00000000|00000000|00000000|11111110|01001110|00111101|11000110|00110001|
        // |000:00: |000:00: |000:00: |254:fe: |078:4e:N|061:3d:=|198:c6: |049:31:1|
        // +--------+--------+--------+--------+--------+--------+--------+--------+
        final long TESTMILLIS = 1092234364465L;

        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, 2004);
        cal.set(Calendar.MONTH, 7);
        cal.set(Calendar.DAY_OF_MONTH, 11);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 26);
        cal.set(Calendar.SECOND, 04);
        cal.set(Calendar.MILLISECOND, 465);
        cal.set(Calendar.ZONE_OFFSET, TimeZone.getTimeZone("US/Eastern").getRawOffset());
        Date date = cal.getTime();

        byte[] data = ByteUtil.renderDate(date);

        long longval = ByteUtil.retrieveLong(data, 0);

        assertEquals(0, data[0], "Byte0=" + data[0]);
        assertEquals(0, data[1], "Byte1=" + data[1]);
        assertEquals(0, data[2], "Byte2=" + data[2]);
        assertEquals(-2, data[3], "Byte3=" + data[3]); // signed value
        assertEquals(78, data[4], "Byte4=" + data[4]);

        if (longval - TESTMILLIS == 0) {
            assertEquals(61, data[5], "Byte5=" + data[5]);
            assertEquals(-58, data[6], "Byte6=" + data[6]); // signed value
            assertEquals(49, data[7], "Byte7=" + data[7]);
        } else if (longval - TESTMILLIS != 3600000) // Daylight saving time issue
        {
            fail("Time discrepancy");
        }
    }


    /**
     * Test method for
     * {@link coyote.commons.ByteUtil#retrieveDate(byte[], int)}.
     */
    @Test
    public void testRetrieveDate() {
        Date value = new Date();
        byte[] data = null;
        data = ByteUtil.renderDate(value);

        assertEquals(ByteUtil.retrieveDate(data, 0).getTime(), value.getTime());

        data = ByteUtil.renderDate(null);

        assertEquals(0, ByteUtil.retrieveDate(data, 0).getTime());
    }


    @Test
    public void testRenderUUID() {
        UUID uuid = UUID.randomUUID();
        byte[] data = ByteUtil.renderUUID(uuid);

        String text = uuid.toString();
        text = text.replace("-", "");
        assertNotNull(data);
        assertEquals(16, data.length);

        int x = 0;
        for (int i = 0; i < text.length(); i += 2) {
            assertTrue((data[x++] == (byte) Integer.parseInt(text.substring(i, i + 2), 16)));
        }
    }


    // We need to support UUIDs in the future
    public void testRetrieveUUID() {
        byte[] data = new byte[16];
        data[0] = (byte) 104;
        data[1] = (byte) 186;
        data[2] = (byte) 137;
        data[3] = (byte) 118;
        data[4] = (byte) 105;
        data[5] = (byte) 164;
        data[6] = (byte) 722;
        data[7] = (byte) 151;
        data[8] = (byte) 160;
        data[9] = (byte) 113;
        data[10] = (byte) 215;
        data[11] = (byte) 163;
        data[12] = (byte) 178;
        data[13] = (byte) 82;
        data[14] = (byte) 226;
        data[15] = (byte) 232;

        System.out.println(ByteUtil.dump(data));
        System.out.println();

        UUID uuid = ByteUtil.retrieveUUID(data, 0);

        byte[] byts = ByteUtil.renderUUID(uuid);
        System.out.println(ByteUtil.dump(byts));

    }

}
