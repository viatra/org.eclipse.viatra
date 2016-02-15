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
package org.eclipse.incquery.runtime.evm.protoapp;

import org.apache.log4j.Level;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.EventDrivenVM;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.event.EventType.RuleEngineEventType;
import org.eclipse.incquery.runtime.evm.proto.ProtoActivationStates;
import org.eclipse.incquery.runtime.evm.proto.ProtoEventFilter;
import org.eclipse.incquery.runtime.evm.proto.ProtoEventSourceSpecification;
import org.eclipse.incquery.runtime.evm.proto.ProtoEventType;
import org.eclipse.incquery.runtime.evm.proto.ProtoRealm;
import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * @author Abel Hegedus
 *
 */
public class ProtoApp {

    @Test
    public void testProtoEventRealm() {

        final ProtoRealm protoRealm = new ProtoRealm();
        final RuleEngine engine = EventDrivenVM.createRuleEngine(protoRealm);
        engine.getLogger().setLevel(Level.DEBUG);

        final ActivationLifeCycle lifeCycle = ActivationLifeCycle.create(ProtoActivationStates.IS_NOT);
        lifeCycle.addStateTransition(ProtoActivationStates.IS_NOT, ProtoEventType.PUSH, ProtoActivationStates.IS);
        lifeCycle.addStateTransition(ProtoActivationStates.IS, RuleEngineEventType.FIRE, ProtoActivationStates.IS_NOT);

        final Job<String> job = new Job<String>(ProtoActivationStates.IS) {
            @Override
            protected void execute(final Activation<? extends String> activation, final Context context) {
                System.out.println("String pushed" + activation.getAtom());
            }
            @Override
            protected void handleError(final Activation<? extends String> activation, final Exception exception, final Context context) {
                // not gonna happen
            }
        };

        final ProtoEventSourceSpecification sourceSpec = new ProtoEventSourceSpecification("test");

        final RuleSpecification<String> ruleSpec = new RuleSpecification<String>(sourceSpec, lifeCycle, Sets.newHashSet(job));

        final ProtoEventFilter filter = new ProtoEventFilter("t");
        engine.addRule(ruleSpec, filter);

        protoRealm.pushString("PUSH THIS!");

        engine.getNextActivation().fire(Context.create());

    }


}
