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

import java.util.Set;

import org.eclipse.viatra.query.patternlanguage.patternLanguage.Expression;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;

import com.google.common.collect.ImmutableSet;

/**
 * @author Zoltan Ujhelyi
 * @since 1.3
 *
 */
public class ParameterTypeJudgement extends TypeConformJudgement {

    /**
     * @since 1.4
     */
    public ParameterTypeJudgement(Expression expression, Variable conformsTo) {
        super(expression, conformsTo);
    }
    
    @Override
    public Set<Expression> getDependingExpressions() {
        return ImmutableSet.of();
    }

}
