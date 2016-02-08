package org.eclipse.viatra.cep.emf.notification.model.traits;

@SuppressWarnings("all")
public interface EObject {
  Object getNotifier();
  
  void setNotifier(final Object notifier);
  
  Object getFeature();
  
  void setFeature(final Object feature);
  
  Object getOldValue();
  
  void setOldValue(final Object oldValue);
  
  Object getNewValue();
  
  void setNewValue(final Object newValue);
}
