/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.model.breakpoint;

import org.eclipse.viatra.transformation.evm.api.Activation;

public class RuleBreakpointHandler implements ITransformationBreakpointHandler {
    private static final long serialVersionUID = 7675110480956658182L;
    private String ruleId;
    private boolean enabled;
    
    public RuleBreakpointHandler(String ruleId) {
        super();
        this.ruleId = ruleId;
    }

    @Override
    public boolean shouldBreak(Activation<?> a) {
        return a.getInstance().getSpecification().getName().equals(ruleId);
    }

    @Override
    public boolean equals(Object item) {
        if (item instanceof RuleBreakpointHandler) {
            return ((RuleBreakpointHandler) item).getRuleId().equals(getRuleId());
        } else {
            return false;
        }
    }

    public String getRuleId() {
        return ruleId;
    }
    
    @Override
    public String toString() {
        return "Rule Transformation Breakpoint - Rule name: "+ruleId;
    }
    
    @Override
    public int hashCode() {
        return getRuleId().hashCode();
    }
    
    @Override
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled(){
        return enabled;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
}
