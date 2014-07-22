/*******************************************************************************
 * Copyright (c) 2010-2013, istvanrath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.databinding.runtime.util;

import java.util.Map;

import org.eclipse.incquery.databinding.runtime.adapter.DatabindingAdapter;
import org.eclipse.incquery.databinding.runtime.adapter.GenericDatabindingAdapter;
import org.eclipse.incquery.databinding.runtime.adapter.ObservableDefinition;
import org.eclipse.incquery.databinding.runtime.adapter.ObservableDefinition.ObservableType;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * @author istvanrath
 *
 */
public class DatabindingUtil {

     public static final String OBSERVABLEVALUE_ANNOTATION = "ObservableValue";

    public static DatabindingAdapter<IPatternMatch> getDatabindingAdapter(IQuerySpecification<?> query) {
        GenericDatabindingAdapter adapter = new GenericDatabindingAdapter(query);
        return adapter;
    }

    /**
     * @param pattern
     * @return
     */
    public static Map<String, ObservableDefinition> calculateObservableValues(IQuerySpecification<?> query) {
        Map<String, ObservableDefinition> propertyMap = Maps.newHashMap();
        for (String v : query.getParameterNames()) {
            ObservableDefinition def = new ObservableDefinition(v, v,
                    ObservableType.OBSERVABLE_FEATURE);
            propertyMap.put(v, def);
        }
        for (PAnnotation annotation : query.getAnnotationsByName(DatabindingUtil.OBSERVABLEVALUE_ANNOTATION)) {
            String name = (String) annotation.getFirstValue("name");
            String expr = (String) annotation.getFirstValue("expression");
            String label = (String) annotation.getFirstValue("labelExpression");
            if (name == null) {
                Preconditions.checkArgument(expr == null && label == null,
                        "Name attribute must not be empty");
                continue;
            }
            Preconditions.checkArgument(expr != null ^ label != null,
                    "Either expression or label expression attribute must not be empty.");
            String obsExpr = null;
            ObservableType type;
            if (expr != null) {
                obsExpr = expr;
                type = ObservableType.OBSERVABLE_FEATURE;
            } else {// if (labelRef != null)
                obsExpr = label;
                type = ObservableType.OBSERVABLE_LABEL;
            }
            ObservableDefinition def = new ObservableDefinition(name, obsExpr, type);
    
            propertyMap.put(name, def);
        }
        return propertyMap;
    }

}
