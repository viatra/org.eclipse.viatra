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
package org.eclipse.viatra.dse.statecode.graph.impl;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

/**
 * Utility class used in the generation of statecodes over arbitrary EMF models.
 * 
 * @see GraphHash
 * 
 * @author Miklos Foldenyi
 * 
 */
public class EEdge implements IModelReference {

    private EVertex sourceVertex;
    private EVertex targetVertex;
    private EReference ref;

    protected EEdge(EGraphBuilderContext ctx, EObject source, EReference ref, EObject target) {
        this.ref = ref;
        this.sourceVertex = ctx.getEVertex(source);
        this.targetVertex = ctx.getEVertex(target);
    }

    @Override
    public String getLabel() {
        return ref.getName();
    }

    @Override
    public IModelObject getSource() {
        return sourceVertex;
    }

    @Override
    public IModelObject getTarget() {
        return targetVertex;
    }

    @Override
    public boolean isDirected() {
        return true;
    }

    @Override
    public void setLabel(String label) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSource(IModelObject source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTarget(IModelObject target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDirected(boolean directed) {
        throw new UnsupportedOperationException();
    }

}
