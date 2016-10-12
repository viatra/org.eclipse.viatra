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

public class RuleBreakpoint extends Breakpoint implements ITransformationBreakpoint {
    private static final long serialVersionUID = -7229317025288796666L;
    private final RuleBreakpointHandler delegatedHandler;
    
    public RuleBreakpoint() {
        super();
        delegatedHandler = new RuleBreakpointHandler("");
    }

    public RuleBreakpoint(String ruleId) {
        super();
        delegatedHandler = new RuleBreakpointHandler(ruleId);
    }

    @Override
    public String getModelIdentifier() {
        return MODEL_ID;
    }

    @Override
    public String getMarkerIdentifier() {
        return RULE;
    }

    @Override
    public boolean equals(Object item) {
        if (item instanceof RuleBreakpoint) {
            return ((RuleBreakpoint) item).delegatedHandler.equals(delegatedHandler);
        } else {
            return false;
        }
    }

    @Override
    public void setMarker(IMarker marker) throws CoreException {
        super.setMarker(marker);
        delegatedHandler.setEnabled(super.isEnabled());
        if (delegatedHandler.getRuleId() != "") {
            marker.setAttribute("content", delegatedHandler.getRuleId());
        }
        delegatedHandler.setRuleId(marker.getAttribute("content", ""));
    }
    
    @Override
    public String toString() {
        return delegatedHandler.toString();
    }
    
    @Override
    public int hashCode() {
        return delegatedHandler.hashCode();
    }
    
    @Override
    public void setEnabled(boolean enabled) throws CoreException {
        if(getMarker() != null){
            super.setEnabled(enabled); 
        }
        this.delegatedHandler.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() throws CoreException {
        if(getMarker() != null){
            return super.isEnabled(); 
        }
        return delegatedHandler.isEnabled();
    }

    @Override
    public ITransformationBreakpointHandler getHandler() {
        return delegatedHandler;
    }
}
