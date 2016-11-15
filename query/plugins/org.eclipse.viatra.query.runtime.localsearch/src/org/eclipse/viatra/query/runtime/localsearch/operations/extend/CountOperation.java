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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper;
import org.eclipse.viatra.query.runtime.localsearch.operations.IMatcherBasedOperation;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryResultProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Calculates the count of matches for a called matcher
 * 
 * @author Zoltan Ujhelyi
 */
public class CountOperation extends ExtendOperation<Integer> implements IMatcherBasedOperation{

    final PQuery calledQuery;
    final Map<Integer, PParameter> parameterMapping;
    final Map<Integer, Integer> frameMapping;
	private IQueryResultProvider matcher;

	/**
     * @since 1.5
     */
	@Override
	public IQueryResultProvider getAndPrepareCalledMatcher(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
		Set<PParameter> adornment = Sets.newHashSet();
		for (Entry<Integer, PParameter> mapping : parameterMapping.entrySet()) {
		    Preconditions.checkNotNull(mapping.getKey(), "Mapping frame must not contain null keys");
            Preconditions.checkNotNull(mapping.getValue(), "Mapping frame must not contain null values");
			Integer source = mapping.getKey();
			if (frame.get(source) != null) {
				adornment.add(mapping.getValue());
			}
		}
		matcher = context.getMatcher(new MatcherReference(calledQuery, adornment));
        return matcher;
	}

	/**
     * @since 1.5
     */
	@Override
	public IQueryResultProvider getCalledMatcher() {
		return matcher;
	}
	
    public CountOperation(PQuery calledQuery, Map<Integer, PParameter> parameterMapping, int position) {
        super(position);
        this.calledQuery = calledQuery;
        this.parameterMapping = parameterMapping;
        this.frameMapping = CallOperationHelper.calculateFrameMapping(calledQuery, parameterMapping);
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        getAndPrepareCalledMatcher(frame, context);
        Object[] parameterValues = new Object[calledQuery.getParameters().size()];
        for (Entry<Integer, Integer> entry : frameMapping.entrySet()) {
            parameterValues[entry.getValue()] = frame.getValue(entry.getKey());
        }
        it = Iterators.singletonIterator(matcher.countMatches(parameterValues));
        
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Extend count for pattern ")
            .append(calledQuery.getFullyQualifiedName().substring(calledQuery.getFullyQualifiedName().lastIndexOf('.') + 1))
            .append("for position ").append(position);
        return builder.toString();
    }
    
    @Override
	public List<Integer> getVariablePositions() {
    	List<Integer> variables = Lists.newArrayList();
    	variables.addAll(frameMapping.keySet());
		return variables;
	}


    
}
