/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.util;

/**
 * The degree of accuracy of a cardinality estimate
 * @author Gabor Bergmann
 * @since 2.1
 */
public enum Accuracy {
    EXACT_COUNT,
    BEST_UPPER_BOUND,
    BEST_LOWER_BOUND,
    APPROXIMATION;
    
    /**
     * Partial order comparison.
     */
    public boolean atLeastAsPreciseAs(Accuracy other) {
        switch (this) {
        case EXACT_COUNT: return true;
        case APPROXIMATION: return APPROXIMATION == other;
        case BEST_UPPER_BOUND: return BEST_UPPER_BOUND == other || APPROXIMATION == other; 
        case BEST_LOWER_BOUND: return BEST_LOWER_BOUND == other || APPROXIMATION == other; 
        default: throw new IllegalArgumentException();
        }
    }
}
