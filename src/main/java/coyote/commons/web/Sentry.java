package coyote.commons.web;

import javax.servlet.http.HttpServletRequest;


/**
 * A utility class which allows for the aggregation of security function into a single component.
 */
public interface Sentry {

  /**
   * Set the access control list rules.
   * 
   * <p>The rules are a semicolon delimited list of CIDR networks with a 
   * directive of either ALLOW or DENY for that network. For example:
   * <pre>192.168/16:ALLOW;150.159/16:DENY;DEFAULT:DENY</pre>Where everything 
   * coming from the 192.168.0.0/255.255.0.0 network is allowed, 
   * 150.159.0.0/255.255.0.0 is denied and everything else is denied.
   * 
   * @param rules the string containing the network specifications and their ability to access.
   * 
   * @throws IllegalArgumentException if the rules cannot be parsed.
   */
  public void setAcl( String rules );




  /**
   * Check to see if the given request is allowed access.
   * 
   * <p>This method checks the remote IP address of the request against the 
   * current ACL rules and determines if the request is allowed access.
   * 
   * @param req the HTTP servlet request requesting access
   * 
   * @return true if the request passes the ACL, false if the remote IP is disallowed.
   */
  public boolean allows( HttpServletRequest req );




  /**
   * This places an INFO entry in the security log.
   * 
   * @param entry the log entry
   */
  public void log( Object entry );
}
