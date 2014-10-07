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

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.designspace.api.IDesignSpace;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;

/**
 * The class that provides the implementation for the {@link IState} interface in the POJO based {@link IDesignSpace}
 * implementation.
 * 
 * @author Andras Szabolcs Nagy
 */
public class State implements IState {

    private final AtomicReference<TraversalStateType> traversalState = new AtomicReference<TraversalStateType>(
            TraversalStateType.TRAVERSED);
    private final Object id;
    private final CopyOnWriteArrayList<Transition> incomingTransitions = new CopyOnWriteArrayList<Transition>();
    private ThreadsafeImmutableList<Transition> outgoingTransitions;

    private final AtomicBoolean isProcessed = new AtomicBoolean(false);

    private final Collection<? extends ITransition> incomingTransitionsView;

    private final Logger logger = Logger.getLogger(this.getClass());

    void addInTransition(Transition transition) {
        incomingTransitions.add(transition);
    }

    void setOutTransitions(Transition[] transitions) {
        outgoingTransitions = new ThreadsafeImmutableList<Transition>(transitions);
    }

    State(Object id) {
        this.id = id;
        incomingTransitionsView = (Collection<? extends ITransition>) Collections
                .unmodifiableCollection(incomingTransitions);
    }

    @Override
    public TraversalStateType getTraversalState() {
        return traversalState.get();
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void setTraversalState(TraversalStateType traversalState) {
        this.traversalState.set(traversalState);
    }

    @Override
    public Collection<? extends ITransition> getIncomingTransitions() {
        return incomingTransitionsView;
    }

    @Override
    public Collection<? extends ITransition> getOutgoingTransitions() {
        return outgoingTransitions;
    }

    @Override
    public boolean isProcessed() {
        return isProcessed.get();
    }

    @Override
    public void setProcessed() {
        isProcessed.set(true);
        logger.debug(Thread.currentThread() + " State with id " + id + " isProcessed set to true");
    }
}
