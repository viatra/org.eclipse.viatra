package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.C_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.Follows_Pattern;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;

@SuppressWarnings("all")
public class NestedOrWithFollows1_Pattern extends ParameterizableComplexEventPattern {
  public NestedOrWithFollows1_Pattern() {
    super();
    setOperator(EventsFactory.eINSTANCE.createOR());
    
    // contained event patterns
    addEventPatternRefrence(new Follows_Pattern(), 1);
    addEventPatternRefrence(new C_Pattern(), 1);
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.nestedorwithfollows1_pattern");setEventContext(EventContext.CHRONICLE);
  }
}
