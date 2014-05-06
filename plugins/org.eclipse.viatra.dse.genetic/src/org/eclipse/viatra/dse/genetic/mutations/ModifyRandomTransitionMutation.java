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
package org.eclipse.viatra.dse.genetic.mutations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.genetic.core.GeneticHelper;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.genetic.interfaces.IMutateTrajectory;

public class ModifyRandomTransitionMutation implements IMutateTrajectory {

    private Random rnd = new Random();

    @Override
    public InstanceData mutate(InstanceData originalTrajectory, ThreadContext context) {
        if (originalTrajectory.trajectory.isEmpty()) {
            throw new DSEException("Can't modify an empty trajectory");
        }

        ArrayList<ITransition> result = new ArrayList<ITransition>(originalTrajectory.trajectory);

        int choosenTransitionIndex = rnd.nextInt(result.size());
        ITransition oldTransition = GeneticHelper.getByIndex(result, choosenTransitionIndex);
        Collection<? extends ITransition> availableTransitions = oldTransition.getFiredFrom().getOutgoingTransitions();

        int choosenNewTransitionIndex = rnd.nextInt(availableTransitions.size());
        Iterator<? extends ITransition> iterator = availableTransitions.iterator();
        while (iterator.hasNext()) {
            ITransition newTransition = iterator.next();
            if (choosenNewTransitionIndex-- == 0) {
                // TODO same transition; there is only 1 transition; everywhere
                // there is only 1 transition
                result.set(choosenTransitionIndex, newTransition);
                return new InstanceData(result);
            }
        }

        return new InstanceData(result);
    }

}
