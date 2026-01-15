package coyote.commons.uml;

public abstract class UmlBehavioralFeature extends UmlFeature {
  private static final Classifier CLASSIFIER = Classifier.BEHAVIORAL_FEATURE;




  /**
   * @param name
   * @param id
   */
  public UmlBehavioralFeature(String name, String id) {
    super(name, id);
  }




  /**
   * @param name
   */
  public UmlBehavioralFeature(String name) {
    super(name);
  }




  /**
   * @see UmlFeature#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }

}
