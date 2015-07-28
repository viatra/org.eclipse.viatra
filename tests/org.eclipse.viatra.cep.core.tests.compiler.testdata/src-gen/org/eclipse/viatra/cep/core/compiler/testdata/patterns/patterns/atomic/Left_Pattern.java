package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic;

import org.eclipse.viatra.cep.core.compiler.testdata.patterns.events.Left_Event;
import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;

/**
 * CASE STUDY
 */
@SuppressWarnings("all")
public class Left_Pattern extends AtomicEventPatternImpl {
  public Left_Pattern() {
    super();
    setType(Left_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.left_pattern");
  }
  
  public boolean evaluateCheckExpression() {
    return true;
  }
}
