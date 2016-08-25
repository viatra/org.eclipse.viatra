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
package org.eclipse.viatra.transformation.debug.transformationtrace.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class ActivationTrace implements Serializable{
    private static final long serialVersionUID = 4482651679137706911L;
    private final String ruleName;
    private List<RuleParameterTrace> ruleParameterTraces = Lists.newArrayList();
    
    public ActivationTrace(String ruleName) {
        super();
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public List<RuleParameterTrace> getRuleParameterTraces() {
        return ruleParameterTraces;
    }
}
