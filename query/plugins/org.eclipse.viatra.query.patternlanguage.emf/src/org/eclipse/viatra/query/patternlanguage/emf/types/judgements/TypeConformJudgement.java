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

import java.util.Set;

import org.eclipse.viatra.query.patternlanguage.emf.vql.Expression;

import com.google.common.collect.ImmutableSet;

/**
 * @author Zoltan Ujhelyi
 * @since 1.3
 *
 */
public class TypeConformJudgement extends AbstractTypeJudgement {

    private final Expression conformsTo;

    /**
     * @param expression
     */
    public TypeConformJudgement(Expression expression, Expression conformsTo) {
        super(expression);
        this.conformsTo = conformsTo;
    }

    public Expression getConformsTo() {
        return conformsTo;
    }

    @Override
    public Set<Expression> getDependingExpressions() {
        return ImmutableSet.of(conformsTo);
    }

    @Override
    public String toString() {
        return "TypeConformConstraint [expression=" + expression + ", type=" + conformsTo + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((conformsTo == null) ? 0 : conformsTo.hashCode());
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
        TypeConformJudgement other = (TypeConformJudgement) obj;
        if (conformsTo == null) {
            if (other.conformsTo != null)
                return false;
        } else if (!conformsTo.equals(other.conformsTo))
            return false;
        return super.equals(obj);
    }
    
}