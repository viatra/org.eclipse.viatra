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
package org.eclipse.viatra.dse.evolutionary;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.viatra.dse.base.DesignSpaceManager;

public class GeneticHelper {

    public static Object getByIndex(Collection<Object> availableTransitions, int index) {
        int i = 0;
        Iterator<Object> iterator = availableTransitions.iterator();
        while (iterator.hasNext()) {
            Object transition = iterator.next();
            if (i == index) {
                // TODO check global constraint?
                return transition;
            } else {
                ++i;
            }
        }
        throw new IndexOutOfBoundsException("size: " + i + ", index: " + index);
    }
    
    public static boolean tryFireRightTransition(DesignSpaceManager dsm, Object transition) {
        for (Object t : dsm.getTransitionsFromCurrentState()) {
            if (t.equals(transition)) {
                return dsm.tryFireActivation(t);
            }
        }
        return false;
    }

}
