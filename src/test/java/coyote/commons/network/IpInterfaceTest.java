/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.network;

import org.junit.Test;

/**
 * 
 */
public class IpInterfaceTest {

	/**
	 * Test method for {@link coyote.commons.network.IpInterface#getPrimary()}.
	 */
	@Test
	public void testGetPrimary() {
		IpInterface intrfc = IpInterface.getPrimary();
		System.out.println(intrfc);
	}

}
