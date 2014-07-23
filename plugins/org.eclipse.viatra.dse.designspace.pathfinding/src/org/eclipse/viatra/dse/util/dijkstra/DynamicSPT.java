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
package org.eclipse.viatra.dse.util.dijkstra;

import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.eclipse.viatra.dse.api.Solution;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.designspace.api.IDesignSpaceChangeHandler;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.statecode.IStateSerializerFactory;

public class DynamicSPT implements IDesignSpaceChangeHandler, IPathfinder {

    private DSEVertex root;

    private DynamicSPTMode mode = DynamicSPTMode.BEST_ON_REQUEST;

    public enum DynamicSPTMode {
        ANY_TIME, BEST_ON_REQUEST, EXPLICIT_CALL
    }

    public DynamicSPTMode getMode() {
        return mode;
    }

    public void setMode(DynamicSPTMode mode) {
        this.mode = mode;
    }

    private final WeakHashMap<IState, DSEVertex> vertexMap = new WeakHashMap<IState, DSEVertex>();

    private final WeakHashMap<ITransition, DSEEdge> edgeMap = new WeakHashMap<ITransition, DSEEdge>();

    private final PriorityBlockingQueue<DSEEdge> workPriorityQueue = new PriorityBlockingQueue<DSEEdge>(20,
            new EdgeComparator());

    public void refreshSPT() {
        DSEEdge edge;

        while ((edge = workPriorityQueue.poll()) != null) {
            solveOne(edge);
        }
    }

    private void solveOne(DSEEdge edge) {
        DSEVertex source = edge.getSource();
        DSEVertex destination = edge.getDestination();

        synchronized (source) {
            synchronized (destination) {
                long costThroughThisEdge = source.getCost() + edge.getWeight();

                if (destination.getCost() > costThroughThisEdge) {
                    destination.setBestIncomingEdge(edge);
                    destination.setCost(costThroughThisEdge);
                    solveVertex(destination);
                }
            }
        }
    }

    private void solveVertex(DSEVertex vertex) {
        for (DSEEdge edge : vertex.getOutgoingEdges()) {
            workPriorityQueue.remove(edge);

            if (edge.getDestination() != null) {
                workPriorityQueue.add(edge);
            }
        }
    }

    @Override
    public void newStateAdded(IState state) {
        create(state);
    }

    @Override
    public void newTransitionAdded(ITransition transition) {
        create(transition);
    }

    @Override
    public void transitionFired(ITransition transition) {
        DSEEdge edge = get(transition);
        if (edge == null) {
            get(transition);
        }
        workPriorityQueue.add(edge);
        switch (mode) {
        case ANY_TIME:
            refreshSPT();
            break;
        default:
        case BEST_ON_REQUEST:
        case EXPLICIT_CALL:
            break;
        }
    }

    @Override
    public void newRootAdded(IState state) {
        // TODO works only for one root, support multiple roots if possible
        if (root != null) {
            root.setCost(Long.MAX_VALUE);
        }
        root = create(state);
        root.setCost(0);
    }

    protected DSEVertex get(IState state) {
        synchronized (vertexMap) {
            return vertexMap.get(state);
        }
    }

    private DSEVertex create(IState state) {
        synchronized (vertexMap) {
            if (!vertexMap.containsKey(state)) {
                vertexMap.put(state, new DSEVertex(state, this));
            }
            return get(state);
        }
    }

    protected DSEEdge get(ITransition transition) {
        synchronized (edgeMap) {
            return edgeMap.get(transition);
        }
    }

    private DSEEdge create(ITransition transition) {
        synchronized (edgeMap) {
            if (!edgeMap.containsKey(transition)) {
                edgeMap.put(transition, new DSEEdge(transition, this));
            }
            return get(transition);
        }
    }

    private Deque<ITransition> getBestTransitionTrajectory(IState solutionState) {
        Deque<ITransition> transitions = new LinkedList<ITransition>();

        DSEVertex currentVertex = get(solutionState);

        while (currentVertex != root) {
            transitions.addFirst(currentVertex.getBestIncomingEdge().getT());
            currentVertex = get(currentVertex.getBestIncomingEdge().getT().getFiredFrom());
        }

        return transitions;
    }

    @Override
    public SolutionTrajectory getBestTrajectoryCheaply(Solution s, IState solutionState,
            IStateSerializerFactory stateSerializerFactory) {
        if (s.getShortestTrajectory().getTrajectoryLength() > get(solutionState).getCost()) {

            List<Object> transitionIds = new LinkedList<Object>();
            List<TransformationRule<?>> transformationRules = new LinkedList<TransformationRule<?>>();

            // get the transition sequence that leads to the solution
            Deque<ITransition> bestTrajectory = getBestTransitionTrajectory(solutionState);

            Iterator<ITransition> transitionIterator = bestTrajectory.iterator();

            if (transitionIterator.hasNext()) {
                ITransition transition = transitionIterator.next();
                transitionIds.add(transition.getId());
                transformationRules.add(transition.getTransitionMetaData().rule);
            }
            while (transitionIterator.hasNext()) {
                ITransition transition = transitionIterator.next();
                transitionIds.add(transition.getId());
                transformationRules.add(transition.getTransitionMetaData().rule);
            }

            SolutionTrajectory trajectory = new SolutionTrajectory(transitionIds, transformationRules,
                    stateSerializerFactory);
            return trajectory;
        } else {
            return null;
        }
    }

    @Override
    public SolutionTrajectory getBestTrajectoryCostly(Solution s, IState solutionState,
            IStateSerializerFactory stateSerializerFactory) {
        switch (mode) {
        case EXPLICIT_CALL:
        case ANY_TIME:
            // because it is either already kept up to date automatically due to
            // being in ANY_TIME mode, or because an explicit refresh has been
            // called prior to calling this method
            return getBestTrajectoryCheaply(s, solutionState, stateSerializerFactory);
        case BEST_ON_REQUEST:
            // on call we refresh the SPT, and based on that, return with a
            // 'cheap' result
            refreshSPT();
            return getBestTrajectoryCheaply(s, solutionState, stateSerializerFactory);
        default:
            break;
        }
        refreshSPT();
        return getBestTrajectoryCheaply(s, solutionState, stateSerializerFactory);
    }

    protected class EdgeComparator implements Comparator<DSEEdge> {

        @Override
        public int compare(DSEEdge o1, DSEEdge o2) {
            if (o1.getSource().getCost() < o2.getSource().getCost()) {
                return -1;
            } else if (o1.getSource().getCost() == o2.getSource().getCost()) {
                return 0;
            } else {
                return 1;
            }
        }
    }

}
