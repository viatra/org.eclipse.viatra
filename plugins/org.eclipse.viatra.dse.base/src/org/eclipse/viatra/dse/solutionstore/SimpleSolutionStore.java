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
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.statecode.IStateSerializerFactory;

public class SimpleSolutionStore implements ISolutionStore {

    private ConcurrentHashMap<Object, Solution> solutions = new ConcurrentHashMap<Object, Solution>(5, 0.75f, 1);

    @Override
    public Solution newSolution(ThreadContext context, Map<String, Double> measurements) {

        DesignSpaceManager dsm = context.getDesignSpaceManager();
        Object id = dsm.getCurrentState().getId();
        IStateSerializerFactory serializerFactory = context.getGlobalContext().getStateSerializerFactory();
        SolutionTrajectory solutionTrajectory = dsm.getTrajectoryInfo().createSolutionTrajectory(serializerFactory);

        Solution existentSolution = solutions.get(id);

        if (existentSolution != null) {
            existentSolution.addTrajectory(solutionTrajectory);
            return existentSolution;
        } else {
            Solution solution = new Solution(id, solutionTrajectory);

            Solution elderSolution = solutions.putIfAbsent(id, solution);

            // If the race condition is lost, put only the trajectory into it
            if (elderSolution != null) {
                elderSolution.addTrajectory(solutionTrajectory);
                return elderSolution;
            }
            return solution;
        }
    }

    @Override
    public Collection<Solution> getSolutions() {
        return solutions.values();
    }

}
