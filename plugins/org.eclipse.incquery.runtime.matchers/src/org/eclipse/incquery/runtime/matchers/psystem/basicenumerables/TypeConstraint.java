/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.psystem.basicenumerables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.psystem.ITypeInfoProviderConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.KeyedEnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

/**
 * Represents a type constraint with using an undefined number of parameters. Subclasses are distinguished by the number
 * of parameters and their inferred type information. Such a constraint maintains how to output its type information.
 *
 * @author Zoltan Ujhelyi
 *
 */
public class TypeConstraint extends KeyedEnumerablePConstraint<IInputKey> implements ITypeInfoProviderConstraint {
    
    public TypeConstraint(PBody pSystem, Tuple variablesTuple, IInputKey inputKey) {
        super(pSystem, variablesTuple, inputKey);
    }

    @Override
    protected String keyToString() {
        return supplierKey.getPrettyPrintableName();
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
    		result.put(Collections.singleton(getVariableInTuple(0)), Collections.singleton(getVariableInTuple(1)));
    	if (context.isBinaryEdgeMultiplicityOneTo(supplierKey))
    		result.put(Collections.singleton(getVariableInTuple(1)), Collections.singleton(getVariableInTuple(0)));
		return result;
    }


}