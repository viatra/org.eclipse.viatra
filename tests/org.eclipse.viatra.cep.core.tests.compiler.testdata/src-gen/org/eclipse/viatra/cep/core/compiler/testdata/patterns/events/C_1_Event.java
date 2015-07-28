package org.eclipse.viatra.cep.core.compiler.testdata.patterns.events;

import org.eclipse.viatra.cep.core.api.events.ParameterizableEventInstance;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;

@SuppressWarnings("all")
public class C_1_Event extends ParameterizableEventInstance {
  private String p;
  
  public C_1_Event(final EventSource eventSource) {
    super(eventSource);
    getParameters().add(p);
    
  }
  
  public String getP() {
    return this.p;
  }
  
  public void setP(final String p) {
    this.p = p;
    getParameters().set(0, p);
  }
}
