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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper;
import org.eclipse.viatra.query.runtime.localsearch.operations.IPatternMatcherOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.CallOperationHelper.PatternCall;
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
public abstract class ExtendBinaryTransitiveClosure extends ExtendOperation<Object> implements IPatternMatcherOperation {

    /**
     * Calculates the transitive closure of a pattern match in a forward direction (first parameter bound, second
     * unbound)
     * 
     * @since 1.7
     */
    public static class Forward extends ExtendBinaryTransitiveClosure {

        public Forward(MatcherReference calledQuery, int sourcePosition, int targetPosition) {
            super(calledQuery, sourcePosition, targetPosition);
        }

        protected Object[] calculateCallFrame(Object seed) {
            return new Object[] { seed, null };
        }

        protected Object getTarget(Tuple frame) {
            return frame.get(1);
        }
    }

    /**
     * Calculates the transitive closure of a pattern match in a backward direction (first parameter unbound, second
     * bound)
     * 
     * @since 1.7
     */
    public static class Backward extends ExtendBinaryTransitiveClosure {

        public Backward(MatcherReference calledQuery, int sourcePosition, int targetPosition) {
            super(calledQuery, targetPosition, sourcePosition);
        }

        protected Object[] calculateCallFrame(Object seed) {
            return new Object[] { null, seed };
        }

        protected Object getTarget(Tuple frame) {
            return frame.get(0);
        }
    }

    private final CallOperationHelper helper;
    private final int seedPosition;

    /**
     * The source position will be matched in the called pattern to the first parameter; while target to the second.
     */
    protected ExtendBinaryTransitiveClosure(MatcherReference calledQuery, int seedPosition, int targetPosition) {
        super(targetPosition);
        this.seedPosition = seedPosition;

        helper = new CallOperationHelper(calledQuery, ImmutableMap.of(calledQuery.getQuery().getParameters().get(0),
                seedPosition, calledQuery.getQuery().getParameters().get(1), targetPosition));
    }

    protected abstract Object[] calculateCallFrame(Object seed);

    protected abstract Object getTarget(Tuple frame);

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        // Note: second parameter is NOT bound during execution, but the first is
        PatternCall call = helper.createCall(context);

        Queue<Object> seedsToEvaluate = new LinkedList<>();
        seedsToEvaluate.add(frame.get(seedPosition));
        Set<Object> seedsEvaluated = new HashSet<>();
        Set<Object> targetsFound = new HashSet<>();

        while(!seedsToEvaluate.isEmpty()) {
            Object currentValue = seedsToEvaluate.poll();
            seedsEvaluated.add(currentValue);
            final Object[] mappedFrame = calculateCallFrame(currentValue);
            for (Tuple match : call.getAllMatches(mappedFrame)) {
                Object foundTarget = getTarget(match);
                targetsFound.add(foundTarget);
                if (!seedsEvaluated.contains(foundTarget)) {
                    seedsToEvaluate.add(foundTarget);
                }
            }
        }

        it = targetsFound.iterator();
    }

    @Override
    public String toString() {
        String c = helper.toString();
        int p = c.indexOf('(');
        return "extend    find " + c.substring(0, p) + "+" + c.substring(p);
    }

    @Override
    public List<Integer> getVariablePositions() {
        return Lists.asList(seedPosition, position, new Integer[0]);
    }

}
