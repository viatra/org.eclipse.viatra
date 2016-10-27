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

/**
 * Class that can be used to specify breakpoint rules via query specifications. It is mainly used by the VIATRA
 * {@link org.eclipse.viatra.transformation.debug.TransformationDebugListener} class.
 * 
 * @author Peter Lunk
 *
 */
public class ConditionalTransformationBreakpoint extends Breakpoint implements ITransformationBreakpoint {
    private static final long serialVersionUID = 8541374098762126605L;
    private final ConditionalTransformationBreakpointHandler delegatedHandler;
        
    
    public ConditionalTransformationBreakpoint() {
        super();
        this.delegatedHandler  = new ConditionalTransformationBreakpointHandler("");
    }
    
    public ConditionalTransformationBreakpoint(String patternString) {
        super();
        this.delegatedHandler  = new ConditionalTransformationBreakpointHandler(patternString);
    }

    @Override
    public String getModelIdentifier() {
        return MODEL_ID;
    }

    @Override
    public String getMarkerIdentifier() {
        return CONDITIONAL;
    }

    @Override
    public boolean equals(Object item) {
        if (item instanceof ConditionalTransformationBreakpoint) {
            return ((ConditionalTransformationBreakpoint) item).delegatedHandler.equals(delegatedHandler);
        } else {
            return false;
        }

    }
    
    @Override
    public void setMarker(IMarker marker) throws CoreException {
        super.setMarker(marker);
        delegatedHandler.setEnabled(super.isEnabled());
        delegatedHandler.setStringRep("Conditional Transformation Breakpoint - "+marker.getResource().getName());
        if(!delegatedHandler.getPatternString().isEmpty()){
            marker.setAttribute("pattern", delegatedHandler.getPatternString());
        }
        delegatedHandler.setPatternString(marker.getAttribute("pattern", ""));
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
