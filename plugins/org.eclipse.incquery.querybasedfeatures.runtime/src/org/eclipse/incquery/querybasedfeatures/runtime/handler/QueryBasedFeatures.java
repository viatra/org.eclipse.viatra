/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.querybasedfeatures.runtime.handler;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.querybasedfeatures.runtime.QueryBasedFeatureKind;

/**
 * @author Abel Hegedus
 *
 */
public class QueryBasedFeatures {

    public static String ANNOTATION_SOURCE = "org.eclipse.incquery.querybasedfeature";
    public static String PATTERN_FQN_KEY = "patternFQN";
    
    public static SingleValueQueryBasedFeature newSingleValueFeature(EStructuralFeature feature, boolean keepCache) {
        return new SingleValueQueryBasedFeature(feature, keepCache);
    }
    
    public static MultiValueQueryBasedFeature newMultiValueFeatue(EStructuralFeature feature, boolean keepCache) {
        return new MultiValueQueryBasedFeature(feature, keepCache);
    }
    
    public static SumQueryBasedFeature newSumFeature(EStructuralFeature feature) {
        return new SumQueryBasedFeature(feature, QueryBasedFeatureKind.SUM);
    }
    
    /**
     * 
     * @param feature
     * @param keepCache
     * @return
     * @deprecated Use <code>count find</code> in query definition instead!
     */
    public static SumQueryBasedFeature newCounterFeature(EStructuralFeature feature) {
        return new SumQueryBasedFeature(feature, QueryBasedFeatureKind.COUNTER);
    }
    
}
