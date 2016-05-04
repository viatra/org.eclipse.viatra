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

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.core.IType;
import org.eclipse.viatra.transformation.debug.DebuggerActions;
import org.eclipse.viatra.transformation.debug.ITransformationDebugListener;
import org.eclipse.viatra.transformation.debug.TransformationDebugger;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.breakpoint.TransformationBreakpoint;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.Lists;

public class TransformationThread extends TransformationDebugElement implements IThread, ITransformationDebugListener {

    private List<ITransformationBreakpoint> breakpoints = Lists.newArrayList();

    private List<ITransformationStateListener> stateListeners = Lists.newArrayList();
    private List<Activation<?>> startedFiring = Lists.newArrayList();
    private TransformationState state;
    private TransformationDebugger debugger;
    private AdaptableEVM evm;

    private boolean stepping = false;
    private boolean suspended = true;
    private boolean terminated = false;
    private boolean initial = true;
    private IType transformationType;

    protected TransformationThread(TransformationDebugTarget target, TransformationDebugger debugger, AdaptableEVM evm, IType transformationType) {
        super(target);
        this.debugger = debugger;
        this.evm = evm;
        this.transformationType = transformationType;
        this.state = debugger.registerTransformationDebugListener(this);
    }

    @Override
    public boolean canResume() {
        return isSuspended();
    }

    @Override
    public boolean canSuspend() {
        return false;
    }

    @Override
    public boolean isSuspended() {
        return suspended;
    }

    @Override
    public void resume() throws DebugException {
        stepping = false;
        suspended = false;
        debugger.setDebuggerAction(DebuggerActions.Continue);
        state.clearNewActivations();
        fireResumeEvent(DebugEvent.CLIENT_REQUEST);
        
    }

    @Override
    public void suspend() throws DebugException {
    }

    @Override
    public boolean canStepInto() {
        return false;
    }

    @Override
    public boolean canStepOver() {
        return isSuspended();
    }

    @Override
    public boolean canStepReturn() {
        return false;
    }

    @Override
    public boolean isStepping() {
        return stepping;
    }

    @Override
    public void stepInto() throws DebugException {
    }

    @Override
    public void stepOver() throws DebugException {
        stepping = true;
        debugger.setDebuggerAction(DebuggerActions.Step);
        state.clearNewActivations();
        fireResumeEvent(DebugEvent.STEP_OVER);
    }

    @Override
    public void stepReturn() throws DebugException {
    }

    @Override
    public boolean canTerminate() {
        return getDebugTarget().canTerminate();
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public void terminate() throws DebugException {

    }

    @Override
    public IStackFrame[] getStackFrames() throws DebugException {
        if (isSuspended()) {
            if(initial){
                startedFiring.add(0, state.getNextActivation());
                initial = false;
            }
            
            List<TransformationStackFrame> frames = Lists.newArrayList();
            for (Activation<?> act : startedFiring) {
                frames.add(new TransformationStackFrame(this, act)); 
            }
            return frames.toArray(new TransformationStackFrame[frames.size()]); 
        } else {
            return new IStackFrame[0];
        }
    }

    @Override
    public boolean hasStackFrames() throws DebugException {
        return isSuspended();
    }

    @Override
    public int getPriority() throws DebugException {
        return 0;
    }

    @Override
    public IStackFrame getTopStackFrame() throws DebugException {
        IStackFrame[] frames = getStackFrames();
        if (frames.length > 0) {
            return frames[frames.length-1];
        }
        return null;
    }

    @Override
    public String getName() throws DebugException {
        return evm.getIdentifier();
    }

    @Override
    public IBreakpoint[] getBreakpoints() {
        if (breakpoints.isEmpty()) {
            return new IBreakpoint[0];
        }
        return breakpoints.toArray(new IBreakpoint[0]);
    }

    // ITransformationDebugListener
    @Override
    public void started() {
        try {
            state = new TransformationState(getName(), debugger.getEngine());
            fireCreationEvent();
            resume();
        } catch (DebugException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void suspended() {
        suspended = true;
        notifyListeners(state);
        fireSuspendEvent(DebugEvent.SUSPEND);
    }

    @Override
    public void breakpointHit(ITransformationBreakpoint breakpoint) {
        suspended = true;
        fireBreakpointHit(breakpoint);
        notifyListeners(state);
    }

    @Override
    public void terminated() {
        terminated = true;
        fireTerminateEvent();
        ((TransformationDebugTarget) getDebugTarget()).requestTermination();
        for (ITransformationStateListener listener : stateListeners) {
            listener.transformationStateDisposed(state, evm.getIdentifier());
        }
        dispose();
        
    }
    
    protected void dispose(){
        stateListeners.clear();
        breakpoints.clear();
        TransformationThreadFactory.getInstance().deleteTransformationThread(this); 
    }

    @Override
    public void activationFired(Activation<?> activation) {
        startedFiring.remove(activation);
    }

    @Override
    public void activationFiring(Activation<?> act) {
        startedFiring.add(act);
        state.activationFiring(act);
    }
    

    @Override
    public void conflictSetChanged(Set<Activation<?>> nextActivations, Set<Activation<?>> conflictingActivations) {
        state.updateActivations(nextActivations, conflictingActivations);
    }
    
    @Override
    public void addedRule(RuleSpecification<?> specification, EventFilter<?> filter) {
        state.ruleAdded(new Pair<RuleSpecification<?>, EventFilter<?>>(specification, filter));
        
    }

    @Override
    public void removedRule(RuleSpecification<?> specification, EventFilter<?> filter) {
        state.ruleRemoved(new Pair<RuleSpecification<?>, EventFilter<?>>(specification, filter));
    }

    // OWN
    
    
    public void toggleTransformationBreakPoint(ITransformationBreakpoint breakpoint) {
        ITransformationBreakpoint breakpointToRemove = null;
        for (ITransformationBreakpoint iTransformationBreakpoint : breakpoints) {
            if(iTransformationBreakpoint.equals(breakpoint)){
                breakpointToRemove = breakpoint;
            }
        }
        if(breakpointToRemove != null){
            removeTransformationBreakpoint(breakpoint);
        }else{
            addTransformationBreakpoint(breakpoint);
        }
    }

    public void removeTransformationBreakpoint(ITransformationBreakpoint breakpointToRemove) {
        if(breakpoints.contains(breakpointToRemove)){
            breakpoints.remove(breakpointToRemove);
            debugger.removeBreakpoint(breakpointToRemove);
            try {
                DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(breakpointToRemove, true);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    public void addTransformationBreakpoint(ITransformationBreakpoint breakpoint) {
        if(!breakpoints.contains(breakpoint)){
            breakpoints.add(breakpoint);
            try {
                breakpoint.setMarker(transformationType.getResource().createMarker(breakpoint.getMarkerIdentifier()));
                breakpoint.setEnabled(true);
                DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(breakpoint);
            } catch (CoreException e) {
                e.printStackTrace();
            }
            debugger.addBreakpoint(breakpoint);
        }
    }

    private void fireBreakpointHit(ITransformationBreakpoint breakpoint) {
        fireSuspendEvent(DebugEvent.BREAKPOINT);
    }
    
    protected void setStepping(boolean stepping) {
        this.stepping = stepping;
    }

    public void registerTransformationStateListener(ITransformationStateListener listener) {
        stateListeners.add(listener);
        notifyListeners(state);
    }

    public void unRegisterTransformationStateListener(ITransformationStateListener listener) {
        stateListeners.remove(listener);
    }

    private void notifyListeners(TransformationState state) {
        for (ITransformationStateListener listener : stateListeners) {
            listener.transformationStateChanged(state, evm.getIdentifier());
        }
    }
    //Manual guidance
    public void setNextActivation(Activation<?> act){
        startedFiring.remove(state.getNextActivation());
        startedFiring.add(act);
        debugger.setNextActivation(act);
        state.setNextActivation(act);
        suspended();
        
    }
    
        
    public boolean containsActivation(Activation<?> activation){
        return state.getConflictingActivations().contains(activation);
    }
    
    public AdaptableEVM getAdaptableEvm() {
        return evm;
    }
}
