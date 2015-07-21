package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.Or_Pattern;

@SuppressWarnings("all")
public class _AnonymousPattern_23 extends ParameterizableComplexEventPattern {
  public _AnonymousPattern_23() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new Or_Pattern(), 1);
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous._anonymouspattern_23");
  }
}
