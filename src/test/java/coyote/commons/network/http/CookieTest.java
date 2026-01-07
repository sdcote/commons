/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote
 *      - Initial concept and implementation
 */
package coyote.commons.network.http;


import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
public class CookieTest {

    @Test
    public void test() {
        final Cookie cookie = new Cookie("test", UUID.randomUUID().toString(), 1).setHttpOnly(true).setDomain("coyote.systems").setSecure(true);
        assertNotNull(cookie);
    }

}
