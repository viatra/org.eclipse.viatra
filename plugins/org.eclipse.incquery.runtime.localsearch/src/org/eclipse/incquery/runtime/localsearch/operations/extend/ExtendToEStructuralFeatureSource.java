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

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.matcher.ISearchContext;

/**
 * Iterates over all sources of {@link EStructuralFeature} using an {@link NavigationHelper EMF-IncQuery Base indexer}.
 * It is assumed that the indexer is initialized for the selected {@link EStructuralFeature}.
 * 
 */
public class ExtendToEStructuralFeatureSource extends ExtendOperation<EObject> {

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

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation#onInitialize(org.eclipse.incquery.runtime.localsearch.MatchingFrame)
     */
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
        builder.append("ExtendToEStructuralFeatureSource(");
        builder.append(position + ", " + targetPosition+ ", ");
        
        EClass container = feature.getEContainingClass();
        String packageNsUri = container.getEPackage().getNsURI();
        builder.append("getFeatureLiteral(\"" + packageNsUri + "\", \"" + container.getName() + "\", \"" + feature.getName() + "\")");
        
        builder.append(")");
        return builder.toString();
    }

}
