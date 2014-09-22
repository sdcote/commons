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

//import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Test;

/**
 * 
 */
public class IpAclTest {

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}




	/**
	 * Test method for {@link coyote.commons.network.IpAcl#IpAcl()}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testIpAcl() throws Exception {
		IpAcl acl = new IpAcl();
		acl.add("192.168/16", true);
		acl.add("10/8", false);
	}




	/**
	 * Test method for {@link coyote.commons.network.IpAcl#allows(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public void testAllowsString() throws Exception {

		// Network rule - - - - - - - - - - - - - - - - - - - - - - - - - - - -

		IpAcl acl = new IpAcl(IpAcl.DENY); // New ACL with DENY as default
		acl.add("192.168/16", true); // add local subnet to be allowed (true)
		acl.add("10/8", false); // add a deny (false) rule for another subnet

		// Local should pass
		String arg = "192.168.1.100";
		assertTrue("Should allow '" + arg + "'", acl.allows(arg));

		// Other local should not
		arg = "10.8.107.12";
		assertFalse("Should NOT allow '" + arg + "'", acl.allows(arg));

		arg = "150.22.78.212";
		assertFalse("Should NOT allow '" + arg + "'", acl.allows(arg));

		// Single Address - - - - - - - - - - - - - - - - - - - - - - - - - - -

		acl = new IpAcl(IpAcl.DENY);

		// Only allow this one IP address
		acl.add("192.168.1.100/32", IpAcl.ALLOW);

		// This should pass
		arg = "192.168.1.100";
		assertTrue("Should allow '" + arg + "'", acl.allows(arg));

		// These should not pass
		arg = "10.8.107.12";

		assertTrue("Should NOT allow '" + arg + "'", !acl.allows(arg));

		arg = "192.168.1.101";

		assertTrue("Should NOT allow '" + arg + "'", !acl.allows(arg));
	}




	@Test
	public void testParse() throws Exception {
		//
		String rules = " 192.168/16:ALLOW;10/8:ALLOW;172.26.39/23:ALLOW;DEFAULT:DENY";
		IpAcl acl = new IpAcl();
		acl.parse(rules);

		// Local should pass
		String arg = "192.168.1.100";
		assertTrue("Should allow '" + arg + "'", acl.allows(arg));

		arg = "10.8.107.12";
		assertTrue("Should allow '" + arg + "'", acl.allows(arg));

		arg = "150.22.78.212";
		assertFalse("Should NOT allow '" + arg + "'", acl.allows(arg));
	}
}
