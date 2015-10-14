package org.eclipse.viatra.cep.emf.notification.model.patterns.atomic;

import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.emf.notification.model.events.ADD_Event;

@SuppressWarnings("all")
public class ADD_Pattern extends AtomicEventPatternImpl {
  public ADD_Pattern() {
    super();
    setType(ADD_Event.class.getCanonicalName());
    setId("org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.add_pattern");
  }
}
