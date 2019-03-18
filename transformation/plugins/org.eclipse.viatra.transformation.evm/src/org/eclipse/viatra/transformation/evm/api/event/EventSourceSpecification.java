/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.event;

/**
 * 
 * 
 * @author Abel Hegedus
 * 
 */
public interface EventSourceSpecification<EventAtom> {
    
    /**
     * Returns a filter that does not filter out any incoming events.
     * </p>
     * 
     * <strong>Warning</strong>: either return a single instance, or make sure all returned instances are equal (
     * {@linkplain Object#equals(Object)} and {@linkplain Object#hashCode()}.
     */
    EventFilter<EventAtom> createEmptyFilter();
    
// NOTE we don't want to restrict filtering by atoms  
//    EventFilter<EventAtom> createFilter(EventAtom atom);
    
    AbstractRuleInstanceBuilder<EventAtom> getRuleInstanceBuilder(EventRealm realm);
}
