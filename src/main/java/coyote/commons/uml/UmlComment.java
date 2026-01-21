/*
 * Copyright (c) 2026 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.uml;

/**
 * Comments are unnamed elements that are owned by other elements.
 */
public class UmlComment extends UmlElement {

    /**
     * The body of the comment
     */
    private String body = "";

    /**
     * Constructor of a comment.
     *
     * @param body the content of the comment.
     */
    public UmlComment(String body) {
        this.body = body;
    }

    /**
     * @return return the body of the comment.
     */
    public String getBody() {
        return body;
    }


    /**
     * Set the contents of the comment.
     *
     * @param body the body of the comment.
     */
    public void setBody(String body) {
        this.body = body;
    }

}
