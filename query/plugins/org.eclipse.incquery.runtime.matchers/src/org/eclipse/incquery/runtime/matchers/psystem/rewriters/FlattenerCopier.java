/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.psystem.rewriters;

import java.util.List;
import java.util.Map;

import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * This rewriter class can add new equality constraints to the copied body
 * 
 * @author Marton Bur
 *
 */
public class FlattenerCopier extends PBodyCopier {

    private List<PositivePatternCall> callsToFlatten;
    private List<PBody> calledBodies;

    private ListMultimap<PVariable, PVariable> variableMultimap = ArrayListMultimap.create();
    private Map<PQuery, Integer> patternCallCounter = Maps.newHashMap();
    
    @Override
    protected void copyVariable(PVariable variable, String newName) {
        PVariable newPVariable = body.getOrCreateVariableByName(newName);
            variableMapping.put(variable, newPVariable);
            variableMultimap.put(variable, newPVariable);
    };
    
    public FlattenerCopier(PQuery query, List<PositivePatternCall> callsToFlatten, List<PBody> calledBodies) {
        super(query);
        this.callsToFlatten = callsToFlatten;
        this.calledBodies = Lists.newArrayList(calledBodies);
    }
    
    @Override
    protected void copyPositivePatternCallConstraint(PositivePatternCall positivePatternCall) {

        if(!callsToFlatten.contains(positivePatternCall)){
            // If the call was not flattened, copy the constraint
            super.copyPositivePatternCallConstraint(positivePatternCall);
        } else {
            PBody bodyToRemoveFromList = null;
            for (PBody calledBody : calledBodies) {
                if(positivePatternCall.getReferredQuery().equals(calledBody.getPattern())){
                    PQuery pattern = calledBody.getPattern();
                    //This index is used to differentiate between the different calls
                    int callIndex = 0;
                    if(patternCallCounter.containsKey(pattern)){
                        callIndex = patternCallCounter.get(pattern);
                        callIndex++;
                        patternCallCounter.put(pattern,callIndex);
                    } else {
                        patternCallCounter.put(pattern,0);
                    }
                    List<PVariable> symbolicParameters = calledBody.getSymbolicParameterVariables();                
                    Object[] elements = positivePatternCall.getVariablesTuple().getElements();
                    for (int i = 0; i < elements.length; i++ ) {
                        // Create equality constraints between the caller PositivePatternCall and the corresponding body parameter variables
                        createEqualityConstraint((PVariable) elements[i], symbolicParameters.get(i), callIndex);
                    }
                    bodyToRemoveFromList = calledBody;
                    break;
                }
            }
            calledBodies.remove(bodyToRemoveFromList);
        }
    }

    private void createEqualityConstraint(PVariable pVariable1, PVariable pVariable2, int index){
        new Equality(body, variableMultimap.get(pVariable1).get(0), variableMultimap.get(pVariable2).get(index));
    }
        
}
