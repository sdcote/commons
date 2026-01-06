/**
 *
 */
package coyote.commons.dataframe;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class models...StringTypeTest
 */
public class StringTypeTest {
    /** The data type under test. */
    static StringType datatype = null;


    /**
     * @throws Exception
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        datatype = new StringType();
    }


    /**
     * @throws Exception
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        datatype = null;
    }


    @Test
    public void testCheckType() {

        String data = "abc";
        assertTrue(datatype.checkType(data));
    }


    @Test
    public void testDecode() {
        byte[] data = new byte[3];
        data[0] = 97;
        data[1] = 98;
        data[2] = 99;
        Object value = datatype.decode(data);
        assertInstanceOf(String.class, value);
        assertEquals("abc", value);
    }


    @Test
    public void testEncode() {
        String data = "abc";
        byte[] value = datatype.encode(data);
        assertEquals(97, value[0]);
        assertEquals(98, value[1]);
        assertEquals(99, value[2]);
        //System.out.println( ByteUtil.dump( value ) );
    }


    @Test
    public void testIsNumeric() {
        assertFalse(datatype.isNumeric());
    }


    @Test
    public void testGetSize() {
        assertEquals(-1, datatype.getSize());
    }


    @Test
    public void testGetTypeName() {
        assertEquals("STR", datatype.getTypeName());
    }

}
