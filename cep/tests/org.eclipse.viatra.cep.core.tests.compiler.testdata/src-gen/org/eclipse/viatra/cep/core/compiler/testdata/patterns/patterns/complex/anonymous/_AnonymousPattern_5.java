package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.anonymous;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.A_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.B_Pattern;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;

@SuppressWarnings("all")
public class _AnonymousPattern_5 extends ParameterizableComplexEventPattern {
  public _AnonymousPattern_5() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new A_Pattern(), 1);
    addEventPatternRefrence(new B_Pattern(), 1);
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.anonymous._anonymouspattern_5");setEventContext(EventContext.CHRONICLE);
  }
}
