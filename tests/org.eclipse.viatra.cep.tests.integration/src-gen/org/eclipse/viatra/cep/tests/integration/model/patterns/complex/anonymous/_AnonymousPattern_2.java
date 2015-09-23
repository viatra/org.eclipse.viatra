package org.eclipse.viatra.cep.tests.integration.model.patterns.complex.anonymous;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.tests.integration.model.patterns.atomic.A1_Pattern;

@SuppressWarnings("all")
public class _AnonymousPattern_2 extends ParameterizableComplexEventPattern {
  public _AnonymousPattern_2() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new A1_Pattern(), EventsFactory.eINSTANCE.createAtLeastOne());
    setId("org.eclipse.viatra.cep.tests.integration.model.patterns.complex.anonymous._anonymouspattern_2");setEventContext(EventContext.CHRONICLE);
  }
}
