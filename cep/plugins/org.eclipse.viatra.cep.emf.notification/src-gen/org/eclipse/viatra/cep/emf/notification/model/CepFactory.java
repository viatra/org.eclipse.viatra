package org.eclipse.viatra.cep.emf.notification.model;

import com.google.common.collect.Lists;
import java.util.List;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;
import org.eclipse.viatra.cep.emf.notification.model.events.ADD_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.ADD_MANY_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.MOVE_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.REMOVE_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.REMOVE_MANY_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.RESOLVE_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.SET_Event;
import org.eclipse.viatra.cep.emf.notification.model.events.UNSET_Event;
import org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.ADD_MANY_Pattern;
import org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.ADD_Pattern;
import org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.MOVE_Pattern;
import org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.REMOVE_MANY_Pattern;
import org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.REMOVE_Pattern;
import org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.RESOLVE_Pattern;
import org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.SET_Pattern;
import org.eclipse.viatra.cep.emf.notification.model.patterns.atomic.UNSET_Pattern;

@SuppressWarnings("all")
public class CepFactory {
  private static CepFactory instance;
  
  public static CepFactory getInstance() {
    if(instance == null){
    	instance = new CepFactory();
    }
    return instance;
  }
  
  /**
   * Factory method for event class {@link ADD_Event}.
   */
  public ADD_Event createADD_Event(final EventSource eventSource) {
    return new ADD_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link ADD_Event}.
   */
  public ADD_Event createADD_Event() {
    return new ADD_Event(null);
  }
  
  /**
   * Factory method for event class {@link ADD_MANY_Event}.
   */
  public ADD_MANY_Event createADD_MANY_Event(final EventSource eventSource) {
    return new ADD_MANY_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link ADD_MANY_Event}.
   */
  public ADD_MANY_Event createADD_MANY_Event() {
    return new ADD_MANY_Event(null);
  }
  
  /**
   * Factory method for event class {@link MOVE_Event}.
   */
  public MOVE_Event createMOVE_Event(final EventSource eventSource) {
    return new MOVE_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link MOVE_Event}.
   */
  public MOVE_Event createMOVE_Event() {
    return new MOVE_Event(null);
  }
  
  /**
   * Factory method for event class {@link REMOVE_Event}.
   */
  public REMOVE_Event createREMOVE_Event(final EventSource eventSource) {
    return new REMOVE_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link REMOVE_Event}.
   */
  public REMOVE_Event createREMOVE_Event() {
    return new REMOVE_Event(null);
  }
  
  /**
   * Factory method for event class {@link REMOVE_MANY_Event}.
   */
  public REMOVE_MANY_Event createREMOVE_MANY_Event(final EventSource eventSource) {
    return new REMOVE_MANY_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link REMOVE_MANY_Event}.
   */
  public REMOVE_MANY_Event createREMOVE_MANY_Event() {
    return new REMOVE_MANY_Event(null);
  }
  
  /**
   * Factory method for event class {@link RESOLVE_Event}.
   */
  public RESOLVE_Event createRESOLVE_Event(final EventSource eventSource) {
    return new RESOLVE_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link RESOLVE_Event}.
   */
  public RESOLVE_Event createRESOLVE_Event() {
    return new RESOLVE_Event(null);
  }
  
  /**
   * Factory method for event class {@link SET_Event}.
   */
  public SET_Event createSET_Event(final EventSource eventSource) {
    return new SET_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link SET_Event}.
   */
  public SET_Event createSET_Event() {
    return new SET_Event(null);
  }
  
  /**
   * Factory method for event class {@link UNSET_Event}.
   */
  public UNSET_Event createUNSET_Event(final EventSource eventSource) {
    return new UNSET_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link UNSET_Event}.
   */
  public UNSET_Event createUNSET_Event() {
    return new UNSET_Event(null);
  }
  
  /**
   * Factory method for atomic event pattern {@link ADD_Pattern}.
   */
  public ADD_Pattern createADD_Pattern() {
    return new ADD_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link ADD_MANY_Pattern}.
   */
  public ADD_MANY_Pattern createADD_MANY_Pattern() {
    return new ADD_MANY_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link MOVE_Pattern}.
   */
  public MOVE_Pattern createMOVE_Pattern() {
    return new MOVE_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link REMOVE_Pattern}.
   */
  public REMOVE_Pattern createREMOVE_Pattern() {
    return new REMOVE_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link REMOVE_MANY_Pattern}.
   */
  public REMOVE_MANY_Pattern createREMOVE_MANY_Pattern() {
    return new REMOVE_MANY_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link RESOLVE_Pattern}.
   */
  public RESOLVE_Pattern createRESOLVE_Pattern() {
    return new RESOLVE_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link SET_Pattern}.
   */
  public SET_Pattern createSET_Pattern() {
    return new SET_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link UNSET_Pattern}.
   */
  public UNSET_Pattern createUNSET_Pattern() {
    return new UNSET_Pattern();
  }
  
  /**
   * Factory method for instantiating every defined rule.
   */
  public List<Class<? extends ICepRule>> allRules() {
    List<Class<? extends ICepRule>> rules = Lists.newArrayList();
    return rules;
  }
}
