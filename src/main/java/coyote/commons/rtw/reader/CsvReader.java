/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.reader;


import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.csv.CSVReader;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.DataFrameException;
import coyote.commons.log.Log;
import coyote.commons.rtw.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;

/**
 * Character Separated Value Reader
 */
public class CsvReader extends AbstractFrameReader implements FrameReader, ConfigurableComponent {

    /** The component responsible for reading CSV files into frames */
    private CSVReader reader = null;

    /** Flag indicating all data should be loaded into and read from memory. */
    private boolean preload = false;

    /** Flag indicating that the first line of data should be considered column names. */
    private boolean hasHeader = false;

    /** The column names read in from the first line */
    private String[] header = new String[0];

    private volatile String[] nextLine = null;

    /** The default separator character */
    public char SEPARATOR = ',';




    /**
     *
     */
    public void setConfiguration(Config cfg) throws ConfigurationException {
        super.setConfiguration(cfg);

        // Check if we are to load all the data into memory and read from there
        if (cfg.containsIgnoreCase(ConfigTag.PRELOAD)) {
            preload = cfg.getBoolean(ConfigTag.PRELOAD);
        }
        Log.debug("Reader.preload_is" + preload);

        // Check if we are to treat the first line as the header names
        if (cfg.containsIgnoreCase(ConfigTag.HEADER)) {
            hasHeader = cfg.getBoolean(ConfigTag.HEADER);
        } else {
            Log.debug("No header config");
        }
        Log.debug("Reader.header_flag_is"+ hasHeader);

        // Check if we are to use a different separator than the default ',' (comma)
        if (cfg.containsIgnoreCase(ConfigTag.CHARACTER)) {
            String value = cfg.getString(ConfigTag.CHARACTER);

            if (StringUtil.isNotEmpty(value)) {
                SEPARATOR = value.charAt(0);
            } else {
                Log.info("Character value not valid ");
            }
        } else {
            Log.debug("No separator config");
        }
        Log.debug("Reader.separator_character_is"+ SEPARATOR+ (int)SEPARATOR);

    }




    /**
     *
     */
    @Override
    public DataFrame read(TransactionContext context) {
        DataFrame retval = null;
        String[] data = nextLine;
        if (data != null) {
            retval = new DataFrame();
            for (int x = 0; x < data.length; x++) {
                retval.add(x < header.length ? header[x] : new String("COL" + x), data[x]);
            }
            // read the next line of data (if it exists)
            readNext();

            // if there is no next line, this is the last frame
            if (eof()) {
                context.setLastFrame(true);
            }
        }

        return retval;
    }




    /**
     *
     */
    @Override
    public boolean eof() {
        return nextLine == null;
    }




    /**
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }




    /**
     *
     */
    @Override
    public void open(TransformContext context) {
        super.open(context);

        // check for a source in our configuration, if not there use the transform
        // context as it may have been set by a previous operation
        String source = super.getString(ConfigTag.SOURCE);
        Log.debug("Component.configured_source_is "+ this.getClass().getSimpleName() + source);
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
                        Log.warn("Reader.source_is_not_file "+ source);
                    }
                }
            } else {
                Log.debug("Source could not be parsed into a URI, assuming a filename");
                sourceFile = new File(source);
            }
            if (sourceFile != null) {
                Log.debug("Using a source file of " + sourceFile.getAbsolutePath());
            } else {
                Log.error("Using a source file of NULL_REF");
            }
            // if not absolute, use the CDX fixture to attempt to resolve the relative file
            if (!sourceFile.isAbsolute()) {
                sourceFile = RTW.resolveFile(sourceFile, getContext());
            }
            Log.debug("Using an absolute source file of " + sourceFile.getAbsolutePath());

            // Basic checks
            if (sourceFile.exists() && sourceFile.canRead()) {
                try {
                    setReader(new CSVReader(new FileReader(sourceFile), SEPARATOR));
                } catch (Exception e) {
                    Log.error("Could not create reader: " + e.getMessage());
                    context.setError(e.getMessage());
                }
            } else {
                context.setError("Reader.could_not_read_from_source "+ getClass().getName()+" - "+ sourceFile.getAbsolutePath());
            }
        } else {
            Log.error("No source specified");
            context.setError(getClass().getName() + " could not determine source");
        }

    }




    /**
     * Placed in a separate method to facilitate testing with different sources.
     *
     * @param csvReader the reader to set
     *
     * @throws IOException if there is problems with the streams
     * @throws ParseException if there are problems parsing the data
     */
    protected void setReader(CSVReader csvReader) throws IOException, ParseException {
        reader = csvReader;
        if (hasHeader) {
            header = reader.readNext();
        }
        readNext();
    }




    /**
     * This reads the next line of data, skipping any empty rows.
     */
    private void readNext() {
        nextLine = null;
        try {
            while (nextLine == null) {
                nextLine = reader.readNext();
                reader.consumeEmptyLines();
                if (reader.eof()) {
                    break;
                }
            }
            if (nextLine != null) {
                super.recordCounter++;
                if (super.readLimit > 0 && super.recordCounter > super.readLimit) {
                    nextLine = null;
                }
            }
        } catch (Exception ignore) {
            // no more lines
        }
    }




    /**
     * @return true indicating the reader will load all available data into and read records from memory, false to get one or (batch size) at a time.
     */
    public boolean isPreload() {
        try {
            return configuration.getAsBoolean(ConfigTag.PRELOAD);
        } catch (DataFrameException e) {
            return false;
        }
    }




    /**
     * Set if the reader should read all data into memory first and read each
     * record from memory.
     *
     * <p>While this can be faster in some cases, it does take more memory to
     * process which can affect processing.</p>
     *
     * @param flag true to read the entire data set into memory before returning the first record, false reads data from the source on each call to the {@code read()} method.
     */
    public void setPreload(boolean flag) {
        configuration.put(ConfigTag.PRELOAD, flag);
    }




    /**
     * @return true for treating the first line(s) of data as a header, false means treat the first line of data as the first record.
     */
    public boolean isUsingHeader() {
        try {
            return configuration.getAsBoolean(ConfigTag.HEADER);
        } catch (DataFrameException e) {
            return false;
        }
    }




    /**
     * Set the reader to treat the first line(s) of data as a header.
     *
     * @param flag true to set the read to process the first line(s) as a header, false to treat the first line as the first record.
     */
    public void setHeaderFlag(boolean flag) {
        configuration.put(ConfigTag.HEADER, flag);
    }




    /**
     * @return the URI representing the source from which data is to be read
     */
    public String getSource() {
        return configuration.getAsString(ConfigTag.SOURCE);
    }




    /**
     * Set the URI representing the source from which data is to be read.
     *
     * @param value The URI from which data is to be read.
     */
    public void setSource(String value) {
        configuration.put(ConfigTag.SOURCE, value);
    }

}
