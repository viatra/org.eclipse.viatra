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
package org.eclipse.viatra.transformation.evm.specific.event;

import static com.google.common.base.Preconditions.checkArgument;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.evm.api.RuleInstance;
import org.eclipse.viatra.transformation.evm.api.event.AbstractRuleInstanceBuilder;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;

public class ViatraQueryRuleInstanceBuilder<Match extends IPatternMatch> extends AbstractRuleInstanceBuilder<Match> {

    private ViatraQueryEventRealm realm;
    private ViatraQueryEventSourceSpecification<Match> sourceSpecification;
    
    @Override
    public void prepareRuleInstance(RuleInstance<Match> ruleInstance, EventFilter<? super Match> filter) {
        checkArgument(ruleInstance != null, "Cannot prepare null rule instance!");
        ViatraQueryEventSource<Match> source = realm.createSource(sourceSpecification);
        ViatraQueryEventHandler<Match> handler = new ViatraQueryEventHandler<Match>(source, filter, ruleInstance);
        handler.prepareEventHandler();
    }

    protected ViatraQueryRuleInstanceBuilder(ViatraQueryEventRealm realm, ViatraQueryEventSourceSpecification<Match> sourceSpecification) {
        checkArgument(realm != null, "Cannot create builder with null realm!");
        checkArgument(sourceSpecification != null, "Cannot create builder with null realm!");
        this.realm = realm;
        this.sourceSpecification = sourceSpecification;
    } 
    
}
