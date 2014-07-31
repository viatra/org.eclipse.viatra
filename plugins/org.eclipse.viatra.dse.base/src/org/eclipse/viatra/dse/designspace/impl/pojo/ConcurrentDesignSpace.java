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
package org.eclipse.viatra.dse.designspace.impl.pojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.designspace.api.IDesignSpace;
import org.eclipse.viatra.dse.designspace.api.IDesignSpaceChangeHandler;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.IState.TraversalStateType;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TransitionMetaData;

public class ConcurrentDesignSpace implements IDesignSpace {

    private final ConcurrentHashMap<Object, State> objectToStateMap = new ConcurrentHashMap<Object, State>(64, 0.75f, 1);
    private final AtomicReference<State> rootState = new AtomicReference<State>();
    private final AtomicLong numberOfTransitions = new AtomicLong(0);

    private final ConcurrentLinkedQueue<State> rootStates = new ConcurrentLinkedQueue<State>();
    private final Collection<IDesignSpaceChangeHandler> changeHandlers = new HashSet<IDesignSpaceChangeHandler>();

    private final Logger logger = Logger.getLogger(this.getClass());

    @Override
    public IState[] getRoot() {
        IState[] result = new IState[rootStates.size()];
        return rootStates.toArray(result);
    }

    @Override
    public void addRoot(final IState root) {
        rootStates.add((State) root);
        State newRoot = (State) root;
        rootState.set(newRoot);
        fireNewRootAddedEvent(newRoot);
    }

    @Override
    public boolean addState(ITransition sourceTransition, Object newStateId,
            Map<Object, TransitionMetaData> outgoingTransitionIds) {

        // Is the state exists? if yes then return false
        State state = getStateById(newStateId);
        if (state != null) {
            // set transition's results in
            if (sourceTransition != null && !state.getIncomingTransitions().contains(sourceTransition)) {
                sourceTransition.setResultsIn(state);
                state.addInTransition((Transition) sourceTransition);
                fireTransitionFiredEvent(sourceTransition);
            }
            return false;
        }

        boolean result;
        // create new state
        state = new State(newStateId);
        // set outgoing transitios
        Transition[] outTransitions = new Transition[outgoingTransitionIds.keySet().size()];
        int i = 0;
        for (Object transitionId : outgoingTransitionIds.keySet()) {
            Transition t = new Transition(transitionId, state, outgoingTransitionIds.get(transitionId));
            outTransitions[i++] = t;
            fireNewTransitionEvent(t); // TODO this can be faulty if race is lost. Is this needed?
        }
        state.setOutTransitions(outTransitions);
        // set incoming transition
        if (sourceTransition != null) {
            state.addInTransition((Transition) sourceTransition);
        }

        // save new state
        State elderState = objectToStateMap.putIfAbsent(newStateId, state);

        // Thread won the state creation
        if (elderState == null) {
            if (sourceTransition != null) {
                sourceTransition.setResultsIn(state);
            } else {
                addRoot(state);
            }
            // finish modifying shared data
            state.setProcessed();

            numberOfTransitions.addAndGet(outgoingTransitionIds.size());

            fireNewStateEvent(state);

            result = true;
        } else {
            // thread lost the state creation
            logger.debug(" LOST the state creation race for id " + state.getId() + " " + state);
            if (sourceTransition != null) {
                sourceTransition.setResultsIn(elderState);
                elderState.addInTransition((Transition) sourceTransition);
            }
            result = false;
        }

        if (sourceTransition != null) {
            fireTransitionFiredEvent(sourceTransition);
        }

        return result;
    }

    @Override
    public State getStateById(final Object id) {
        return objectToStateMap.get(id);
    }

    /**
     * Saves the design space into a given file. Relative or absolute path can be included.
     * <p>
     * If the {@code fileName} ends with .dgml or .gml, it will save the design space in a correct format, otherwise it
     * will use a simple xml format.
     */
    @Override
    public void saveDesignSpace(String fileName) throws IOException {

        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        if (fileName.endsWith(".dgml")) {
            sb.append("<?xml version='1.0' encoding='utf-8'?>\r\n"
                    + "<DirectedGraph xmlns=\"http://schemas.microsoft.com/vs/2009/dgml\">\r\n<Nodes>");
            sb.append("<Node Id=\"notexist\" Label=\"notexist\"/>");
            int i = 0;
            for (Entry<Object, State> entry : objectToStateMap.entrySet()) {
                Object id = entry.getKey();
                State state = entry.getValue();
                sb.append("<Node Id=\"" + id + "\" Label=\"" + i + "\"");
                if (state.getTraversalState().equals(TraversalStateType.GOAL)) {
                    sb.append(" Category=\"Goal\"");
                } else if (state.getTraversalState().equals(TraversalStateType.CUT)) {
                    sb.append(" Category=\"Cut\"");
                }
                sb.append("/>\r\n");
                for (ITransition transition : state.getOutgoingTransitions()) {
                    if (transition.getResultsIn() != null) {
                        sb2.append("<Link Source=\"" + id + "\" Target=\"" + transition.getResultsIn().getId()
                                + "\" Label=\"" + transition.getId() + "\"/>\r\n");
                    } else {
                        sb2.append("<Link Source=\"" + id + "\" Target=\"notexist\" Label=\"" + transition.getId()
                                + "\"/>\r\n");
                    }
                }
                i++;
            }
            sb.append("</Nodes>\r\n<Links>\r\n");
            sb.append(sb2);
            sb.append("</Links>\r\n" + "<Categories>\r\n"
                    + "    <Category Id=\"Goal\" Label=\"Goal\" Background=\"#11FB01E2\" IsTag=\"True\" />\r\n"
                    + "    <Category Id=\"Cut\" Label=\"Cut\" Background=\"#FF1B21E2\" IsTag=\"True\" />\r\n"
                    + "  </Categories>" + " <Properties>\r\n"
                    + "  <Property Id=\"Label\" Label=\"Label\" DataType=\"String\" />\r\n"
                    + "  <Property Id=\"Background\" Label=\"Background\" DataType=\"Brush\" />" + " </Properties>\r\n"
                    + "<Styles>\r\n"
                    + "    <Style TargetType=\"Node\" GroupLabel=\"Goal\" ValueLabel=\"Has category\">\r\n"
                    + "      <Condition Expression=\"HasCategory('Goal')\" />\r\n"
                    + "      <Setter Property=\"Background\" Value=\"#FF00FF00\" />\r\n" + "    </Style>\r\n"
                    + "    <Style TargetType=\"Node\" GroupLabel=\"Cut\" ValueLabel=\"Has category\">\r\n"
                    + "      <Condition Expression=\"HasCategory('Cut')\" />\r\n"
                    + "      <Setter Property=\"Background\" Value=\"FFFF0000\" />\r\n" + "    </Style>\r\n"
                    + "  </Styles>" + "</DirectedGraph>");
        } else if (fileName.endsWith(".gml")) {
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                    + "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"  \r\n"
                    + "      xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
                    + "      xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns \r\n"
                    + "        http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\r\n"
                    + "  <key id=\"hash\" for=\"node\" attr.name=\"hash\" attr.type=\"string\"/>\r\n"
                    + "  <key id=\"rule\" for=\"edge\" attr.name=\"rule\" attr.type=\"string\"/>\r\n"
                    + "  <graph id=\"G\" edgedefault=\"directed\">\r\n");

            int e = 0;
            for (Entry<Object, State> entry : objectToStateMap.entrySet()) {
                Object id = entry.getKey();
                State state = entry.getValue();
                sb.append("<node id=\"" + id + "\">\r\n");
                sb.append("<data key=\"hash\">" + id + "</data>\r\n</node>\r\n");

                for (ITransition transition : state.getOutgoingTransitions()) {
                    if (transition.getResultsIn() != null) {
                        sb.append("<edge id=\"e" + e + "\" source=\"" + id + "\"");
                        sb.append(" target=\"" + transition.getResultsIn().getId() + "\"");
                        sb.append(">\r\n<data key=\"rule\">" + transition.getId() + "</data>\r\n");
                        sb.append("</edge>\r\n");
                        ++e;
                    }
                }
            }

            sb.append("</graph>\\r\\n</graphml>");
        } else {
            sb.append("<designspace>\r\n");

            for (Entry<Object, State> entry : objectToStateMap.entrySet()) {
                Object id = entry.getKey();
                State state = entry.getValue();
                sb.append("<state id=\"");
                sb.append(id);
                sb.append("\" state=\"");
                sb.append(state.getTraversalState().name());
                sb.append("\" inTransitions=\"");
                for (ITransition transition : state.getIncomingTransitions()) {
                    sb.append(transition.getId());
                    sb.append("; ");
                }
                sb.append("\" outTransitions=\"");
                for (ITransition transition : state.getOutgoingTransitions()) {
                    sb.append(transition.getId());
                    sb.append("; ");
                }
                sb.append("\" />\r\n");
            }

            sb.append("</designspace>");
        }

        FileWriter fw = new FileWriter(new File(fileName));
        fw.write(sb.toString());
        fw.close();
    }

    @Override
    public long getNumberOfStates() {
        return objectToStateMap.size();
    }

    @Override
    public long getNumberOfTransitions() {
        return numberOfTransitions.get();
    }

    @Override
    public void addDesignSpaceChangedListener(IDesignSpaceChangeHandler changeEventHandler) {
        if (!changeHandlers.contains(changeEventHandler)) {
            changeHandlers.add(changeEventHandler);
        }
    }

    @Override
    public void removeDesignSpaceChangedListener(IDesignSpaceChangeHandler changeEventHandler) {
        if (changeHandlers.contains(changeEventHandler)) {
            changeHandlers.remove(changeEventHandler);
        }
    }

    private void fireNewRootAddedEvent(IState state) {
        logger.debug("Root is set to " + state.getId());
        for (IDesignSpaceChangeHandler handler : changeHandlers) {
            handler.newRootAdded(state);
        }
    }

    private void fireTransitionFiredEvent(ITransition transition) {
        logger.debug("Transition fired from " + transition.getFiredFrom().getId() + " with id " + transition.getId()
                + " and resulted in " + transition.getResultsIn().getId());
        for (IDesignSpaceChangeHandler handler : changeHandlers) {
            handler.transitionFired(transition);
        }
    }

    private void fireNewStateEvent(IState state) {
        logger.debug("Created state with id " + state.getId());
        for (IDesignSpaceChangeHandler handler : changeHandlers) {
            handler.newStateAdded(state);
        }
    }

    private void fireNewTransitionEvent(ITransition transition) {
        logger.debug("New transition from " + transition.getFiredFrom().getId() + " with id " + transition.getId());
        for (IDesignSpaceChangeHandler handler : changeHandlers) {
            handler.newTransitionAdded(transition);
        }
    }

    public Enumeration<State> getStates() {
        return objectToStateMap.elements();
    }
}
