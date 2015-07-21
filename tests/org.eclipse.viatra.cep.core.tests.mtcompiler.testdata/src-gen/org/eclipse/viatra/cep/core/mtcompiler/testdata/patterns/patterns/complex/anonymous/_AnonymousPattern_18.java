package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.A_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.B_Pattern;

@SuppressWarnings("all")
public class _AnonymousPattern_18 extends ParameterizableComplexEventPattern {
  public _AnonymousPattern_18() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new B_Pattern(), 1);
    addEventPatternRefrence(new A_Pattern(), 1);
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous._anonymouspattern_18");
  }
}
