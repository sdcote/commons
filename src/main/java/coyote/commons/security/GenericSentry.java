package coyote.commons.security;

import javax.servlet.http.HttpServletRequest;

import coyote.commons.Log;
import coyote.commons.network.IpAcl;
import coyote.commons.web.Sentry;

/**
 * This class is intended to demonstrate the use of the Sentry interface with the commons security classes.
 */
public class GenericSentry implements Sentry {

	// our IP-based access control list
	IpAcl acl = new IpAcl(IpAcl.DENY);




	@Override
	public void setAcl(String rules) {
		acl.parse(rules);
	}




	@Override
	public boolean allows(HttpServletRequest req) {
		return acl.allows(req.getRemoteAddr());
	}




	@Override
	public void log(Object entry) {
		Log.info(entry);
	}

}
