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

package org.eclipse.incquery.runtime.rete.construction.psystem.basicenumerables;

import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.rete.construction.psystem.KeyedEnumerablePConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PSystem;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * @author Gabor Bergmann
 * 
 */
public class PositivePatternCall extends KeyedEnumerablePConstraint<Object> {

    /**
     * @param buildable
     * @param variablesTuple
     * @param pattern
     */
    public PositivePatternCall(PSystem pSystem, Tuple variablesTuple,
            Object pattern) {
        super(pSystem, variablesTuple, pattern);
    }

    @Override
    protected String keyToString() {
        return pSystem.getContext().printPattern(supplierKey);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.rete.construction.psystem.BasePConstraint#getFunctionalKeys()
     */
    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies() {
    	// TODO insert inferred functional dependencies here
		return super.getFunctionalDependencies();
    }
    
}
