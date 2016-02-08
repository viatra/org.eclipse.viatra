package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex;

import com.google.common.collect.Lists;
import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.A_1_Pattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.atomic.B_1_Pattern;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;

@SuppressWarnings("all")
public class ParamsAnd_Pattern extends ParameterizableComplexEventPattern {
  public ParamsAnd_Pattern() {
    super();
    setOperator(EventsFactory.eINSTANCE.createAND());
    
    // contained event patterns
    addEventPatternRefrence(new A_1_Pattern(), 1, Lists.newArrayList("param"));
    addEventPatternRefrence(new B_1_Pattern(), 1, Lists.newArrayList("param"));
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.paramsand_pattern");setEventContext(EventContext.CHRONICLE);
  }
}
