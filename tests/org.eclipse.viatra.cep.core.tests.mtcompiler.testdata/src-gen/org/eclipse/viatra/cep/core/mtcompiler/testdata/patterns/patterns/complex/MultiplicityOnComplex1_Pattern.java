package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous._AnonymousPattern_2;

@SuppressWarnings("all")
public class MultiplicityOnComplex1_Pattern extends ParameterizableComplexEventPattern {
  public MultiplicityOnComplex1_Pattern() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new _AnonymousPattern_2(), 2);
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.multiplicityoncomplex1_pattern");
  }
}
