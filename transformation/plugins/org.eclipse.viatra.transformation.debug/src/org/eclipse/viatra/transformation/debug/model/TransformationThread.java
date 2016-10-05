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

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Stack;

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
import org.eclipse.debug.ui.actions.ExportBreakpointsOperation;
import org.eclipse.debug.ui.actions.ImportBreakpointsOperation;
import org.eclipse.jdt.core.IType;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.debug.activator.TransformationDebugActivator;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgent;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgentListener;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.debug.model.transformationstate.RuleActivation;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationModelProvider;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;
import org.eclipse.viatra.transformation.debug.util.BreakpointCacheUtil;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class TransformationThread extends TransformationDebugElement implements IThread, IDebuggerHostAgentListener, IBreakpointListener {
    private boolean stepping = false;
    private boolean suspended = true;
    private boolean terminated = false;
    
    private List<ITransformationBreakpoint> breakpoints;
    private final TransformationModelProvider modelProvider;
    
    private String name;

    private IType transformationClass;
    private TransformationState state;
    private IDebuggerHostAgent agent;
    
    protected TransformationThread(String name, IDebuggerHostAgent agent, TransformationDebugTarget target, IType transformationClass) {
        super(target);
        Preconditions.checkNotNull(transformationClass, "Transformation Class must not be null.");
        breakpoints = Lists.newArrayList();
        modelProvider = new TransformationModelProvider(agent);
        this.transformationClass = transformationClass;
        this.name = name;
        this.agent = agent;
        //Register breakpoint listener
        DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);        
        if(BreakpointCacheUtil.breakpointCacheExists()){
            ImportBreakpointsOperation operation = new ImportBreakpointsOperation(
                    BreakpointCacheUtil.getBreakpointCacheLocation().trim(), 
                    false, 
                    false);
            try {
                operation.run(null);
            } catch (InvocationTargetException e) {
                ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
            }
        }
        //Register host agent listener
        agent.registerDebuggerHostAgentListener(this);
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
        agent.sendContinueMessage();
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
        agent.sendStepMessage();
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
            List<TransformationStackFrame> frames = Lists.newArrayList();
            
            if (state != null) {
                Stack<RuleActivation> activationStack = new Stack<RuleActivation>();
                activationStack.addAll(state.getActivationStack());
                while(!activationStack.isEmpty()){
                    try {
                        frames.add(new TransformationStackFrame(this, activationStack.pop(), modelProvider));
                    } catch (Exception e) {
                        throw new DebugException(new Status(IStatus.ERROR, TransformationDebugActivator.PLUGIN_ID,
                                "No transformation rules detected"));
                    }
                }
            }
            return frames.toArray(new TransformationStackFrame[frames.size()]); 
        } else {
            return new IStackFrame[0];
        }
    }
    
    public TransformationModelProvider getModelProvider() {
        return modelProvider;
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
        throw new DebugException(new Status(IStatus.ERROR, TransformationDebugActivator.PLUGIN_ID, "No transformation rules detected"));
    }

    @Override
    public String getName() throws DebugException {
        return name;
    }

    @Override
    public IBreakpoint[] getBreakpoints() {
        return breakpoints.toArray(new IBreakpoint[breakpoints.size()]);
    }



    @Override
    public void terminated(IDebuggerHostAgent agent) throws CoreException, DebugException {
        terminated = true;
        DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
        TransformationThreadFactory.getInstance().deleteTransformationThread(this);
        fireTerminateEvent();
        try {
            ((TransformationDebugTarget) getDebugTarget()).requestTermination();
        } catch (DebugException e) {
            throw e;
        }       
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
        agent.sendNextActivationMessage(act.getTrace());
    }
        
    //BreakpointListener
    
    @Override
    public void breakpointAdded(IBreakpoint breakpoint) {
        if(breakpoint instanceof ITransformationBreakpoint){
            breakpoints.add((ITransformationBreakpoint) breakpoint);
            agent.sendAddBreakpointMessage((ITransformationBreakpoint) breakpoint);           
            ExportBreakpointsOperation operation = new ExportBreakpointsOperation(
                    BreakpointCacheUtil.filterBreakpoints(getBreakpoints()),
                    BreakpointCacheUtil.getBreakpointCacheLocation());
            
            try {
                operation.run(null);
            } catch (InvocationTargetException e) {
                ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
        if(breakpoint instanceof ITransformationBreakpoint){
            breakpoints.remove(breakpoint);
            agent.sendRemoveBreakpointMessage((ITransformationBreakpoint) breakpoint);
            
            ExportBreakpointsOperation operation = new ExportBreakpointsOperation(
                    BreakpointCacheUtil.filterBreakpoints(getBreakpoints()),
                    BreakpointCacheUtil.getBreakpointCacheLocation());
            
            try {
                operation.run(null);
            } catch (InvocationTargetException e) {
                ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
        if(breakpoint instanceof ITransformationBreakpoint){
            try {
                if(breakpoint.isEnabled()){
                    agent.sendEnableBreakpointMessage((ITransformationBreakpoint) breakpoint);
                }else{
                    agent.sendDisableBreakpointMessage((ITransformationBreakpoint) breakpoint);
                }
            } catch (CoreException e1) {
                ViatraQueryLoggingUtil.getDefaultLogger().error(e1.getMessage(), e1);
            }
            
            
            ExportBreakpointsOperation operation = new ExportBreakpointsOperation(
                    BreakpointCacheUtil.filterBreakpoints(getBreakpoints()),
                    BreakpointCacheUtil.getBreakpointCacheLocation());
            
            try {
                operation.run(null);
            } catch (InvocationTargetException e) {
                ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
            }
        }
    }


    @Override
    public void transformationStateChanged(TransformationState state) {
        suspended = true;
        fireSuspendEvent(DebugEvent.BREAKPOINT);
        this.state = state;
    }
    
    public TransformationState getTransformationState() {
        return state;
    }
    
    public IDebuggerHostAgent getHostAgent() {
        return agent;
    }
        
  

}


