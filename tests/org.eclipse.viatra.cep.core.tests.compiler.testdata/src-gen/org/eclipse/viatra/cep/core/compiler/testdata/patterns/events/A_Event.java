package org.eclipse.viatra.cep.core.compiler.testdata.patterns.events;

import org.eclipse.viatra.cep.core.api.events.ParameterizableEventInstance;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;

@SuppressWarnings("all")
public class A_Event extends ParameterizableEventInstance {
  public A_Event(final EventSource eventSource) {
    super(eventSource);
  }
  
  @Override
  public boolean evaluateCheckExpression() {
    return true;
  }
}
