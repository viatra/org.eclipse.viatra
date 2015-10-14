package org.eclipse.viatra.cep.emf.notification.model.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.emf.notification.model.events.UNSET_Event;

@SuppressWarnings("all")
public class UNSET_Pattern extends AtomicEventPatternImpl {
  public UNSET_Pattern() {
    super();
    setType(UNSET_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.unset_pattern");
  }
}
