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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class EventListTest {

    /**
     * @throws Exception
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }


    //@Test
    public void testLastSequence() {
        fail("Not yet implemented");
    }


    //@Test
    public void testEventList() {
        fail("Not yet implemented");
    }


    @Test
    public void testGetMaxEvents() {
        EventList list = new EventList();
        list.setMaxEvents(5);

        AppEvent alert0 = list.createEvent("Zero");
        AppEvent alert1 = list.createEvent("One");
        AppEvent alert2 = list.createEvent("Two");
        AppEvent alert3 = list.createEvent("Three");
        AppEvent alert4 = list.createEvent("Four");
        AppEvent alert5 = list.createEvent("Five");
        AppEvent alert6 = list.createEvent("Six");
        //System.out.println( "Max="+list.getMaxEvents()+" Size=" + list.getSize() );
        assertEquals(5, list._list.size());

        // should result in the list being trimmed immediately
        list.setMaxEvents(2);
        assertEquals(2, list._list.size());

        list.add(alert0);
        list.add(alert1);
        list.add(alert2);
        list.add(alert3);
        list.add(alert4);
        list.add(alert5);
        list.add(alert6);

        // should still only contain 2 events
        assertEquals(2, list._list.size());

        // Check the first and last event in the list
        assertEquals(alert5, list.getFirst());
        assertEquals(alert6, list.getLast());
    }


    //@Test
    public void testSetMaxEvents() {
        fail("Not yet implemented");
    }


    //@Test
    public void testAdd() {
        fail("Not yet implemented");
    }


    //@Test
    public void testRemove() {
        fail("Not yet implemented");
    }


    //@Test
    public void testGet() {
        fail("Not yet implemented");
    }


    //@Test
    public void testGetFirst() {
        fail("Not yet implemented");
    }


    //@Test
    public void testGetLast() {
        fail("Not yet implemented");
    }


    //@Test
    public void testGetSize() {
        fail("Not yet implemented");
    }


    //@Test
    public void testCreateEventStringStringStringStringIntIntIntString() {
        fail("Not yet implemented");
    }


    //@Test
    public void testCreateEventString() {
        fail("Not yet implemented");
    }


    //@Test
    public void testCreateEventStringIntInt() {
        fail("Not yet implemented");
    }

}
