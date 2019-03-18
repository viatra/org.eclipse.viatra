/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.event;

import org.eclipse.viatra.transformation.evm.api.RuleInstance;


/**
 * @author Abel Hegedus
 * 
 */
public abstract class AbstractRuleInstanceBuilder<EventAtom> {

    public abstract void prepareRuleInstance(RuleInstance<EventAtom> ruleInstance, EventFilter<? super EventAtom> filter);
    
}
