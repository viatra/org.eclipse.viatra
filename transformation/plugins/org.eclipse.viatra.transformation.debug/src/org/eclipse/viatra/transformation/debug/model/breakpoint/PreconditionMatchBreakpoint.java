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

import java.util.List;
import java.util.Set;

import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.debug.model.TransformationDebugElement;
import org.eclipse.viatra.transformation.evm.api.Activation;

import com.google.common.collect.Sets;

public class PreconditionMatchBreakpoint extends Breakpoint implements ITransformationBreakpoint{
    private Set<Object> breakpointObjects = Sets.newHashSet();
    
    public PreconditionMatchBreakpoint(Set<Object> breakpointObjects) {
        super();
        this.breakpointObjects = breakpointObjects;
    }

    @Override
    public String getModelIdentifier() {
        return TransformationDebugElement.MODEL_ID;
    }

    @Override
    public boolean shouldBreak(Activation<?> a) {
        Object atom = a.getAtom();
        if (atom instanceof IPatternMatch) {
            List<String> parameterNames = ((IPatternMatch) atom).parameterNames();
            for (String string : parameterNames) {
                Object object = ((IPatternMatch) atom).get(string);
                for (Object breakpointObject : breakpointObjects) {
                    if(breakpointObject.equals(object)){
                        return true;
                    }
                }
                
            }
        }
        return false;
    }

    @Override
    public String getMarkerIdentifier() {
        return NON_PERSISTENT;
    }
    
    @Override
    public boolean equals(Object item) {
        if(item instanceof PreconditionMatchBreakpoint){
            return ((PreconditionMatchBreakpoint) item).breakpointObjects.equals(breakpointObjects);
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return breakpointObjects.hashCode();
    }
    
}
