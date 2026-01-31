/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.writer;


import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigurableComponent;
import coyote.commons.rtw.FrameWriter;

/**
 * Writes a data frame as a simple JSON string to either standard output
 * (default) or standard error.
 *
 * <p>All data is formatted as an array of JSON objects.</p>
 */
public class JsonWriter extends AbstractFrameFileWriter implements FrameWriter, ConfigurableComponent {

  /**
   *
   */
  @Override
  public void write(final DataFrame frame) {

    // If there is a conditional expression
    if (expression != null) {

      try {
        // if the condition evaluates to true...
        if (evaluator.evaluateBoolean(expression)) {
          writeFrame(frame);
        }
      } catch (final IllegalArgumentException e) {
        Log.warn(String.format( "Writer.boolean_evaluation_error %s %s", expression, e.getMessage()));
      }
    } else {
      // Unconditionally writing frame
      writeFrame(frame);
    }

  }


  /**
   * This is where we actually write the frame.
   *
   * @param frame the frame to be written
   */
  private void writeFrame(final DataFrame frame) {
    if (rowNumber == 0) {
      printwriter.write('[');
    }
    printwriter.write(JSONMarshaler.toFormattedString(frame));
    if (getContext().getTransaction().isLastFrame()) {
      printwriter.write(']');
    } else {
      printwriter.write(',');
    }
    printwriter.flush();
    rowNumber++;
  }

}
