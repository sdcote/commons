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
package coyote.commons.web;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


/**
 * Filter for viewing Request Parameters and Session Attributes in log.
 * 
 * In your web.xml, add the following:
 * 
 * <pre>
 * &lt;filter&gt;
 *     &lt;filter-name&gt;RequestDebuggingFilter&lt;/filter-name&gt;
 *     &lt;filter-class&gt;your.package.path.RequestDebuggingFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;RequestDebuggingFilter&lt;/filter-name&gt;
 *     &lt;servlet-name&gt;MyServlet&lt;/servlet-name&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 * 
 * <p>Use of Java Logging is assumed. Make sure you logging framework is 
 * registered with the Java framework for unified logging.</p>
 */
public class RequestLoggingFilter implements Filter {
  private final static Logger LOG = Logger.getLogger( RequestLoggingFilter.class.getName() );

  public static final DateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss:SSS" );

  public static final String LF = System.getProperty( "line.separator" );




  /**
   * Handy static method to perform diagnostic dumps of requests in other parts of the system.
   * 
   * @param request the request to dump into a string format.
   * 
   * @return String containing human readable text of the servlet request details.
   */
  public static String dump( HttpServletRequest request ) {
    StringBuilder b = new StringBuilder( "Request for: " );
    b.append( request.getRequestURI() );

    if ( request.getRequestURI().contains( ".js" ) ) {
      // nothing to do here
    } else {
      b.append( LF );
      dumpHeaders( request, b );
      dumpRequestParameters( request, b );
      dumpSessionAttributes( request, b );
      dumpCookies( request, b );
    }
    return b.toString();
  }




  /**
   * Only displays details for log entries if log level is set below WARNING.
   * 
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {
    StringBuilder b = new StringBuilder();
    HttpServletRequest hreq = (HttpServletRequest)request;
    b.append( LF );
    b.append( "Request for: " );
    b.append( hreq.getRequestURI() );
    if ( hreq.getRequestURI().contains( ".js" ) ) {
      // nothing to do here
    } else {
      if ( LOG.getLevel().intValue() < Level.WARNING.intValue() ) {
        b.append( LF );
        dumpHeaders( hreq, b );
        dumpRequestParameters( hreq, b );
        dumpSessionAttributes( hreq, b );
        dumpCookies( hreq, b );
      }
    }
    LOG.info( b.toString() );
    chain.doFilter( request, response );
  }




  /**
   * Dump the headers of the given request into the given string builder.
   * 
   * @param request The request from which the headers are to be retrieved.
   * 
   * @param b The string builder to which the header data is to be appended.
   */
  public static void dumpHeaders( HttpServletRequest request, StringBuilder b ) {
    b.append( "Headers:" );
    b.append( LF );
    Map<String, String> sortedHeaders = sortHeaders( request );
    for ( Map.Entry<String, String> entry : sortedHeaders.entrySet() ) {
      b.append( "  " );
      b.append( entry.getKey() );
      b.append( ": " );
      b.append( entry.getValue() );
      b.append( LF );
    }
    b.append( "---------------- End of Headers" );
    b.append( LF );
  }




  /**
   * Dump cookies of the given request to the given string builder.
   * 
   * @param request The request from which the cookies are to be retrieved.
   * 
   * @param b The string builder to which the cookie data is to be appended.
   */
  public static void dumpCookies( HttpServletRequest request, StringBuilder b ) {
    b.append( "Cookies:" );
    b.append( LF );
    if ( null != request.getCookies() ) {
      for ( Cookie cookie : request.getCookies() ) {
        String description = "";
        try {
          description = cookie.toString();
        } catch ( Exception e ) {
          LOG.warning( "Exception describing cookie: " + e.getMessage() );
        }
        b.append( "  " + description + LF );
      }
    }
    b.append( "---------------- End of Cookies" );
    b.append( LF );
  }




  /**
   * Dump session attributes for the given request to the given string builder.
   *   
   * @param request The request from which the session attributes parameters are to be retrieved.
   * 
   * @param b The string builder to which the attributes are to be appended.
   */
  public static void dumpSessionAttributes( HttpServletRequest request, StringBuilder b ) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis( request.getSession().getCreationTime() );
    b.append( "Session " );
    b.append( request.getSession().getId() );
    b.append( " created at: " );
    b.append( DATE_FORMAT.format( cal.getTime() ) );
    b.append( LF );
    b.append( "Attributes:" );
    b.append( LF );
    Map<String, Object> sortedAttrs = sortSessionAttributes( request );
    for ( Map.Entry<String, Object> entry : sortedAttrs.entrySet() ) {
      String description = "";
      try {
        description = entry.getValue().toString();
      } catch ( Exception e ) {
        LOG.warning( "Exception describing attribute '" + entry.getKey() + "' " + e.getMessage() );
      }
      b.append( "  " );
      b.append( entry.getKey() );
      b.append( ": " );
      b.append( description );
      b.append( LF );
    }
    b.append( "---------------- End of Session Attributes" );
    b.append( LF );
  }




  /**
   * Dump request parameters for the given request to the given string builder.
   * 
   * @param request The request from which the request parameters are to be retrieved.
   * 
   * @param b The string builder to which the parameters are to be appended.
   */
  public static void dumpRequestParameters( ServletRequest request, StringBuilder b ) {
    b.append( "Request Parameters:" );
    b.append( LF );
    Map<String, String[]> sortedParams = sortRequestParameters( request );
    for ( Map.Entry<String, String[]> entry : sortedParams.entrySet() ) {
      StringBuilder builder = new StringBuilder();
      for ( String s : entry.getValue() ) {
        builder.append( s );
        builder.append( ", " );
      }
      b.append( "  " );
      b.append( entry.getKey() );
      b.append( ": " );
      b.append( builder.toString() );
      b.append( LF );
    }
    b.append( "---------------- End of Request Attributes" );
    b.append( LF );
  }




  /**
   * Sort the headers for the given request.
   * 
   * @param request The request from which to retrieve the headers.
   * 
   * @return Collection of header values attached to the request mapped by their name.
   */
  @SuppressWarnings("unchecked")
  private static Map<String, String> sortHeaders( HttpServletRequest request ) {
    Map<String, String> sortedHeaders = new TreeMap<String, String>();
    Enumeration<String> attrEnum = request.getHeaderNames();
    while ( attrEnum.hasMoreElements() ) {
      String s = attrEnum.nextElement();
      sortedHeaders.put( s, request.getHeader( s ) );
    }
    return sortedHeaders;
  }




  /**
   * Sort the session attributes for the given request.
   *  
   * @param request The request from which to retrieve the session attributes.
   * 
   * @return Collection of objects attached to the session mapped by their name.
   */
  @SuppressWarnings("unchecked")
  private static Map<String, Object> sortSessionAttributes( HttpServletRequest request ) {
    Map<String, Object> sortedAttrs = new TreeMap<String, Object>();
    Enumeration<String> attrEnum = request.getSession().getAttributeNames();
    while ( attrEnum.hasMoreElements() ) {
      String s = attrEnum.nextElement();
      sortedAttrs.put( s, request.getAttribute( s ) );
    }
    return sortedAttrs;
  }




  /**
   * Sort the request parameters by their name.
   *  
   * @param request The request from which to retrieve the parameters.
   * 
   * @return a collection of objects representing request parameters mapped by their name.
   */
  @SuppressWarnings("unchecked")
  private static Map<String, String[]> sortRequestParameters( ServletRequest request ) {
    Map<String, String[]> sortedParams = new TreeMap<String, String[]>();
    Set<Map.Entry<String, String[]>> params = request.getParameterMap().entrySet();
    for ( Map.Entry<String, String[]> entry : params ) {
      sortedParams.put( entry.getKey(), entry.getValue() );
    }
    return sortedParams;
  }




  /**
   * 
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy() {
    // nothing to implement here
  }




  /**
   * 
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init( FilterConfig fConfig ) throws ServletException {
    // nothing to implement here
  }
}