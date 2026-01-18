package coyote.commons.uml.marshal;

import coyote.commons.uml.UmlElement;

/**
 * The geometry settings for an element on a diagram.
 */
public class Geometry {

  private UmlElement element;
  int top;
  int bottom;
  int left;
  int right;

  public Geometry(UmlElement element) {
    this(element, 0, 0, 100, 100);
  }

  public Geometry(UmlElement element, int left, int top, int right, int bottom) {
    this.element = element;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  public UmlElement getElement() {
    return element;
  }

  public int getTop() {
    return top;
  }

  public void setTop(int top) {
    this.top = top;
  }

  public int getBottom() {
    return bottom;
  }

  public void setBottom(int bottom) {
    this.bottom = bottom;
  }

  public int getLeft() {
    return left;
  }

  public void setLeft(int left) {
    this.left = left;
  }

  public int getRight() {
    return right;
  }

  public void setRight(int right) {
    this.right = right;
  }

  public String toString() {
    return "Left=" + left + ";" + "Top=" + top + ";" + "Right=" + right + ";" + "Bottom=" + bottom + ";";
  }
}
