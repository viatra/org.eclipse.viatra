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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.core.IType;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.debug.activator.TransformationDebugActivator;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgent;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgentListener;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.transformationstate.RuleActivation;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelProvider;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;

public class TransformationThread extends TransformationDebugElement implements IThread, IDebuggerHostAgentListener, IBreakpointListener {
    private static final String CONNECTING_DECORATOR_STRING = " connecting...";
    private String name = "Model Transformation Debugger Session";
    
    
    private boolean stepping = false;
    private boolean suspended = true;
    private boolean terminated = false;
    private boolean connecting = false;
   
    
    private final TransformationModelProvider modelProvider;
    
    private IType transformationClass;
    private TransformationState state;
    private IDebuggerHostAgent agent;
    
    private IStackFrame[] frames;
    
    protected TransformationThread(IDebuggerHostAgent agent, TransformationDebugTarget target, IType transformationClass) {
        super(target);
        frames = new IStackFrame[0];
        modelProvider = new TransformationModelProvider(agent);
        this.transformationClass = Objects.requireNonNull(transformationClass, "Transformation Class must not be null.");;
        this.agent = agent;
        //Register breakpoint listener
        DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);        
        //Register host agent listener
        agent.registerDebuggerHostAgentListener(this);
        //get initial breakpoints from the manager
        IBreakpoint[] transfBreakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(MODEL_ID);
        for (IBreakpoint iBreakpoint : transfBreakpoints) {
            breakpointAdded(iBreakpoint);
        }
        
        try {
            stepOver();
        } catch (DebugException e) {
            ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
        }
        
        
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
        
        startConnecting();
        agent.sendContinueMessage();
        endConnecting();
        
        fireResumeEvent(DebugEvent.CLIENT_REQUEST);
        
    }

    @Override
    public void suspend() throws DebugException {
        throw new UnsupportedOperationException("Suspend is not supported");
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
        throw new UnsupportedOperationException("Step into operation is not supported");
    }

    @Override
    public void stepOver() throws DebugException {
        stepping = true;
        
        startConnecting();
        agent.sendStepMessage();
        endConnecting();

        fireResumeEvent(DebugEvent.STEP_OVER);
    }

    @Override
    public void stepReturn() throws DebugException {
        throw new UnsupportedOperationException("Step return operation is not supported");
    }

    @Override
    public boolean canTerminate() {
        return !terminated;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public void terminate() throws DebugException {
        startConnecting();
        agent.sendDisconnectMessage();
        endConnecting();
    }

    @Override
    public IStackFrame[] getStackFrames() throws DebugException {
        return frames;
    }
    
    public TransformationModelProvider getModelProvider() {
        return modelProvider;
    }

    @Override
    public boolean hasStackFrames() throws DebugException {
        return getStackFrames().length > 0;
    }

    @Override
    public int getPriority() throws DebugException {
        return 0;
    }

    @Override
    public IStackFrame getTopStackFrame() throws DebugException {
        IStackFrame[] stackFrames = getStackFrames();
        if (stackFrames.length > 0) {
            return stackFrames[frames.length-1];
        }
        throw new DebugException(new Status(IStatus.ERROR, TransformationDebugActivator.PLUGIN_ID, "No transformation rules detected"));
    }

    @Override
    public String getName() throws DebugException {
        return (connecting) ? name+CONNECTING_DECORATOR_STRING : name;
    }

    @Override
    public IBreakpoint[] getBreakpoints() {
        return DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(MODEL_ID);
    }



    @Override
    public void terminated(IDebuggerHostAgent agent){
        terminated = true;
        stepping = false;
        suspended = false;
        frames = new IStackFrame[0];
        
        DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
        TransformationThreadFactory.getInstance().deleteTransformationThread(this);

        ((TransformationDebugTarget)getDebugTarget()).requestTermination();
        fireTerminateEvent();
    }


    // OWN
    
    public IType getTransformationType() {
        return transformationClass;
    }
    
    protected void setStepping(boolean stepping) {
        this.stepping = stepping;
    }

    //Manual guidance
    public void setNextActivation(RuleActivation act){
        startConnecting();
        agent.sendNextActivationMessage(act.getTrace());
        endConnecting();
    }

    private void endConnecting() {
        connecting = false;
    }

    private void startConnecting() {
        connecting = true;
    }
        
    //BreakpointListener
    
    @Override
    public void breakpointAdded(IBreakpoint breakpoint) {
        if(breakpoint instanceof ITransformationBreakpoint){
            startConnecting();
            agent.sendAddBreakpointMessage(((ITransformationBreakpoint) breakpoint).getHandler());  
            endConnecting();
        }
    }

    @Override
    public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
        if(breakpoint instanceof ITransformationBreakpoint){
            startConnecting();
            agent.sendRemoveBreakpointMessage(((ITransformationBreakpoint) breakpoint).getHandler());
            endConnecting();
        }
    }

    @Override
    public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
        if(breakpoint instanceof ITransformationBreakpoint){
            try {
                startConnecting();
                if(breakpoint.isEnabled()){
                    agent.sendEnableBreakpointMessage(((ITransformationBreakpoint) breakpoint).getHandler());
                }else{
                    agent.sendDisableBreakpointMessage(((ITransformationBreakpoint) breakpoint).getHandler());
                }
                endConnecting();
            } catch (CoreException e1) {
                ViatraQueryLoggingUtil.getDefaultLogger().error(e1.getMessage(), e1);
            }
        }
    }


    @Override
    public void transformationStateChanged(TransformationState state) {
        List<TransformationStackFrame> stackFrames = new ArrayList<>();
        
        if (state != null) {
            Deque<RuleActivation> activationStack = new ArrayDeque<>();
            activationStack.addAll(state.getActivationStack());
            while(!activationStack.isEmpty()){
                try {
                    stackFrames.add(new TransformationStackFrame(this, activationStack.pop(), modelProvider));
                } catch (EmptyStackException e) {
                    ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage());
                }
            }
        }
        this.frames = stackFrames.toArray(new TransformationStackFrame[stackFrames.size()]);
        
        suspended = true;
        fireSuspendEvent(DebugEvent.BREAKPOINT);
        this.state = state;
        this.name = (state == null) ? "" : state.getID();
    }
    
    public TransformationState getTransformationState() {
        return state;
    }
    
    public IDebuggerHostAgent getHostAgent() {
        return agent;
    }
}


