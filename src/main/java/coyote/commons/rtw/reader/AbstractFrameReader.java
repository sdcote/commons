/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.reader;

import coyote.commons.log.Log;
import coyote.commons.rtw.*;
import coyote.commons.rtw.context.TransformContext;

import java.io.IOException;

/**
 *
 */
public abstract class AbstractFrameReader extends AbstractConfigurableComponent implements FrameReader, ConfigurableComponent {
    protected long readLimit = 0;
    protected volatile long recordCounter = 0;




    /**
     * @return true of the reader has a limit on the number of records it reads,
     *         false otherwise
     */
    protected boolean isLimitingReads() {
        return readLimit > 0;
    }




    /**
     * @return the number of reads to which the reader is limited.
     */
    protected long getReadLimit() {
        return readLimit;
    }




    /**
     * @param limit the number of reads to which the reader is limited.
     */
    protected void setReadLimit(long limit) {
        if (limit < 0) {
            readLimit = 0;
        } else {
            readLimit = limit;
        }
    }




    /**
     *
     */
    public void open(final TransformContext context) {
        super.context = context;
        if (getConfiguration().containsIgnoreCase(ConfigTag.LIMIT)) {
            readLimit = getLong(ConfigTag.LIMIT);
        }
        Log.debug( "Reader.limit_is"+ readLimit);
    }




    /**
     *
     */
    @Override
    public TransformContext getContext() {
        return context;
    }




    /**
     * @see java.io.Closeable#close()
     */
    public void close() throws IOException {
        // no-op implementation
    }




    /**
     * @return the source URI from which the reader will read
     */
    public String getSource() {
        return configuration.getAsString(ConfigTag.SOURCE);
    }

}