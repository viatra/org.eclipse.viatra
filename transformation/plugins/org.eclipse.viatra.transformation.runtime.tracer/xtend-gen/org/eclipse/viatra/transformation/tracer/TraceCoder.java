/**
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.tracer;

import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.viatra.transformation.debug.activationcoder.DefaultActivationCoder;
import org.eclipse.viatra.transformation.debug.activationcoder.IActivationCoder;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.TransformationTrace;
import org.eclipse.viatra.transformation.debug.transformationtrace.serializer.DefaultTraceModelSerializer;
import org.eclipse.viatra.transformation.debug.transformationtrace.serializer.ITraceModelSerializer;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.adapter.AbstractEVMListener;
import org.eclipse.xtext.xbase.lib.Extension;

/**
 * Adapter implementation that creates transformation traces based on the ongoing transformation.
 * 
 *  @author Peter Lunk
 */
@SuppressWarnings("all")
public class TraceCoder extends AbstractEVMListener {
  @Extension
  private IActivationCoder activationCoder;
  
  private TransformationTrace trace;
  
  private ITraceModelSerializer serializer;
  
  public TraceCoder(final IActivationCoder activationCoder, final ITraceModelSerializer serializer) {
    this.activationCoder = activationCoder;
    this.serializer = serializer;
    TransformationTrace _transformationTrace = new TransformationTrace();
    this.trace = _transformationTrace;
  }
  
  public TraceCoder(final IActivationCoder activationCoder, final URI location) {
    this.activationCoder = activationCoder;
    DefaultTraceModelSerializer _defaultTraceModelSerializer = new DefaultTraceModelSerializer(location);
    this.serializer = _defaultTraceModelSerializer;
    TransformationTrace _transformationTrace = new TransformationTrace();
    this.trace = _transformationTrace;
  }
  
  public TraceCoder(final URI location) {
    DefaultActivationCoder _defaultActivationCoder = new DefaultActivationCoder();
    this.activationCoder = _defaultActivationCoder;
    DefaultTraceModelSerializer _defaultTraceModelSerializer = new DefaultTraceModelSerializer(location);
    this.serializer = _defaultTraceModelSerializer;
    TransformationTrace _transformationTrace = new TransformationTrace();
    this.trace = _transformationTrace;
  }
  
  @Override
  public void beforeFiring(final Activation<?> activation) {
    List<ActivationTrace> _activationTraces = this.trace.getActivationTraces();
    ActivationTrace _createActivationCode = this.activationCoder.createActivationCode(activation);
    _activationTraces.add(_createActivationCode);
  }
  
  @Override
  public void endTransaction(final String transactionID) {
    this.serializer.serializeTraceModel(this.trace);
  }
}
