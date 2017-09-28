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

import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IPatternMatcherOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.util.CallInformation;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.tuple.VolatileModifiableMaskedTuple;

import com.google.common.collect.Iterators;

/**
 * Calculates the count of matches for a called matcher
 * 
 * @author Zoltan Ujhelyi
 */
public class CountOperation extends ExtendOperation<Integer> implements IPatternMatcherOperation{

    private final CallInformation information; 
    private final VolatileModifiableMaskedTuple maskedTuple;
    private IQueryResultProvider matcher;

    
    /**
     * @since 1.7
     */
    public CountOperation(CallInformation information, int position) {
        super(position);
        this.information = information;
        maskedTuple = new VolatileModifiableMaskedTuple(information.getThinFrameMask());
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        matcher = context.getMatcher(information.getReference());
        maskedTuple.updateTuple(frame);
        it = Iterators.singletonIterator(matcher.countMatches(information.getParameterMask(), maskedTuple));
        
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return information.getVariablePositions();
    }

    @Override
    public String toString() {
        return "extend    -"+position+" = count find " + information.toString();
    }
    
}
