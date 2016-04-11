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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;

import com.google.common.collect.Lists;

/**
 * A simple operation that checks whether a {@link EStructuralFeature} connects two selected variables.
 */
public class ContainmentCheck extends CheckOperation {

    int childPosition, containerPosition;
    private boolean transitive;

    public ContainmentCheck(int childPosition, int containerPosition, boolean transitive) {
        super();
        this.childPosition = childPosition;
        this.containerPosition = containerPosition;
        this.transitive = transitive;
    }

    @Override
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
        try {
            EObject child = (EObject) frame.getValue(childPosition);
            EObject container = (EObject)frame.getValue(containerPosition);
            
            if (transitive) {
                return EcoreUtil.isAncestor(container, child);
            } else {
                return child.eContainer().equals(container);
            }
        } catch (ClassCastException e) {
            throw new LocalSearchException(LocalSearchException.TYPE_ERROR, e);
        }
    }
    
    @Override
    public String toString() {
        return "ContainmentCheck";
    }

    @Override
	public List<Integer> getVariablePositions() {
		return Lists.asList(childPosition, containerPosition, new Integer[0]);
	}

}
