package org.eclipse.viatra.cep.emf.notification.model.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.emf.notification.model.events.SET_Event;

@SuppressWarnings("all")
public class SET_Pattern extends AtomicEventPatternImpl {
  public SET_Pattern() {
    super();
    setType(SET_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.set_pattern");
  }
}
