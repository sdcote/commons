/*
 * Copyright (c) 2003 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.dataframe.selector;

import coyote.commons.SegmentFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class SegmentFilterTest {

    /**
     * Method testConstructor0
     */
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
        assertEquals(3, segments.length);
        assertEquals("my", segments[0]);
        assertEquals("sample", segments[1]);
        assertEquals("subject", segments[2]);
    }


    /**
     * Method testConstructor1
     */
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
        assertEquals(3, segments.length);
        assertEquals("my", segments[0]);
        assertEquals("sample", segments[1]);
        assertEquals(">", segments[2]);
    }


    /**
     * Method testConstructor2
     */
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
        assertEquals(5, segments.length);
        assertEquals("my", segments[0]);
        assertEquals("simple", segments[1]);
        assertEquals("*", segments[2]);
        assertEquals("subject", segments[3]);
        assertEquals(">", segments[4]);

        // System.out.println( filter.toString() );
        // for( int i = 0; i < segments.length; i++ )
        // {
        // System.out.println( "segment" + i + ": '" + segments[i] + "'" );
        // }

    }


    /**
     * Method testConstructor3
     */
    @Test
    public void testConstructor3() {
        String testPattern = "my.si>mple.*.subject.>";

        try {
            new SegmentFilter(testPattern);

            fail("Should not have parsed '" + testPattern + "'");
        } catch (Exception ex) {
            // good
        }
    }


    /**
     * Method testMatch0
     */
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


    /**
     * Method testMatch1
     */
    @Test
    public void testMatch1() {
        SegmentFilter filter = null;
        String testPattern = "net.bralyn.util.SegmentFilter.>";

        try {
            filter = new SegmentFilter(testPattern);
        } catch (Exception ex) {
            fail("Could not parse '" + testPattern + "'");
        }

        assertTrue(filter.matches("net.bralyn.util.SegmentFilter.class"));
        assertTrue(filter.matches("net.bralyn.util.SegmentFilter.java"));

    }


    /**
     * Method testMatch2
     */
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


    @Test
    public void tooManySegmentsBug() {
        String testPattern = "CVE_Items.*.cve";
        SegmentFilter filter = new SegmentFilter(testPattern);
        assertTrue(filter.matches("CVE_Items.[3].cve"));
        assertFalse(filter.matches("CVE_Items.[3].cve.references.reference_data.[0]"));
    }

}