package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic;

import org.eclipse.viatra.cep.core.compiler.testdata.patterns.events.Far_Event;
import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;

@SuppressWarnings("all")
public class Far_Pattern extends AtomicEventPatternImpl {
  public Far_Pattern() {
    super();
    setType(Far_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.far_pattern");
  }
}
