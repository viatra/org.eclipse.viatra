package org.eclipse.viatra.transformation.debug.communication;

import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.transformationtrace.transformationtrace.ActivationTrace;

public interface IDebuggerHostAgent {
    //Send messages
    public void sendStepMessage();
    public void sendContinueMessage();
    
    public void sendNextActivationMessage(ActivationTrace activation);

    public void sendAddBreakpointMessage(ITransformationBreakpoint breakpoint);
    public void sendRemoveBreakpointMessage(ITransformationBreakpoint breakpoint);
    public void sendDisableBreakpointMessage(ITransformationBreakpoint breakpoint);
    public void sendEnableBreakpointMessage(ITransformationBreakpoint breakpoint);
    
    public void sendDisconnectMessage();
    
    //Listen to changes
    public void registerDebuggerHostAgentListener(IDebuggerHostAgentListener listener);
    public void unRegisterDebuggerHostAgentListener(IDebuggerHostAgentListener listener);
    
}
