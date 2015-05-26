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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.incquery.runtime.matchers.psystem.ITypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.KeyedEnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.TypeJudgement;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

/**
 * Represents a type constraint with using an undefined number of parameters. Subclasses are distinguished by the number
 * of parameters and their inferred type information. Such a constraint maintains how to output its type information.
 *
 * <p> InputKey must be enumerable!
 *
 * @author Zoltan Ujhelyi
 *
 */
public class TypeConstraint extends KeyedEnumerablePConstraint<IInputKey> implements ITypeConstraint {
    
	private TypeJudgement equivalentJudgement;
	
    public TypeConstraint(PBody pSystem, Tuple variablesTuple, IInputKey inputKey) {
        super(pSystem, variablesTuple, inputKey);
        this.equivalentJudgement = new TypeJudgement(inputKey, variablesTuple);
        
        if (! inputKey.isEnumerable())
        	throw new IllegalArgumentException(
        			this.getClass().getSimpleName() + 
        			" applicable for enumerable input keys only; received instead " + 
        					inputKey);
    }

    @Override
    protected String keyToString() {
        return supplierKey.getPrettyPrintableName();
    }
    
    @Override
	public TypeJudgement getEquivalentJudgement() {
		return equivalentJudgement;
	}

	@Override
	public Set<TypeJudgement> getImpliedJudgements(IQueryMetaContext context) {
		return Collections.singleton(equivalentJudgement);
		//return equivalentJudgement.getDirectlyImpliedJudgements(context);
	}
	
	@Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies(IQueryMetaContext context) {
    	final Map<Set<PVariable>, Set<PVariable>> result = new HashMap<Set<PVariable>, Set<PVariable>>();
    	
    	Set<Entry<Set<Integer>, Set<Integer>>> dependencies = context.getFunctionalDependencies(supplierKey).entrySet();
    	for (Entry<Set<Integer>, Set<Integer>> dependency : dependencies) {
			result.put(transcribeVariables(dependency.getKey()), transcribeVariables(dependency.getValue()));
		}

    	return result;
    }

	private Set<PVariable> transcribeVariables(Set<Integer> indices) {
		Set<PVariable> result = new HashSet<PVariable>();
		for (Integer index : indices) {
			result.add((PVariable) variablesTuple.get(index));
		}
		return result;
	}


}