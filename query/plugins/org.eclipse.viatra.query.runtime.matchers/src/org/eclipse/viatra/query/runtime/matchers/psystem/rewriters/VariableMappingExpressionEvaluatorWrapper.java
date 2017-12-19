/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.rewriters;

import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.viatra.query.runtime.matchers.psystem.IValueProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * A wrapper for {@link IExpressionEvaluator} which is capable of correctly mapping variable names used by the
 * expression.
 * 
 * @author Grill Balázs
 *
 */
class VariableMappingExpressionEvaluatorWrapper implements IExpressionEvaluator {

    private final IExpressionEvaluator wrapped;
    private final Map<String, String> variableMapping;
    
    public VariableMappingExpressionEvaluatorWrapper(IExpressionEvaluator wrapped,
            Map<PVariable, PVariable> variableMapping) {
        
        // Support to rewrap an already wrapped expression.
        boolean rewrap = wrapped instanceof VariableMappingExpressionEvaluatorWrapper;
        this.wrapped = rewrap ? ((VariableMappingExpressionEvaluatorWrapper)wrapped).wrapped : wrapped;

        // Instead of just saving the reference of the mapping, save the actual (trimmed) state of the mapping as it
        // may change during copying (especially during flattening). A LinkedHashMap is used to retain ordering of
        // original parameter names iterator.
        this.variableMapping = Maps.newLinkedHashMap();

        // Index map by variable names
        Map<String, PVariable> names = Maps.newHashMap();
        for (PVariable originalVar : variableMapping.keySet()) {
            names.put(originalVar.getName(), originalVar);
        }
        
        // In case of rewrapping, current names are contained by the previous mapping
        Map<String, String> previousMapping = null;
        if (rewrap){
            previousMapping = ((VariableMappingExpressionEvaluatorWrapper)wrapped).variableMapping;
        }

        // Populate mapping
        for (String inputParameterName : this.wrapped.getInputParameterNames()) {
            String parameterName = rewrap ? previousMapping.get(inputParameterName) : inputParameterName;
            Preconditions.checkArgument(parameterName != null);
            PVariable original = names.get(parameterName);
            Preconditions.checkArgument(original != null);
            PVariable mapped = variableMapping.get(original);
            if (mapped != null){
                this.variableMapping.put(inputParameterName, mapped.getName());
            }
        }
    }

    @Override
    public String getShortDescription() {
        return wrapped.getShortDescription();
    }

    @Override
    public Iterable<String> getInputParameterNames() {
        return variableMapping.values();
    }

    @Override
    public Object evaluateExpression(final IValueProvider provider) throws Exception {
        return wrapped.evaluateExpression(variableName -> {
            String mappedVariableName = variableMapping.get(variableName);
            Preconditions.checkArgument(mappedVariableName != null, "Could not find variable %s", variableName);
            return provider.getValue(mappedVariableName);
        });
    }

}
