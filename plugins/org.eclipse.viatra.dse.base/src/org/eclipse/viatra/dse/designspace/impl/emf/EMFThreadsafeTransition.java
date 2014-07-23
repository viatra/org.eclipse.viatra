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

import org.eclipse.viatra.dse.designspace.api.IDesignSpace;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TransitionMetaData;
import org.eclipse.viatra.dse.emf.designspace.State;
import org.eclipse.viatra.dse.emf.designspace.Transition;


/**
 * The class that provides the implementation for the {@link ITransition} interface in the EMF based
 * {@link IDesignSpace} implementation.
 * 
 * @author Miklos Foldenyi
 */
public class EMFThreadsafeTransition implements ITransition {

    private final Transition internalTransition;

    private volatile boolean assignedToFire = false;

    protected EMFThreadsafeTransition(State source, Object id, Transition internalTransition) {
        this.internalTransition = internalTransition;
        internalTransition.setFiredFrom(source);
        internalTransition.setId(id);
        internalTransition.setThreadsafeFacade(this);
    }

    protected Transition getInternalTransition() {
        return internalTransition;
    }

    @Override
    public synchronized Object getId() {
        return internalTransition.getId();
    }

    @Override
    public synchronized IState getResultsIn() {
        return (EMFThreadsafeState) internalTransition.getResultsIn().getThreadsafeFacade();
    }

    @Override
    public synchronized IState getFiredFrom() {
        return (EMFThreadsafeState) internalTransition.getFiredFrom().getThreadsafeFacade();
    }

    @Override
    public synchronized void setResultsIn(IState state) {
        State resultState = ((EMFThreadsafeState) state).getState();
        synchronized (resultState) {
            internalTransition.setResultsIn(resultState);
        }
    }

    @Override
    public synchronized boolean isAssignedToFire() {
        return assignedToFire;
    }

    @Override
    public synchronized boolean tryToLock() {
        if (!assignedToFire) {
            assignedToFire = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public TransitionMetaData getTransitionMetaData() {
        return (TransitionMetaData) internalTransition.getRuleData();
    }

}
