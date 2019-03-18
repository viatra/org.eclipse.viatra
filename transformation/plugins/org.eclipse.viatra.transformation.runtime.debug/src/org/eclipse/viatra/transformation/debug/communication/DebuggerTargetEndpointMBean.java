/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.communication;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.management.InstanceNotFoundException;

import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpointHandler;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelElement;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;

public interface DebuggerTargetEndpointMBean {
    public void stepForward() throws InstanceNotFoundException, IOException;
    public void continueExecution() throws InstanceNotFoundException, IOException;
    
    public void setNextActivation(ActivationTrace activation) throws InstanceNotFoundException, IOException;

    public void addBreakpoint(ITransformationBreakpointHandler breakpoint) throws InstanceNotFoundException, IOException, ViatraDebuggerException;
    public void removeBreakpoint(ITransformationBreakpointHandler breakpoint) throws InstanceNotFoundException, IOException, ViatraDebuggerException;
    public void disableBreakpoint(ITransformationBreakpointHandler breakpoint) throws InstanceNotFoundException, IOException, ViatraDebuggerException;
    public void enableBreakpoint(ITransformationBreakpointHandler breakpoint) throws InstanceNotFoundException, IOException, ViatraDebuggerException;
    
    public void disconnect() throws InstanceNotFoundException, IOException;
    
    public String getID() throws InstanceNotFoundException, IOException;
    
    public List<TransformationModelElement> getRootElements() throws InstanceNotFoundException, IOException;
    public Map<String, List<TransformationModelElement>> getChildren(TransformationModelElement parent) throws InstanceNotFoundException, IOException;
    public Map<String, List<TransformationModelElement>> getCrossReferences(TransformationModelElement parent) throws InstanceNotFoundException, IOException;
    
    
}
