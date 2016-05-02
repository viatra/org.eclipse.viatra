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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.viatra.transformation.debug.TransformationDebugger;

public class TransformationDebugProcess implements IProcess{
    private TransformationDebugger debugger;
    private String label;
    private ILaunch launch;
    
    public TransformationDebugProcess(ILaunch launch, String label, TransformationDebugger debugger) {
        this.debugger = debugger;
        this.label = label;
        this.launch = launch;
    }
    
    public TransformationDebugger getDebugger() {
        return debugger;
    }

    @Override
    public boolean canTerminate() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public void terminate() throws DebugException {
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public ILaunch getLaunch() {
        return launch;
    }

    @Override
    public IStreamsProxy getStreamsProxy() {
        return null;
    }

    @Override
    public void setAttribute(String key, String value) {
        
    }

    @Override
    public String getAttribute(String key) {
        return null;
    }

    @Override
    public int getExitValue() throws DebugException {
        return 0;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

}
