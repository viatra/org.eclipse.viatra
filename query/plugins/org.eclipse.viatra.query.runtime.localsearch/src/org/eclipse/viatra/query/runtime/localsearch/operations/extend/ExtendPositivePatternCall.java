/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.extend;

import java.util.Iterator;
import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IPatternMatcherOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.util.CallInformation;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.VolatileModifiableMaskedTuple;

/**
 * @author Grill Balázs
 * @since 1.4
 *
 */
public class ExtendPositivePatternCall implements ISearchOperation, IPatternMatcherOperation {

    private final CallInformation information; 
    private final VolatileModifiableMaskedTuple maskedTuple;
    private Iterator<? extends Tuple> matches = null;
    
    /**
     * @since 1.7
     */
    public ExtendPositivePatternCall(CallInformation information) {
       this.information = information;
       maskedTuple = new VolatileModifiableMaskedTuple(information.getThinFrameMask());
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        maskedTuple.updateTuple(frame);
        IQueryResultProvider matcher = context.getMatcher(information.getReference());
        matches = matcher.getAllMatches(information.getParameterMask(), maskedTuple).iterator();
    }

    @Override
    public boolean execute(MatchingFrame frame, ISearchContext context) {
        if (matches.hasNext()){
            Tuple tuple = matches.next();
            while(!fillInResult(frame, tuple) && matches.hasNext()){
                tuple = matches.next();
            }
            return true;
        }else{
            return false;
        }
    }

    private boolean fillInResult(MatchingFrame frame, Tuple result) {
        TupleMask mask = information.getFullFrameMask();
        // The first loop clears out the elements from a possible previous iteration 
        for(int i : information.getFreeParameterIndices()) {
            mask.set(frame, i, null);
        }
        for(int i : information.getFreeParameterIndices()) {
            Object oldValue = mask.getValue(frame, i);
            Object valueToFill = result.get(i);
            if (oldValue != null && !oldValue.equals(valueToFill)){
                // If the inverse map contains more than one values for the same key, it means that these arguments are unified by the caller. 
                // In this case if the callee assigns different values the frame shall be dropped
                return false;
            }
            mask.set(frame, i, valueToFill);
        }
        return true;
    }
    
    @Override
    public void onBacktrack(MatchingFrame frame, ISearchContext context) {
        TupleMask mask = information.getFullFrameMask();
        for(int i : information.getFreeParameterIndices()){
            mask.set(frame, i, null);
        }
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return information.getVariablePositions();
    }
    
    @Override
    public String toString() {
        return "extend find " + information.toString();
    }
    
}
