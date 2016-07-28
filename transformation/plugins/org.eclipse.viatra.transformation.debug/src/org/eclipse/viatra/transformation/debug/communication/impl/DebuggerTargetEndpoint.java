package org.eclipse.viatra.transformation.debug.communication.impl;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra.transformation.debug.DebuggerActions;
import org.eclipse.viatra.transformation.debug.TransformationDebugger;
import org.eclipse.viatra.transformation.debug.communication.DebuggerEndpointService;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerTargetAgent;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerTargetEndpoint;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationStateBuilder;
import org.eclipse.viatra.transformation.debug.transformationtrace.transformationtrace.ActivationTrace;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.xtext.xbase.lib.Pair;

public class DebuggerTargetEndpoint implements IDebuggerTargetAgent, IDebuggerTargetEndpoint{
    private String ID;
    private TransformationDebugger debugger;
    
    //TargetEndpoint
    public DebuggerTargetEndpoint(String ID, TransformationDebugger debugger){
        builder.setID(ID);
        this.ID = ID;
        this.debugger  = debugger;
        DebuggerEndpointService.getInstance().registerTargetEndpoint(ID, this);
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void stepForward() {
       debugger.setDebuggerAction(DebuggerActions.Step);
    }

    @Override
    public void continueExecution() {
        debugger.setDebuggerAction(DebuggerActions.Continue);
    }

    @Override
    public void setNextActivation(ActivationTrace activation) {
        debugger.setNextActivation(activation);
    }

    @Override
    public void addBreakpoint(ITransformationBreakpoint breakpoint) {
        debugger.addBreakpoint(breakpoint);
    }

    @Override
    public void removeBreakpoint(ITransformationBreakpoint breakpoint) {
        debugger.removeBreakpoint(breakpoint);
        
    }

    @Override
    public void disableBreakpoint(ITransformationBreakpoint breakpoint) {
        debugger.disableBreakpoint(breakpoint);
        
    }

    @Override
    public void enableBreakpoint(ITransformationBreakpoint breakpoint) {
       debugger.enableBreakpoint(breakpoint);
    }
    
    @Override
    public void disconnect() {
        debugger.disconnect();
    }

    
    
    //TargetAgent
    TransformationStateBuilder builder = new TransformationStateBuilder();
    
    @Override
    public void suspended() {
        builder.setBreakpointHit(null);
        DebuggerEndpointService.getInstance().getHostEndpoint(ID).transformationStateChanged(builder.build());
    }

    @Override
    public void breakpointHit(ITransformationBreakpoint breakpoint) {
        builder.setBreakpointHit(breakpoint);
        DebuggerEndpointService.getInstance().getHostEndpoint(ID).transformationStateChanged(builder.build());
    }

    @Override
    public void terminated() throws CoreException {
        DebuggerEndpointService.getInstance().getHostEndpoint(ID).terminated();
        DebuggerEndpointService.getInstance().unRegisterTargetEndpoint(ID);
        
    }

    @Override
    public void conflictSetChanged(Set<Activation<?>> nextActivations, Set<Activation<?>> conflictingActivations) {
        builder.setActivations(conflictingActivations, nextActivations);
        
    }

    @Override
    public void activationFired(Activation<?> activation) {
        builder.activationFired(activation);
    }

    @Override
    public void activationFiring(Activation<?> activation) {
        builder.activationFiring(activation);
    }

    @Override
    public void addedRule(RuleSpecification<?> specification, EventFilter<?> filter) {
        builder.addRule(new Pair<RuleSpecification<?>, EventFilter<?>>(specification, filter));
        
    }

    @Override
    public void removedRule(RuleSpecification<?> specification, EventFilter<?> filter) {
        builder.removeRule(new Pair<RuleSpecification<?>, EventFilter<?>>(specification, filter));
    }

    @Override
    public void nextActivationChanged(Activation<?> activation) {
        builder.nextActivationChanged(activation);
        DebuggerEndpointService.getInstance().getHostEndpoint(ID).transformationStateChanged(builder.build());
    }


    
   
    
    
    

}
