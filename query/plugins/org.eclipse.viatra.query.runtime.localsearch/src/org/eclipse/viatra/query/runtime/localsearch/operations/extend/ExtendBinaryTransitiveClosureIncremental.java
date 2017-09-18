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
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper;
import org.eclipse.viatra.query.runtime.localsearch.operations.IPatternMatcherOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.TransitiveClosureGraph;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Checking for a transitive closure expressed as a local search pattern matcher. The matched pattern must have two
 * parameters of the same model type.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.7
 * 
 */
public abstract class ExtendBinaryTransitiveClosureIncremental extends ExtendOperation<Object> implements IPatternMatcherOperation {

    /**
     * Calculates the transitive closure of a pattern match in a forward direction (first parameter bound, second
     * unbound)
     * 
     * @since 1.7
     */
    public static class Forward extends ExtendBinaryTransitiveClosureIncremental {

        public Forward(MatcherReference calledQuery, int sourcePosition, int targetPosition) {
            super(calledQuery, sourcePosition, targetPosition);
        }
        
        @Override
        protected Object getSource(Tuple frame) {
            return frame.get(1);
        }

        @Override
        protected Iterable<Object> getTargets(Tuple frame, TransitiveClosureGraph closure) {
            return closure.getAllSources(frame.get(0));
        }
    }

    /**
     * Calculates the transitive closure of a pattern match in a backward direction (first parameter unbound, second
     * bound)
     * 
     * @since 1.7
     */
    public static class Backward extends ExtendBinaryTransitiveClosureIncremental {

        public Backward(MatcherReference calledQuery, int sourcePosition, int targetPosition) {
            super(calledQuery, targetPosition, sourcePosition);
        }

        @Override
        protected Object getSource(Tuple frame) {
            return frame.get(1);
        }
        
        @Override
        protected Iterable<Object> getTargets(Tuple frame, TransitiveClosureGraph closure) {
            return closure.getAllSources(frame.get(1));
        }

    }

    private final CallOperationHelper helper;
    private final int seedPosition;

    /**
     * The source position will be matched in the called pattern to the first parameter; while target to the second.
     */
    protected ExtendBinaryTransitiveClosureIncremental(MatcherReference calledQuery, int seedPosition, int targetPosition) {
        super(targetPosition);
        this.seedPosition = seedPosition;

        helper = new CallOperationHelper(calledQuery, ImmutableMap.of(calledQuery.getQuery().getParameters().get(0),
                seedPosition, calledQuery.getQuery().getParameters().get(1), targetPosition));
    }

    protected abstract Object getSource(Tuple frame);
    protected abstract Iterable<Object> getTargets(Tuple frame, TransitiveClosureGraph closure);

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        TransitiveClosureGraph closure = TransitiveClosureGraph.accessClosureGraph(context, helper);

        it = getTargets(frame, closure).iterator();
    }

    @Override
    public String toString() {
        String c = helper.toString();
        int p = c.indexOf('(');
        return "extend    find " + c.substring(0, p) + "+" + c.substring(p) + "[incremental]";
    }

    @Override
    public List<Integer> getVariablePositions() {
        return Lists.asList(seedPosition, position, new Integer[0]);
    }

}
