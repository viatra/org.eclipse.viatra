package org.eclipse.viatra.cep.core.compiler.testdata.patterns.events;

import org.eclipse.viatra.cep.core.api.events.ParameterizableEventInstance;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;

@SuppressWarnings("all")
public class Fall_Event extends ParameterizableEventInstance {
  public Fall_Event(final EventSource eventSource) {
    super(eventSource);
  }
}
