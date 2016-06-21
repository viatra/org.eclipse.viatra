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
package org.eclipse.viatra.transformation.debug.model.breakpoint;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.viatra.transformation.debug.model.TransformationDebugElement;
import org.eclipse.viatra.transformation.evm.api.Activation;

public class RuleBreakpoint extends Breakpoint implements ITransformationBreakpoint {
    private String ruleId;

    public RuleBreakpoint() {
    }

    public RuleBreakpoint(String ruleId) {
        super();
        this.ruleId = ruleId;
    }

    @Override
    public String getModelIdentifier() {
        return TransformationDebugElement.MODEL_ID;
    }

    @Override
    public boolean shouldBreak(Activation<?> a) {
        return a.getInstance().getSpecification().getName().equals(ruleId);
    }

    @Override
    public String getMarkerIdentifier() {
        return RULE;
    }

    @Override
    public boolean equals(Object item) {
        if (item instanceof RuleBreakpoint) {
            return ((RuleBreakpoint) item).getRuleId().equals(getRuleId());
        } else {
            return false;
        }
    }

    protected String getRuleId() {
        return ruleId;
    }

    @Override
    public void setMarker(IMarker marker) throws CoreException {
        super.setMarker(marker);
        if (ruleId != null) {
            marker.setAttribute("content", ruleId);
        } else {
            ruleId = marker.getAttribute("content", ruleId);
        }
    }
    
    @Override
    public String toString() {
        return "Rule Transformation Breakpoint - Rule name: "+ruleId;
    }
    
    @Override
    public int hashCode() {
        return getRuleId().hashCode();
    }
}