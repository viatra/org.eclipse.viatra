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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IIteratingSearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.VolatileMaskedTuple;

import com.google.common.base.Preconditions;

/**
 * @author Zoltan Ujhelyi
 * @since 1.7
 * @noextend This class is not intended to be subclassed by clients.
 */
public class GenericTypeExtendSingleValue implements ISearchOperation, IIteratingSearchOperation {

    private final IInputKey type;
    private final List<Integer> positionList;
    private Iterator<? extends Object> it;
    private final VolatileMaskedTuple maskedTuple;
    private TupleMask indexerMask;
    private final int unboundVariableIndex;

    /**
     * 
     * @param type
     *            the type to execute the extend operation on
     * @param positions
     *            the parameter positions that represent the variables of the input key
     */
    public GenericTypeExtendSingleValue(IInputKey type, int[] positions, TupleMask callMask, TupleMask indexerMask, int unboundVariableIndex) {
        Preconditions.checkArgument(positions.length == type.getArity(),
                "The type %s requires %s parameters, but %s positions are provided", type.getPrettyPrintableName(),
                type.getArity(), positions.length);
        List<Integer> positionList = new ArrayList<>();
        for (int position : positions) {
            positionList.add(position);
        }
        this.positionList = Collections.unmodifiableList(positionList);
        this.type = type;

        this.unboundVariableIndex = unboundVariableIndex;
        this.maskedTuple = new VolatileMaskedTuple(callMask);
        this.indexerMask = indexerMask;
    }

    @Override
    public IInputKey getIteratedInputKey() {
        return type;
    }

    @Override
    public void onBacktrack(MatchingFrame frame, ISearchContext context) {
        frame.setValue(unboundVariableIndex, null);
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        maskedTuple.updateTuple(frame);
        it = context.getRuntimeContext().enumerateValues(type, indexerMask, maskedTuple).iterator();

    }

    @Override
    public boolean execute(MatchingFrame frame, ISearchContext context) {
        if (it.hasNext()) {
            final Object next = it.next();
            frame.setValue(unboundVariableIndex, next);
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
        return "extend    " + type.getPrettyPrintableName() + "("
                + positionList.stream().map(
                        input -> String.format("%s%d", Objects.equals(input, unboundVariableIndex) ? "-" : "+", input))
                        .collect(Collectors.joining(", "))
                + ")";
    }
}
