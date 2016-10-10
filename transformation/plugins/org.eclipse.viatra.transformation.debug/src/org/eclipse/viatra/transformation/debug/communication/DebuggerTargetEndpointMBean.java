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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.management.InstanceNotFoundException;

import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelElement;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;

public interface DebuggerTargetEndpointMBean {
    public void stepForward() throws InstanceNotFoundException, IOException;
    public void continueExecution() throws InstanceNotFoundException, IOException;
    
    public void setNextActivation(ActivationTrace activation) throws InstanceNotFoundException, IOException;

    public void addBreakpoint(ITransformationBreakpoint breakpoint) throws InstanceNotFoundException, IOException;
    public void removeBreakpoint(ITransformationBreakpoint breakpoint) throws InstanceNotFoundException, IOException;
    public void disableBreakpoint(ITransformationBreakpoint breakpoint) throws InstanceNotFoundException, IOException;
    public void enableBreakpoint(ITransformationBreakpoint breakpoint) throws InstanceNotFoundException, IOException;
    
    public void disconnect() throws InstanceNotFoundException, IOException;
    
    public String getID() throws InstanceNotFoundException, IOException;
    
    public List<TransformationModelElement> getRootElements() throws InstanceNotFoundException, IOException;
    public Map<String, List<TransformationModelElement>> getChildren(TransformationModelElement parent) throws InstanceNotFoundException, IOException;
    public Map<String, List<TransformationModelElement>> getCrossReferences(TransformationModelElement parent) throws InstanceNotFoundException, IOException;
    
    
}
