package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous._AnonymousPattern_24;

@SuppressWarnings("all")
public class NotAnd_Pattern extends ParameterizableComplexEventPattern {
  public NotAnd_Pattern() {
    super();
    setOperator(EventsFactory.eINSTANCE.createNEG());
    
    // contained event patterns
    addEventPatternRefrence(new _AnonymousPattern_24(), 1);
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.notand_pattern");
  }
}
