package org.eclipse.viatra.cep.tests.integration.model.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.tests.integration.model.events.A1_Event;

@SuppressWarnings("all")
public class A1_Pattern extends AtomicEventPatternImpl {
  public A1_Pattern() {
    super();
    setType(A1_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.tests.integration.model.patterns.atomic.a1_pattern");
  }
  
  public boolean checkStaticBindings() {
    return true;
  }
}
