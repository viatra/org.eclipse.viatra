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
package org.eclipse.incquery.runtime.base.api;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A helper class to process the content of {@link NavigationHelper} instances.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class BaseIndexProcessor {

    NavigationHelper index;

    public BaseIndexProcessor(NavigationHelper index) {
        super();
        this.index = index;
    }

    public void processFeatureInstances(EStructuralFeature feature, IEStructuralFeatureProcessor processor) {
        for (EObject holder : index.getHoldersOfFeature(feature)) {
            final Object featureContent = holder.eGet(feature);
            if (feature.isMany()) {
                for (Object target : (Collection<?>) featureContent) {
                    processor.process(feature, holder, target);
                }
            } else {
                processor.process(feature, holder, featureContent);
            }
        }
    }
}
