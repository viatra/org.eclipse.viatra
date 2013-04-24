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
package org.eclipse.incquery.runtime.localsearch.operations.extend;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Iterates all available {@link EStructuralReference} elements using an {@link NavigationHelper EMF-IncQuery Base
 * indexer}. It is assumed that the base indexer has been registered for the selected reference type.
 * 
 */
public class IterateOverEStructuralFeatureInstances implements ISearchOperation {

    private NavigationHelper baseIndexNavigator;
    private EStructuralFeature feature;
    private Integer sourcePosition, targetPosition;
    protected Iterator<FeatureEnds> it;
    
    public IterateOverEStructuralFeatureInstances(int sourcePosition, int targetPosition, EStructuralFeature feature,
            NavigationHelper baseIndexNavigator) {
        this.sourcePosition = sourcePosition;
        this.targetPosition = targetPosition;
        this.feature = feature;
        this.baseIndexNavigator = baseIndexNavigator;
    }
    
    @Override
    public void onBacktrack(MatchingFrame frame) throws LocalSearchException {
        frame.setValue(sourcePosition, null);
        frame.setValue(targetPosition, null);
        it = null;
    }

    @Override
    public void onInitialize(MatchingFrame frame) {

        final Map<EObject, Set<Object>> featureInstances = baseIndexNavigator.getFeatureInstances(feature);

        @SuppressWarnings("unchecked")
        Iterable<FeatureEnds>[] iterators = new Iterable[featureInstances.size()];
        int index = 0;
        for (final Entry<EObject, Set<Object>> entry : featureInstances.entrySet()) {
            final Iterable<FeatureEnds> pairIterator = Iterables.transform(entry.getValue(), new Function<Object, FeatureEnds>() {

                @Override
                public FeatureEnds apply(Object element) {
                    return new FeatureEnds(entry.getKey(), element);
                }

            });
            iterators[index] = pairIterator;
            index++;
        }

        it = Iterables.concat(iterators).iterator();
    }

    @Override
    public boolean execute(MatchingFrame frame) {
        if (it.hasNext()) {
            FeatureEnds next = it.next();
            frame.setValue(sourcePosition, next.getSrc());
            frame.setValue(targetPosition, next.getTrg());
            return true;
        } else {
            return false;
        }
    }


}
