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

package org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.IQueryReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.ITypeInfoProviderConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.KeyedEnumerablePConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.TypeJudgement;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

/**
 * @author Gabor Bergmann
 *
 */
public class PositivePatternCall extends KeyedEnumerablePConstraint<PQuery> implements IQueryReference, ITypeInfoProviderConstraint {

    public PositivePatternCall(PBody pBody, Tuple variablesTuple,
            PQuery pattern) {
        super(pBody, variablesTuple, pattern);
    }

    @Override
    protected String keyToString() {
        return supplierKey.getFullyQualifiedName();
    }

    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies(IQueryMetaContext context) {
    	// TODO insert inferred functional dependencies here
		return super.getFunctionalDependencies(context);
    }

    @Override
    public PQuery getReferredQuery() {
        return supplierKey;
    }

	@Override
	public Set<TypeJudgement> getImpliedJudgements(IQueryMetaContext context) {
		Set<TypeJudgement> result = new HashSet<TypeJudgement>();
		for (TypeJudgement parameterJudgement : getReferredQuery().getTypeGuarantees()) {			
			IInputKey inputKey = parameterJudgement.getInputKey();
			Tuple judgementIndexTuple = parameterJudgement.getVariablesTuple();
			
			Object[] judgementVariables = new Object[judgementIndexTuple.getSize()];
			for (int i=0; i<judgementVariables.length; ++i)
				judgementVariables[i] = variablesTuple.get((int) judgementIndexTuple.get(i));
			
			result.add(new TypeJudgement(inputKey, new FlatTuple(judgementVariables))); 
		}
		return result;
	}

}
