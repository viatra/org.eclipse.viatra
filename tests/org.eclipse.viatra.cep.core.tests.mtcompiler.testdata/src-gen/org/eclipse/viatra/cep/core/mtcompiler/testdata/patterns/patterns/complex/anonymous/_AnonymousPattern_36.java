package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.Right_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous._AnonymousPattern_35;

@SuppressWarnings("all")
public class _AnonymousPattern_36 extends ParameterizableComplexEventPattern {
  public _AnonymousPattern_36() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new _AnonymousPattern_35(), 1);
    addEventPatternRefrence(new Right_Pattern(), 1);
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous._anonymouspattern_36");
  }
}
