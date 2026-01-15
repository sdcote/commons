package coyote.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SegmentFilterTest {

    @Test
    public void testConstructor0() {
        SegmentFilter filter = null;
        String testPattern = "my.sample.subject";

        try {
            filter = new SegmentFilter(testPattern);
        } catch (Exception ex) {
            fail("Could not parse '" + testPattern + "'");
        }

        String[] segments = filter.getSegments();
        assertEquals(3, segments.length, "SegmentCount wrong");
        assertEquals("my", segments[0], "SegmentContent wrong");
        assertEquals("sample", segments[1], "SegmentContent wrong");
        assertEquals("subject", segments[2], "SegmentContent wrong");
    }


    @Test
    public void testConstructor1() {
        SegmentFilter filter = null;
        String testPattern = "my.sample.>";

        try {
            filter = new SegmentFilter(testPattern);
        } catch (Exception ex) {
            fail("Could not parse '" + testPattern + "'");
        }

        String[] segments = filter.getSegments();
        assertEquals(3, segments.length, "SegmentCount wrong");
        assertEquals("my", segments[0], "SegmentContent wrong");
        assertEquals("sample", segments[1], "SegmentContent wrong");
        assertEquals(">", segments[2], "SegmentContent wrong");
    }

    @Test
    public void testConstructor2() {
        SegmentFilter filter = null;
        String testPattern = "my.simple.*.subject.>";

        try {
            filter = new SegmentFilter(testPattern);
        } catch (Exception ex) {
            fail("Could not parse '" + testPattern + "'");
        }

        String[] segments = filter.getSegments();
        assertEquals(5, segments.length, "SegmentCount wrong");
        assertEquals("my", segments[0], "SegmentCount wrong");
        assertEquals("simple", segments[1], "SegmentCount wrong");
        assertEquals("*", segments[2], "SegmentCount wrong");
        assertEquals("subject", segments[3], "SegmentCount wrong");
        assertEquals(">", segments[4], "SegmentCount wrong");

    }


    @Test
    public void testConstructor3() {
        SegmentFilter filter = null;
        String testPattern = "my.si>mple.*.subject.>";

        try {
            filter = new SegmentFilter(testPattern);
            fail("Should not have parsed '" + testPattern + "'");
        } catch (Exception ex) {
            // good
        }
    }


    @Test
    public void testMatch0() {
        SegmentFilter filter = null;
        String testPattern = "my.simple.*.subject.>";

        try {
            filter = new SegmentFilter(testPattern);
        } catch (Exception ex) {
            fail("Could not parse '" + testPattern + "'");
        }

        assertTrue(filter.matches("my.simple.wildcard.subject.test"));
    }


    @Test
    public void testMatch1() {
        SegmentFilter filter = null;
        String testPattern = "coyote.commons.util.SegmentFilter.>";

        try {
            filter = new SegmentFilter(testPattern);
        } catch (Exception ex) {
            fail("Could not parse '" + testPattern + "'");
        }

        assertTrue(filter.matches("coyote.commons.util.SegmentFilter.class"));
        assertTrue(filter.matches("coyote.commons.util.SegmentFilter.java"));

    }


    @Test
    public void testMatch2() {
        SegmentFilter filter = null;
        String testPattern = "EVENT.>";

        try {
            filter = new SegmentFilter(testPattern);
        } catch (Exception ex) {
            fail("Could not parse '" + testPattern + "'");
        }

        assertTrue(filter.matches("EVENT.Message"));
        assertTrue(filter.matches("EVENT.Metric"));

        if (filter.matches("METRIC.EVENT.description")) {
            fail("Failed to filter 'METRIC'");
        }

    }

}
