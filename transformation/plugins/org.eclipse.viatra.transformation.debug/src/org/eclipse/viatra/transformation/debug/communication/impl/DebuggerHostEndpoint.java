package org.eclipse.viatra.transformation.debug.communication.impl;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra.transformation.debug.communication.DebuggerEndpointService;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgent;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgentListener;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostEndpoint;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;
import org.eclipse.viatra.transformation.debug.transformationtrace.transformationtrace.ActivationTrace;

import com.google.common.collect.Lists;

public class DebuggerHostEndpoint implements IDebuggerHostAgent, IDebuggerHostEndpoint{
    private String ID;
    private List<IDebuggerHostAgentListener> listeners = Lists.newArrayList();
   
    public DebuggerHostEndpoint(String ID){
        this.ID = ID;
    }
    
    @Override
    public void sendStepMessage() {
        DebuggerEndpointService.getInstance().getTargetEndpoint(ID).stepForward();
        
    }

    @Override
    public void sendContinueMessage() {
        DebuggerEndpointService.getInstance().getTargetEndpoint(ID).continueExecution();
        
    }

    @Override
    public void sendNextActivationMessage(ActivationTrace activation) {
        DebuggerEndpointService.getInstance().getTargetEndpoint(ID).setNextActivation(activation);
        
    }

    @Override
    public void sendAddBreakpointMessage(ITransformationBreakpoint breakpoint) {
        DebuggerEndpointService.getInstance().getTargetEndpoint(ID).addBreakpoint(breakpoint);
        
    }

    @Override
    public void sendRemoveBreakpointMessage(ITransformationBreakpoint breakpoint) {
        DebuggerEndpointService.getInstance().getTargetEndpoint(ID).removeBreakpoint(breakpoint);
        
    }

    @Override
    public void sendDisableBreakpointMessage(ITransformationBreakpoint breakpoint) {
        DebuggerEndpointService.getInstance().getTargetEndpoint(ID).disableBreakpoint(breakpoint);
        
    }

    @Override
    public void sendEnableBreakpointMessage(ITransformationBreakpoint breakpoint) {
        DebuggerEndpointService.getInstance().getTargetEndpoint(ID).enableBreakpoint(breakpoint);
        
    }

    @Override
    public void sendDisconnectMessage() {
        DebuggerEndpointService.getInstance().getTargetEndpoint(ID).disconnect();
        
    }

    @Override
    public void registerDebuggerHostAgentListener(IDebuggerHostAgentListener listener) {
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    @Override
    public void unRegisterDebuggerHostAgentListener(IDebuggerHostAgentListener listener) {
        if(listeners.contains(listener)){
            listeners.remove(listener);
        }
    }

    @Override
    public String getID() {
        return ID;
    }

    
    //HostEndpoint
    
    @Override
    public void transformationStateChanged(TransformationState state) {
        for (IDebuggerHostAgentListener listener : listeners) {
            listener.transformationStateChanged(state);
        }

    }

    @Override
    public void terminated() throws CoreException {
        for (IDebuggerHostAgentListener listener : listeners) {
            listener.terminated(this);
        }
        listeners.clear();
        DebuggerEndpointService.getInstance().unRegisterHostEndpoint(ID);
    }
    
}
