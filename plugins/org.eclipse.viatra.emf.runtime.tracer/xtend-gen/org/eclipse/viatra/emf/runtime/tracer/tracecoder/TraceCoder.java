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
package org.eclipse.viatra.emf.runtime.tracer.tracecoder;

import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictSet;
import org.eclipse.viatra.emf.runtime.adapter.impl.AbstractTransformationAdapter;
import org.eclipse.viatra.emf.runtime.rules.eventdriven.EventDrivenTransformationRule;
import org.eclipse.viatra.emf.runtime.tracer.activationcoder.IActivationCoder;
import org.eclipse.viatra.emf.runtime.tracer.activationcoder.impl.DefaultActivationCoder;
import org.eclipse.viatra.emf.runtime.tracer.tracemodelserializer.ITraceModelSerializer;
import org.eclipse.viatra.emf.runtime.tracer.tracemodelserializer.impl.DefaultTraceModelSerializer;
import org.eclipse.xtext.xbase.lib.Extension;
import transformationtrace.ActivationTrace;
import transformationtrace.TransformationTrace;
import transformationtrace.TransformationtraceFactory;

/**
 * Adapter implementation that creates transformation traces based on the ongoing transformation.
 * @author Lunk Péter
 */
@SuppressWarnings("all")
public class TraceCoder extends AbstractTransformationAdapter {
  @Extension
  private TransformationtraceFactory factory = TransformationtraceFactory.eINSTANCE;
  
  @Extension
  private IActivationCoder activationCoder;
  
  private TransformationTrace trace;
  
  private ITraceModelSerializer serializer;
  
  private Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rules;
  
  public TraceCoder(final IActivationCoder activationCoder, final ITraceModelSerializer serializer) {
    this.activationCoder = activationCoder;
    this.serializer = serializer;
    TransformationTrace _createTransformationTrace = this.factory.createTransformationTrace();
    this.trace = _createTransformationTrace;
  }
  
  public TraceCoder(final IActivationCoder activationCoder, final URI location) {
    this.activationCoder = activationCoder;
    DefaultTraceModelSerializer _defaultTraceModelSerializer = new DefaultTraceModelSerializer(location);
    this.serializer = _defaultTraceModelSerializer;
    TransformationTrace _createTransformationTrace = this.factory.createTransformationTrace();
    this.trace = _createTransformationTrace;
  }
  
  public TraceCoder(final URI location) {
    DefaultActivationCoder _defaultActivationCoder = new DefaultActivationCoder();
    this.activationCoder = _defaultActivationCoder;
    DefaultTraceModelSerializer _defaultTraceModelSerializer = new DefaultTraceModelSerializer(location);
    this.serializer = _defaultTraceModelSerializer;
    TransformationTrace _createTransformationTrace = this.factory.createTransformationTrace();
    this.trace = _createTransformationTrace;
  }
  
  @Override
  public Activation<?> beforeFiring(final Activation<?> activation) {
    Activation<?> _xblockexpression = null;
    {
      EList<ActivationTrace> _activationTraces = this.trace.getActivationTraces();
      ActivationTrace _createActivationCode = this.activationCoder.createActivationCode(activation, this.rules);
      _activationTraces.add(_createActivationCode);
      _xblockexpression = activation;
    }
    return _xblockexpression;
  }
  
  @Override
  public ConflictSet afterSchedule(final ConflictSet conflictSet) {
    ConflictSet _xblockexpression = null;
    {
      this.serializer.serializeTraceModel(this.trace);
      _xblockexpression = conflictSet;
    }
    return _xblockexpression;
  }
  
  public Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> setRules(final Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rules) {
    return this.rules = rules;
  }
}
