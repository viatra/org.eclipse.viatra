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

import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.viatra.transformation.debug.activationcoder.DefaultActivationCoder;
import org.eclipse.viatra.transformation.debug.model.TransformationDebugElement;
import org.eclipse.viatra.transformation.debug.transformationtrace.transformationtrace.ActivationTrace;
import org.eclipse.viatra.transformation.debug.transformationtrace.util.ActivationTraceUtil;
import org.eclipse.viatra.transformation.evm.api.Activation;

/**
 * Class that can be used to identify individual EVM rule activations. It is mainly used by the VIATRA
 * {@link org.eclipse.viatra.transformation.debug.TransformationDebugListener} class.
 * 
 * @author Peter Lunk
 *
 */
public class TransformationBreakpoint extends Breakpoint implements ITransformationBreakpoint{
    private DefaultActivationCoder activationCoder;
    private ActivationTrace trace;
    private Activation<?> activation;

    public ActivationTrace getTrace() {
        return trace;
    }
    
    public TransformationBreakpoint(Activation<?> activation){
        super();
        this.activation = activation;
        activationCoder = new DefaultActivationCoder();
        trace = activationCoder.createActivationCode(activation);
    }
    
    /**
     * Checks if the given EVM rule activation matches the specified transformation breakpoint.
     * 
     * @param a
     * @return
     */
    @Override
    public boolean shouldBreak(Activation<?> a) {
        return ActivationTraceUtil.compareActivationCodes(trace, activationCoder.createActivationCode(a));
    }
   
    public Activation<?> getActivation(){
        return activation;
    }

    @Override
    public String getModelIdentifier() {
        return TransformationDebugElement.MODEL_ID;
    }

    @Override
    public String getMarkerIdentifier() {
        return NON_PERSISTENT;
    }
    
    @Override
    public boolean equals(Object item) {
        if(item instanceof TransformationBreakpoint){
            return ((TransformationBreakpoint) item).getActivation().equals(activation);
        }else{
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return activation.hashCode();
    }
}
