/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.model.breakpoint;

import org.eclipse.viatra.transformation.debug.activationcoder.DefaultActivationCoder;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;
import org.eclipse.viatra.transformation.debug.transformationtrace.util.ActivationTraceUtil;
import org.eclipse.viatra.transformation.evm.api.Activation;

public class ActivationBreakpointHandler implements ITransformationBreakpointHandler{
    private static final long serialVersionUID = -3411754558480479805L;
    private ActivationTrace trace;
    private boolean enabled = true;

    public ActivationTrace getTrace() {
        return trace;
    }
    
    public ActivationBreakpointHandler(ActivationTrace trace){
        super();
        this.trace = trace;
    }
    
    /**
     * Checks if the given EVM rule activation matches the specified transformation breakpoint.
     */
    @Override
    public boolean shouldBreak(Activation<?> a) {
        return ActivationTraceUtil.compareActivationCodes(trace, new DefaultActivationCoder().createActivationCode(a));
    }
    
    @Override
    public boolean equals(Object item) {
        if(item instanceof ActivationBreakpointHandler){
            return ((ActivationBreakpointHandler) item).getTrace().equals(trace);
        }else{
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return trace.hashCode();
    }
    
    @Override
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled(){
        return enabled;
    }
}
