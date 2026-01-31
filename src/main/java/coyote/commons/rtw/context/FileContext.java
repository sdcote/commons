/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import coyote.commons.dataframe.marshal.MarshalException;
import coyote.commons.log.Log;
import coyote.commons.log.LogMsg;
import coyote.commons.rtw.RTW;
import coyote.commons.rtw.Symbols;


/**
 * This context is persisted to the file system.
 * 
 * <p>An example use case is the sequential numbering of an output file after 
 * each run of a transform. After the transform completes successfully, its 
 * data is persisted to disk so when it initializes the next time, it can 
 * increment values to be used in naming files.</p>
 * 
 * <p>This class is created by the TransformEngineFactory and keys off the 
 * name of the context to determine if it is a regular context or a persistent 
 * context.
 * 
 * <p>Contexts are opened and closed like other components so this component 
 * has the ability to read itself from a file on opening and persist itself to 
 * disk on closing. 
 * 
 * <p>Because FileContexts are simple text files, they can be edited prior to 
 * their respective transforms being run.
 */
public class FileContext extends PersistentContext {
  private static final String FILENAME = "context.json";
  File contextFile = null;




  public FileContext() {
    // default constructor    
  }




  /**
   * 
   */
  @Override
  public void open() {

    contextFile = new File(engine.getJobDirectory(), FILENAME);
    Log.debug("Reading context from " + contextFile.getAbsolutePath());
    String contents = FileUtil.fileToString(contextFile);

    // fill the context with data previously persisted to the file (if any)
    if (StringUtil.isNotBlank(contents)) {
      try {
        List<DataFrame> frames = JSONMarshaler.marshal(contents);
        if (frames.get(0) != null) {
          for (DataField field : frames.get(0).getFields()) {
            set(field.getName(), field.getObjectValue());
          }
        }
      } catch (MarshalException e) {
        Log.warn("Could not load context: " + e.getClass().getSimpleName() + " - " + e.getMessage());
      }
    }

    // now resolve our configuration
    super.open();
  }




  /**
   * 
   */
  @Override
  public void close() {
    super.close();

    // create a data frame to structure our data
    DataFrame frame = new DataFrame();

    // Add each property in the context to the frame
    for (String key : properties.keySet()) {
      try {
        frame.add(key, properties.get(key));
      } catch (Exception e) {
        Log.debug("Cannot persist property '" + key + "' - " + e.getMessage());
      }
    }

    // add the current value of the run counter
    frame.put(Symbols.RUN_COUNT, runcount);

    // Save the current run date
    Object rundate = get(Symbols.DATETIME);
    if (rundate != null) {
      // it should be a date reference
      if (rundate instanceof Date) {
        // format it in the default format
        frame.put(Symbols.PREVIOUS_RUN_DATETIME, new SimpleDateFormat(RTW.DEFAULT_DATETIME_FORMAT).format((Date)rundate));
      } else {
        Log.warn(String.format( "Context.run_date_reset %s", rundate));
      }
    }

    // write the context to disk using JSON 
    FileUtil.stringToFile(JSONMarshaler.toFormattedString(frame), contextFile.getAbsolutePath());

  }
}
