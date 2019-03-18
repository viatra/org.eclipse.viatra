/*******************************************************************************
 * Copyright (c) 2004-2013, Abel Hegedus, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.rules;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;


public interface ITransformationRule<Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> {

    String getName();
    RuleSpecification<Match> getRuleSpecification();
    IQuerySpecification<Matcher> getPrecondition();
    /**
     * Returns the event filter set up for this rule; if no specific filter is
     * set up, an dedicated empty filter is returned
     * 
     * @return the event filter for this rule, never null
     */
    EventFilter<? super Match> getFilter();
}
