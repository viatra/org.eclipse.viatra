/**
 * Copyright (c) 2015-2016, IncQuery Labs Ltd. and Ericsson AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.viatra.integration.evm.jdt;

import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public abstract class CompositeEventFilter<EventAtom extends Object> implements EventFilter<EventAtom> {
  @Accessors
  private final EventFilter<EventAtom> innerFilter;
  
  public CompositeEventFilter(final EventFilter<EventAtom> filter) {
    this.innerFilter = filter;
  }
  
  @Override
  public boolean isProcessable(final EventAtom eventAtom) {
    boolean _and = false;
    boolean _isCompositeProcessable = this.isCompositeProcessable(eventAtom);
    if (!_isCompositeProcessable) {
      _and = false;
    } else {
      boolean _isProcessable = this.innerFilter.isProcessable(eventAtom);
      _and = _isProcessable;
    }
    return _and;
  }
  
  public abstract boolean isCompositeProcessable(final EventAtom eventAtom);
  
  @Pure
  public EventFilter<EventAtom> getInnerFilter() {
    return this.innerFilter;
  }
}
