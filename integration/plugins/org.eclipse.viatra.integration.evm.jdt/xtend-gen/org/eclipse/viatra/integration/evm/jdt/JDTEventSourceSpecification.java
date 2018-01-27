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
import org.eclipse.viatra.integration.evm.jdt.JDTEventFilter;
import org.eclipse.viatra.integration.evm.jdt.JDTEventHandler;
import org.eclipse.viatra.integration.evm.jdt.JDTEventSource;
import org.eclipse.viatra.integration.evm.jdt.JDTRealm;
import org.eclipse.viatra.transformation.evm.api.RuleInstance;
import org.eclipse.viatra.transformation.evm.api.event.AbstractRuleInstanceBuilder;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;
import org.eclipse.viatra.transformation.evm.api.event.EventSourceSpecification;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;

@SuppressWarnings("all")
public class JDTEventSourceSpecification implements EventSourceSpecification<JDTEventAtom> {
  @Override
  public EventFilter<JDTEventAtom> createEmptyFilter() {
    return new JDTEventFilter();
  }
  
  @Override
  public AbstractRuleInstanceBuilder<JDTEventAtom> getRuleInstanceBuilder(final EventRealm realm) {
    final Procedure2<RuleInstance<JDTEventAtom>, EventFilter<? super JDTEventAtom>> _function = new Procedure2<RuleInstance<JDTEventAtom>, EventFilter<? super JDTEventAtom>>() {
      @Override
      public void apply(final RuleInstance<JDTEventAtom> ruleInstance, final EventFilter<? super JDTEventAtom> filter) {
        JDTEventSource source = new JDTEventSource(JDTEventSourceSpecification.this, ((JDTRealm) realm));
        JDTEventHandler handler = new JDTEventHandler(source, filter, ruleInstance);
        source.addHandler(handler);
        ruleInstance.setHandler(handler);
      }
    };
    return ((AbstractRuleInstanceBuilder<JDTEventAtom>) new AbstractRuleInstanceBuilder<JDTEventAtom>() {
        public void prepareRuleInstance(RuleInstance<JDTEventAtom> arg0, EventFilter<? super JDTEventAtom> arg1) {
          _function.apply(arg0, arg1);
        }
    });
  }
}
