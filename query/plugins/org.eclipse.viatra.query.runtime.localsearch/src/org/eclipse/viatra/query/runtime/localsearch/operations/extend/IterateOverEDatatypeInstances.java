/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.extend;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.viatra.query.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IIteratingSearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;


/**
 * Iterates over all {@link EDataType} instances using an {@link IQueryRuntimeContext VIATRA Base indexer}. It is
 * assumed that the indexer is initialized for the selected {@link EDataType}.
 * 
 */
public class IterateOverEDatatypeInstances implements IIteratingSearchOperation {

    private class Executor extends SingleValueExtendOperationExecutor<Object> {
        
        public Executor(int position) {
            super(position);
        }

        @Override
        public Iterator<? extends Object> getIterator(MatchingFrame frame, ISearchContext context) {
            return context.getRuntimeContext().enumerateValues(type, indexerMask, Tuples.staticArityFlatTupleOf()).iterator();
        }
        
        @Override
        public ISearchOperation getOperation() {
            return IterateOverEDatatypeInstances.this;
        }
    }
    
    private final EDataType dataType;
    private final EDataTypeInSlotsKey type;
    private static final TupleMask indexerMask = TupleMask.empty(1);
    private final int position;

    public IterateOverEDatatypeInstances(int position, EDataType dataType) {
        this.position = position;
        this.dataType = dataType;
        type = new EDataTypeInSlotsKey(dataType);
    }

    public EDataType getDataType() {
        return dataType;
    }
    
    @Override
    public ISearchOperationExecutor createExecutor() {
        return new Executor(position);
    }

    @Override
    public String toString() {
        return toString(Object::toString);
    }
    
    @Override
    public String toString(Function<Integer, String> variableMapping) {
        return "extend    "+dataType.getName()+"(-"+variableMapping.apply(position)+") indexed";
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return Collections.singletonList(position);
    }

    /**
     * @since 1.4
     */
    @Override
    public IInputKey getIteratedInputKey() {
        return type;
    }
    

}
