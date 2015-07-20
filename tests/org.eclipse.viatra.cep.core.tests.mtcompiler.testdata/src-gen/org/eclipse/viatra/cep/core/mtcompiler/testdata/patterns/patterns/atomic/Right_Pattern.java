package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.Right_Event;

@SuppressWarnings("all")
public class Right_Pattern extends AtomicEventPatternImpl {
  public Right_Pattern() {
    super();
    setType(Right_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.right_pattern");
  }
  
  public boolean evaluateCheckExpression() {
    return true;
  }
}
