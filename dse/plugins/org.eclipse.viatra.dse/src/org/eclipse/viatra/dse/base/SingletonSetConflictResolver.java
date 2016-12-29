/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.base;

import org.eclipse.viatra.transformation.evm.api.RuleEngine;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;

/**
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class SingletonSetConflictResolver implements ConflictResolver {

    protected ChangeableConflictSet conflictSet;
    protected ConflictResolver conflictResolver;
    protected ConflictResolver prevConflictResolver;
    protected RuleEngine ruleEngine;

    public SingletonSetConflictResolver(ConflictResolver conflictResolver) {
        this.conflictResolver = conflictResolver;
        conflictSet = conflictResolver.createConflictSet();
    }

    @Override
    public ChangeableConflictSet createConflictSet() {
        return conflictSet;
    }

    public void changeConflictResolver(ConflictResolver conflictResolver) {
        ConflictResolver tmp = this.conflictResolver;
        this.conflictResolver = conflictResolver;
        prevConflictResolver = tmp;
        conflictSet = conflictResolver.createConflictSet();
        ruleEngine.setConflictResolver(this);
    }

    public void changeConflictResolverBack() {
        changeConflictResolver(prevConflictResolver);
    }

    public void setRuleEngine(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
        ruleEngine.setConflictResolver(this);
    }
}
