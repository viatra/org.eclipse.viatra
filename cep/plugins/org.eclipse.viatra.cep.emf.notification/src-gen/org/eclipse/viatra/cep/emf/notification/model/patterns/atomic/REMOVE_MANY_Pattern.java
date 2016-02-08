package org.eclipse.viatra.cep.emf.notification.model.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.emf.notification.model.events.REMOVE_MANY_Event;

@SuppressWarnings("all")
public class REMOVE_MANY_Pattern extends AtomicEventPatternImpl {
  public REMOVE_MANY_Pattern() {
    super();
    setType(REMOVE_MANY_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.remove_many_pattern");
  }
}
