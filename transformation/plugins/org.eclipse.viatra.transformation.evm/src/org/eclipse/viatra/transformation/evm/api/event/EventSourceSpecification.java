/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
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
     * 
     * @return
     */
    EventFilter<EventAtom> createEmptyFilter();
    
// NOTE we don't want to restrict filtering by atoms  
//    EventFilter<EventAtom> createFilter(EventAtom atom);
    
    AbstractRuleInstanceBuilder<EventAtom> getRuleInstanceBuilder(EventRealm realm);
}
