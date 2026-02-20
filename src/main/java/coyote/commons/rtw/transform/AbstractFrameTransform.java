/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.transform;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.rtw.AbstractConfigurableComponent;
import coyote.commons.rtw.ConfigurableComponent;
import coyote.commons.rtw.FrameTransform;
import coyote.commons.rtw.context.TransformContext;

import java.io.IOException;




/**
 * Base class for frame transformers
 * 
 * <p>This class works like a clone operation except each field is checked for 
 * a name matching the pattern of a transform action. When it matches, the 
 * value is passed to the transform action for processing.</p>
 * 
 * <p>A common use case for frame transformation is encryption of data. Fields 
 * are stored and transferred in an encrypted format, but need to be decrypted 
 * before use.</p>
 * 
 * <p>Another use case for the transform is collecting metrics on the frames 
 * observed and aggregating values for post processing and context listeners to 
 * report.</p>
 */
public abstract class AbstractFrameTransform extends AbstractConfigurableComponent implements FrameTransform, ConfigurableComponent {

  /**
   * @see java.io.Closeable#close()
   */
  public void close() throws IOException {}




  /**
   * All components are initialized through the {@code open(TransformContext)} method.
   *
   * <p>A last chack of the configuration parameters should be performed here
   * to make sure there are no issues or conflicts with other settings. For
   * example, setting that were present during configuration may have changed
   * at the time of initialization. If there are any issues during
   * initialization, simply place an error in the Transform context:
   * {@code context.setError("Initialization error");}</p>
   *
   * <p>Once all components have been initialized, the engine will start
   * running, unless there is an error in the Transform context.</p>
   *
   * @param context The transform context all components share.
   */
  public void open(final TransformContext context) {
    super.setContext(context);
  }




  /**
   * Resolve the argument.
   * 
   * <p>This has the transform context resolve the argument.
   * 
   * @param value the value to resolve (or use as a literal)
   * 
   * @return the resolved value of the argument. 
   */
  protected String resolveArgument(final String value) {
    return context.resolveArgument(value);
  }




  /**
  * Resolve the argument by looking for fields in the various holding areas.
  * 
  * <p>This method will assume that the name of the file is prepended with a 
  * locator token such as "Working.", "Source.", "Context.", etc.
  * 
  * @param arg the locator pattern for the field.
  * 
  * @return the resolved value of the argument or null if a field with that 
  *         name could not be found. 
  */
  protected String resolveField(final String arg) {
    return context.resolveField(arg);
  }




  /**
   *
   */
  @Override
  public void preload(DataFrame frame) {
    // override to receive historic data, if the job is configured with a preloader    
  }

}
