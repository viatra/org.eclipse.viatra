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
package org.eclipse.viatra.query.runtime.localsearch.operations.extend;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IPatternMatcherOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.util.CallInformation;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.tuple.VolatileModifiableMaskedTuple;

/**
 * Calculates the count of matches for a called matcher
 * 
 * @author Zoltan Ujhelyi
 */
public class CountOperation implements ISearchOperation, IPatternMatcherOperation{

    private class Executor extends SingleValueExtendOperationExecutor<Integer>  {
        private final VolatileModifiableMaskedTuple maskedTuple;
        private IQueryResultProvider matcher;
        
        public Executor(int position) {
            super(position);
            maskedTuple = new VolatileModifiableMaskedTuple(information.getThinFrameMask());
        }
        
        @Override
        public Iterator<Integer> getIterator(MatchingFrame frame, ISearchContext context) {
            matcher = context.getMatcher(information.getReference());
            maskedTuple.updateTuple(frame);
            return Collections.singletonList(matcher.countMatches(information.getParameterMask(), maskedTuple)).iterator();
        }
        
        @Override
        public ISearchOperation getOperation() {
            return CountOperation.this;
        }
    }
    
    private final CallInformation information;
    private final int position; 
    
    /**
     * @since 1.7
     */
    public CountOperation(CallInformation information, int position) {
        this.information = information;
        this.position = position;
    }

    @Override
    public ISearchOperationExecutor createExecutor() {
        return new Executor(position);
    }

    @Override
    public List<Integer> getVariablePositions() {
        return information.getVariablePositions();
    }

    @Override
    public String toString() {
        return toString(Object::toString);
    }
    
    @Override
    public String toString(Function<Integer, String> variableMapping) {
        return "extend    -"+variableMapping.apply(position)+" = count find " + information.toString(variableMapping);
    }
    
    @Override
    public CallInformation getCallInformation() {
        return information;
    }
}
