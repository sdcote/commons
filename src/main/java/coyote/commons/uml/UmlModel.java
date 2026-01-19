/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

import java.util.ArrayList;
import java.util.List;


/**
 * This represent a UML model.
 */
public class UmlModel extends UmlPackage {
    private static final Classifier CLASSIFIER = Classifier.MODEL;

    private final List<UmlDiagram> diagrams = new ArrayList<>();


    /**
     * @param name
     * @param id
     */
    public UmlModel(String name, String id) {
        super(name, id);
    }


    /**
     * @param name
     */
    public UmlModel(String name) {
        super(name);
    }


    /**
     * @see UmlNamedElement#getClassifier()
     */
    @Override
    public Classifier getClassifier() {
        return CLASSIFIER;
    }


    public void addDiagram(UmlDiagram diagram) {
        diagrams.add(diagram);
    }


    /**
     * @return the diagrams owned by this model
     */
    public List<UmlDiagram> getDiagrams() {
        List<UmlDiagram> retval = new ArrayList<>();
        retval.addAll(diagrams);
        retval.addAll(findAllDiagrams(this));
        return retval;
    }


    private List<UmlDiagram> findAllDiagrams(UmlElement root) {
        List<UmlDiagram> diagrams = new ArrayList<>();
        searchRecursive(root, diagrams);
        return diagrams;
    }

    private void searchRecursive(UmlElement element, List<UmlDiagram> results) {
        if (element instanceof UmlDiagram) {
            results.add((UmlDiagram) element);
        }
        for (UmlNamedElement child : element.getOwnedElements()) {
            searchRecursive(child, results);
        }
    }

}
