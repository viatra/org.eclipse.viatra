package org.eclipse.viatra.cep.emf.notification.model.events;

import org.eclipse.viatra.cep.core.api.events.ParameterizableEventInstance;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;
import org.eclipse.viatra.cep.emf.notification.model.traits.EObject;

@SuppressWarnings("all")
public class REMOVE_Event extends ParameterizableEventInstance implements EObject {
  private Object notifier;
  
  private Object feature;
  
  private Object oldValue;
  
  private Object newValue;
  
  public REMOVE_Event(final EventSource eventSource) {
    super(eventSource);
    getParameters().add(notifier);
    getParameters().add(feature);
    getParameters().add(oldValue);
    getParameters().add(newValue);
    
  }
  
  @Override
  public Object getNotifier() {
    return this.notifier;
  }
  
  @Override
  public void setNotifier(final Object notifier) {
    this.notifier = notifier;
    getParameters().set(0, notifier);
  }
  
  @Override
  public Object getFeature() {
    return this.feature;
  }
  
  @Override
  public void setFeature(final Object feature) {
    this.feature = feature;
    getParameters().set(1, feature);
  }
  
  @Override
  public Object getOldValue() {
    return this.oldValue;
  }
  
  @Override
  public void setOldValue(final Object oldValue) {
    this.oldValue = oldValue;
    getParameters().set(2, oldValue);
  }
  
  @Override
  public Object getNewValue() {
    return this.newValue;
  }
  
  @Override
  public void setNewValue(final Object newValue) {
    this.newValue = newValue;
    getParameters().set(3, newValue);
  }
  
  @Override
  public boolean evaluateCheckExpression() {
    return true;
  }
}
