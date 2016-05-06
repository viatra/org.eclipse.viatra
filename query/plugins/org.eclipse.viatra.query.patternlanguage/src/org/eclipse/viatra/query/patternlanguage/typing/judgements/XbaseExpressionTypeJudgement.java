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
package org.eclipse.viatra.query.patternlanguage.typing.judgements;

import java.util.List;
import java.util.Set;

import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Expression;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.typesystem.IBatchTypeResolver;
import org.eclipse.xtext.xbase.typesystem.references.LightweightTypeReference;
import org.eclipse.xtext.xbase.typesystem.references.UnknownTypeReference;

import com.google.common.collect.Sets;

/**
 * @author Zoltan Ujhelyi
 * @since 1.3
 */
public class XbaseExpressionTypeJudgement extends AbstractTypeJudgement {

    private XExpression xExpression;
    private IBatchTypeResolver xbaseResolver;

    public XbaseExpressionTypeJudgement(Expression expression, XExpression xExpression,
            IBatchTypeResolver xbaseResolver) {
        super(expression);
        this.xExpression = xExpression;
        this.xbaseResolver = xbaseResolver;
    }

    @Override
    public Set<Expression> getDependingExpressions() {
        final List<Variable> usedVariables = CorePatternLanguageHelper.getUsedVariables(xExpression, EcoreUtil2
                .getContainerOfType(expression, PatternBody.class).getVariables());
        return Sets.<Expression> newHashSet(usedVariables);
    }

    public IInputKey getExpressionType() {
        LightweightTypeReference expressionType = xbaseResolver.resolveTypes(xExpression).getReturnType(xExpression);
        
        if (expressionType == null) {
             return new JavaTransitiveInstancesKey(Object.class);
        } else if (expressionType instanceof UnknownTypeReference) {
            return new JavaTransitiveInstancesKey(Object.class);
        } else {
             return new JavaTransitiveInstancesKey(expressionType.getWrapperTypeIfPrimitive().getJavaIdentifier());
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((xExpression == null) ? 0 : xExpression.hashCode());
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
        XbaseExpressionTypeJudgement other = (XbaseExpressionTypeJudgement) obj;
        if (xExpression == null) {
            if (other.xExpression != null)
                return false;
        } else if (!xExpression.equals(other.xExpression))
            return false;
        return super.equals(obj);
    }
    
}