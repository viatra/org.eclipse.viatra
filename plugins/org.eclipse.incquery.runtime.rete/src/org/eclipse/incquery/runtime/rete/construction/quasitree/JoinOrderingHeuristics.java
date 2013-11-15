/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.construction.quasitree;

import java.util.Comparator;

import org.eclipse.incquery.runtime.rete.util.Options;
import org.eclipse.incquery.runtime.rete.util.OrderingCompareAgent;

/**
 * @author Gabor Bergmann
 * 
 */
public class JoinOrderingHeuristics implements Comparator<JoinCandidate> {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(JoinCandidate jc1, JoinCandidate jc2) {
        return new OrderingCompareAgent<JoinCandidate>(jc1, jc2) {
            @Override
            protected void doCompare() {
                swallowBoolean(true && consider(preferTrue(a.isTrivial(), b.isTrivial()))
                        && consider(preferTrue(a.isCheckOnly(), b.isCheckOnly()))
                        && consider( 
                        		Options.functionalDependencyOption == Options.FunctionalDependencyOption.OFF ?
                        		dontCare() :
                        		preferTrue(a.isHeath(), b.isHeath())
                        	)
                        && consider(preferFalse(a.isDescartes(), b.isDescartes()))

                        // TODO main heuristic decisions

                        && consider(preferLess(a.toString(), b.toString())));
            }
        }.compare();

    }

}