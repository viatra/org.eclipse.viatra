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

import org.eclipse.viatra.query.patternlanguage.emf.vql.Constraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Expression;
import org.eclipse.xtext.EcoreUtil2;

/**
 * @author Zoltan Ujhelyi
 * @since 1.3
 */
public abstract class AbstractTypeJudgement {

    final Constraint constraint;
    final Expression expression;

    public AbstractTypeJudgement(Expression expression) {
        super();
        this.expression = expression;
        this.constraint = EcoreUtil2.getContainerOfType(expression, Constraint.class);
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public Expression getExpression() {
        return expression;
    }

    /**
     * Returns a set of expressions whose type changes must trigger a re-evaluation of this constraint
     * 
     * @return a non-null, but possibly empty set of expressions
     */
    public abstract Set<Expression> getDependingExpressions();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((constraint == null) ? 0 : constraint.hashCode());
        result = prime * result + ((expression == null) ? 0 : expression.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractTypeJudgement other = (AbstractTypeJudgement) obj;
        if (constraint == null) {
            if (other.constraint != null)
                return false;
        } else if (!constraint.equals(other.constraint))
            return false;
        if (expression == null) {
            if (other.expression != null)
                return false;
        } else if (!expression.equals(other.expression))
            return false;
        return true;
    }
    
}