/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.databinding.runtime.adapter;

import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.incquery.databinding.runtime.api.IncQueryObservables;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;

import com.google.common.base.Preconditions;

public class GenericDatabindingAdapter extends DatabindingAdapter<IPatternMatch> {

    private final Map<String, ObservableDefinition> parameterMap;

    public GenericDatabindingAdapter(Pattern pattern) {
        this.parameterMap = DatabindingAdapterUtil.calculateObservableValues(pattern);
    }

    @Override
    public String[] getParameterNames() {
        return parameterMap.keySet().toArray(new String[parameterMap.keySet().size()]);
    }

    @Override
    public IObservableValue getObservableParameter(IPatternMatch match, String parameterName) {
        if (parameterMap.size() > 0) {
            ObservableDefinition def = parameterMap.get(parameterName);
            String expression = def.getExpression();
            switch (def.getType()) {
            case OBSERVABLE_FEATURE:
                return IncQueryObservables.getObservableValue(match, expression);
            case OBSERVABLE_LABEL:
                return IncQueryObservables.getObservableLabelFeature(match, expression);
            }

        }
        return null;
    }

    public IValueProperty getProperty(String parameterName) {
        Preconditions.checkArgument(parameterMap.containsKey(parameterName), "Invalid parameter name");
        ObservableDefinition def = parameterMap.get(parameterName);
        switch (def.getType()) {
        case OBSERVABLE_FEATURE:
            return new MatcherProperty(def.getExpression());
        case OBSERVABLE_LABEL:
            return new MatcherLabelProperty(def.getExpression());
        }
        return null;
    }

}
