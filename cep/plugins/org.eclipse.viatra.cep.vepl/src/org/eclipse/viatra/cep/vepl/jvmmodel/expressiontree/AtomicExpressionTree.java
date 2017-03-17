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

import java.util.Collections;
import java.util.List;

import org.eclipse.viatra.cep.core.metamodels.events.Timewindow;
import org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity;
import org.eclipse.viatra.cep.vepl.vepl.Atom;
import org.eclipse.viatra.cep.vepl.vepl.VeplFactory;

public class AtomicExpressionTree extends ExpressionTree {

    public AtomicExpressionTree(Atom atom, AbstractMultiplicity multiplicity, Timewindow timewindow) {
        super();

        Node root = new Node(VeplFactory.eINSTANCE.createFollowsOperator(), multiplicity, timewindow);
        Leaf leaf = new Leaf(atom);
        root.addChild(leaf);
        super.setRoot(root);
    }

    @Override
    public List<Leaf> getComplexLeaves() {
        return Collections.emptyList();
    }
}
