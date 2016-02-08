package org.eclipse.viatra.cep.tests.integration.model.events;

import org.eclipse.viatra.cep.core.api.events.ParameterizableEventInstance;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;

@SuppressWarnings("all")
public class A1_Event extends ParameterizableEventInstance {
  public A1_Event(final EventSource eventSource) {
    super(eventSource);
  }
  
  @Override
  public boolean evaluateCheckExpression() {
    return true;
  }
}
