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

import org.eclipse.viatra.integration.evm.jdt.JDTEventAtom;
import org.eclipse.viatra.transformation.evm.api.event.Event;
import org.eclipse.viatra.transformation.evm.api.event.EventType;

@SuppressWarnings("all")
public class JDTEvent implements Event<JDTEventAtom> {
  private EventType type;
  
  private JDTEventAtom atom;
  
  /**
   * @param type
   * @param atom
   */
  public JDTEvent(final EventType type, final JDTEventAtom atom) {
    this.type = type;
    this.atom = atom;
  }
  
  @Override
  public EventType getEventType() {
    return this.type;
  }
  
  @Override
  public JDTEventAtom getEventAtom() {
    return this.atom;
  }
}
