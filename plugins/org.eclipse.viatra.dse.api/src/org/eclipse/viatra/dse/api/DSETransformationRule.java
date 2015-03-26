/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.api;

import java.util.Map;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra.emf.runtime.rules.batch.BatchTransformationRule;

import com.google.common.base.Preconditions;

/**
 * An instance of this class is a specification of a graph transformation rule on a given metamodel. Such a rule
 * consists of a left hand side (LHS), which is specified by an {@link IQuerySpecification} and a right hand side (RHS),
 * which is specified by an {@link IMatchProcessor}.
 * 
 * @author Andras Szabolcs Nagy
 * 
 * @param <Match>
 *            An IncQuery pattern match - left hand side of the rule
 * @param <Matcher>
 *            An IncQuery pattern matcher - left hand side of the rule
 * 
 */
public class DSETransformationRule<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> extends
        BatchTransformationRule<Match, Matcher> {

    public interface ActivationCostProcessor<Match> {
        public Map<String, Double> process(Match match);
    }
    
    private RuleMetaData metaData;
    private Map<String, Double> costs;
    private ActivationCostProcessor<Match> activationCostProcessor;

    
    public DSETransformationRule(String name, IQuerySpecification<Matcher> querySpec,
            IMatchProcessor<Match> action) {
        super(name, querySpec, BatchTransformationRule.STATELESS_RULE_LIFECYCLE, action);

        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(querySpec);
        Preconditions.checkNotNull(action);

    }

    public DSETransformationRule(IQuerySpecification<Matcher> querySpec,
            IMatchProcessor<Match> action) {
        this(querySpec.getFullyQualifiedName(), querySpec, action);
    }

    public Map<String, Double> measureCosts(IPatternMatch match) {
        if (activationCostProcessor != null) {
            @SuppressWarnings("unchecked")
            Match typedMatch = (Match) match;
            return activationCostProcessor.process(typedMatch);
        } else {
            return null;
        }
    }
    
    public RuleMetaData getMetaData() {
        return metaData;
    }

    public Map<String, Double> getCosts() {
        return costs;
    }

    public void setCosts(Map<String, Double> costs) {
        this.costs = costs;
    }

    public ActivationCostProcessor<Match> getActivationCostProcessor() {
        return activationCostProcessor;
    }

    public void setActivationCostProcessor(ActivationCostProcessor<Match> activationCostProcessor) {
        this.activationCostProcessor = activationCostProcessor;
    }
    
    @Override
    public int hashCode() {
        return getPrecondition().getFullyQualifiedName().hashCode();
    }

    public void setMetaData(RuleMetaData metaData) {
        this.metaData = metaData;
    }

}
