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
package org.eclipse.viatra.transformation.evm.specific;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.eclipse.viatra.query.runtime.api.IncQueryEngine;
import org.eclipse.viatra.transformation.evm.api.EventDrivenVM;
import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.specific.event.IncQueryEventRealm;

/**
 * @author Abel Hegedus
 *
 */
public class RuleEngines {

    /**
     * Creates a new rule engine that is initialized over the given
     * IncQueryEngine and an agenda without rules.
    
     * @param engine
     * @return the prepared rule engine
     */
    public static RuleEngine createIncQueryRuleEngine(final IncQueryEngine engine) {
        //return EventDrivenVM.createRuleEngine(IncQueryEventRealm.create(engine));
        return EventDrivenVM.createRuleEngine(IncQueryEventRealm.create(engine));
    }

    /**
     * Creates a new rule engine that is initialized over the given
     * IncQueryEngine and an agenda with the given (unfiltered) rule specifications.
    
     * @param engine
     * @param specifications
     * @return the prepared rule engine
     */
    public static RuleEngine createIncQueryRuleEngine(final IncQueryEngine engine,
            final Set<RuleSpecification<?>> specifications) {
        checkNotNull(specifications, "Cannot create rule engine with null rule specification set");
        RuleEngine ruleEngine = createIncQueryRuleEngine(engine);
        for (RuleSpecification<?> ruleSpecification : specifications) {
            ruleEngine.addRule(ruleSpecification);
        }
        return ruleEngine;
    }

}
