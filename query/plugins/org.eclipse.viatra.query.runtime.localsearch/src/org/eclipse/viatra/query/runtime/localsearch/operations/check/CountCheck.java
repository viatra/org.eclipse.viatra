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

import java.util.Collections;
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
 * Calculates the count of matches for a called matcher
 * 
 * @author Zoltan Ujhelyi
 * @noextend This class is not intended to be subclassed by clients.
 */
public class CountCheck implements ISearchOperation, IPatternMatcherOperation {

    private class Executor extends CheckOperationExecutor {
        
        private final VolatileModifiableMaskedTuple maskedTuple;
        private IQueryResultProvider matcher;
        
        public Executor() {
            maskedTuple = new VolatileModifiableMaskedTuple(information.getThinFrameMask());
        }
        
        @Override
        public void onInitialize(MatchingFrame frame, ISearchContext context) {
            super.onInitialize(frame, context);
            maskedTuple.updateTuple(frame);
            matcher = context.getMatcher(information.getReference());
        }
        
        @Override
        protected boolean check(MatchingFrame frame, ISearchContext context) {
            int count = matcher.countMatches(information.getParameterMask(), maskedTuple);
            return ((Integer)frame.getValue(position)) == count;
        }
        
        @Override
        public ISearchOperation getOperation() {
            return CountCheck.this;
        }
    }
    
    private final int position;
    private final CallInformation information;
    
    /**
     * @since 1.7
     */
    public CountCheck(CallInformation information, int position) {
        super();
        this.information = information;
        this.position = position;
    }


    @Override
    public ISearchOperationExecutor createExecutor() {
        return new Executor();
    }

    @Override
    public List<Integer> getVariablePositions() {
        return Collections.singletonList(position);
    }
    
    @Override
    public String toString(Function<Integer, String> variableMapping) {
        return "check     "+variableMapping.apply(position)+" = count find "+ information.toString(variableMapping);
    }
    
    @Override
    public CallInformation getCallInformation() {
        return information;
    }
}
