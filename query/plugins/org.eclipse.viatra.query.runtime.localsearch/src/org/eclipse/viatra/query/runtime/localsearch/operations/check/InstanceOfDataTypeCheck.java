/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.check;

import java.util.List;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * @author Zoltan Ujhelyi
 * @noextend This class is not intended to be subclassed by clients.
 */
public class InstanceOfDataTypeCheck extends CheckOperation {

    private int position;
    private EDataType dataType;

    public InstanceOfDataTypeCheck(int position, EDataType dataType) {
        this.position = position;
        this.dataType = dataType;

    }

    /**
     * @deprecated Use {@link #check(MatchingFrame, ISearchContext)} instead
     */
    @Deprecated
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
        return check(frame, null);
    }

    @Override
    protected boolean check(MatchingFrame frame, ISearchContext context) {
        Preconditions.checkNotNull(frame.getValue(position), "Invalid plan, variable %s unbound", position);
        return dataType.isInstance(frame.getValue(position));
    }

    @Override
    public String toString() {
        return "check     "+dataType.getName()+"(+"+position+")";
    }
    
    @Override
    public List<Integer> getVariablePositions() {
        return Lists.asList(position, new Integer[0]);
    }
    
}
