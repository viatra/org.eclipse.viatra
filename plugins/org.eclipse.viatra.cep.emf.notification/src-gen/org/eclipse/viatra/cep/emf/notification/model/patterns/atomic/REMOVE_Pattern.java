package org.eclipse.viatra.cep.emf.notification.model.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.emf.notification.model.events.REMOVE_Event;

@SuppressWarnings("all")
public class REMOVE_Pattern extends AtomicEventPatternImpl {
  public REMOVE_Pattern() {
    super();
    setType(REMOVE_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.remove_pattern");
  }
}
