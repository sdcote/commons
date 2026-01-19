/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * 
 */
public class UmlDependency extends UmlClassifier {
  private static final Classifier CLASSIFIER = Classifier.DEPENDENCY;

  private String clientId = null;
  private String supplierId = null;

  public UmlDependency() {

  }

  /**
   * 
   * @param name
   * @param client
   * @param supplier
   */
  public UmlDependency(String name, String client, String supplier) {
    super(name);
    setClientId(client);
    setSupplierId(supplier);
  }

  /**
   * 
   * @param name
   * @param client
   * @param supplier
   */
  public UmlDependency(String name, UmlElement client, UmlElement supplier) {
    super(name);
    setClient(client);
    setSupplier(supplier);
  }

  /**
   * 
   * @param name
   * @param id
   */
  public UmlDependency(String name, String id) {
    super(name, id);
  }

  /**
   * 
   * @param name
   */
  public UmlDependency(String name) {
    super(name);
  }

  /**
   * @see UmlNamedElement#getClassifier()
   */
  @Override
  public Classifier getClassifier() {
    return CLASSIFIER;
  }

  /**
   * 
   * @return
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * 
   * @param id
   * @return
   */
  public UmlDependency setClientId(String id) {
    this.clientId = id;
    return this;
  }

  /**
   * 
   * @return
   */
  public String getSupplierId() {
    return supplierId;
  }

  /**
   * 
   * @param id
   * @return
   */
  public UmlDependency setSupplierId(String id) {
    this.supplierId = id;
    return this;
  }

  /**
   * 
   * @param element
   * @return
   */
  public UmlDependency setClient(UmlElement element) {
    this.clientId = element.getId();
    return this;
  }

  /**
   * 
   * @param element
   * @return
   */
  public UmlDependency setSupplier(UmlElement element) {
    this.supplierId = element.getId();
    return this;
  }

}
