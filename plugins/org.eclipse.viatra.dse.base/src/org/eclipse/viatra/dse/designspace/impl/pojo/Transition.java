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

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.viatra.dse.designspace.api.IDesignSpace;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TransitionMetaData;

/**
 * The class that provides the implementation for the {@link ITransition} interface in the POJO based
 * {@link IDesignSpace} implementation.
 * 
 * @author Andras Szabolcs Nagy
 */
public class Transition implements ITransition {

    private final Object id;
    private final State firedFrom;
    private volatile State resultsIn;
    private final AtomicBoolean assignedToFire = new AtomicBoolean(false);
    private final TransitionMetaData metaData;

    Transition(Object id, State firedFrom, TransitionMetaData metaData) {
        this.id = id;
        this.firedFrom = firedFrom;
        this.metaData = metaData;
    }

    @Override
    public boolean tryToLock() {
        return assignedToFire.compareAndSet(false, true);
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public State getResultsIn() {
        return resultsIn;
    }

    @Override
    public State getFiredFrom() {
        return firedFrom;
    }

    @Override
    public boolean isAssignedToFire() {
        return assignedToFire.get();
    }

    @Override
    public TransitionMetaData getTransitionMetaData() {
        return metaData;
    }

    @Override
    public void setResultsIn(IState state) {
        resultsIn = (State) state;
    }

}
