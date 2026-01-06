/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.dataframe.selector;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class FrameSelectorTest {

    private static String json;


    /**
     * @throws Exception
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        ClassLoader classLoader = FrameSelectorTest.class.getClassLoader();
        File file = new File(classLoader.getResource("nvdcve.json").getFile());
        byte[] bytes = new byte[Long.valueOf(file.length()).intValue()];
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            dis.readFully(bytes);
        } catch (final Exception ignore) {
        }
        json = new String(bytes);
    }


    @Test
    public void readArrayData() {
        List<DataFrame> frames = JSONMarshaler.marshal(json);
        assertEquals(1, frames.size());
        DataFrame frame = frames.get(0);

        FrameSelector selector = new FrameSelector("CVE_Items.*.cve");
        List<DataFrame> results = selector.select(frame);

        assertEquals(4, results.size());
    }


    /**
     * Test the ability of the selector to publish the hierarchy of the selected
     * frame is a field with a specific name.
     */
    @Test
    public void readArrayDataWithPath() {
        List<DataFrame> frames = JSONMarshaler.marshal(json);
        assertEquals(1, frames.size());
        DataFrame frame = frames.get(0);

        FrameSelector selector = new FrameSelector("CVE_Items.*.cve", "path");
        List<DataFrame> results = selector.select(frame);
        assertEquals(4, results.size());

        DataFrame child = results.get(0);
        assertNotNull(child);
        assertEquals("CVE_Items.[0].cve", child.getAsString("path"));
        child = results.get(1);
        assertNotNull(child);
        assertEquals("CVE_Items.[1].cve", child.getAsString("path"));
        child = results.get(2);
        assertNotNull(child);
        assertEquals("CVE_Items.[2].cve", child.getAsString("path"));
        child = results.get(3);
        assertNotNull(child);
        assertEquals("CVE_Items.[3].cve", child.getAsString("path"));
    }

}
