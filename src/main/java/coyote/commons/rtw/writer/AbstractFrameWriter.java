/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.writer;


import coyote.commons.StringUtil;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.rtw.AbstractConfigurableComponent;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.FrameWriter;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.rtw.eval.Evaluator;

import java.io.IOException;

/**
 * Base class for all frame writers
 */
public abstract class AbstractFrameWriter extends AbstractConfigurableComponent implements FrameWriter {
    protected Evaluator evaluator = new Evaluator();
    protected String expression = null;




    /**
     *
     */
    public void open(TransformContext context) {
        super.context = context;

        evaluator.setContext(context);

        // Look for a conditional statement the writer may use to control if it is
        // to write the record or not
        expression = getConfiguration().getString(ConfigTag.CONDITION);
        if (StringUtil.isNotBlank(expression)) {
            expression = expression.trim();

            try {
                evaluator.evaluateBoolean(expression);
            } catch (final IllegalArgumentException e) {
                context.setError("Invalid boolean expression in writer: " + e.getMessage());
            }
        }

    }




    /**
     * @return the target URI to which the writer will write
     */
    public String getTarget() {
        return configuration.getAsString(ConfigTag.TARGET);
    }




    /**
     * Set the URI to where the write will write its data.
     *
     * @param value the URI to where the writer should write its data
     */
    public void setTarget(final String value) {
        configuration.put(ConfigTag.TARGET, value);
    }




    /**
     * @see java.io.Closeable#close()
     */
    public void close() throws IOException {}




    /**
     *
     */
    @Override
    public void write(DataFrame frame) {}

}