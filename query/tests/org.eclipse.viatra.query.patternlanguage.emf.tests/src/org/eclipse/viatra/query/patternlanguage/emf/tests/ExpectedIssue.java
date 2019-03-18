/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests;

import org.eclipse.xtext.nodemodel.INode;

/**
 * An ExpectedIssue stores a partial error message and a line number to identify
 * a parser error message.
 * 
 */
public class ExpectedIssue {

    String desc;
    int line;

    public ExpectedIssue(String desc, int line) {
        this.desc = desc;
        this.line = line;
    }

    public String getDesc() {
        return desc;
    }

    public int getLine() {
        return line;
    }

    
    /**
     * Decides whether the expected issue matches an error node
     */
    public boolean matchesErrorNode(INode node) {
        return (node.getStartLine() == line && node.getSyntaxErrorMessage()
                .getMessage().toLowerCase().contains(desc.toLowerCase()));
    }
}
