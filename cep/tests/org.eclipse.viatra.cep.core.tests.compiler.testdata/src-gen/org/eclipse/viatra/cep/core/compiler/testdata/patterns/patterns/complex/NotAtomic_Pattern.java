package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.anonymous._AnonymousPattern_20;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;

@SuppressWarnings("all")
public class NotAtomic_Pattern extends ParameterizableComplexEventPattern {
  public NotAtomic_Pattern() {
    super();
    setOperator(EventsFactory.eINSTANCE.createNEG());
    
    // contained event patterns
    addEventPatternRefrence(new _AnonymousPattern_20(), 1);
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.notatomic_pattern");setEventContext(EventContext.CHRONICLE);
  }
}
