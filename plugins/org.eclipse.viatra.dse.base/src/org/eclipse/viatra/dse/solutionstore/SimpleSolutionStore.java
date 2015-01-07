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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.api.strategy.interfaces.ISolutionFoundHandler;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.statecode.IStateSerializerFactory;

/**
 * This is a simple implementation of the {@link ISolutionStore} interface which stores all the found solution
 * trajectory (i.e. for which the implementation of {@link ICheckGoalState#isGoalState(ThreadContext)} returns with
 * anything but null).
 * 
 * It can be configured to stop the exploration after a predefined number of solutions is found.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class SimpleSolutionStore implements ISolutionStore {

    private final ConcurrentHashMap<Object, Solution> solutions = new ConcurrentHashMap<Object, Solution>(5, 0.75f, 1);
    private ConcurrentLinkedQueue<ISolutionFoundHandler> solutionFoundHandlers;
    private final int numOfSolutionsToFind;
    private final AtomicInteger foundSolutions = new AtomicInteger(0);
    
    public SimpleSolutionStore() {
        this(0);
    }

    public SimpleSolutionStore(int numOfSolutionsToFind) {
        this.numOfSolutionsToFind = numOfSolutionsToFind;
    }

    @Override
    public StopExecutionType newSolution(ThreadContext context, Map<String, Double> objectives) {

        DesignSpaceManager dsm = context.getDesignSpaceManager();
        Object id = dsm.getCurrentState().getId();
        IStateSerializerFactory serializerFactory = context.getGlobalContext().getStateSerializerFactory();
        SolutionTrajectory solutionTrajectory = dsm.getTrajectoryInfo().createSolutionTrajectory(serializerFactory);
        solutionTrajectory.setObjectives(objectives);
        
        Solution solution = solutions.get(id);

        if (solution != null) {
            solution.addTrajectory(solutionTrajectory);
        } else {
            Solution newSolution = new Solution(id, solutionTrajectory);

            Solution elderSolution = solutions.putIfAbsent(id, newSolution);

            // If the race condition is lost, put only the trajectory into it
            if (elderSolution != null) {
                elderSolution.addTrajectory(solutionTrajectory);
                solution = elderSolution;
            }
            else {
                solution = newSolution;
            }
        }

        if (solutionFoundHandlers != null) {
            for (ISolutionFoundHandler handler : solutionFoundHandlers) {
                handler.solutionFound(solutionTrajectory, solution, context);
            }
        }

        if (numOfSolutionsToFind > 0 && foundSolutions.incrementAndGet() >= numOfSolutionsToFind) {
            return StopExecutionType.STOP_ALL;
        }
        
        return StopExecutionType.CONTINUE;
    }

    @Override
    public Collection<Solution> getSolutions() {
        return solutions.values();
    }

    @Override
    public synchronized void registerSolutionFoundHandler(ISolutionFoundHandler handler) {
        if (solutionFoundHandlers == null) {
            solutionFoundHandlers = new ConcurrentLinkedQueue<ISolutionFoundHandler>();
        }
        solutionFoundHandlers.add(handler);
    }

}
