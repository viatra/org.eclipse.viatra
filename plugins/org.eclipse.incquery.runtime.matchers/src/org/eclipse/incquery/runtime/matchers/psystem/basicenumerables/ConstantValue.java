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

package org.eclipse.incquery.runtime.matchers.psystem.basicenumerables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.KeyedEnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * @author Gabor Bergmann
 * 
 */
public class ConstantValue extends KeyedEnumerablePConstraint<Object> {

    public ConstantValue(PBody pSystem, PVariable variable, Object value) {
        super(pSystem, new FlatTuple(variable), value);
    }

    @Override
    protected String keyToString() {
        return supplierKey.toString();
    }
    
    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies() {
    	final Map<Set<PVariable>, Set<PVariable>> result = new HashMap<Set<PVariable>, Set<PVariable>>();
        final Set<PVariable> emptySet = Collections.emptySet(); // a constant value is functionally determined by everything
        result.put(emptySet, Collections.singleton(getVariableInTuple(0)));
        return result;
    }


}
