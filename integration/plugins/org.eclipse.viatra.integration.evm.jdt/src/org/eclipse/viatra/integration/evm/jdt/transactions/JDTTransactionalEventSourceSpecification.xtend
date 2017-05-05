/*******************************************************************************
 * Copyright (c) 2015-2016, IncQuery Labs Ltd. and Ericsson AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.evm.jdt.transactions

import org.eclipse.viatra.integration.evm.jdt.JDTEventAtom
import org.eclipse.viatra.integration.evm.jdt.JDTEventHandler
import org.eclipse.viatra.integration.evm.jdt.JDTEventSourceSpecification
import org.eclipse.viatra.integration.evm.jdt.JDTRealm
import org.eclipse.viatra.transformation.evm.api.RuleInstance
import org.eclipse.viatra.transformation.evm.api.event.AbstractRuleInstanceBuilder
import org.eclipse.viatra.transformation.evm.api.event.EventFilter
import org.eclipse.viatra.transformation.evm.api.event.EventRealm

class JDTTransactionalEventSourceSpecification extends JDTEventSourceSpecification {
    override getRuleInstanceBuilder(EventRealm realm) {
        return ( [ RuleInstance<JDTEventAtom> ruleInstance, EventFilter<? super JDTEventAtom> filter |
            val source = new JDTTransactionalEventSource(JDTTransactionalEventSourceSpecification.this, realm as JDTRealm)
            val handler = new JDTEventHandler(source, filter, ruleInstance)
            source.addHandler(handler)
            ruleInstance.handler = handler
        ] as AbstractRuleInstanceBuilder<JDTEventAtom>)
    }
}
