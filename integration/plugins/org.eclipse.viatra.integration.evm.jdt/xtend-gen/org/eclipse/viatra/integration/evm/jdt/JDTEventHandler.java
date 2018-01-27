/**
 * Copyright (c) 2015-2016, IncQuery Labs Ltd., Ericsson AB, CEA LIST
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.viatra.integration.evm.jdt;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import java.util.Collection;
import java.util.Deque;
import java.util.Set;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.viatra.integration.evm.jdt.JDTEventAtom;
import org.eclipse.viatra.integration.evm.jdt.JDTEventSource;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleInstance;
import org.eclipse.viatra.transformation.evm.api.event.Event;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventHandler;
import org.eclipse.viatra.transformation.evm.api.event.EventSource;
import org.eclipse.viatra.transformation.evm.api.event.EventType;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class JDTEventHandler implements EventHandler<JDTEventAtom> {
  private EventFilter<? super JDTEventAtom> filter;
  
  private JDTEventSource source;
  
  private RuleInstance<JDTEventAtom> instance;
  
  public JDTEventHandler(final JDTEventSource source, final EventFilter<? super JDTEventAtom> filter, final RuleInstance<JDTEventAtom> instance) {
    this.source = source;
    this.filter = filter;
    this.instance = instance;
  }
  
  @Override
  public void handleEvent(final Event<JDTEventAtom> event) {
    EventType _eventType = event.getEventType();
    final EventType type = ((EventType) _eventType);
    final JDTEventAtom eventAtom = event.getEventAtom();
    boolean _isProcessable = this.filter.isProcessable(eventAtom);
    if (_isProcessable) {
      final Activation<JDTEventAtom> activation = this.getOrCreateActivation(eventAtom);
      this.instance.activationStateTransition(activation, type);
    }
  }
  
  @Override
  public EventSource<JDTEventAtom> getSource() {
    return this.source;
  }
  
  @Override
  public EventFilter<? super JDTEventAtom> getEventFilter() {
    return this.filter;
  }
  
  @Override
  public void dispose() {
    this.source.removeHandler(this);
  }
  
  private Activation<JDTEventAtom> getOrCreateActivation(final JDTEventAtom eventAtom) {
    final Collection<Activation<JDTEventAtom>> activations = this.instance.getAllActivations();
    final Function1<Activation<JDTEventAtom>, Boolean> _function = new Function1<Activation<JDTEventAtom>, Boolean>() {
      @Override
      public Boolean apply(final Activation<JDTEventAtom> it) {
        JDTEventAtom _atom = it.getAtom();
        return Boolean.valueOf(Objects.equal(_atom, eventAtom));
      }
    };
    final Activation<JDTEventAtom> activation = IterableExtensions.<Activation<JDTEventAtom>>findFirst(activations, _function);
    if ((activation == null)) {
      return this.instance.createActivation(eventAtom);
    } else {
      final JDTEventAtom atom = activation.getAtom();
      Optional<? extends IJavaElementDelta> _delta = eventAtom.getDelta();
      atom.setDelta(_delta);
      Optional<? extends IJavaElementDelta> _delta_1 = eventAtom.getDelta();
      Set<? extends IJavaElementDelta> _asSet = _delta_1.asSet();
      final Procedure1<IJavaElementDelta> _function_1 = new Procedure1<IJavaElementDelta>() {
        @Override
        public void apply(final IJavaElementDelta it) {
          Deque<IJavaElementDelta> _unprocessedDeltas = atom.getUnprocessedDeltas();
          _unprocessedDeltas.add(it);
        }
      };
      IterableExtensions.forEach(_asSet, _function_1);
    }
    return activation;
  }
}
