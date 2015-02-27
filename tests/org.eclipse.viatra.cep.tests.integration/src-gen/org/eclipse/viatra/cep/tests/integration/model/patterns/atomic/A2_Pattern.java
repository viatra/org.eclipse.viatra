package org.eclipse.viatra.cep.tests.integration.model.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.tests.integration.model.events.A2_Event;

@SuppressWarnings("all")
public class A2_Pattern extends AtomicEventPatternImpl {
  public A2_Pattern() {
    super();
    setType(A2_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.tests.integration.model.patterns.atomic.a2_pattern");
  }
  
  public boolean checkStaticBindings() {
    return true;
  }
}
