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
package org.eclipse.viatra.transformation.debug.configuration;

import com.google.common.collect.Lists;
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
@SuppressWarnings("all")
public class TransformationDebuggerConfiguration implements IAdapterConfiguration {
  private List<IEVMAdapter> adapters = Lists.<IEVMAdapter>newArrayList();
  
  private List<IEVMListener> listeners = Lists.<IEVMListener>newArrayList();
  
  public TransformationDebuggerConfiguration() {
    this(("Transformation_" + Long.valueOf(System.nanoTime()).toString()));
  }
  
  public TransformationDebuggerConfiguration(final String ID) {
    final String id = ID;
    final TransformationDebugger debugger = new TransformationDebugger(id);
    this.listeners.add(debugger);
    this.adapters.add(debugger);
  }
  
  @Override
  public List<IEVMAdapter> getAdapters() {
    return Collections.<IEVMAdapter>unmodifiableList(this.adapters);
  }
  
  @Override
  public List<IEVMListener> getListeners() {
    return Collections.<IEVMListener>unmodifiableList(this.listeners);
  }
}
