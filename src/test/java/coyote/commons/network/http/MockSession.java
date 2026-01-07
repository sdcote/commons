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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coyote.commons.network.IpAddress;


/**
 *
 */
public class MockSession implements HTTPSession {
  private static final List<String> EMPTY_LIST = new ArrayList<String>( 0 );;
  private final Map<String, String> requestHeaders;
  private final Map<String, String> responseHeaders;
  private String username = null;
  private List<String> usergroups = EMPTY_LIST;




  public MockSession() {
    requestHeaders = new HashMap<String, String>();
    responseHeaders = new HashMap<String, String>();
  }




  /**
   * @param name header name
   * @param value header value
   */
  public void addRequestHeader( final String name, final String value ) {
    requestHeaders.put( name, value );
  }




  /**
   * @see HTTPSession#execute()
   */
  @Override
  public void execute() throws IOException {}




  /**
   * @see HTTPSession#getCookies()
   */
  @Override
  public CookieHandler getCookies() {
    return null;
  }




  /**
   * @see HTTPSession#getInputStream()
   */
  @Override
  public InputStream getInputStream() {
    return null;
  }




  /**
   * @see HTTPSession#getMethod()
   */
  @Override
  public Method getMethod() {
    return null;
  }




  /**
   * @see HTTPSession#getParms()
   */
  @Override
  public Map<String, String> getParms() {
    return null;
  }




  /**
   * @see HTTPSession#getQueryParameterString()
   */
  @Override
  public String getQueryParameterString() {
    return null;
  }









  /**
   * @see HTTPSession#getRemoteIpAddress()
   */
  @Override
  public IpAddress getRemoteIpAddress() {
    return IpAddress.IPV4_LOOPBACK_ADDRESS;
  }




  /**
   * @see HTTPSession#getRemoteIpPort()
   */
  @Override
  public int getRemoteIpPort() {
    return 0;
  }




  /**
   * @see HTTPSession#getRequestHeaders()
   */
  @Override
  public final Map<String, String> getRequestHeaders() {
    return requestHeaders;
  }




  /**
   * @see HTTPSession#getResponseHeaders()
   */
  @Override
  public Map<String, String> getResponseHeaders() {
    return responseHeaders;
  }




  /**
   * @see HTTPSession#getUri()
   */
  @Override
  public String getUri() {
    return null;
  }




  /**
   * @see HTTPSession#getUserGroups()
   */
  @Override
  public List<String> getUserGroups() {
    return usergroups;
  }




  /**
   * @see HTTPSession#getUserName()
   */
  @Override
  public String getUserName() {
    return username;
  }




  /**
   * @see HTTPSession#isSecure()
   */
  @Override
  public boolean isSecure() {
    return false;
  }




  


  /**
   * @see HTTPSession#setUserGroups(List)
   */
  @Override
  public void setUserGroups( final List<String> groups ) {
    if ( groups != null ) {
      usergroups = groups;
    } else {
      usergroups = EMPTY_LIST;
    }
  }




  /**
   * @see HTTPSession#setUserName(String)
   */
  @Override
  public void setUserName( final String user ) {
    username = user;
  }




  /**
   * @see HTTPSession#parseBody()
   */
  @Override
  public Body parseBody() throws IOException, ResponseException {
    // TODO Auto-generated method stub
    return null;
  }

}
