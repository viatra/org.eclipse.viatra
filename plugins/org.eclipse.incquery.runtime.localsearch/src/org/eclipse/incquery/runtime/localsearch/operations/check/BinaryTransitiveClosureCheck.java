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
package org.eclipse.incquery.runtime.localsearch.operations.check;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.incquery.runtime.localsearch.matcher.LocalSearchMatcher;

import com.google.common.collect.Sets;

/**
 * Checking for a transitive closure expressed as a local search pattern matcher. The matched pattern must have two
 * parameters of the same model type.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class BinaryTransitiveClosureCheck extends CheckOperation {

    private LocalSearchMatcher calledMatcher;
    private int sourcePosition;
    private int targetPosition;

    /**
     * The source position will be matched in the called pattern to the first parameter; while target to the second.
     * 
     * @param calledMatcher
     * @param sourcePosition
     * @param targetPosition
     */
    public BinaryTransitiveClosureCheck(LocalSearchMatcher calledMatcher, int sourcePosition, int targetPosition) {
        super();
        this.calledMatcher = calledMatcher;
        this.sourcePosition = sourcePosition;
        this.targetPosition = targetPosition;
    }

    @Override
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
        Object targetValue = frame.get(targetPosition);
        LinkedHashSet<Object> sourcesToEvaluate = Sets.newLinkedHashSet();
        sourcesToEvaluate.add(frame.get(sourcePosition));
        Set<Object> sourceEvaluated = Sets.newHashSet();
        do {
            Object currentValue = sourcesToEvaluate.iterator().next();
            sourcesToEvaluate.remove(currentValue);
            final MatchingFrame mappedFrame = calledMatcher.editableMatchingFrame();
            mappedFrame.setValue(0, currentValue);
            for (MatchingFrame match : calledMatcher.getAllMatches(mappedFrame)) {
                Object foundTarget = match.get(1);
                if (targetValue.equals(foundTarget)) {
                    return true;
                } else if (!sourceEvaluated.contains(foundTarget)) {
                    sourcesToEvaluate.add(foundTarget);
                }
            }
        } while (!sourcesToEvaluate.isEmpty());
        return false;
    }

}
