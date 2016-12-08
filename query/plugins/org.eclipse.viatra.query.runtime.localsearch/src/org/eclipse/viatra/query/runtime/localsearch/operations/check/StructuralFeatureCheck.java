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

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * A simple operation that checks whether a {@link EStructuralFeature} connects two selected variables.
 */
public class StructuralFeatureCheck extends CheckOperation {

    int sourcePosition, targetPosition;
    EStructuralFeature feature;

    public StructuralFeatureCheck(int sourcePosition, int targetPosition, EStructuralFeature feature) {
        super();
        this.sourcePosition = sourcePosition;
        this.targetPosition = targetPosition;
        this.feature = feature;
    }

    @Override
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
        Preconditions.checkNotNull(frame.getValue(sourcePosition), "Invalid plan, variable %s unbound", sourcePosition);
        Preconditions.checkNotNull(frame.getValue(targetPosition), "Invalid plan, variable %s unbound", targetPosition);
        try {
            EObject source = (EObject) frame.getValue(sourcePosition);
            if(! feature.getEContainingClass().isSuperTypeOf(source.eClass()) ){
                // TODO planner should ensure the proper supertype relation, see bug 500968
                return false;
            }
            Object target = frame.getValue(targetPosition);
            if (feature.isMany()) {
                return ((Collection<?>) source.eGet(feature)).contains(target);
            } else {
                return target.equals(source.eGet(feature));
            }
        } catch (ClassCastException e) {
            throw new LocalSearchException(LocalSearchException.TYPE_ERROR, e);
        }
    }
    
    @Override
    public String toString() {
        return "check     "+feature.getContainerClass().getSimpleName()+"."+feature.getName()+"(+"+sourcePosition+", +"+targetPosition+")";
    }
    
    @Override
	public List<Integer> getVariablePositions() {
		return Lists.asList(sourcePosition, targetPosition, new Integer[0]);
	}

}
