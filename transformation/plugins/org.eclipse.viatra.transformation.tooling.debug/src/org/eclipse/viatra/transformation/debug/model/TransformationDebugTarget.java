/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.core.IType;
import org.eclipse.viatra.transformation.debug.communication.IDebuggerHostAgent;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpointHandler;

public class TransformationDebugTarget extends TransformationDebugElement implements IDebugTarget{
    private TransformationDebugProcess process;
    // containing launch object
    private ILaunch launch;
    // program name
    private String name; 
    // threads
    private List<TransformationThread> threads = new ArrayList<>();
    private boolean terminated = false;
    
    public TransformationDebugTarget(ILaunch launch, IDebuggerHostAgent agent, IType transformationType) {
        super(null);
        this.launch = launch;
        this.name = "Model Transformation Debugger Session";
        this.process = new TransformationDebugProcess(launch, name);
        
        threads.add(TransformationThreadFactory.getInstance().createTransformationThread(agent, this, transformationType));
        DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
        fireCreationEvent();
    }
    
    //IDebugElement
    @Override
    public ILaunch getLaunch() {
        return launch;
    }
    
    //IDebugTarget
    @Override
    public IProcess getProcess() {
        return process;
    }

    @Override
    public IDebugTarget getDebugTarget() {
        return this;
    }
    
    @Override
    public IThread[] getThreads() throws DebugException {
        return threads.toArray(new IThread[0]);
    }

    @Override
    public boolean hasThreads() throws DebugException {
        return !threads.isEmpty();
    }

    @Override
    public String getName() throws DebugException {
        return name;
    }
    
    @Override
    public boolean supportsBreakpoint(IBreakpoint breakpoint) {
        return breakpoint instanceof ITransformationBreakpointHandler;
    }
    
    //ITerminate
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
        for (TransformationThread transformationThread : threads) {
            transformationThread.terminate();
        }
    }
    
    //ISuspendResume
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
        for (TransformationThread transformationThread : threads) {
            if(transformationThread.isSuspended()){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void suspend() throws DebugException {
        for (TransformationThread transformationThread : threads) {
            transformationThread.suspend();
        }
    }
    
    @Override
    public void resume() throws DebugException {
        for (TransformationThread transformationThread : threads) {
            transformationThread.resume();
        }
    }
    
    //IBreakpointListener
    @Override
    public void breakpointAdded(IBreakpoint breakpoint) {
      //Instead of the debug target, debug threads handle breakpoints
    }

    @Override
    public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
      //Instead of the debug target, debug threads handle breakpoints
    }

    @Override
    public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
      //Instead of the debug target, debug threads handle breakpoints
    }
    
    //IDisconnect

    @Override
    public boolean canDisconnect() {
        return false;
    }

    //Disconnect is not supported, Terminate is used instead
    @Override
    public void disconnect() throws DebugException {
        throw new UnsupportedOperationException("Disconnect is not supported");
    }

    @Override
    public boolean isDisconnected() {
        return false;
    }
    
    //IMemoryBlockRetrieval

    @Override
    public boolean supportsStorageRetrieval() {
        return false;
    }

    @Override
    public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
        return null;
    }
    
    protected void requestTermination() {
        terminated = true;
        fireTerminateEvent();
    }
}
