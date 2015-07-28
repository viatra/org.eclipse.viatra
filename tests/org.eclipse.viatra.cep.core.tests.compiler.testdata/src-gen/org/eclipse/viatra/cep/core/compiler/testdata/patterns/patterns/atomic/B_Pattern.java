package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic;

import org.eclipse.viatra.cep.core.compiler.testdata.patterns.events.B_Event;
import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;

@SuppressWarnings("all")
public class B_Pattern extends AtomicEventPatternImpl {
  public B_Pattern() {
    super();
    setType(B_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.b_pattern");
  }
  
  public boolean evaluateCheckExpression() {
    return true;
  }
}
