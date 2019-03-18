/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.model.breakpoint;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;

public class ActivationBreakpoint extends Breakpoint implements ITransformationBreakpoint{
    private static final long serialVersionUID = -2412809220911146065L;
    private final ActivationBreakpointHandler delegatedHandler;
    
    public ActivationBreakpoint(ActivationTrace trace){
        super();
        this.delegatedHandler = new ActivationBreakpointHandler(trace);
    }
       
    @Override
    public String getModelIdentifier() {
        return MODEL_ID;
    }

    @Override
    public String getMarkerIdentifier() {
        return NON_PERSISTENT;
    }
    
    @Override
    public void setMarker(IMarker marker) throws CoreException {
        super.setMarker(marker);
        delegatedHandler.setEnabled(super.isEnabled());
    }
    
    @Override
    public boolean equals(Object item) {
        if(item instanceof ActivationBreakpoint){
            return ((ActivationBreakpoint) item).delegatedHandler.equals(delegatedHandler);
        }else{
            return false;
        }
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
        delegatedHandler.setEnabled(enabled);
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
