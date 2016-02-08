package org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.anonymous._AnonymousPattern_12;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.events.Timewindow;

@SuppressWarnings("all")
public class TimewinFollows2_Pattern extends ParameterizableComplexEventPattern {
  public TimewinFollows2_Pattern() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new _AnonymousPattern_12(), 1);
    						
    Timewindow timewindow = EventsFactory.eINSTANCE.createTimewindow();
    timewindow.setTime(1000);
    setTimewindow(timewindow);
    	
    setId("org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.timewinfollows2_pattern");setEventContext(EventContext.CHRONICLE);
  }
}
