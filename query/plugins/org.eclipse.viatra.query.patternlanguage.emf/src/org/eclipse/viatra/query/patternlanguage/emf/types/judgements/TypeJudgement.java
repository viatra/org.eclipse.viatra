/*******************************************************************************
 * Copyright (c) 2010-2015, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.types.judgements;

import java.util.Set;

import org.eclipse.viatra.query.patternlanguage.emf.vql.Expression;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;

import com.google.common.collect.Sets;

/**
 * States a selected expression has a preselected type; multiple type judgements form a conjunction.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.3
 *
 */
public class TypeJudgement extends AbstractTypeJudgement {

    final IInputKey type;
    
    public TypeJudgement(Expression expression, IInputKey type) {
        super(expression);
        this.type = type;
    }

    public IInputKey getType() {
        return type;
    }

    @Override
    public Set<Expression> getDependingExpressions() {
        return Sets.newHashSet();
    }

    @Override
    public String toString() {
        return "TypeConstraint [expression=" + expression + ", type=" + type + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result+super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TypeJudgement other = (TypeJudgement) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return super.equals(obj);
    }
    
    
}