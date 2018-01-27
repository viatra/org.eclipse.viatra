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
package org.eclipse.viatra.transformation.debug.model.transformationstate;

import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import org.eclipse.viatra.transformation.debug.model.transformationstate.ActivationParameter;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;
import org.eclipse.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class RuleActivation implements Serializable {
  private final TransformationState transformationState;
  
  private final ActivationTrace trace;
  
  private final boolean nextActivation;
  
  private final String state;
  
  private final String ruleName;
  
  private final List<ActivationParameter> paremeters;
  
  public RuleActivation(final ActivationTrace trace, final boolean nextActivation, final String state, final String ruleName, final List<ActivationParameter> paremeters, final TransformationState transformationState) {
    super();
    this.trace = trace;
    this.nextActivation = nextActivation;
    this.state = state;
    this.ruleName = ruleName;
    this.paremeters = paremeters;
    this.transformationState = transformationState;
  }
  
  public ActivationTrace getTrace() {
    return this.trace;
  }
  
  public boolean isNextActivation() {
    return this.nextActivation;
  }
  
  public String getState() {
    return this.state;
  }
  
  public String getRuleName() {
    return this.ruleName;
  }
  
  public List<ActivationParameter> getParameters() {
    return Lists.<ActivationParameter>newArrayList(this.paremeters);
  }
  
  public TransformationState getTransformationState() {
    return this.transformationState;
  }
  
  @Override
  public String toString() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("State: ");
    _builder.append(this.state, "");
    _builder.append(" - Parameters(");
    {
      List<ActivationParameter> _parameters = this.getParameters();
      boolean _hasElements = false;
      for(final ActivationParameter param : _parameters) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(",", "");
        }
        _builder.append(" ");
        String _name = param.getName();
        _builder.append(_name, "");
        _builder.append(" = ");
        Object _value = param.getValue();
        String _string = _value.toString();
        _builder.append(_string, "");
      }
    }
    _builder.append(")");
    return _builder.toString();
  }
}
