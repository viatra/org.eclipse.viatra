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

import org.eclipse.incquery.runtime.matchers.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.psystem.ITypeInfoProviderConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * @author Gabor Bergmann
 *
 */
public class TypeBinary extends TypeConstraint implements ITypeInfoProviderConstraint {
    private final IPatternMatcherContext context;
    private PVariable source; private PVariable target;
    public TypeBinary(PBody pSystem,
            IPatternMatcherContext context, PVariable source, PVariable target, Object typeKey, String typeString) {
        super(pSystem, new FlatTuple(source, target), typeKey, typeString);
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
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies() {
    	final Map<Set<PVariable>, Set<PVariable>> result = new HashMap<Set<PVariable>, Set<PVariable>>();
    	if (context.isBinaryEdgeMultiplicityToOne(supplierKey))
    		result.put(Collections.singleton(source), Collections.singleton(target));
    	if (context.isBinaryEdgeMultiplicityOneTo(supplierKey))
    		result.put(Collections.singleton(target), Collections.singleton(source));
		return result;
    }

    /**
     * Returns the metamodel context used for creating this constraint
     * @return the context
     */
    public IPatternMatcherContext getContext() {
        return context;
    }


}
