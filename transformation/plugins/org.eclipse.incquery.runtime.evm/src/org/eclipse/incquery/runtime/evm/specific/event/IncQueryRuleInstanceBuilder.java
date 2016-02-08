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
package org.eclipse.incquery.runtime.evm.specific.event;

import static com.google.common.base.Preconditions.checkArgument;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.RuleInstance;
import org.eclipse.incquery.runtime.evm.api.event.AbstractRuleInstanceBuilder;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.exception.IncQueryException;

public class IncQueryRuleInstanceBuilder<Match extends IPatternMatch> extends AbstractRuleInstanceBuilder<Match> {

    private IncQueryEventRealm realm;
    private IncQueryEventSourceSpecification<Match> sourceSpecification;
    
    @Override
    public void prepareRuleInstance(RuleInstance<Match> ruleInstance, EventFilter<? super Match> filter) {
        try {
            checkArgument(ruleInstance != null, "Cannot prepare null rule instance!");
            // checkArgument(filter instanceof IncQueryEventFilter, "Filter must be IncQueryEventFilter!");
            IncQueryEventSource<Match> source = realm.createSource(sourceSpecification);
            IncQueryEventHandler<Match> handler = new IncQueryEventHandler<Match>(source, filter, ruleInstance);
            handler.prepareEventHandler();
        } catch (IncQueryException e) {
            throw new RuntimeException("Could not create matcher for event source definition " + sourceSpecification + " in realm "
                    + realm, e);
        }
    }

    protected IncQueryRuleInstanceBuilder(IncQueryEventRealm realm, IncQueryEventSourceSpecification<Match> sourceSpecification) {
        checkArgument(realm != null, "Cannot create builder with null realm!");
        checkArgument(sourceSpecification != null, "Cannot create builder with null realm!");
        this.realm = realm;
        this.sourceSpecification = sourceSpecification;
    } 
    
}
