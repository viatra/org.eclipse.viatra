/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.viatra.dse.base.DesignSpaceManager;

public class GeneticHelper {

    private GeneticHelper() {/* Hidden utility class constructor */}
    
    public static Object getByIndex(Collection<Object> availableTransitions, int index) {
        int i = 0;
        Iterator<Object> iterator = availableTransitions.iterator();
        while (iterator.hasNext()) {
            Object transition = iterator.next();
            if (i == index) {
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
