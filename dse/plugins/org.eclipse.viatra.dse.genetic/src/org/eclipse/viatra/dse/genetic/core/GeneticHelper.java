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
package org.eclipse.viatra.dse.genetic.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.designspace.api.ITransition;

public class GeneticHelper {

    /**
     * Checks if two trajectories are the same.
     * 
     * @param trajectory1
     * @param trajectory2
     * @return Returns true, if they are equivalent.
     */
    public static boolean isSameTrajectory(List<ITransition> trajectory1, List<ITransition> trajectory2) {

        if (trajectory1.size() != trajectory2.size()) {
            return false;
        }

        for (int i = trajectory1.size() - 1; i >= 1; --i) {
            ITransition t1 = trajectory1.get(i);
            ITransition t2 = trajectory2.get(i);
            if (!t1.getId().equals(t2.getId()) || !t1.getFiredFrom().getId().equals(t2.getFiredFrom().getId())) {
                return false;
            }
        }

        // The first transitions don't have parent state
        ITransition t1 = trajectory1.get(0);
        ITransition t2 = trajectory2.get(0);
        if (!t1.getId().equals(t2.getId())) {
            return false;
        }

        return true;
    }

    public static ITransition getByIndex(Collection<? extends ITransition> availableTransitions, int index) {
        int i = 0;
        Iterator<? extends ITransition> iterator = availableTransitions.iterator();
        while (iterator.hasNext()) {
            ITransition transition = iterator.next();
            if (i == index) {
                // TODO check global constraint?
                return transition;
            } else {
                ++i;
            }
        }
        throw new IndexOutOfBoundsException("size: " + i + ", index: " + index);
    }
    
    public static boolean tryFireRightTransition(DesignSpaceManager dsm, ITransition transition) {
        Object id = transition.getId();
        for (ITransition t : dsm.getTransitionsFromCurrentState()) {
            if (t.getId().equals(id)) {
                return dsm.tryFireActivation(t);
            }
        }
        return false;
    }

}
