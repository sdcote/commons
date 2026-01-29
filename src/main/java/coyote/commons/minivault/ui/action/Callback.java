package coyote.commons.minivault.ui.action;

/**
 * Simple callback method interface.
 */
public interface Callback {

  /**
   * Callback method.
   *
   * @param result the result of the callback
   */
  void call(boolean result);
}
