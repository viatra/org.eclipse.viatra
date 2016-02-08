package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.anonymous;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.C_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.anonymous._AnonymousPattern_24;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;

@SuppressWarnings("all")
public class _AnonymousPattern_25 extends ParameterizableComplexEventPattern {
  public _AnonymousPattern_25() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new _AnonymousPattern_24(), 1);
    addEventPatternRefrence(new C_Pattern(), 1);
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.anonymous._anonymouspattern_25");setEventContext(EventContext.CHRONICLE);
  }
}
