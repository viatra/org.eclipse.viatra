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

import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;

/**
 * This rewriter class can add new equality constraints to the copied body
 * 
 * @author Marton Bur
 *
 */
public class FlattenerCopier extends PBodyCopier {

    private List<PositivePatternCall> callsToFlatten;
    private List<PBody> calledBodies;

    public FlattenerCopier(PQuery query, List<PositivePatternCall> callsToFlatten, List<PBody> calledBodies) {
        super(query);
        this.callsToFlatten = callsToFlatten;
        this.calledBodies = calledBodies;
    }
    
    @Override
    protected void copyPositivePatternCallConstraint(PositivePatternCall positivePatternCall) {

        if(!callsToFlatten.contains(positivePatternCall)){
            // If the call was not flattened, copy the constraint
            super.copyPositivePatternCallConstraint(positivePatternCall);
        } else {
            for (PBody calledBody : calledBodies) {
                if(positivePatternCall.getReferredQuery().equals(calledBody.getPattern())){
                    List<ExportedParameter> symbolicParameters = calledBody.getSymbolicParameters();                
                    Object[] elements = positivePatternCall.getVariablesTuple().getElements();
                    for (int i = 0; i < elements.length; i++ ) {
                        // Create equality constraints between the caller PositivePatternCall and the corresponding body parameter variables
                        createEqualityConstraint((PVariable) elements[i], symbolicParameters.get(i).getAffectedVariables().iterator().next());
                    }
                }
            }
        }
    }

    private void createEqualityConstraint(PVariable pVariable1, PVariable pVariable2){
        new Equality(body, variableMapping.get(pVariable1), variableMapping.get(pVariable2));
    }
        
}
