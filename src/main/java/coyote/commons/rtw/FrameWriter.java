/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.rtw;

import coyote.commons.dataframe.DataFrame;

/**
 *
 */
public interface FrameWriter extends ConfigurableComponent {

    void write(DataFrame frame);

}