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

import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * This operation handles constants in search plans by binding a variable to a constant value. Such operations should be
 * executed as early as possible during plan execution.
 * 
 * @author Marton Bur
 *
 */
public class ExtendConstant extends ExtendOperation<Object> {

    private Object value;

    public ExtendConstant(int position, Object value) {
        super(position);
        this.value = value;
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        it = Iterators.singletonIterator(value);
    }

    @Override
    public List<Integer> getVariablePositions() {
        return Lists.asList(position, new Integer[0]);
    }
    
    @Override
    public String toString() {
        return "extend    constant -"+position+"='"+value+"'";
    }
    
}
