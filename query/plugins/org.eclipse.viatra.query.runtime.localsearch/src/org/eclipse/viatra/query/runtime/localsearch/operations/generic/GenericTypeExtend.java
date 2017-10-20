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

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IIteratingSearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.VolatileMaskedTuple;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableList.Builder;

/**
 * @author Zoltan Ujhelyi
 * @since 1.7
 * @noextend This class is not intended to be subclassed by clients.
 */
public class GenericTypeExtend implements ISearchOperation, IIteratingSearchOperation {

    private final IInputKey type;
    private final int[] positions;
    private final ImmutableList<Integer> positionList;
    private final Set<Integer> unboundVariableIndices;
    private Iterator<Tuple> it;
    private final VolatileMaskedTuple maskedTuple;
    private TupleMask indexerMask;

    /**
     * 
     * @param type
     *            the type to execute the extend operation on
     * @param positions
     *            the parameter positions that represent the variables of the input key
     * @param unboundVariableIndices
     *            the set of positions that are bound at the start of the operation
     */
    public GenericTypeExtend(IInputKey type, int[] positions, TupleMask callMask, TupleMask indexerMask, Set<Integer> unboundVariableIndices) {
        Preconditions.checkArgument(positions.length == type.getArity(),
                "The type %s requires %s parameters, but %s positions are provided", type.getPrettyPrintableName(),
                type.getArity(), positions.length);
        Builder<Integer> builder = ImmutableList.<Integer>builder();
        for (int position : positions) {
            builder.add(position);
        }
        this.positionList = builder.build();
        this.positions = positions;
        this.type = type;

        this.unboundVariableIndices = unboundVariableIndices;
        this.maskedTuple = new VolatileMaskedTuple(callMask);
        this.indexerMask = indexerMask;
    }

    @Override
    public IInputKey getIteratedInputKey() {
        return type;
    }

    @Override
    public void onBacktrack(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        for (Integer position : unboundVariableIndices) {
            frame.setValue(position, null);
        }
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        maskedTuple.updateTuple(frame);
        it = context.getRuntimeContext().enumerateTuples(type, indexerMask, maskedTuple).iterator();

    }

    @Override
    public boolean execute(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        if (it.hasNext()) {
            final Tuple next = it.next();
            for (Integer position : unboundVariableIndices) {
                frame.setValue(position, null);
            }
            for (int i = 0; i < positions.length; i++) {
                Object newValue = next.get(i);
                Object oldValue = frame.getValue(positions[i]);
                if (oldValue != null && !Objects.equals(oldValue, newValue)) {
                    // If positions tuple maps more than one values for the same element (e.g. loop), it means that
                    // these arguments are to unified by the caller. In this case if the callee assigns different values
                    // the frame shall be considered a failed match
                    return false;
                }
                frame.setValue(positions[i], newValue);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Integer> getVariablePositions() {
        return positionList;
    }

    @Override
    public String toString() {
        Iterable<String> parameterNames = Iterables.transform(positionList, new Function<Integer, String>() {

                    @Override
                    public String apply(Integer input) {
                        return String.format("%s%d", unboundVariableIndices.contains(input) ? "-" : "+", input);
                    }
                });
        return "extend    " + type.getPrettyPrintableName() + "(" + Joiner.on(", ").join(parameterNames) + ")";
    }
}
