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
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.base.api.IEStructuralFeatureProcessor;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Iterates all available {@link EStructuralFeature} elements using an {@link NavigationHelper VIATRA Base
 * indexer}. It is assumed that the base indexer has been registered for the selected reference type.
 * 
 */
public class IterateOverEStructuralFeatureInstances implements ISearchOperation {

    private EStructuralFeature feature;
    private Integer sourcePosition, targetPosition;
    protected Iterator<Entry<EObject, Object>> it;
    
    public IterateOverEStructuralFeatureInstances(int sourcePosition, int targetPosition, EStructuralFeature feature) {
        this.sourcePosition = sourcePosition;
        this.targetPosition = targetPosition;
        this.feature = feature;
    }
    
    public EStructuralFeature getFeature() {
        return feature;
    }

    @Override
    public void onBacktrack(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        frame.setValue(sourcePosition, null);
        frame.setValue(targetPosition, null);
        it = null;
    }

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        final Map<EObject, Object> instances = Maps.newHashMap();
        context.getBaseIndex().processAllFeatureInstances(feature, new IEStructuralFeatureProcessor() {

            @Override
            public void process(EStructuralFeature feature, EObject source, Object target) {
                instances.put(source, target);
            }
        });

        it = instances.entrySet().iterator();
    }

    @Override
    public boolean execute(MatchingFrame frame, ISearchContext context) {
        if (it.hasNext()) {
            final Entry<EObject, Object> next = it.next();
            frame.setValue(sourcePosition, next.getKey());
            frame.setValue(targetPosition, next.getValue());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("extend ")
        	.append(feature.getContainerClass().getSimpleName())
        	.append('.')
        	.append(feature.getName());
        return builder.toString();
    }

    @Override
	public List<Integer> getVariablePositions() {
		return Lists.asList(sourcePosition, targetPosition, new Integer[0]);
	}
    
}
