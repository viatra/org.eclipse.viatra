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

package org.eclipse.incquery.runtime.matchers.psystem.basicdeferred;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

/**
 * @author Gabor Bergmann
 * 
 */
public class PatternMatchCounter extends PatternCallBasedDeferred {

    private PVariable resultVariable;

    public PatternMatchCounter(PBody pSystem, Tuple actualParametersTuple,
            PQuery query, PVariable resultVariable) {
        super(pSystem, actualParametersTuple, query, Collections.singleton(resultVariable));
        this.resultVariable = resultVariable;
    }

    @Override
    public Set<PVariable> getDeducedVariables() {
        return Collections.singleton(resultVariable);
    }
    
    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies() {
    	final HashMap<Set<PVariable>, Set<PVariable>> result = new HashMap<Set<PVariable>, Set<PVariable>>();
    	result.put(getDeferringVariables(), getDeducedVariables());
		return result;
    }

    @Override
    protected void doDoReplaceVariables(PVariable obsolete, PVariable replacement) {
        if (resultVariable.equals(obsolete))
            resultVariable = replacement;
    }

    @Override
    protected Set<PVariable> getCandidateQuantifiedVariables() {
        return actualParametersTuple.<PVariable> getDistinctElements();
    }


    @Override
    protected String toStringRest() {
        return query.getFullyQualifiedName() + "@" + actualParametersTuple.toString() + "->"
                + resultVariable.toString();
    }

    public PVariable getResultVariable() {
        return resultVariable;
    }

}
