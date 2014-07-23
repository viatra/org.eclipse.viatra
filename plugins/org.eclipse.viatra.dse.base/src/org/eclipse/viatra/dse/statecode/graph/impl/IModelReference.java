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

/**
 * Utility interface used in the generation of statecodes over arbitrary EMF models.
 * 
 * @see GraphHash
 */
public interface IModelReference {
    /**
     * Returns the "label" of the edge.
     * 
     * @return the label.
     */
    String getLabel();

    /**
     * Returns the source {@link IModelObject} that this {@link IModelReference} is pointing outwards from.
     * 
     * @return the relevant {@link IModelObject}.
     */
    IModelObject getSource();

    /**
     * Returns the source {@link IModelObject} that this {@link IModelReference} is pointing to.
     * 
     * @return the relevant {@link IModelObject}.
     */
    IModelObject getTarget();

    /**
     * Returns whether this edge is bidirectional or not.
     * 
     * @return false if this edge can be traversed in both directions, true otherwise.
     */
    boolean isDirected();

    /**
     * Sets the "label" of this edge.
     * 
     * @param label
     *            the new label.
     */
    void setLabel(String label);

    /**
     * Setter for the source {@link IModelObject}.
     * 
     * @param source
     *            the source {@link IModelObject}
     */
    void setSource(IModelObject source);

    /**
     * Setter for the target {@link IModelObject}.
     * 
     * @param target
     *            the target {@link IModelObject}
     */
    void setTarget(IModelObject target);

    /**
     * Setter for the directed attribute.
     * 
     * @param directed
     *            the new value.
     */
    void setDirected(boolean directed);
}
