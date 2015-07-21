package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.events.Timewindow;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous._AnonymousPattern_28;

@SuppressWarnings("all")
public class _AnonymousPattern_29 extends ParameterizableComplexEventPattern {
  public _AnonymousPattern_29() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new _AnonymousPattern_28(), 1);
    						
    Timewindow timewindow = EventsFactory.eINSTANCE.createTimewindow();
    timewindow.setTime(1000);
    setTimewindow(timewindow);
    	
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous._anonymouspattern_29");
  }
}
