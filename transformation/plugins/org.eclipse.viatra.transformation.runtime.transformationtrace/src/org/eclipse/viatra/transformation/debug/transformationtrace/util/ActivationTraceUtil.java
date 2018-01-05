/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.transformationtrace.util;

import java.util.List;
import java.util.Objects;

import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.RuleParameterTrace;

public class ActivationTraceUtil {

    public static boolean compareActivationCodes(final ActivationTrace a1, final ActivationTrace a2) {
        List<RuleParameterTrace> a1RuleParameterTraces = a1.getRuleParameterTraces();
        List<RuleParameterTrace> a2ruleParameterTraces = a2.getRuleParameterTraces();
        if (a1RuleParameterTraces.size() != a2ruleParameterTraces.size()) {
            return false;
        }
        for (int i = 0; i < a1RuleParameterTraces.size(); i++) {
            RuleParameterTrace a1Trace = a1RuleParameterTraces.get(i);
            RuleParameterTrace a2Trace = a2ruleParameterTraces.get(i);
            if (((!Objects.equals(a1Trace.getParameterName(), a2Trace.getParameterName()))
                    || (!Objects.equals(a1Trace.getObjectId(), a2Trace.getObjectId())))) {
                return false;
            }
        }
        return true;
    }
}
