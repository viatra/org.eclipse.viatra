/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.construction.quasitree;

import java.util.Comparator;

import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.rete.util.LexicographicComparator;

/**
 * Class providing comparators for breaking ties somewhat more deterministically.
 * @author Bergmann Gabor
 *
 */
public class TieBreaker {
    public static final Comparator<PConstraint> CONSTRAINT_COMPARATOR = new Comparator<PConstraint>() {
        @Override
        public int compare(PConstraint arg0, PConstraint arg1) {
            return arg0.getMonotonousID() - arg1.getMonotonousID();
        }
    };
    public static final Comparator<Iterable<? extends PConstraint>> CONSTRAINT_LIST_COMPARATOR = 
            new LexicographicComparator<PConstraint>(CONSTRAINT_COMPARATOR);

}
