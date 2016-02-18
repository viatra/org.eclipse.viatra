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

package org.eclipse.viatra.addon.databinding.runtime.adapter;

import java.util.Map;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.addon.databinding.runtime.api.ViatraObservables;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.helper.ViatraQueryRuntimeHelper;

/**
 * @deprecated This class has been deprecated after 0.8.0, can be removed completely later.
 * @author Abel Hegedus
 *
 */
@Deprecated
public final class DatabindingAdapterUtil {

    /**
     * @deprecated Use {@link ViatraObservables#OBSERVABLEVALUE_ANNOTATION} instead
     */
    public static final String OBSERVABLEVALUE_ANNOTATION = ViatraObservables.OBSERVABLEVALUE_ANNOTATION;

    private DatabindingAdapterUtil() {
    }

    /**
     * @deprecated Use {@link ViatraQueryRuntimeHelper#getFeature(Object,String)} instead
     */
    public static EStructuralFeature getFeature(Object o, String featureName) {
        return ViatraQueryRuntimeHelper.getFeature(o, featureName);
    }

    /**
     * @deprecated Use {@link ViatraQueryRuntimeHelper#getMessage(IPatternMatch,String)} instead
     */
    public static String getMessage(IPatternMatch match, String message) {
        return ViatraQueryRuntimeHelper.getMessage(match, message);
    }

    /**
     * @deprecated Use {@link ViatraObservables#calculateObservableValues(IQuerySpecification) } instead
     */
    public static Map<String, ObservableDefinition> calculateObservableValues(IQuerySpecification<?> query) {
        return ViatraObservables.calculateObservableValues(query);
    }
}
