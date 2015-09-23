package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.Or_Pattern;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;

@SuppressWarnings("all")
public class MultiplicityOnComplex4_Pattern extends ParameterizableComplexEventPattern {
  public MultiplicityOnComplex4_Pattern() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new Or_Pattern(), 2);
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.multiplicityoncomplex4_pattern");setEventContext(EventContext.CHRONICLE);
  }
}
