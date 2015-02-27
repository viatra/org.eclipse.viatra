package org.eclipse.viatra.cep.tests.integration.model.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.tests.integration.model.events.A3_Event;

@SuppressWarnings("all")
public class A3_Pattern extends AtomicEventPatternImpl {
  public A3_Pattern() {
    super();
    setType(A3_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.tests.integration.model.patterns.atomic.a3_pattern");
  }
  
  public boolean checkStaticBindings() {
    return true;
  }
}
