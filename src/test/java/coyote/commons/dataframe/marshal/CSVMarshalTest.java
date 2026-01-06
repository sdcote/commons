/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.dataframe.marshal;



import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.FrameSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * 
 */
public class CSVMarshalTest {

  private File tempFile = null;




  @BeforeEach
  public void setUp() throws IOException {
    tempFile = File.createTempFile("CSVMarshalerTest", ".csv");
    tempFile.deleteOnExit();
  }




  /**
   * Test the full cycle of write-read
   */
  @Test
  public void testWriteRead() throws IOException {
    FrameSet set = new FrameSet();

    DataFrame frame1 = new DataFrame();
    frame1.add("Column1", "first,text");
    frame1.add("Column2", "second,text");
    set.add(frame1);
    DataFrame frame2 = new DataFrame();
    frame2.add("Column1", "third");
    frame2.add("Column2", new Date());
    set.add(frame2);
    DataFrame frame3 = new DataFrame();
    frame3.add("Column1", 123.4D);
    frame3.add("Column2", 1234L);
    set.add(frame3);
    assertTrue(set.size() == 3);

    // create a new writer
    StringWriter sw = new StringWriter();

    // write our frameset to it
    CSVMarshaler.write(set, sw);

    // print result by converting to string
    System.out.println("" + sw.toString());

    CSVMarshaler.write(set, tempFile);

    // Now read it

    FrameSet frameset = CSVMarshaler.read(tempFile);
    assertNotNull(frameset);
    //assertTrue( frameset.size() == 3 );

    // 
  }

}