/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.mapper;


import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.log.Log;
import coyote.commons.log.LogMsg;
import coyote.commons.rtw.AbstractConfigurableComponent;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TransformContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the base class for all the mappers.
 */
public abstract class AbstractFrameMapper extends AbstractConfigurableComponent {
    /** An insertion ordered list of target fields to be written to the target frame */
    List<SourceToTarget> fields = new ArrayList<>();




    /**
     * Expects a configuration in the form of "Fields" : { "SourceField" : "TargetField", ... }
     */
    @Override
    public void setConfiguration(Config cfg) throws ConfigurationException {
        setConfiguration(cfg);

        // Retrieve the "fields" section from the configuration frame
        DataFrame mapFrame = null;
        try {
            if (cfg.containsIgnoreCase(ConfigTag.FIELDS)) {
                DataField field = cfg.getFieldIgnoreCase(ConfigTag.FIELDS);
                if (field.isFrame()) {
                    mapFrame = (DataFrame)field.getObjectValue();
                } else {
                    Log.warn("Mapper.invalid_section_in_configuration");
                }
            }
        } catch (Exception e) {
            mapFrame = null;
        }

        // If we found the "fields" frame...
        if (mapFrame != null) {
            // For each name:value pair, setup a source:target mapping
            for (DataField field : mapFrame.getFields()) {
                if (StringUtil.isNotBlank(field.getName()) && field.getValue().length > 0) {
                    fields.add(new SourceToTarget(field.getName(), field.getObjectValue().toString()));
                }
            }
        } else {
            Log.warn(LogMsg.createMsg("Mapper.no_section_in_configuration"));
        }

    }




    /**
     *
     */
    public TransformContext getContext() {
        return context;
    }




    /**
     *
     */
    public void open(TransformContext context) {
        this.context = context;
    }




    /**
     *
     */
    public void close() throws IOException {

    }

    //

    //

    /**
     *
     */
    protected class SourceToTarget {
        private final String sourceName;
        private final String targetName;




        public SourceToTarget(String source, String target) {
            sourceName = source;
            targetName = target;
        }




        /**
         * @return the target field name
         */
        public String getTargetName() {
            return targetName;
        }




        /**
         * @return the source field name
         */
        public String getSourceName() {
            return sourceName;
        }




        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Mapping: '".concat(sourceName).concat("' to '").concat(targetName).concat("'");
        }

    }

}