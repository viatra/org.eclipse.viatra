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
package org.eclipse.viatra.dse.guidance.dependencygraph.simpleimpl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.EdgeType;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IEdge;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IEdgeAtom;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.INode;

public class Edge implements IEdge {

    private final INode toNode;
    private final INode fromNode;
    private final List<IEdgeAtom> edgeAtoms = new ArrayList<IEdgeAtom>();

    public Edge(INode toNode, INode fromNode) {
        this.toNode = toNode;
        this.fromNode = fromNode;
    }

    @Override
    public INode getFromNode() {
        return fromNode;
    }

    @Override
    public INode getToNode() {
        return toNode;
    }

    @Override
    public List<IEdgeAtom> getEdgeAtoms() {
        return edgeAtoms;
    }

    @Override
    public void addEdgeAtom(EdgeType type, EModelElement modelElement, int numOfElements) {
        edgeAtoms.add(new EdgeAtom(type, modelElement, numOfElements));
    }

    @Override
    public boolean isInhibit() {
        for (IEdgeAtom atom : edgeAtoms) {
            if (atom.getType().isInhibit()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isTrigger() {
        if (isInhibit()) {
            return false;
        }
        for (IEdgeAtom atom : edgeAtoms) {
            if (atom.getType().isTrigger()) {
                return true;
            }
        }
        return false;
    }

}
