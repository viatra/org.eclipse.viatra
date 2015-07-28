package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.C_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.Follows_Pattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;

@SuppressWarnings("all")
public class And2_Pattern extends ParameterizableComplexEventPattern {
  public And2_Pattern() {
    super();
    setOperator(EventsFactory.eINSTANCE.createAND());
    
    // contained event patterns
    addEventPatternRefrence(new Follows_Pattern(), 1);
    addEventPatternRefrence(new C_Pattern(), 1);
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.and2_pattern");
  }
}
