/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.mutations;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.interfaces.IMutation;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class AddRandomTransitionMutation implements IMutation {

    @Override
    public boolean mutate(TrajectoryFitness parent, ThreadContext context) {

        context.executeTrajectoryWithMinimalBacktrackWithoutStateCoding(parent.trajectory);

        return context.executeRandomActivationId();
    }

    @Override
    public IMutation createNew() {
        return this;
    }
}
