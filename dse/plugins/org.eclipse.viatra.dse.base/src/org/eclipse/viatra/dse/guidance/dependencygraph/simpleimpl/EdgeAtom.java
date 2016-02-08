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

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.EdgeType;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IEdgeAtom;

public class EdgeAtom implements IEdgeAtom {

    private final EdgeType type;

    private final EModelElement modelElement;

    private final int numOfElements;

    public EdgeAtom(EdgeType type, EModelElement modelElement, int numOfElements) {
        this.type = type;
        this.modelElement = modelElement;
        this.numOfElements = numOfElements;
    }

    @Override
    public EModelElement getModelElement() {
        return modelElement;
    }

    @Override
    public int getNumOfElements() {
        return numOfElements;
    }

    @Override
    public EdgeType getType() {
        return type;
    }
}
