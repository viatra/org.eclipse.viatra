package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.anonymous;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.C_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.anonymous._AnonymousPattern_13;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;

@SuppressWarnings("all")
public class _AnonymousPattern_14 extends ParameterizableComplexEventPattern {
  public _AnonymousPattern_14() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new _AnonymousPattern_13(), 1);
    addEventPatternRefrence(new C_Pattern(), 1);
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.anonymous._anonymouspattern_14");setEventContext(EventContext.CHRONICLE);
  }
}
