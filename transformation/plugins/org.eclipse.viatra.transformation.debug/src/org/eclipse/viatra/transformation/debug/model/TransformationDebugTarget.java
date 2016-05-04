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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.core.IType;
import org.eclipse.viatra.transformation.debug.TransformationDebugger;
import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;
import org.eclipse.viatra.transformation.evm.api.adapter.IEVMAdapter;

import com.google.common.collect.Lists;

public class TransformationDebugTarget extends TransformationDebugElement implements IDebugTarget{
    private TransformationDebugProcess process;
    // containing launch object
    private ILaunch launch;
    // program name
    private String name; 
    // threads
    private List<TransformationThread> threads = new ArrayList<>();
    private boolean terminated = false;
    
    public TransformationDebugTarget(ILaunch launch, AdaptableEVM evm, IType transformationType, String name) throws CoreException {
        super(null);
        this.launch = launch;
        this.name = name;
        List<TransformationDebugger> debuggers = Lists.newArrayList();
        for(IEVMAdapter adapter : evm.getAdapters()){
            if(adapter instanceof TransformationDebugger){
                debuggers.add((TransformationDebugger) adapter);
            }
        }
        for(TransformationDebugger debugger : debuggers){
            threads.add(TransformationThreadFactory.getInstance().createTransformationThread(this, debugger, evm, transformationType));
        }
        DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
        installDeferredBreakpoints();
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
    public IThread[] getThreads() throws DebugException {
        return threads.toArray(new IThread[0]);
    }

    @Override
    public boolean hasThreads() throws DebugException {
        return threads.size() > 0;
    }

    @Override
    public String getName() throws DebugException {
        return name;
    }
    
    @Override
    public boolean supportsBreakpoint(IBreakpoint breakpoint) {
        return breakpoint instanceof ITransformationBreakpoint;
    }
    
    //ITerminate
    @Override
    public boolean canTerminate() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }
    
    @Override
    public void terminate() throws DebugException {
         
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
    }

    @Override
    public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {}

    @Override
    public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
        // TODO
        //Not supported yet
    }
    
    //IDisconnect

    @Override
    public boolean canDisconnect() {
        return false;
    }

    @Override
    public void disconnect() throws DebugException {
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

    private void installDeferredBreakpoints() {
        IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(TransformationDebugElement.MODEL_ID);
        for (int i = 0; i < breakpoints.length; i++) {
            breakpointAdded(breakpoints[i]);
        }
    }
    
    protected void requestTermination() {
        if(threadsTerminated()){
            try {
                launch.terminate();
                terminated = true;
                fireTerminateEvent();
            } catch (DebugException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected boolean threadsTerminated(){
        for (TransformationThread transformationThread : threads) {
            if(!transformationThread.isTerminated()){
                return false;
            }
        }
        return true;
    }
}
