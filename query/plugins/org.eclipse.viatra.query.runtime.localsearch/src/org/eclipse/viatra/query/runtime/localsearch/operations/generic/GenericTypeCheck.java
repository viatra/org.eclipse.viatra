/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.generic;

import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IIteratingSearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.CheckOperation;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.VolatileMaskedTuple;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;

/**
 * @author Zoltan Ujhelyi
 * @since 1.7
 * @noextend This class is not intended to be subclassed by clients.
 */
public class GenericTypeCheck extends CheckOperation implements IIteratingSearchOperation {

    private final IInputKey type;
    private ImmutableList<Integer> positionList;
    private VolatileMaskedTuple maskedTuple;

    public GenericTypeCheck(IInputKey type, int[] positions, TupleMask callMask) {
        Preconditions.checkArgument(positions.length == type.getArity(),
                "The type %s requires %s parameters, but %s positions are provided", type.getPrettyPrintableName(),
                type.getArity(), positions.length);
        Builder<Integer> builder = ImmutableList.<Integer>builder();
        for (int position : positions) {
            builder.add(position);
        }
        this.positionList = builder.build();
        this.maskedTuple = new VolatileMaskedTuple(callMask);
        this.type = type;
    }

    @Override
    public List<Integer> getVariablePositions() {
        return positionList;
    }

    @Override
    protected boolean check(MatchingFrame frame, ISearchContext context) {
        maskedTuple.updateTuple(frame);
        return context.getRuntimeContext().containsTuple(type, maskedTuple);
    }

    @Override
    public String toString() {
        Iterable<String> parameterIndices = Iterables.transform(positionList, new Function<Integer, String>() {

                    @Override
                    public String apply(Integer input) {
                        return String.format("+%d", input);
                    }
                });
        return "check     " + type.getPrettyPrintableName() + "(" + Joiner.on(", ").join(parameterIndices) + ")";
    }

    @Override
    public IInputKey getIteratedInputKey() {
        return type;
    }
}
