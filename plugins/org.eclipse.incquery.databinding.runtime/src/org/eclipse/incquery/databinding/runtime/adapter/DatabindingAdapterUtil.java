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

import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.databinding.runtime.adapter.ObservableDefinition.ObservableType;
import org.eclipse.incquery.databinding.runtime.api.IncQueryObservables;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public final class DatabindingAdapterUtil {

    public static final String OBSERVABLEVALUE_ANNOTATION = "ObservableValue";

    private DatabindingAdapterUtil() {
    }

    /**
     * Returns an IObservableValue for the given match based on the given expression. If an attribute is not present in
     * the expression than it tries with the 'name' attribute. If it is not present the returned value will be null.
     *
     * @param match
     *            the match object
     * @param expression
     *            the expression
     * @return IObservableValue instance or null
     * @deprecated Use {@link IncQueryObservables#getObservableValue(IPatternMatch,String)} instead
     */
    @Deprecated
    public static IObservableValue getObservableValue(IPatternMatch match, String expression) {
        return IncQueryObservables.getObservableValue(match, expression);
    }

    /**
     * Registers the given changeListener for the appropriate features of the given signature. The features will be
     * computed based on the message parameter.
     *
     * @param signature
     *            the signature instance
     * @param changeListener
     *            the changle listener
     * @param message
     *            the message which can be found in the appropriate PatternUI annotation
     * @return the list of IObservableValue instances for which the IValueChangeListener was registered
     * @deprecated Use {@link IncQueryObservables#observeFeatures(IPatternMatch,IValueChangeListener,String)} instead
     */
    @Deprecated
    public static List<IObservableValue> observeFeatures(IPatternMatch match, IValueChangeListener changeListener,
            String message) {
        return IncQueryObservables.observeFeatures(match, changeListener, message);
    }

    /**
     * Registers the given change listener on the given object's all accessible fields. This function uses Java
     * Reflection.
     *
     * @param changeListener
     *            the changle listener
     * @param object
     *            the observed object
     * @return the list of IObservableValue instances for which the IValueChangeListener was registered
     * @deprecated Use {@link IncQueryObservables#observeAllAttributes(IValueChangeListener,Object)} instead
     */
    @Deprecated
    public static List<IObservableValue> observeAllAttributes(IValueChangeListener changeListener, Object object) {
        return IncQueryObservables.observeAllAttributes(changeListener, object);
    }

    /**
     * Get the structural feature with the given name of the given object.
     *
     * @param o
     *            the object (must be an EObject)
     * @param featureName
     *            the name of the feature
     * @return the EStructuralFeature of the object or null if it can not be found
     */
    public static EStructuralFeature getFeature(Object o, String featureName) {
        if (o instanceof EObject) {
            EStructuralFeature feature = ((EObject) o).eClass().getEStructuralFeature(featureName);
            return feature;
        }
        return null;
    }

    public static String getMessage(IPatternMatch match, String message) {
        String[] tokens = message.split("\\$");
        StringBuilder newText = new StringBuilder();

        for (int i = 0; i < tokens.length; i++) {
            if (i % 2 == 0) {
                newText.append(tokens[i]);
            } else {
                String[] objectTokens = tokens[i].split("\\.");
                if (objectTokens.length > 0) {
                    Object o = null;
                    EStructuralFeature feature = null;

                    if (objectTokens.length == 1) {
                        o = match.get(objectTokens[0]);
                        feature = DatabindingAdapterUtil.getFeature(o, "name");
                    }
                    if (objectTokens.length == 2) {
                        o = match.get(objectTokens[0]);
                        feature = DatabindingAdapterUtil.getFeature(o, objectTokens[1]);
                    }

                    if (o != null && feature != null) {
                        Object value = ((EObject) o).eGet(feature);
                        if (value != null) {
                            newText.append(value.toString());
                        } else {
                            newText.append("null");
                        }
                    } else if (o != null) {
                        newText.append(o.toString());
                    }
                } else {
                    newText.append("[no such parameter]");
                }
            }
        }

        return newText.toString();
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
        for (PAnnotation annotation : query.getAnnotationsByName(OBSERVABLEVALUE_ANNOTATION)) {
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
