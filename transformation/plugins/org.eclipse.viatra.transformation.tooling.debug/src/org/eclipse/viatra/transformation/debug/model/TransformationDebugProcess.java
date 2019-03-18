/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

public class TransformationDebugProcess implements IProcess{
    private String label;
    private ILaunch launch;
    private boolean terminated = false;
    
    
    public TransformationDebugProcess(ILaunch launch, String label) {
        this.label = label;
        this.launch = launch;
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
        terminated = true;
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
