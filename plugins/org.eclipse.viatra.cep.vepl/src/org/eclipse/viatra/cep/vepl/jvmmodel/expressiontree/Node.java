/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree;

import java.util.List;

import org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator;

import com.google.common.collect.Lists;

/**
 * Intermediate nodes in the {@link ExpressionTree}, representing {@link ComplexEventOperator}s.
 * 
 * @author Istvan David
 * 
 */
public class Node extends TreeElement {
    private ComplexEventOperator operator;
    private List<TreeElement> children = Lists.newArrayList();

    public Node(ComplexEventOperator operator) {
        this.operator = operator;
    }

    public Node(ComplexEventOperator operator, int multiplicity) {
        this.operator = operator;
        setMultiplicity(multiplicity);
    }

    public ComplexEventOperator getOperator() {
        return operator;
    }

    public List<TreeElement> getChildren() {
        return children;
    }

    public void setChildren(List<TreeElement> children) {
        this.children = children;
    }

    public void addChild(TreeElement child) {
        children.add(child);
        child.setParentNode(this);
    }

    public void addChildren(List<TreeElement> children) {
        for (TreeElement child : children) {
            addChild(child);
        }
    }
}
