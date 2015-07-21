package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex;

import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.events.Timewindow;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.anonymous._AnonymousPattern_33;

@SuppressWarnings("all")
public class NotAndTimewin_Pattern extends ParameterizableComplexEventPattern {
  public NotAndTimewin_Pattern() {
    super();
    setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
    
    // contained event patterns
    addEventPatternRefrence(new _AnonymousPattern_33(), 1);
    						
    Timewindow timewindow = EventsFactory.eINSTANCE.createTimewindow();
    timewindow.setTime(1000);
    setTimewindow(timewindow);
    	
    setId("org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.notandtimewin_pattern");
  }
}
