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
import java.util.Iterator;
import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IIteratingSearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.CheckOperation;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

/**
 * @author Zoltan Ujhelyi
 * @since 1.7
 * @noextend This class is not intended to be subclassed by clients.
 */
public class GenericTypeCheck extends CheckOperation implements IIteratingSearchOperation {

    private final IInputKey type;
    private final Integer[] positions;

    public GenericTypeCheck(IInputKey type, Integer[] positions) {
        Preconditions.checkArgument(positions.length == type.getArity(),
                "The type %s requires %s parameters, but %s positions are provided", type.getPrettyPrintableName(),
                type.getArity(), positions.length);
        this.positions = positions;
        this.type = type;
    }

    @Override
    public List<Integer> getVariablePositions() {
        return Arrays.asList(positions);
    }

    @Override
    protected boolean check(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        Object[] seed = new Object[positions.length];
        for (int i = 0; i < positions.length; i++) {
            seed[i] = frame.get(positions[i]);
        }
        return context.getRuntimeContext().containsTuple(type, Tuples.flatTupleOf(seed));
    }

    @Override
    public String toString() {
        Iterator<String> parameterIndexii = Iterators.transform(Iterators.forArray(positions),
                new Function<Integer, String>() {

                    @Override
                    public String apply(Integer input) {
                        return String.format("+%d", input);
                    }
                });
        return "check     " + type.getPrettyPrintableName() + "(" + Joiner.on(", ").join(parameterIndexii) + ")";
    }

    @Override
    public IInputKey getIteratedInputKey() {
        return type;
    }
}
