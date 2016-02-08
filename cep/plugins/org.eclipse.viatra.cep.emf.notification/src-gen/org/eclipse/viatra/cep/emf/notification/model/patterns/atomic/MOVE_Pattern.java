package org.eclipse.viatra.cep.emf.notification.model.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.emf.notification.model.events.MOVE_Event;

@SuppressWarnings("all")
public class MOVE_Pattern extends AtomicEventPatternImpl {
  public MOVE_Pattern() {
    super();
    setType(MOVE_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.move_pattern");
  }
}
