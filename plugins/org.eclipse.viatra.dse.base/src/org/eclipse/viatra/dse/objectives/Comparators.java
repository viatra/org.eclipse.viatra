/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.objectives;

import java.util.Comparator;

/**
 * This helper class holds comparators for objective implementations.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class Comparators {

    public static final Comparator<Double> BIGGER_IS_BETTER = new Comparator<Double>() {
        @Override
        public int compare(Double o1, Double o2) {
            return o1.compareTo(o2);
        }
    };

    public static final Comparator<Double> LOWER_IS_BETTER = new Comparator<Double>() {
        @Override
        public int compare(Double o1, Double o2) {
            return -o1.compareTo(o2);
        }
    };
    
    private static final Double zero = new Double(0);
    
    public static final Comparator<Double> DIFFERENCE_TO_ZERO_IS_BETTER = new Comparator<Double>() {
        @Override
        public int compare(Double o1, Double o2) {
            return zero.compareTo(Math.abs(o1)-Math.abs(o2));
        }
    };
    
}
