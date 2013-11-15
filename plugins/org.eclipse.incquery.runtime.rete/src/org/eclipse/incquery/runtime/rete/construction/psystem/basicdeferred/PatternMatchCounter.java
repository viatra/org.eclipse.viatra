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

package org.eclipse.incquery.runtime.rete.construction.psystem.basicdeferred;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.rete.construction.psystem.PSystem;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * @author Gabor Bergmann
 * 
 */
public class PatternMatchCounter extends PatternCallBasedDeferred {

    private PVariable resultVariable;

    /**
     * @param buildable
     * @param affectedVariables
     */
    public PatternMatchCounter(PSystem pSystem, Tuple actualParametersTuple,
            Object pattern, PVariable resultVariable) {
        super(pSystem, actualParametersTuple, pattern, Collections.singleton(resultVariable));
        this.resultVariable = resultVariable;
    }

    @Override
    public Set<PVariable> getDeducedVariables() {
        return Collections.singleton(resultVariable);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.rete.construction.psystem.BasePConstraint#getFunctionalKeys()
     */
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
        return pSystem.getContext().printPattern(pattern) + "@" + actualParametersTuple.toString() + "->"
                + resultVariable.toString();
    }

    /**
     * @return the resultVariable
     */
    public PVariable getResultVariable() {
        return resultVariable;
    }

}
