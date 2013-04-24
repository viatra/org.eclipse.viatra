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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;

/**
 * Iterates over all sources of {@link EStructuralFeature} using an {@link NavigationHelper EMF-IncQuery Base indexer}.
 * It is assumed that the indexer is initialized for the selected {@link EStructuralFeature}.
 * 
 * 
 */
public class ExtendToEStructuralFeatureTarget extends ExtendOperation<Object> {

    private NavigationHelper baseIndexNavigator;
    private int sourcePosition;
    private EStructuralFeature feature;

    public ExtendToEStructuralFeatureTarget(int sourcePosition, int targetPosition, EStructuralFeature feature,
            NavigationHelper baseIndexNavigator) {
        super(targetPosition);
        this.sourcePosition = sourcePosition;
        this.feature = feature;
        this.baseIndexNavigator = baseIndexNavigator;
    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation#onInitialize(org.eclipse.incquery.runtime.localsearch.MatchingFrame)
     */
    @Override
    public void onInitialize(MatchingFrame frame) throws LocalSearchException {
        try {
            it = baseIndexNavigator.getFeatureTargets((EObject) frame.getValue(sourcePosition), feature)
                .iterator();
        } catch (ClassCastException e) {
            throw new LocalSearchException("Invalid feature source in parameter" + Integer.toString(sourcePosition));
        }
    }

}
