/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.extend;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;

/**
 * This operation handles constants in search plans by binding a variable to a constant value. Such operations should be
 * executed as early as possible during plan execution.
 * 
 * @author Marton Bur
 *
 */
public class ExtendConstant implements ISearchOperation {

    private class Executor extends SingleValueExtendOperationExecutor<Object> {

        public Executor(int position) {
            super(position);
        }

        @Override
        public Iterator<?> getIterator(MatchingFrame frame, ISearchContext context) {
            return Collections.singletonList(value).iterator();
        }
        
        @Override
        public ISearchOperation getOperation() {
            return ExtendConstant.this;
        }
    }
    
    private final Object value;
    private final int position;

    public ExtendConstant(int position, Object value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public ISearchOperationExecutor createExecutor() {
        return new Executor(position);
    }

    @Override
    public List<Integer> getVariablePositions() {
        return Arrays.asList(position);
    }
    
    @Override
    public String toString() {
        return toString(Object::toString);
    }
    
    @Override
    public String toString(Function<Integer, String> variableMapping) {
        return "extend    constant -"+variableMapping.apply(position)+"='"+value+"'";
    }
    
}
