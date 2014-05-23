/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.eventprocessingstrategy;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Calendar;

import org.eclipse.viatra.cep.core.engine.IEventModelManager;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.State;
import org.eclipse.viatra.cep.core.metamodels.automaton.TrapState;

import com.google.common.base.Preconditions;

public abstract class AbstractEventProcessingStrategy implements IEventProcessingStrategy {

    private IEventModelManager eventModelManager;

    public AbstractEventProcessingStrategy(IEventModelManager eventModelManager) {
        this.eventModelManager = eventModelManager;
    }

    protected IEventModelManager getEventModelManager() {
        return this.eventModelManager;
    }

    protected long getCurrentTimeStamp() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }

    protected boolean handleTimeConstraints(EventToken eventToken, State nextState) {
        return true;
    }

    protected void moveToTrapState(EventToken eventToken) {
        Preconditions.checkArgument(eventToken.getCurrentState().eContainer() instanceof Automaton);

        Automaton sm = (Automaton) eventToken.getCurrentState().eContainer();
        TrapState trapState = getTrapState(sm);
        Preconditions.checkNotNull(trapState);

        eventToken.setCurrentState(trapState);
    }

    private TrapState getTrapState(Automaton automaton) {
        checkArgument(automaton != null);

        for (State s : automaton.getStates()) {
            if (s instanceof TrapState) {
                return (TrapState) s;
            }
        }
        return null;
    }

    // protected boolean isTimeConstraintSatisfied(TimeConstraint constraint) {
    // long start = constraint.getTimeConstraintSpecification().getStartTimestamp();
    // long stop = constraint.getTimeConstraintSpecification().getStopTimestamp();
    // long expected = constraint.getTimeConstraintSpecification().getExpectedLength();
    //
    // if (start == 0l || expected == 0l) {
    // return true;
    // }
    //
    // if (stop != 0l) {
    // if (stop - start < expected) {
    // return true;
    // }
    // }
    // if (stop == 0l) {
    // if (getCurrentTimeStamp() - start < expected) {
    // return true;
    // }
    // }
    //
    // return false;
    // }
}