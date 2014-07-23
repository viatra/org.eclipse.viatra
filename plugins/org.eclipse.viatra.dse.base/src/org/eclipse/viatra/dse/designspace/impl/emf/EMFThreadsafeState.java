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
package org.eclipse.viatra.dse.designspace.impl.emf;

import java.util.Collection;

import org.eclipse.viatra.dse.designspace.api.IDesignSpace;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.emf.designspace.EMFInternalTraversalState;
import org.eclipse.viatra.dse.emf.designspace.State;

/**
 * The class that provides the implementation for the {@link IState} interface in the EMF based {@link IDesignSpace}
 * implementation.
 * 
 * @author Miklos Foldenyi
 */
public class EMFThreadsafeState implements IState {

    private EMFConcurrentTransitionList inTransitions;
    private EMFConcurrentTransitionList outTransitions;

    private final State state;

    protected EMFThreadsafeState(State state, Object id) {
        this.state = state;
        state.setId(id);
        state.setThreadsafeFacade(this);
        inTransitions = new EMFConcurrentTransitionList(state.getInTransitions());
        outTransitions = new EMFConcurrentTransitionList(state.getOutTransitions());
    }

    protected synchronized State getState() {
        return state;
    }

    @Override
    public synchronized TraversalStateType getTraversalState() {
        switch (state.getState()) {
        case CUT:
            return TraversalStateType.CUT;
        case GOAL:
            return TraversalStateType.GOAL;
        case NOT_YET_PROCESSED:
        case TRAVERSED:
        default:
            return TraversalStateType.TRAVERSED;
        }
    }

    @Override
    public synchronized Object getId() {
        return state.getId();
    }

    @Override
    public synchronized Collection<EMFThreadsafeTransition> getIncomingTransitions() {
        return inTransitions;
    }

    @Override
    public synchronized Collection<EMFThreadsafeTransition> getOutgoingTransitions() {
        return outTransitions;
    }

    @Override
    public synchronized void setTraversalState(TraversalStateType traversalState) {
        switch (traversalState) {
        case CUT:
            state.setState(EMFInternalTraversalState.CUT);
            break;
        case GOAL:
            state.setState(EMFInternalTraversalState.GOAL);
            break;
        case TRAVERSED:
            state.setState(EMFInternalTraversalState.TRAVERSED);
        default:
            break;
        }
    }

    @Override
    public synchronized boolean isProcessed() {
        return state.getState() != EMFInternalTraversalState.NOT_YET_PROCESSED;
    }

    @Override
    public synchronized void setProcessed() {
        switch (state.getState()) {
        case NOT_YET_PROCESSED:
            state.setState(EMFInternalTraversalState.TRAVERSED);
            break;
        default:
            break;
        }
    }
}
