/*******************************************************************************
 * Copyright (c) 2010-2016, Gyorgy Gerencser, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gyorgy Gerencser - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybyexample.interfaces;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLPattern;

public interface IExplorer {
    /**
     * Explores an EMF instance model in the given depth starting out from the selected EObjects.
     */
    void explore(int depth);

    /**
     * From <i>root</i> node performs a depth limited search with <i>limit</i> depth in order to construct a proper
     * EIQPattern instance
     */
    void eObjectsDLS(EObject root, int limit);

    /**
     * This function returns all referred EObject of a node. By default both the <i>forwarded</i> and the
     * <i>backwarded</i> nodes were ascertained with the EIQ API
     */
    Set<EObject> getAllReferredEObjects(EObject eo);

    /**
     * Determines the references between two EObjects where <i>first</i> is the referrer and <i>second</i> is the
     * referred EObject
     */
    Set<VQLConstraint> determineAllConstraints(EObject first, EObject second);

    /**
     * Provides the generated EIQPattern instance
     */
    VQLPattern getPattern();

    /**
     * Resets the exploration. It is called before every single new search.
     */
    void reset();

    /**
     * Returns true if the current pattern is coherent, so a syntactically correct code could be generated. A DLS
     * algorithm will be performed to determine the coherence.
     */
    boolean isPatternConnected();

    /**
     * Returns the minimum depth, that provides the pattern will be coherent, or 0 if it could not produce this number
     * after several tries.
     */
    int determineCoherenceMinimumDepth();

    void findAndRegisterNegativeConstraints();
}
