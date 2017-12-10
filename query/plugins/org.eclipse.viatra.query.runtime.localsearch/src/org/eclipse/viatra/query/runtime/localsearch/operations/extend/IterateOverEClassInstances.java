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

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IIteratingSearchOperation;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;

import com.google.common.collect.Lists;

/**
 * Iterates all available {@link EClass} instances using an {@link NavigationHelper VIATRA Base indexer}. It is
 * assumed that the base indexer has been registered for the selected type.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class IterateOverEClassInstances extends ExtendOperation<EObject> implements IIteratingSearchOperation{

    private final EClass clazz;
    private final EClassTransitiveInstancesKey type;
    private static final TupleMask indexerMask = TupleMask.empty(1);

    public IterateOverEClassInstances(int position, EClass clazz) {
        super(position);
        this.clazz = clazz;
        type = new EClassTransitiveInstancesKey(clazz);
    }

    public EClass getClazz() {
        return clazz;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        Iterable<? extends Object> values = context.getRuntimeContext().enumerateValues(type, indexerMask, Tuples.staticArityFlatTupleOf());
        // XXX This casting is only required for API backwards compatibility
        it = (Iterator<EObject>) values.iterator();
    }
    
    @Override
    public String toString() {
        return "extend    "+clazz.getName()+"(-"+ position+") indexed";
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return Lists.asList(position, new Integer[0]);
    }
    
    /**
     * @since 1.4
     */
    @Override
    public IInputKey getIteratedInputKey() {
        return type;
    }

}
