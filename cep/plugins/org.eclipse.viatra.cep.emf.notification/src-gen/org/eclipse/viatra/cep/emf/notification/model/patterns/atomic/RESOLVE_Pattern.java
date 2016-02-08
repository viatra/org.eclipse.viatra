package org.eclipse.viatra.cep.emf.notification.model.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.emf.notification.model.events.RESOLVE_Event;

@SuppressWarnings("all")
public class RESOLVE_Pattern extends AtomicEventPatternImpl {
  public RESOLVE_Pattern() {
    super();
    setType(RESOLVE_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.resolve_pattern");
  }
}
