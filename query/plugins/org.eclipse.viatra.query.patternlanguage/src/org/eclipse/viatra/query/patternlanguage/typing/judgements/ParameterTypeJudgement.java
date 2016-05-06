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

import org.eclipse.viatra.query.patternlanguage.patternLanguage.Expression;

/**
 * @author Zoltan Ujhelyi
 * @since 1.3
 *
 */
public class ParameterTypeJudgement extends TypeConformJudgement {

    /**
     * @param expression
     * @param conformsTo
     */
    public ParameterTypeJudgement(Expression expression, Expression conformsTo) {
        super(expression, conformsTo);
    }

}
