package coyote.commons.network.http.wsd;

import java.io.IOException;

import coyote.commons.network.http.wsd.WebSocketFrame.CloseCode;


public class WebSocketException extends IOException {

  private static final long serialVersionUID = 777960923156306743L;
  private final CloseCode code;
  private final String reason;




  public WebSocketException(final Exception cause) {
    this(CloseCode.InternalServerError, cause.toString(), cause);
  }




  public WebSocketException(final CloseCode code, final String reason) {
    this(code, reason, null);
  }




  public WebSocketException(final CloseCode code, final String reason, final Exception cause) {
    super(code + ": " + reason, cause);
    this.code = code;
    this.reason = reason;
  }




  public CloseCode getCode() {
    return code;
  }




  public String getReason() {
    return reason;
  }

}