/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.communication;

import java.util.List;
import java.util.Map;

import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpointHandler;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelElement;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;

public interface IDebuggerHostAgent {
    //Send messages
    public void sendStepMessage();
    public void sendContinueMessage();
    
    public void sendNextActivationMessage(ActivationTrace activation);

    public void sendAddBreakpointMessage(ITransformationBreakpointHandler breakpoint);
    public void sendRemoveBreakpointMessage(ITransformationBreakpointHandler breakpoint);
    public void sendDisableBreakpointMessage(ITransformationBreakpointHandler breakpoint);
    public void sendEnableBreakpointMessage(ITransformationBreakpointHandler breakpoint);
    
    public void sendDisconnectMessage();
    
    //Listen to changes
    public void registerDebuggerHostAgentListener(IDebuggerHostAgentListener listener);
    public void unRegisterDebuggerHostAgentListener(IDebuggerHostAgentListener listener);
    
    public List<TransformationModelElement> getRootElements();
    public Map<String, List<TransformationModelElement>> getChildren(TransformationModelElement parent);
    public Map<String, List<TransformationModelElement>> getCrossReferences(TransformationModelElement parent);
    
}
