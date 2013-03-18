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
package org.eclipse.incquery.databinding.runtime.collection;

import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.specific.RulePriorityActivationComparator;

/**
 * @author Abel Hegedus
 *
 */
public class PriorityObservablePatternMatchSet<Match extends IPatternMatch> extends ObservablePatternMatchSet<Match> {

    /**
     * Priority support is only available with an existing engine that has a
     * {@link RulePriorityActivationComparator} set up.
     *
     * @param factory
     * @param engine
     */
    public <Matcher extends IncQueryMatcher<Match>> PriorityObservablePatternMatchSet(IMatcherFactory<Matcher> factory, RuleEngine engine, int priority) {
        super(factory);
        ObservableCollectionHelper.addPrioritizedRuleSpecification(engine, getSpecification(), priority);
    }

}
