package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous._AnonymousPattern_17;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous._AnonymousPattern_18;

@SuppressWarnings("all")
public class _AnonymousPattern_19 extends ParameterizableComplexEventPattern {
  public _AnonymousPattern_19() {
    super();
    setOperator(EventsFactory.eINSTANCE.createOR());
    
    // contained event patterns
    addEventPatternRefrence(new _AnonymousPattern_17(), 1);
    addEventPatternRefrence(new _AnonymousPattern_18(), 1);
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous._anonymouspattern_19");
  }
}
