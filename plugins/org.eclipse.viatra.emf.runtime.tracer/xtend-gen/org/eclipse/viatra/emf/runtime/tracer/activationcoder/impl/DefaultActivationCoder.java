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
package org.eclipse.viatra.emf.runtime.tracer.activationcoder.impl;

import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleInstance;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.viatra.emf.runtime.rules.eventdriven.EventDrivenTransformationRule;
import org.eclipse.viatra.emf.runtime.tracer.activationcoder.IActivationCoder;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import transformationtrace.ActivationTrace;
import transformationtrace.RuleParameterTrace;
import transformationtrace.TransformationtraceFactory;

/**
 * Default activation coder implementation that creates transformation trace objects based on the rule
 * instance of the activation and the parameter objects of the rule query specification.
 */
@SuppressWarnings("all")
public class DefaultActivationCoder implements IActivationCoder {
  @Extension
  private TransformationtraceFactory factory = TransformationtraceFactory.eINSTANCE;
  
  @Override
  public ActivationTrace createActivationCode(final Activation<?> activation, final Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rules) {
    ActivationTrace _xblockexpression = null;
    {
      RuleInstance<?> _instance = activation.getInstance();
      final RuleSpecification<?> specification = _instance.getSpecification();
      final EventDrivenTransformationRule<?, ?> rule = rules.get(specification);
      final ActivationTrace trace = this.factory.createActivationTrace();
      String _name = rule.getName();
      trace.setRuleName(_name);
      try {
        Object _atom = activation.getAtom();
        final IPatternMatch match = ((IPatternMatch) _atom);
        boolean running = true;
        int i = 0;
        while (running) {
          {
            final Object param = match.get(i);
            if ((param instanceof EObject)) {
              List<String> _parameterNames = match.parameterNames();
              final String paramName = _parameterNames.get(i);
              EList<RuleParameterTrace> _ruleParameterTraces = trace.getRuleParameterTraces();
              RuleParameterTrace _createRuleParameterTrace = this.factory.createRuleParameterTrace();
              final Procedure1<RuleParameterTrace> _function = new Procedure1<RuleParameterTrace>() {
                @Override
                public void apply(final RuleParameterTrace it) {
                  it.setParameterName(paramName);
                  URI _uRI = EcoreUtil.getURI(((EObject)param));
                  String _string = _uRI.toString();
                  it.setObjectId(_string);
                }
              };
              RuleParameterTrace _doubleArrow = ObjectExtensions.<RuleParameterTrace>operator_doubleArrow(_createRuleParameterTrace, _function);
              _ruleParameterTraces.add(_doubleArrow);
              i++;
            } else {
              running = false;
            }
          }
        }
      } catch (final Throwable _t) {
        if (_t instanceof ClassCastException) {
          final ClassCastException e = (ClassCastException)_t;
          e.printStackTrace();
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      _xblockexpression = trace;
    }
    return _xblockexpression;
  }
}
