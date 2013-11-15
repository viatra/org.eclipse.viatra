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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.rete.construction.psystem.ITypeInfoProviderConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.KeyedEnumerablePConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PSystem;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherContext;
import org.eclipse.incquery.runtime.rete.tuple.FlatTuple;

/**
 * @author Gabor Bergmann
 * 
 */
public class TypeBinary extends
        KeyedEnumerablePConstraint<Object> implements ITypeInfoProviderConstraint {
    private final IPatternMatcherContext context;
    private PVariable source; private PVariable target;

    /**
     * @param buildable
     * @param variablesTuple
     * @param typeKey
     */
    public TypeBinary(PSystem pSystem,
            IPatternMatcherContext context, PVariable source, PVariable target, Object typeKey) {
        super(pSystem, new FlatTuple(source, target), typeKey);
        this.source = source;
        this.target = target;
        this.context = context;
    }

    @Override
    public Object getTypeInfo(PVariable variable) {
        if (variable.equals(variablesTuple.get(0)))
            return context.binaryEdgeSourceType(supplierKey);
        if (variable.equals(variablesTuple.get(1)))
            return context.binaryEdgeTargetType(supplierKey);
        return ITypeInfoProviderConstraint.TypeInfoSpecials.NO_TYPE_INFO_PROVIDED;
    }

    @Override
    protected String keyToString() {
        return pSystem.getContext().printType(supplierKey);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.rete.construction.psystem.BasePConstraint#getFunctionalKeys()
     */
    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies() {
    	final HashMap<Set<PVariable>, Set<PVariable>> result = new HashMap<Set<PVariable>, Set<PVariable>>();
    	if (context.isBinaryEdgeMultiplicityToOne(supplierKey))
    		result.put(Collections.singleton(source), Collections.singleton(target));
    	if (context.isBinaryEdgeMultiplicityOneTo(supplierKey))
    		result.put(Collections.singleton(target), Collections.singleton(source));    	
		return result;
    }
    

}
