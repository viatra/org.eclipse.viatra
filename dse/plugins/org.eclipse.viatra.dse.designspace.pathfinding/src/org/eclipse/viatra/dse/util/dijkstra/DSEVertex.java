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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;

public class DSEVertex implements IVertex {
    private final IState state;

    private DSEEdge bestIncomingEdge;

    private long cost = Long.MAX_VALUE;

    private final DynamicSPT tree;

    public IState getState() {
        return state;
    }

    public DSEVertex(IState state, DynamicSPT tree) {
        this.state = state;
        this.tree = tree;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public DSEEdge getBestIncomingEdge() {
        return bestIncomingEdge;
    }

    public void setBestIncomingEdge(DSEEdge bestIncomingEdge) {
        this.bestIncomingEdge = bestIncomingEdge;
    }

    public List<DSEEdge> getOutgoingEdges() {
        LinkedList<DSEEdge> list = new LinkedList<DSEEdge>();
        for (ITransition transition : state.getOutgoingTransitions()) {
            DSEEdge edge = tree.get(transition);
            if (edge != null) {
                list.add(edge);
            }
        }
        return list;
    }

}
