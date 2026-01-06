package coyote.commons;

//import static org.junit.Assert.*;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VersionTest {

    /*
     * Test method for Version.toString()
     */
    @Test
    public void testToString() {
        System.out.println();
        Version version = new Version(1, 2, 3, Version.EXPERIMENTAL);
        System.out.println("x: " + version);
        version = new Version(1, 2, 3, Version.DEVELOPMENT);
        System.out.println("d: " + version);
        version = new Version(1, 2, 3, Version.ALPHA);
        System.out.println("a: " + version);
        version = new Version(1, 2, 3, Version.BETA);
        System.out.println("b: " + version);
        version = new Version(1, 2, 3, Version.GENERAL);
        System.out.println("g: " + version);
    }


    @Test
    public void testEquals() {
        Version version = new Version(0, 0, 0);
        Version version1 = new Version(0, 0, 0);

        assertTrue(version.equals(version1), "Empty attributes forward");
        assertTrue(version1.equals(version), "Empty attributes reverse");

        version = new Version(1, 2, 3, Version.EXPERIMENTAL);
        version1 = new Version(1, 2, 3, Version.EXPERIMENTAL);

        assertTrue(version.equals(version1), "Completed attributes forward");
        assertTrue(version1.equals(version), "Completed attributes reverse");

    }

}
