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

    public RuleMetaData getMetaData() {
        return metaData;
    }

    @Override
    public int hashCode() {
        return getPrecondition().getFullyQualifiedName().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DSETransformationRule<?, ?>) {
            IQuerySpecification<?> precondition2 = ((DSETransformationRule<?,?>) obj).getPrecondition();
            return getPrecondition().getFullyQualifiedName().equals(precondition2.getFullyQualifiedName());
        } else {
            return false;
        }
    }

    public void setMetaData(RuleMetaData metaData) {
        this.metaData = metaData;
    }

}
