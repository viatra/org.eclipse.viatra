package org.eclipse.viatra.cep.tests.integration.model.patterns.complex;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.tests.integration.model.patterns.atomic.A1_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.atomic.A2_Pattern;

@SuppressWarnings("all")
public class Follows_Pattern extends ParameterizableComplexEventPattern {
  public Follows_Pattern() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new A1_Pattern(), 1);
    addEventPatternRefrence(new A2_Pattern(), 1);
    setId("org.eclipse.viatra.cep.tests.integration.model.patterns.complex.follows_pattern");setEventContext(EventContext.CHRONICLE);
  }
}
