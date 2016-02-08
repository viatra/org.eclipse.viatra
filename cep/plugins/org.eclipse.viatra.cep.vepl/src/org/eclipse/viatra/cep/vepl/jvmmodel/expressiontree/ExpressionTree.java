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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Tree-like representation of complex event expressions, supporting arbitrarily nesting sub-expressions. The tree is
 * not binary, i.e. a {@link Node} might have more than two children. (Of type {@link Node} or {@link Leaf}.)
 * 
 * <p>
 * The tree is traversed in an <i>inorder</i> fashion, in left-to-right direction across the leaves of single nodes.
 * <p>
 * 
 * @author Istvan David
 * 
 */
public class ExpressionTree {
    private Node root;
    private List<Leaf> complexLeaves = new CopyOnWriteArrayList<Leaf>();

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public List<Leaf> getComplexLeaves() {
        return complexLeaves;
    }
}
