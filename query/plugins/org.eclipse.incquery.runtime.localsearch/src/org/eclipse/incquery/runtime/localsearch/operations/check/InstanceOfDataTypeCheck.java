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
package org.eclipse.incquery.runtime.localsearch.operations.check;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class InstanceOfDataTypeCheck extends CheckOperation {

    private Integer position;
    private EDataType dataType;

    public InstanceOfDataTypeCheck(int position, EDataType dataType) {
        this.position = position;
        this.dataType = dataType;

    }

    @Override
    protected boolean check(MatchingFrame frame) {
        Preconditions.checkNotNull(frame.getValue(position), "Invalid plan, variable %s unbound", position);
        return dataType.getInstanceClassName().equals(frame.getValue(position).getClass().getName());
    }

    @Override
    public String toString() {
        return "InstanceOfCheck";
    }
    @Override
	public List<Integer> getVariablePositions() {
		return Lists.asList(position, new Integer[0]);
	}
    
}
