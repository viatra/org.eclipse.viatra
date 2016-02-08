/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.util.dijkstra;

import org.eclipse.viatra.dse.designspace.api.ITransition;

public class DSEEdge {

    private ITransition t;

    private boolean reverse;

    private int weight = 1;

    private final DynamicSPT tree;

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public DSEEdge(ITransition t, DynamicSPT tree) {
        this(t, false, tree);
    }

    /**
     * Creates an Edge within the given {@link DSEGraph} that represents this {@link Transition}. If rev is true then in
     * the {@link DSEGraph} the link will be pointing towards the source of the {@link Transition}.
     * 
     * @param t
     * @param graph
     * @param rev
     */
    public DSEEdge(ITransition t, boolean rev, DynamicSPT tree) {
        super();
        this.reverse = rev;
        this.t = t;
        this.tree = tree;
    }

    public DSEVertex getDestination() {
        if (reverse) {
            return tree.get(t.getFiredFrom());
        } else {
            return tree.get(t.getResultsIn());
        }
    }

    public DSEVertex getSource() {
        if (reverse) {
            return tree.get(t.getResultsIn());
        } else {
            return tree.get(t.getFiredFrom());
        }
    }

    public int getWeight() {
        return weight;
    }

    public ITransition getT() {
        return t;
    }

}
