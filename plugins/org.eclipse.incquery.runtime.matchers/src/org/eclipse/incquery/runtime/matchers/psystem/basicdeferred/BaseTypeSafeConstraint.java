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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.matchers.planning.helpers.TypeHelper;
import org.eclipse.incquery.runtime.matchers.psystem.PSystem;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.VariableDeferredPConstraint;

/**
 * @author Gabor Bergmann
 * 
 */
public abstract class BaseTypeSafeConstraint extends
        VariableDeferredPConstraint {
    private Map<PVariable, Set<Object>> allTypeRestrictions;
    
    protected Set<PVariable> inputVariables;
    protected PVariable outputVariable;

    public PVariable getOutputVariable() {
        return outputVariable;
    }

    /**
     * @param buildable
     * @param inputVariables
     * @param outputVariable null iff no output (check-only)
     */
    public BaseTypeSafeConstraint(PSystem pSystem,
            Set<PVariable> inputVariables, final PVariable outputVariable) {
        super(pSystem, 
        		(outputVariable == null) ? 
        				inputVariables : 
        				new HashSet<PVariable>(inputVariables){{add(outputVariable);}});
        this.inputVariables = inputVariables;
        this.outputVariable = outputVariable;
    }

    @Override
    public Set<PVariable> getDeducedVariables() {
        if (outputVariable == null) 
        	return Collections.emptySet(); 
        else
        	return Collections.singleton(outputVariable);
    }

    @Override
    public Set<PVariable> getDeferringVariables() {
        return inputVariables;
    }

    @Override
    public boolean isReadyAt(SubPlan plan) {
        if (super.isReadyAt(plan)) {
            return checkTypeSafety(plan) == null;
        }
        return false;
    }

    /**
     * Checks whether all type restrictions are already enforced on affected variables.
     * 
     * @param plan
     * @return a variable whose type safety is not enforced yet, or null if the plan is typesafe
     */
    public PVariable checkTypeSafety(SubPlan plan) {
        for (PVariable pVariable : inputVariables) {
            Set<Object> allTypeRestrictionsForVariable = getAllTypeRestrictions().get(pVariable);
            Set<Object> checkedTypeRestrictions = TypeHelper.inferTypes(pVariable, plan.getAllEnforcedConstraints());
            Set<Object> uncheckedTypeRestrictions = TypeHelper.subsumeTypes(allTypeRestrictionsForVariable,
                    checkedTypeRestrictions, this.pSystem.getContext());
            if (!uncheckedTypeRestrictions.isEmpty())
                return pVariable;
        }
        return null;
    }

    public Map<PVariable, Set<Object>> getAllTypeRestrictions() {
        if (allTypeRestrictions == null) {
            allTypeRestrictions = new HashMap<PVariable, Set<Object>>();
            for (PVariable pVariable : inputVariables) {
                allTypeRestrictions.put(pVariable,
                        TypeHelper.inferTypes(pVariable, pVariable.getReferringConstraints()));
            }
        }
        return allTypeRestrictions;
    }
    
    @Override
    protected void doReplaceVariable(PVariable obsolete, PVariable replacement) {
    	if (inputVariables.remove(obsolete)) 
    		inputVariables.add(replacement);
    	if (outputVariable == obsolete) 
    		outputVariable = replacement;
    }
}
