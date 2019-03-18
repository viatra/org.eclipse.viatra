/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.event;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.RuleInstance;
import org.eclipse.viatra.transformation.evm.api.event.AbstractRuleInstanceBuilder;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;

public class ViatraQueryRuleInstanceBuilder<Match extends IPatternMatch> extends AbstractRuleInstanceBuilder<Match> {

    private ViatraQueryEventRealm realm;
    private ViatraQueryEventSourceSpecification<Match> sourceSpecification;
    
    @Override
    public void prepareRuleInstance(RuleInstance<Match> ruleInstance, EventFilter<? super Match> filter) {
        Preconditions.checkArgument(ruleInstance != null, "Cannot prepare null rule instance!");
        ViatraQueryEventSource<Match> source = realm.createSource(sourceSpecification);
        ViatraQueryEventHandler<Match> handler = new ViatraQueryEventHandler<Match>(source, filter, ruleInstance);
        handler.prepareEventHandler();
    }

    protected ViatraQueryRuleInstanceBuilder(ViatraQueryEventRealm realm, ViatraQueryEventSourceSpecification<Match> sourceSpecification) {
        Preconditions.checkArgument(realm != null, "Cannot create builder with null realm!");
        Preconditions.checkArgument(sourceSpecification != null, "Cannot create builder with null realm!");
        this.realm = realm;
        this.sourceSpecification = sourceSpecification;
    } 
    
}
