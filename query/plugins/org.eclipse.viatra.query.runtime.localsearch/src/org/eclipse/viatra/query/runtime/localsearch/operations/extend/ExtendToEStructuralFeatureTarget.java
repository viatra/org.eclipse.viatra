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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * Iterates over all sources of {@link EStructuralFeature}
 */
public class ExtendToEStructuralFeatureTarget extends ExtendOperation<Object> {

    private int sourcePosition;
    private EStructuralFeature feature;

    public ExtendToEStructuralFeatureTarget(int sourcePosition, int targetPosition, EStructuralFeature feature) {
        super(targetPosition);
        this.sourcePosition = sourcePosition;
        this.feature = feature;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        try {
            final EObject value = (EObject) frame.getValue(sourcePosition);
            if(! feature.getEContainingClass().isSuperTypeOf(value.eClass()) ){
                // TODO planner should ensure the proper supertype relation
                it = Collections.emptyIterator();
                return;
            }
            final Object featureValue = value.eGet(feature);
            if (feature.isMany()) {
                if (featureValue != null) {
                    final Collection<Object> objectCollection = (Collection<Object>) featureValue;
                    it = objectCollection.iterator();
                } else {
                    it = Collections.emptyIterator();
                }
            } else {
                if (featureValue != null) {
                    it = Iterators.singletonIterator(featureValue);
                } else {
                    it = Collections.emptyIterator();
                }
            }
        } catch (ClassCastException e) {
            throw new LocalSearchException("Invalid feature source in parameter" + Integer.toString(sourcePosition), e);
        }
    }
    
    @Override
    public String toString() {
        return "extend    "+feature.getEContainingClass().getName()+"."+feature.getName()+"(+"+sourcePosition+", -"+position+")";
    }

    @Override
    public List<Integer> getVariablePositions() {
        return Lists.asList(sourcePosition, position, new Integer[0]);
    }
    
}
