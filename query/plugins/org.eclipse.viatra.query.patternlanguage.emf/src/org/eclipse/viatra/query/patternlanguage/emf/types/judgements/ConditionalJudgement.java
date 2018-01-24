/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.types.judgements;

import java.util.Objects;
import java.util.Set;

import org.eclipse.viatra.query.patternlanguage.emf.vql.Expression;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;

import com.google.common.collect.ImmutableSet;

/**
 * A conditional judgement is used to express two expressions having types in a causal relationship, e.g. if variable v
 * has a type of 't' then variable v2 has to have a type 't2'
 * 
 * @author Zoltan Ujhelyi
 * @since 1.4
 *
 */
public class ConditionalJudgement extends AbstractTypeJudgement {

    private IInputKey type;
    private Expression conditionExpression;
    private IInputKey conditionType;

    public ConditionalJudgement(Expression expression, IInputKey type, Expression conditionExpression, IInputKey conditionType) {
        super(expression);
        this.type = type;
        this.conditionExpression = conditionExpression;
        this.conditionType = conditionType;
    }

    @Override
    public Set<Expression> getDependingExpressions() {
        return ImmutableSet.of(conditionExpression);
    }

    public IInputKey getType() {
        return type;
    }

    public Expression getConditionExpression() {
        return conditionExpression;
    }

    public IInputKey getConditionType() {
        return conditionType;
    }

    @Override
    public String toString() {
        return "ConditionalJudgement [type=" + type + ", conditionExpression=" + conditionExpression
                + ", conditionType=" + conditionType + ", expression=" + expression + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, type, conditionExpression, conditionType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConditionalJudgement) {
            ConditionalJudgement other = (ConditionalJudgement) obj;
            return Objects.equals(type, other.type) && Objects.equals(conditionExpression, other.conditionExpression)
                    && Objects.equals(expression, other.expression) && Objects.equals(conditionType, other.conditionType);
        }
        return false;
    }

    
}
