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
package org.eclipse.viatra.dse.guidance;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.TransformationRule;

/**
 * This interface defines a method which calculates an occurrence vector for the {@link TransformationRule}s. Basically
 * it is lower bound for the how many times they should be executed.
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public interface IOccurrenceVectorResolver {

    /**
     * Calculates an occurrence vector for the {@link TransformationRule}s.
     * 
     * @param eList
     * 
     * @param initialMarking
     *            The initial marking.
     * @param transformations
     *            The {@link TransformationRule}s.
     * @param targetMarking
     *            The targetMarking.
     * @return The occurrence vector defined by a map.
     */
    PetriAbstractionResult calculateOccurrenceVector(List<? extends EModelElement> classesAndReferences,
            Map<? extends EModelElement, Integer> initialMarking,
            Set<TransformationRule<? extends IPatternMatch>> transformations, List<Predicate> predicates);
}
