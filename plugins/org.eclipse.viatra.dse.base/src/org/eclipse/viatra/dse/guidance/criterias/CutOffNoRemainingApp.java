/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.guidance.criterias;

import org.eclipse.viatra.dse.guidance.CriteriaContext;
import org.eclipse.viatra.dse.guidance.ICriteria;
import org.eclipse.viatra.dse.guidance.RuleInfo;

public class CutOffNoRemainingApp implements ICriteria {

    @Override
    public EvaluationResult evaluate(CriteriaContext context) {

        for (RuleInfo ruleInfo : context.getRuleInfos().values()) {
            if (ruleInfo.getRemainingApp() > 0) {
                return EvaluationResult.NONE;
            }
        }

        return EvaluationResult.CUT_OFF;
    }

}
