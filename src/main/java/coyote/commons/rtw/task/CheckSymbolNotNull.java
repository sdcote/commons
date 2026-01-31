/*
 * Copyright (c) 2021 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw.task;

import coyote.commons.StringUtil;
import coyote.commons.cfg.Config;
import coyote.commons.cfg.ConfigurationException;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.TaskException;


/**
 * Designed to abort a job if a symbol is missing from the symbol table of the
 * transformation context.
 */
public class CheckSymbolNotNull extends AbstractTransformTask {


    /**
     *
     */
    @Override
    public void setConfiguration(Config cfg) throws ConfigurationException {
        super.setConfiguration(cfg);

        if (cfg.contains(ConfigTag.SYMBOL)) {
            if (StringUtil.isBlank(cfg.getString(ConfigTag.SYMBOL))) {
                throw new ConfigurationException(String.format( "Task.checksymbolnotnull.empty_cfg_param %s %s", getClass().getSimpleName(), ConfigTag.SYMBOL));
            }
            // we have a symbol name to check
        } else {
            throw new ConfigurationException(String.format( "Task.checksymbolnotnull.missing_cfg_param %s %s", getClass().getSimpleName(), ConfigTag.SYMBOL));
        }

    }


    /**
     * @return the name of the symbol to check.
     */
    private String getSymbolName() {
        String retval = null;
        if (getConfiguration().containsIgnoreCase(ConfigTag.SYMBOL)) {
            retval = getConfiguration().getString(ConfigTag.SYMBOL);
        }
        return retval;
    }


    /**
     *
     */
    @Override
    protected void performTask() throws TaskException {
        if (StringUtil.isEmpty(getContext().getSymbols().getString(getSymbolName())))
            throw new TaskException(String.format( "Task.checksymbolnotnull.symbol_is_empty %s", getSymbolName()));
    }

}
