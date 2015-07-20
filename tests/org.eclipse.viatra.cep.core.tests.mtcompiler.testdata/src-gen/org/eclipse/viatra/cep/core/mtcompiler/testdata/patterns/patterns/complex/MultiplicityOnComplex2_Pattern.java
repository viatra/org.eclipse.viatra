package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.Follows_Pattern;

@SuppressWarnings("all")
public class MultiplicityOnComplex2_Pattern extends ParameterizableComplexEventPattern {
  public MultiplicityOnComplex2_Pattern() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new Follows_Pattern(), 2);
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.multiplicityoncomplex2_pattern");
  }
}
