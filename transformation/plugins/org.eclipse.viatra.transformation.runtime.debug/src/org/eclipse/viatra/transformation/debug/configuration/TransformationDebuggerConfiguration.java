/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.viatra.transformation.debug.TransformationDebugger;
import org.eclipse.viatra.transformation.evm.api.adapter.IAdapterConfiguration;
import org.eclipse.viatra.transformation.evm.api.adapter.IEVMAdapter;
import org.eclipse.viatra.transformation.evm.api.adapter.IEVMListener;

/**
 * Configuration class that defines the debugger.
 * 
 * @author Peter Lunk
 */
public class TransformationDebuggerConfiguration implements IAdapterConfiguration {
    private List<IEVMAdapter> adapters = new ArrayList<>();

    private List<IEVMListener> listeners = new ArrayList<>();

    public TransformationDebuggerConfiguration() {
        this("Transformation_" + Long.toString(System.nanoTime()));
    }

    public TransformationDebuggerConfiguration(final String ID) {
        final String id = ID;
        final TransformationDebugger debugger = new TransformationDebugger(id);
        this.listeners.add(debugger);
        this.adapters.add(debugger);
    }

    @Override
    public List<IEVMAdapter> getAdapters() {
        return Collections.unmodifiableList(adapters);
    }

    @Override
    public List<IEVMListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }
}
