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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IIteratingSearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

/**
 * @author Zoltan Ujhelyi
 * @since 1.7
 * @noextend This class is not intended to be subclassed by clients.
 */
public class GenericTypeExtend implements ISearchOperation, IIteratingSearchOperation {

    private final IInputKey type;
    private final Integer[] positions;
    private final Set<Integer> unboundVariableIndex;
    private Iterator<Tuple> it;

    /**
     * 
     * @param type
     *            the type to execute the extend operation on
     * @param positions
     *            the parameter positions that represent the variables of the input key
     * @param adornment
     *            the set of positions that are bound at the start of the operation
     */
    public GenericTypeExtend(IInputKey type, Integer[] positions, Set<Integer> adornment) {
        Preconditions.checkArgument(positions.length == type.getArity(),
                "The type %s requires %s parameters, but %s positions are provided", type.getPrettyPrintableName(),
                type.getArity(), positions.length);
        this.positions = positions;
        this.type = type;

        this.unboundVariableIndex = new HashSet<Integer>();
        for (Integer position : positions) {
            if (!adornment.contains(position)) {
                unboundVariableIndex.add(position);
            }
        }
    }

    @Override
    public IInputKey getIteratedInputKey() {
        return type;
    }

    @Override
    public void onBacktrack(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        for (Integer position : unboundVariableIndex) {
            frame.setValue(position, null);
        }
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        Object[] seed = new Object[positions.length];
        for (int i = 0; i < positions.length; i++) {
            seed[i] = frame.get(positions[i]);
        }
        it = context.getRuntimeContext().enumerateTuples(type, new FlatTuple(seed)).iterator();

    }

    @Override
    public boolean execute(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        if (it.hasNext()) {
            final Tuple next = it.next();
            for (int i : unboundVariableIndex) {
                frame.setValue(positions[i], next.get(i));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Integer> getVariablePositions() {
        return Arrays.asList(positions);
    }

    @Override
    public String toString() {
        Iterator<String> parameterNames = Iterators.transform(Iterators.forArray(positions),
                new Function<Integer, String>() {

                    @Override
                    public String apply(Integer input) {
                        return String.format("%s%d", unboundVariableIndex.contains(input) ? "-" : "+", input);
                    }
                });
        return "extend    " + type.getPrettyPrintableName() + "(" + Joiner.on(", ").join(parameterNames) + ")";
    }
}
