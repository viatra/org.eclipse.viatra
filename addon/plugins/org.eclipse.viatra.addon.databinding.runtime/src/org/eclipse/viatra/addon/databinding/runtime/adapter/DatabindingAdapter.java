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

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.ValueProperty;
import org.eclipse.viatra.addon.databinding.runtime.api.ViatraObservables;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;

import com.google.common.base.Preconditions;

/**
 * The class is used to observe given parameters of a pattern.
 * 
 * @author Tamas Szabo
 * 
 * @param <T>
 *            the type parameter of the match
 */
public abstract class DatabindingAdapter<T extends IPatternMatch> {

    private static final String SOURCE_MUST_BE_A_TYPED_PATTERN_MATCH = "Source must be a typed Pattern Match";
    
    protected class MatcherProperty extends ValueProperty {

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
            Preconditions.checkArgument((source instanceof IPatternMatch), SOURCE_MUST_BE_A_TYPED_PATTERN_MATCH);
            return ViatraObservables.getObservableValue((IPatternMatch) source, expression);
        }

    }

    protected class MatcherLabelProperty extends ValueProperty {
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
            Preconditions.checkArgument((source instanceof IPatternMatch), SOURCE_MUST_BE_A_TYPED_PATTERN_MATCH);
            return ViatraObservables.getObservableLabelFeature((IPatternMatch) source, expression);
        }

    }

    /**
     * Returns the array of observable values.
     * 
     * @return the array of values
     */
    public abstract String[] getParameterNames();

    /**
     * Returns an observable value for the given match and parameterName.
     * 
     * @param match
     *            the match object
     * @param parameterName
     *            the parameter name
     * @return an observable value
     */
    public abstract IObservableValue getObservableParameter(T match, String parameterName);
}
