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

import java.util.List;

/**
 * Utility interface used in the generation of statecodes over arbitrary EMF models.
 * 
 * @see GraphHash
 */
public interface IModelObject {
    /**
     * Returns the internal state of this object.
     * 
     * @return the internal state as a String.
     */
    String getLabel();

    /**
     * Returns all the {@link IModelReference} objects that either point to, or point from this {@link IModelObject}.
     * 
     * @return a {@link List} of {@link IModelReference} objects.
     */
    List<IModelReference> getEdges();

    /**
     * Setter for label.
     * 
     * @param label
     *            the new label.
     */
    void setLabel(String label);

    /**
     * Setter for edges.
     * 
     * @param edges
     *            the new edges.
     */
    void setEdges(List<IModelReference> edges);
}
