/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.reader;

import coyote.commons.DataFrameUtil;
import coyote.commons.FileUtil;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import coyote.commons.dataframe.selector.FrameSelector;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.ConfigurableComponent;
import coyote.commons.rtw.FrameReader;
import coyote.commons.rtw.RTW;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;


import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * TODO: Make into a streaming reader with preload optional.
 */
public abstract class MarshalingFrameReader extends AbstractFrameReader implements FrameReader, ConfigurableComponent {

  private final List<DataFrame> buffer = new ArrayList<DataFrame>();
  private volatile int currentFrame = 0;
  private volatile DataFrame nextFrame = null;

  /** Flag indicating all data should be loaded into and read from memory. */
  private final boolean preload = true; // we only support buffered reads

  /** Flag indicating the frame should be be flattened to a single level of fields. */
  private boolean flatten = true;


  /**
   * 
   */
  @Override
  public DataFrame read(TransactionContext context) {
    DataFrame retval;
    if (nextFrame != null) {
      if(flatten){
        retval = DataFrameUtil.flatten(nextFrame);
      } else {
        retval = nextFrame;
      }
    } else {
      retval = null;
    }
    currentFrame++;
    if (preload) {
      if (currentFrame < buffer.size()) {
        nextFrame = buffer.get(currentFrame);
      } else {
        nextFrame = null;
      }
    } else {
      Log.warn("Whoops! We don't support streaming yet");
    }

    // Support the concept of last frame    
    if (nextFrame == null) {
      context.setLastFrame(true);
    }

    return retval;
  }




  /**
   * 
   */
  @Override
  public boolean eof() {
    return nextFrame == null;
  }




  /**
   *
   */
  @Override
  public void open(TransformContext context) {
    setContext(context);
    buffer.clear();
    currentFrame = 0;

    if(getConfiguration().containsIgnoreCase(ConfigTag.FLATTEN)){
      flatten = getBoolean(ConfigTag.FLATTEN);
    }

    // check for a source in our configuration, if not there use the transform 
    // context as it may have been set by a previous operation
    String source = getString(ConfigTag.SOURCE);
    Log.debug(String.format( "Reader.configured_source_is", source));
    if (StringUtil.isNotBlank(source)) {

      File sourceFile = null;
      URI uri = UriUtil.parse(source);
      if (uri != null) {
        sourceFile = UriUtil.getFile(uri);
        if (sourceFile == null) {
          if (uri.getScheme() == null) {
            // Assume a file if there is no scheme
            Log.debug("Source URI did not contain a scheme, assuming a filename");
            sourceFile = new File(source);
          } else {
            Log.warn(String.format( "Reader.source_is_not_file", source));
          }
        }
      } else {
        sourceFile = new File(source);
        Log.debug("Source could not be parsed into a URI, assuming a filename: "+ sourceFile.getPath());
      }
      if (sourceFile != null) {
        Log.debug("Using a source file of " + sourceFile.getPath());
      } else {
        Log.error("Using a source file of NULL_REF");
      }
      // if not absolute, use the CDX fixture to attempt to resolve the relative file
      if (!sourceFile.isAbsolute()) {
        Log.debug("Source file " + sourceFile.getPath()+" is not absolute, resolving...");
        sourceFile = RTW.resolveFile(sourceFile, getContext());
      }
      Log.debug("Using an absolute source file of " + sourceFile.getPath()+" - ("+sourceFile.getAbsolutePath()+")");

      // Basic checks
      if (sourceFile.exists() && sourceFile.canRead()) {
        String data = FileUtil.fileToString(sourceFile);
        Log.debug("Read in " + data.length() + " characters of data");

        List<DataFrame> frames = getFrames(data);
        Log.debug("Read in " + frames.size() + " frames");
        DataFrame frame = frames.get(0);

        String pattern = getString(ConfigTag.SELECTOR);
        if (StringUtil.isNotBlank(pattern)) {
          FrameSelector selector = new FrameSelector(pattern, RTW.DEFAULT_FRAMEPATH_NAME);
          List<DataFrame> results = selector.select(frame);
          Log.debug("Selected " + results.size() + " frames");
          frames = results;
        }

        if (frames.size() > 0) {
          buffer.addAll(frames);
          Log.info("Preloaded " + buffer.size() + " frames");
          nextFrame = buffer.get(currentFrame);
        }

      } else {
        context.setError(String.format( "Reader.could_not_read_from_source", getClass().getName(), sourceFile.getAbsolutePath()).toString());
      }
    } else {
      Log.error("No source specified");
      context.setError(getClass().getName() + " could not determine source");
    }
  }




  /**
   * @param data
   * @return
   */
  protected List<DataFrame> getFrames(String data) {
    return JSONMarshaler.marshal(data);
  }

}
