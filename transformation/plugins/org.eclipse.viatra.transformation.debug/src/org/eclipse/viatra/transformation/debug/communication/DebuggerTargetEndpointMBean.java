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
package org.eclipse.viatra.transformation.debug.communication;

import java.util.List;
import java.util.Map;

import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelElement;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;

public interface DebuggerTargetEndpointMBean {
    public void stepForward();
    public void continueExecution();
    
    public void setNextActivation(ActivationTrace activation);

    public void addBreakpoint(ITransformationBreakpoint breakpoint);
    public void removeBreakpoint(ITransformationBreakpoint breakpoint);
    public void disableBreakpoint(ITransformationBreakpoint breakpoint);
    public void enableBreakpoint(ITransformationBreakpoint breakpoint);
    
    public void disconnect();
    
    public String getID();
    
    public List<TransformationModelElement> getRootElements();
    public Map<String, List<TransformationModelElement>> getChildren(TransformationModelElement parent);
    public Map<String, List<TransformationModelElement>> getCrossReferences(TransformationModelElement parent);
    
    
}
