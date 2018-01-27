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
package org.eclipse.viatra.transformation.debug.activationcoder;

import com.google.common.base.Objects;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.debug.activationcoder.IActivationCoder;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.RuleParameterTrace;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleInstance;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.xtext.xbase.lib.Exceptions;

/**
 * Default activation coder implementation that creates transformation trace objects based on the rule
 * instance of the activation and the parameter objects of the rule query specification.
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class DefaultActivationCoder implements IActivationCoder {
  @Override
  public ActivationTrace createActivationCode(final Activation<?> activation) {
    ActivationTrace _xblockexpression = null;
    {
      RuleInstance<?> _instance = activation.getInstance();
      final RuleSpecification<?> specification = _instance.getSpecification();
      String _name = specification.getName();
      boolean _equals = Objects.equal(_name, "");
      if (_equals) {
        String _string = specification.toString();
        String _plus = ("Rule specification has no defined name:" + _string);
        throw new IllegalStateException(_plus);
      }
      String _name_1 = specification.getName();
      final ActivationTrace trace = new ActivationTrace(_name_1);
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
              List<RuleParameterTrace> _ruleParameterTraces = trace.getRuleParameterTraces();
              URI _uRI = EcoreUtil.getURI(((EObject)param));
              String _string_1 = _uRI.toString();
              RuleParameterTrace _ruleParameterTrace = new RuleParameterTrace(paramName, _string_1);
              _ruleParameterTraces.add(_ruleParameterTrace);
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
