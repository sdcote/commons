/**
 *
 */
package coyote.commons.dataframe;


import coyote.commons.ByteUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class DateTypeTest {
    /** The data type under test. */
    private static DateType datatype = null;
    private static final byte[] datedata = new byte[8];
    private static Calendar cal = null;


    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        datatype = new DateType();
        //SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        datedata[0] = (byte) 0;
        datedata[1] = (byte) 0;
        datedata[2] = (byte) 0;
        datedata[3] = (byte) 191;
        datedata[4] = (byte) 169;
        datedata[5] = (byte) 97;
        datedata[6] = (byte) 245;
        datedata[7] = (byte) 248;

        cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, 1996);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 23);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.ZONE_OFFSET, TimeZone.getTimeZone("US/Eastern").getRawOffset());
    }


    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        datatype = null;
    }


    @Test
    public void testCheckType() {
        Date date = new Date();
        assertTrue(datatype.checkType(date));
    }


    @Test
    public void testDecode() {
        System.out.println("=================================================================================\r\n");
        System.out.println("Decoding test data:\r\n" + ByteUtil.dump(datedata));
        Object obj = datatype.decode(datedata);
        assertNotNull(obj);
        assertInstanceOf(Date.class, obj);
        Date date = (Date) obj;
        System.out.println("Decoded as: " + date);
        System.out.println("a long value of: " + date.getTime());
        assertEquals(cal.getTime(), date);
        System.out.println("Test Completed Successfully =====================================================\r\n");
    }


    @Test
    public void testEncode() {
        Date date = cal.getTime();
        System.out.println("Encoding date '" + date + "' as long value: " + date.getTime());
        System.out.println("Expecting an encoding of:\r\n" + ByteUtil.dump(datedata));
        byte[] data = datatype.encode(date);
        assertNotNull(data);
        System.out.println("Encoded as:\r\n" + ByteUtil.dump(data));
        assertEquals(8, data.length, "Dat should be 8 bytes in length, is actually " + data.length + " bytes");
        for (int i = 0; i < datedata.length; i++) {
            assertEquals(data[i], datedata[i], "element " + i + " should be " + datedata[i] + " but is '" + data[i] + "'");
        }
    }


    @Test
    public void testGetTypeName() {
        assertEquals("DAT", datatype.getTypeName());
    }


    @Test
    public void testIsNumeric() {
        assertFalse(datatype.isNumeric());
    }


    @Test
    public void testGetSize() {
        assertEquals(8, datatype.getSize());
    }

}
