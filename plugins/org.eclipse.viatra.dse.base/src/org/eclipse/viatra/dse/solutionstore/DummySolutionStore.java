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
package org.eclipse.viatra.dse.solutionstore;

import java.util.Collection;
import java.util.Map;

import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFoundHandler;
import org.eclipse.viatra.dse.base.ThreadContext;

/**
 * An empty implementation of {@link ISolutionStore} which doesn't store any solution. Useful when the framework is
 * extended in such a way, that the solutions are stored elsewhere and in other structure.
 * 
 * @author Andras Szabolcs Nagy
 * 
 */
public class DummySolutionStore implements ISolutionStore {

    @Override
    public StopExecutionType newSolution(ThreadContext context, Map<String, Double> objectives) {
        return StopExecutionType.CONTINUE;
    }

    @Override
    public Collection<Solution> getSolutions() {
        throw new UnsupportedOperationException("DummySolutionStore cannot return any solutions.");
    }

    @Override
    public void registerSolutionFoundHandler(ISolutionFoundHandler handler) {
    }

}
