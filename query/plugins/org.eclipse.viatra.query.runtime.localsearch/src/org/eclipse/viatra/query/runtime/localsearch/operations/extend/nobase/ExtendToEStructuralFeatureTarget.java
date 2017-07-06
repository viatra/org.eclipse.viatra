/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur, Zoltan Ujhelyi, Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.extend.nobase;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Iterates over all sources of {@link EStructuralFeature}
 * @deprecated this class was introduced because an incorrect javadoc specification; use superclass instead
 */
@Deprecated
public class ExtendToEStructuralFeatureTarget extends org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendToEStructuralFeatureTarget {

    public ExtendToEStructuralFeatureTarget(int sourcePosition, int targetPosition, EStructuralFeature feature) {
        super(sourcePosition, targetPosition, feature);
    }
    
}
