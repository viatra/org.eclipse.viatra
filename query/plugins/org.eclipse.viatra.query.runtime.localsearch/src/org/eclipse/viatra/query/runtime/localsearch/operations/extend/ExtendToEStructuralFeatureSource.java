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
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.IIteratingSearchOperation;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;

import com.google.common.collect.Lists;

/**
 * Iterates over all sources of {@link EStructuralFeature} using an {@link NavigationHelper VIATRA Base indexer}.
 * It is assumed that the indexer is initialized for the selected {@link EStructuralFeature}.
 * 
 */
public class ExtendToEStructuralFeatureSource extends ExtendOperation<EObject> implements IIteratingSearchOperation{

    private int targetPosition;
    private EStructuralFeature feature;

    public ExtendToEStructuralFeatureSource(int sourcePosition, int targetPosition, EStructuralFeature feature) {
        super(sourcePosition);
        this.targetPosition = targetPosition;
        this.feature = feature;
    }

    public EStructuralFeature getFeature() {
        return feature;
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        final Collection<EObject> values = context.getBaseIndex().findByFeatureValue(frame.getValue(targetPosition), feature);
        // System.out.println("**FeatureSource " + feature.getContainerClass().getName() + "." + feature.getName() + " "
        // + values.size());
        it = values
                .iterator();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("extend to source ")
    	.append(((EClass)feature.eContainer()).getName())
    	.append('.')
    	.append(feature.getName());
        return builder.toString();
    }

    @Override
	public List<Integer> getVariablePositions() {
		return Lists.asList(position, targetPosition, new Integer[0]);
	}
    
    /**
     * @since 1.4
     */
    @Override
    public IInputKey getIteratedInputKey() {
        return new EStructuralFeatureInstancesKey(feature);
    }
    
}
