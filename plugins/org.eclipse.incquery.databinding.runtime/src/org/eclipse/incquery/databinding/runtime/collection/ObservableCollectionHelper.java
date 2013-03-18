/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.databinding.runtime.collection;

import java.util.Comparator;

import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.base.itc.alg.incscc.Direction;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.ActivationState;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.specific.DefaultActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.specific.RulePriorityActivationComparator;
import org.eclipse.incquery.runtime.evm.specific.Rules;
import org.eclipse.incquery.runtime.evm.specific.StatelessJob;

import com.google.common.collect.Sets;

/**
 * Utility class to prepare a rule in an agenda for an observable collection. For use cases, see
 * {@link ObservablePatternMatchSet} and {@link ObservablePatternMatchList}.
 * 
 * @author Abel Hegedus
 * 
 */
public final class ObservableCollectionHelper {

    /**
     * Constructor hidden for utility class
     */
    private ObservableCollectionHelper() {
    }

    /**
     * Creates the rule used for updating the results in the given agenda.
     * 
     * @param observableCollectionUpdate
     *            the observable collection to handle
     * @param factory
     *            the {@link IMatcherFactory} used to create the rule
     */
    @SuppressWarnings("unchecked")
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> RuleSpecification<Match> createRuleSpecification(
            IObservablePatternMatchCollectionUpdate<Match> observableCollectionUpdate, IMatcherFactory<Matcher> factory) {

        Job<Match> insertJob = new StatelessJob<Match>(ActivationState.APPEARED,
                new ObservableCollectionProcessor<Match>(Direction.INSERT, observableCollectionUpdate));
        Job<Match> deleteJob = new StatelessJob<Match>(ActivationState.DISAPPEARED,
                new ObservableCollectionProcessor<Match>(Direction.DELETE, observableCollectionUpdate));
        return Rules.newSimpleMatcherRuleSpecification(factory, DefaultActivationLifeCycle.DEFAULT_NO_UPDATE,
                Sets.newHashSet(insertJob, deleteJob));
    }

    public static <Match extends IPatternMatch> void addPrioritizedRuleSpecification(RuleEngine engine, RuleSpecification<Match> specification, int priority) {
        Comparator<Activation<?>> comparator = engine.getActivationComparator();
        if (comparator instanceof RulePriorityActivationComparator) {
            ((RulePriorityActivationComparator) comparator).setRuleSpecificationPriority(specification, priority);
        }
        engine.addRule(specification, true);
    }

}
