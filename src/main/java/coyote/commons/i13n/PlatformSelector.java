/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.i13n;

import coyote.commons.i13n.platform.DefaultPlatform;


/**
 * Returns the appropriate platform class based on system properties.
 */
class PlatformSelector {

  Platform retrievePlatform() {
    return new DefaultPlatform();
  }
}
