package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.Left_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.anonymous._AnonymousPattern_39;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;

@SuppressWarnings("all")
public class Teq1_Pattern extends ParameterizableComplexEventPattern {
  public Teq1_Pattern() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new Left_Pattern(), 1);
    addEventPatternRefrence(new _AnonymousPattern_39(), 1);
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.teq1_pattern");setEventContext(EventContext.CHRONICLE);
  }
}
