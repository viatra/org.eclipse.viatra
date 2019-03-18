/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific;

import java.util.Objects;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.transformation.evm.api.EventDrivenVM;
import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.specific.event.ViatraQueryEventRealm;

/**
 * @author Abel Hegedus
 *
 */
public class RuleEngines {

    private RuleEngines() {/*Utility class constructor*/}
    
    /**
     * Creates a new rule engine that is initialized over the given
     * ViatraQueryEngine and an agenda without rules.
    
     * @param engine
     * @return the prepared rule engine
     */
    public static RuleEngine createViatraQueryRuleEngine(final ViatraQueryEngine engine) {
        return EventDrivenVM.createRuleEngine(ViatraQueryEventRealm.create(engine));
    }

    /**
     * Creates a new rule engine that is initialized over the given
     * ViatraQueryEngine and an agenda with the given (unfiltered) rule specifications.
    
     * @param engine
     * @param specifications
     * @return the prepared rule engine
     */
    public static RuleEngine createViatraQueryRuleEngine(final ViatraQueryEngine engine,
            final Set<RuleSpecification<?>> specifications) {
        Objects.requireNonNull(specifications, "Cannot create rule engine with null rule specification set");
        RuleEngine ruleEngine = createViatraQueryRuleEngine(engine);
        for (RuleSpecification<?> ruleSpecification : specifications) {
            ruleEngine.addRule(ruleSpecification);
        }
        return ruleEngine;
    }

}
