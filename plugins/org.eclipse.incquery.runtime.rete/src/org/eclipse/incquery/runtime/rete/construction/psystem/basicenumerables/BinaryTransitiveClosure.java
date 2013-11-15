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

import org.eclipse.incquery.runtime.rete.construction.psystem.KeyedEnumerablePConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PSystem;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * @author Bergmann GĂˇbor
 * 
 *         For a binary base pattern, computes the irreflexive transitive closure (base)+
 */
public class BinaryTransitiveClosure extends KeyedEnumerablePConstraint<Object> {

    /**
     * @param pSystem
     * @param variablesTuple
     * @param pattern
     */
    public BinaryTransitiveClosure(PSystem pSystem, Tuple variablesTuple,
            Object pattern) {
        super(pSystem, variablesTuple, pattern);
    }

    @Override
    protected String keyToString() {
        return pSystem.getContext().printType(supplierKey) + "+";
    }
    
    
}
