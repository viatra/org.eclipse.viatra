/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.solutionstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.Fitness;
import org.eclipse.viatra.dse.statecode.IStateCoderFactory;

/**
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class SolutionStore {

    public interface IEnoughSolutions extends ISolutionFoundHandler {
        boolean enoughSolutions();
    }

    public interface ISolutionFoundHandler {
        void solutionFound(ThreadContext context, SolutionTrajectory trajectory);
        void solutionTriedToSave(ThreadContext context, SolutionTrajectory trajectory);
    }

    public static class ANumberOfEnoughSolutions implements IEnoughSolutions {

        private final AtomicInteger foundSolutions;
        private final AtomicBoolean enoughSolutions;

        public ANumberOfEnoughSolutions(int number) {
            foundSolutions = new AtomicInteger(number);
            enoughSolutions = new AtomicBoolean(false);
        }

        @Override
        public boolean enoughSolutions() {
            return enoughSolutions.get();
        }

        @Override
        public void solutionFound(ThreadContext context, SolutionTrajectory trajectory) {
            int solutionsToFind = foundSolutions.decrementAndGet();
            if (solutionsToFind == 0) {
                enoughSolutions.set(true);
            }
        }

        @Override
        public void solutionTriedToSave(ThreadContext context, SolutionTrajectory trajectory) {
        }
    }

    public static class LogSolutionHandler implements ISolutionFoundHandler {

        @Override
        public void solutionFound(ThreadContext context, SolutionTrajectory trajectory) {
            Logger.getLogger("Solution registered: " + getClass()).info(trajectory.toPrettyString());
        }

        @Override
        public void solutionTriedToSave(ThreadContext context, SolutionTrajectory trajectory) {
            Logger.getLogger("Not good enough solution: " + getClass()).info(trajectory.toPrettyString());
        }
    }

    private boolean acceptOnlyGoalSolutions = true;
    private final Map<Object, Solution> solutions = new HashMap<Object, Solution>();
    private List<ISolutionFoundHandler> solutionFoundHandlers;

    private final IEnoughSolutions enoughSolutions;

    public SolutionStore() {
        this(new IEnoughSolutions() {
            @Override
            public void solutionFound(ThreadContext context, SolutionTrajectory trajectory) {
            }

            @Override
            public boolean enoughSolutions() {
                return false;
            }

            @Override
            public void solutionTriedToSave(ThreadContext context, SolutionTrajectory trajectory) {
            }
        });
    }

    public SolutionStore(int numOfSolutionsToFind) {
        this(new ANumberOfEnoughSolutions(numOfSolutionsToFind));
    }

    public SolutionStore(IEnoughSolutions enoughSolutionsImpl) {
        enoughSolutions = enoughSolutionsImpl;
    }

    /**
     * 
     * @param context
     * @return True if the solutions is not found previously.
     */
    public synchronized boolean newSolution(ThreadContext context) {

        Fitness fitness = context.getLastFitness();

        if (acceptOnlyGoalSolutions && !fitness.isSatisifiesHardObjectives()) {
            return false;
        }

        DesignSpaceManager dsm = context.getDesignSpaceManager();
        Object id = dsm.getCurrentState().getId();
        IStateCoderFactory stateCoderFactory = context.getGlobalContext().getStateCoderFactory();
        SolutionTrajectory solutionTrajectory = dsm.getTrajectoryInfo().createSolutionTrajectory(stateCoderFactory);
        solutionTrajectory.setFitness(fitness);

        Solution solution = solutions.get(id);

        if (solution != null) {
            if (solution.getTrajectories().contains(solutionTrajectory)) {
                return false;
            } else {
                solution.addTrajectory(solutionTrajectory);
            }
        } else {
            Solution newSolution = new Solution(id, solutionTrajectory);
            solutions.put(id, newSolution);
        }

        enoughSolutions.solutionFound(context, solutionTrajectory);

        if (solutionFoundHandlers != null) {
            for (ISolutionFoundHandler handler : solutionFoundHandlers) {
                handler.solutionFound(context, solutionTrajectory);
            }
        }

        if (enoughSolutions.enoughSolutions()) {
            context.getGlobalContext().stopAllThreads();
        }

        return true;
    }

    public synchronized Collection<Solution> getSolutions() {
        return solutions.values();
    }

    public synchronized void registerSolutionFoundHandler(ISolutionFoundHandler handler) {
        if (solutionFoundHandlers == null) {
            solutionFoundHandlers = new ArrayList<ISolutionFoundHandler>(1);
        }
        solutionFoundHandlers.add(handler);
    }

    public void logSolutionsWhenFound() {
        registerSolutionFoundHandler(new LogSolutionHandler());
        Logger.getLogger(LogSolutionHandler.class).setLevel(Level.INFO);
    }

    public void acceptGoalSolutionsOnly() {
        acceptOnlyGoalSolutions = true;
    }

    public void acceptAnySolutions() {
        acceptOnlyGoalSolutions = false;
    }
}
