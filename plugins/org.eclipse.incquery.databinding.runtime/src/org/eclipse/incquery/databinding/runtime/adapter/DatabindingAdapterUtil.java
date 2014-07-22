/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.databinding.runtime.adapter;

import java.util.Map;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.databinding.runtime.util.DatabindingUtil;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.helper.IncQueryRuntimeHelper;

/**
 * @deprecated This class has been deprecated after 0.8.0, can be removed completely later.
 * @author Abel Hegedus
 *
 */
@Deprecated
public final class DatabindingAdapterUtil {

    /**
     * @deprecated Use {@link DatabindingUtil#OBSERVABLEVALUE_ANNOTATION} instead
     */
    public static final String OBSERVABLEVALUE_ANNOTATION = DatabindingUtil.OBSERVABLEVALUE_ANNOTATION;

    private DatabindingAdapterUtil() {
    }

    /**
     * @deprecated Use {@link IncQueryRuntimeHelper#getFeature(Object,String)} instead
     */
    public static EStructuralFeature getFeature(Object o, String featureName) {
        return IncQueryRuntimeHelper.getFeature(o, featureName);
    }

    /**
     * @deprecated Use {@link IncQueryRuntimeHelper#getMessage(IPatternMatch,String)} instead
     */
    public static String getMessage(IPatternMatch match, String message) {
        return IncQueryRuntimeHelper.getMessage(match, message);
    }

    /**
     * @param pattern
     * @return
     * @deprecated Use {@link DatabindingUtil#calculateObservableValues(IQuerySpecification<?>)} instead
     */
    public static Map<String, ObservableDefinition> calculateObservableValues(IQuerySpecification<?> query) {
        return DatabindingUtil.calculateObservableValues(query);
    }
}
