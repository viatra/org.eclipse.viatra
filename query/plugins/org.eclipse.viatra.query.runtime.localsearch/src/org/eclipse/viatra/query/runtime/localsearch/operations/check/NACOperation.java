/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.check;

import java.util.List;
import java.util.function.Function;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.CheckOperationExecutor;
import org.eclipse.viatra.query.runtime.localsearch.operations.IPatternMatcherOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.util.CallInformation;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.tuple.VolatileModifiableMaskedTuple;

/**
 * @author Zoltan Ujhelyi
 * @noextend This class is not intended to be subclassed by clients.
 */
public class NACOperation implements ISearchOperation, IPatternMatcherOperation {

    private class Executor extends CheckOperationExecutor {
        private final VolatileModifiableMaskedTuple maskedTuple;
        private IQueryResultProvider matcher;
        
        private Executor() {
            this.maskedTuple = new VolatileModifiableMaskedTuple(information.getThinFrameMask());
        }
        
        @Override
        public void onInitialize(MatchingFrame frame, ISearchContext context) {
            super.onInitialize(frame, context);
            maskedTuple.updateTuple(frame);
            matcher = context.getMatcher(information.getCallWithAdornment());
        }
        
        @Override
        protected boolean check(MatchingFrame frame, ISearchContext context) {
            return !matcher.hasMatch(information.getParameterMask(), maskedTuple);
        }
        
        @Override
        public ISearchOperation getOperation() {
            return NACOperation.this;
        }
    }
    
    private final CallInformation information;

    /**
     * @since 1.7
     */
    public NACOperation(CallInformation information) {
        super();
        this.information = information;
    }
    
    @Override
    public ISearchOperationExecutor createExecutor() {
        return new Executor();
    }
    
    @Override
    public String toString() {
        return toString(Object::toString);
    }
    
    @Override
    public String toString(Function<Integer, String> variableMapping) {
        return "check     neg find "+information.toString(variableMapping);
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return information.getVariablePositions();
    }

    @Override
    public CallInformation getCallInformation() {
        return information;
    }
}
