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
package org.eclipse.viatra.transformation.debug.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.internal.core.BreakpointManager;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.transformation.evm.api.Activation;

/**
 * Class that can be used to specify breakpoint rules via query specifications. It is mainly used by 
 * the VIATRA {@link org.eclipse.viatra.transformation.debug.TransformationDebugListener}  class.
 * 
 * @author Peter Lunk
 *
 */
@SuppressWarnings("restriction")
public class ConditionalTransformationBreakpoint extends Breakpoint implements ITransformationBreakpoint{
    private ViatraQueryEngine engine;
    private IQuerySpecification<?> spec;
    private int numberOfMatches;
    private boolean isActive = true;
    private boolean isRegistered = true;
    
    public ConditionalTransformationBreakpoint(ViatraQueryEngine engine, IQuerySpecification<?> spec, int numberOfMatches){
        super();
        this.engine = engine;
        this.spec = spec;
        this.numberOfMatches = numberOfMatches;
        try {
            ((BreakpointManager)DebugPlugin.getDefault().getBreakpointManager()).addBreakpoint(this);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean shouldBreak(Activation<?> a) {
        try {
            return engine.getMatcher(spec).getAllMatches().size() == numberOfMatches;
        } catch (ViatraQueryException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean isEnabled() throws CoreException {
        return isActive;
    }

    @Override
    public void setEnabled(boolean enabled) throws CoreException {
        this.isActive = enabled;
    }

    @Override
    public boolean isRegistered() throws CoreException {
        return isRegistered;
    }

    @Override
    public void setRegistered(boolean registered) throws CoreException {
        this.isRegistered = registered;
        
    }

    @Override
    public String getModelIdentifier() {
        return TransformationDebugElement.MODEL_ID;
    }

}
