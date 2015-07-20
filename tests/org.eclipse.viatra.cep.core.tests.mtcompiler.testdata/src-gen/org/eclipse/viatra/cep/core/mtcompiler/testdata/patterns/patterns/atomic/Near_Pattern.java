package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.Near_Event;

@SuppressWarnings("all")
public class Near_Pattern extends AtomicEventPatternImpl {
  public Near_Pattern() {
    super();
    setType(Near_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.near_pattern");
  }
  
  public boolean evaluateCheckExpression() {
    return true;
  }
}
