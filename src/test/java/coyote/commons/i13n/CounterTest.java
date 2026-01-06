/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote
 *      - Initial concept and implementation
 */
package coyote.commons.i13n;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class CounterTest {

    /**
     * @throws Exception
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
    }


    /**
     * @throws Exception
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }


    @Test
    public void testIncrement() {
        String NAME = "testIncrement";
        long LIMIT = 10;

        Counter counter = new Counter(NAME);

        for (int x = 0; x < LIMIT; x++) {
            counter.increment();
        }

        assertEquals(counter.getValue(), LIMIT, "Value is " + counter.getValue() + " and should be " + LIMIT);
        assertEquals(counter.getMaxValue(), LIMIT, "MaxValue is " + counter.getMaxValue() + " and should be " + LIMIT);
        assertEquals(0, counter.getMinValue(), "MinValue is " + counter.getMinValue() + " and should be 0");
        assertEquals(counter.getUpdateCount(), LIMIT, "UpdateCount is " + counter.getUpdateCount() + " and should be " + LIMIT);
    }


    @Test
    public void testConstructor() {
        String NAME = "test";
        Counter counter = new Counter(NAME);

        assertEquals(counter.getName(), NAME);
        assertEquals(0, counter.getValue());
        assertEquals(0, counter.getMaxValue());
        assertEquals(0, counter.getMinValue());
        assertNull(counter.getUnits());
        assertEquals(0, counter.getUpdateCount());
    }


    @Test
    public void testReset() {
        String NAME = "testReset";
        long LIMIT = 10;

        Counter counter = new Counter(NAME);

        for (int x = 0; x < LIMIT; x++) {
            counter.increment();
        }

        Counter delta = counter.reset();

        assertEquals(delta.getName(), NAME);
        assertEquals(delta.getValue(), LIMIT);
        assertEquals(delta.getMaxValue(), LIMIT);
        assertEquals(0, delta.getMinValue());
        assertNull(delta.getUnits());
        assertEquals(delta.getUpdateCount(), LIMIT);

        assertEquals(counter.getName(), NAME);
        assertEquals(0, counter.getValue());
        assertEquals(0, counter.getMaxValue());
        assertEquals(0, counter.getMinValue());
        assertNull(counter.getUnits());
        assertEquals(0, counter.getUpdateCount());

    }

}
