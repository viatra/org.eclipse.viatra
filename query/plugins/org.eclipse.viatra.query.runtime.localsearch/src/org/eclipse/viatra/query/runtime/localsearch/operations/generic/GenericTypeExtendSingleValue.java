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
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.SingleValueExtendOperationExecutor;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.VolatileMaskedTuple;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

/**
 * @author Zoltan Ujhelyi
 * @since 1.7
 * @noextend This class is not intended to be subclassed by clients.
 */
public class GenericTypeExtendSingleValue implements IIteratingSearchOperation {

    private class Executor extends  SingleValueExtendOperationExecutor<Object> {
        
        private final VolatileMaskedTuple maskedTuple;
        
        public Executor(int position) {
            super(position);
            this.maskedTuple = new VolatileMaskedTuple(callMask);
        }

        @Override
        protected Iterator<? extends Object> getIterator(MatchingFrame frame, ISearchContext context) {
            maskedTuple.updateTuple(frame);
            return context.getRuntimeContext().enumerateValues(type, indexerMask, maskedTuple).iterator();
        }
        
        @Override
        public ISearchOperation getOperation() {
            return GenericTypeExtendSingleValue.this;
        }
    }
    
    private final IInputKey type;
    private final List<Integer> positionList;
    private final TupleMask indexerMask;
    private final TupleMask callMask;
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
                "The type %s requires %d parameters, but %d positions are provided", type.getPrettyPrintableName(),
                type.getArity(), positions.length);
        List<Integer> modifiablePositionList = new ArrayList<>();
        for (int position : positions) {
            modifiablePositionList.add(position);
        }
        this.unboundVariableIndex = unboundVariableIndex;
        this.positionList = Collections.unmodifiableList(modifiablePositionList);
        this.type = type;

        this.callMask = callMask;
        this.indexerMask = indexerMask;
    }

    @Override
    public IInputKey getIteratedInputKey() {
        return type;
    }

    @Override
    public ISearchOperationExecutor createExecutor() {
        return new Executor(unboundVariableIndex);
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
