/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo, Peter Lunk - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.addon.databinding.runtime.adapter;

import java.util.Map;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.databinding.property.value.ValueProperty;
import org.eclipse.viatra.addon.databinding.runtime.api.ViatraObservables;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;

import com.google.common.base.Preconditions;

/**
 * 
 * 
 * @author Peter Lunk
 *
 */
public class MatcherProperties{

    private static final String SOURCE_MUST_BE_A_PATTERN_MATCH = "Source must be a Pattern Match";
    
    /**
     * Returns the array of observable values based on a VIATRA Query specification.
     * 
     * @param The query specification
     * @return the array of values
     */
    public static String[] getPropertyNames(IQuerySpecification query) {
        Map<String, ObservableDefinition> parameterMap = ViatraObservables.calculateObservableValues(query);
        return parameterMap.keySet().toArray(new String[parameterMap.keySet().size()]);
    }
    
    /**
     * Returns an observable value for the given match and parameterName.
     * 
     * @param query
     * 			  the query specification
     * @param match
     *            the match object
     * @param parameterName
     *            the parameter name
     * @return an observable value
     */
    public static IObservableValue getObservableValue(IQuerySpecification query, IPatternMatch match, String parameterName) {
        Map<String, ObservableDefinition> parameterMap = ViatraObservables.calculateObservableValues(query);
        if (parameterMap.size() > 0) {
            ObservableDefinition def = parameterMap.get(parameterName);
            String expression = def.getExpression();
            switch (def.getType()) {
                case OBSERVABLE_FEATURE:
                    return ViatraObservables.getObservableValue(match, expression);
                case OBSERVABLE_LABEL:
                    return ViatraObservables.getObservableLabelFeature(match, expression);
                default:
                    return null;
            }
        }
        return null;
    }
    
    /**
     * Returns an IValueProperty for the given query specification and parameterName.
     * 
     * @param query
     * 			  the query specification
     * @param parameterName
     *            the parameter name
     * @return a value property
     */
    public static IValueProperty getValueProperty(IQuerySpecification query, String parameterName) {
        Map<String, ObservableDefinition> parameterMap = ViatraObservables.calculateObservableValues(query);
        Preconditions.checkArgument(parameterMap.containsKey(parameterName), "Invalid parameter name");
        ObservableDefinition def = parameterMap.get(parameterName);
        switch (def.getType()) {
            case OBSERVABLE_FEATURE:
                return new MatcherProperty(def.getExpression());
            case OBSERVABLE_LABEL:
                return new MatcherLabelProperty(def.getExpression());
            default:
                return null;
        }
    }
    
    protected static class MatcherProperty extends ValueProperty {

        private String expression;

        public MatcherProperty(String expression) {
            this.expression = expression;
        }

        @Override
        public Object getValueType() {
            //TODO if typed as object bindings are not displayed correctly
            return null;
        }

        @Override
        public IObservableValue observe(Realm realm, Object source) {
            Preconditions.checkArgument((source instanceof IPatternMatch), SOURCE_MUST_BE_A_PATTERN_MATCH);
            return ViatraObservables.getObservableValue((IPatternMatch) source, expression);
        }

    }

    protected static class MatcherLabelProperty extends ValueProperty {
        private String expression;

        public MatcherLabelProperty(String expression) {
            this.expression = expression;
        }

        @Override
        public Object getValueType() {
            return String.class;
        }

        @Override
        public IObservableValue observe(Realm realm, Object source) {
            Preconditions.checkArgument((source instanceof IPatternMatch), SOURCE_MUST_BE_A_PATTERN_MATCH);
            return ViatraObservables.getObservableLabelFeature((IPatternMatch) source, expression);
        }

    }

}
