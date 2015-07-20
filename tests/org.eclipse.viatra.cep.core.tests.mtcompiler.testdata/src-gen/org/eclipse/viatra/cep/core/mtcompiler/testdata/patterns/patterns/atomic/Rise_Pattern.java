package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.Rise_Event;

@SuppressWarnings("all")
public class Rise_Pattern extends AtomicEventPatternImpl {
  public Rise_Pattern() {
    super();
    setType(Rise_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.rise_pattern");
  }
  
  public boolean evaluateCheckExpression() {
    return true;
  }
}
