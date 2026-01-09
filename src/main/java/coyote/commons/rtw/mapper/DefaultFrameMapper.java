/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.mapper;

import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.rtw.FrameMapper;
import coyote.commons.rtw.TransactionContext;

/**
 * This is the default mapper which simply copies fields from the source frame
 * with one name to the target frame with another.
 */
public class DefaultFrameMapper extends AbstractFrameMapper implements FrameMapper {

    /**
     *
     */
    @Override
    public void process(TransactionContext context) throws MappingException {

        if (fields.size() > 0) {
            // Ensure there is a target frame into which the fields will be placed
            if (context.getTargetFrame() == null) {
                context.setTargetFrame(new DataFrame());
            }

            // for each frame in the list (insertion order)
            for (SourceToTarget mapping : fields) {

                DataField targetField;

                if (context.getWorkingFrame().contains(mapping.getSourceName())) {

                    // clone the named field from the working frame
                    targetField = (DataField) context.getWorkingFrame().getField(mapping.getSourceName()).clone();

                    // re-name the field to that of the target frame
                    targetField.setName(mapping.getTargetName());

                } else {
                    // apparently there is no working field named with the source name.
                    // This is normal, the value could just be missing for this record
                    // only. Create a new null frame with the desired name
                    targetField = new DataField(mapping.getTargetName(), null);
                }

                // place the mapped field in the target data frame for writing
                context.getTargetFrame().getFields().add(targetField);
            }
        } else {
            // if no field map, just perform a straight clone of the working frame
            context.setTargetFrame((DataFrame) context.getWorkingFrame().clone());
        }

    }

}