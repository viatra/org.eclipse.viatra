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

import java.util.Set;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.PatternWithCardinality;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.guidance.dependencygraph.interfaces.IDependencyGraph;

/**
 * Defines a method to calculate a {@link IDependencyGraph} from {@link TransformationRule}s.
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public interface IDependencyGraphResolver {

    /**
     * Creates a {@link IDependencyGraph} from the give {@link TransformationRule}s, constraints and goal patterns.
     * 
     * @param transformations
     *            The transformation rules.
     * @param constraints
     *            The global constraints.
     * @param goalPatterns
     *            The goal patterns
     * @return A dependency graph between the given parameters.
     */
    IDependencyGraph createRuleDependencyGraph(Set<TransformationRule<? extends IPatternMatch>> transformations,
            Set<PatternWithCardinality> constraints, Set<PatternWithCardinality> goalPatterns);
}
