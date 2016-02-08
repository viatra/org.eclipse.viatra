/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.databinding.runtime.observables;

import java.util.StringTokenizer;

import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.viatra.addon.databinding.runtime.api.IncQueryObservables;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;

/**
 * An observable label feature is a computed label, that can refer to the various parameters of the match, and reacts to
 * the corresponding model changes.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ObservableLabelFeature extends ComputedValue {
    IPatternMatch match;
    String expression;
    Object container;

    public ObservableLabelFeature(IPatternMatch match, String expression, Object container) {
        super(String.class);
        this.match = match;
        this.expression = expression;
        this.container = container;
    }

    public Object getContainer() {
        return container;
    }

    public IPatternMatch getMatch() {
        return match;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    protected Object calculate() {

        StringBuilder sb = new StringBuilder();
        try {

            StringTokenizer tokenizer = new StringTokenizer(expression, "$", true);
            if (expression.isEmpty() || tokenizer.countTokens() == 0) {
                throw new IllegalArgumentException("Expression must not be empty.");
            }
            boolean inExpression = false;
            boolean foundToken = false;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.equals("$")) {
                    if (inExpression && !foundToken) {
                        throw new IllegalArgumentException("Empty reference ($$) in message is not allowed.");
                    }
                    inExpression = !inExpression;
                } else if (inExpression) {
                    IObservableValue value = IncQueryObservables.getObservableValue(match, token);
                    sb.append(value.getValue());
                    foundToken = true;
                } else {
                    sb.append(token);
                }
            }
            if (inExpression) {
                throw new IllegalArgumentException("Inconsistent model references - a $ character is missing.");
            }

        } catch (IllegalArgumentException e) {
            return "ERROR: " + e.getMessage();
        }
        return sb.toString();
    }
}