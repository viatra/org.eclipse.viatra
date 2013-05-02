///*******************************************************************************
// * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *   Abel Hegedus - initial API and implementation
// *******************************************************************************/
//package org.eclipse.incquery.databinding.runtime.collection;
//
//import org.eclipse.incquery.runtime.api.IMatcherquerySpecification;
//import org.eclipse.incquery.runtime.api.IPatternMatch;
//import org.eclipse.incquery.runtime.api.IncQueryMatcher;
//import org.eclipse.incquery.runtime.evm.api.RuleEngine;
//import org.eclipse.incquery.runtime.evm.specific.RulePriorityActivationComparator;
//
//
///**
// * @author Abel Hegedus
// *
// */
//public class PriorityObservablePatternMatchList<Match extends IPatternMatch> 
//extends ObservablePatternMatchList<Match> {
//
//    /**
//     * Priority support is only available with an existing engine that has a
//     * {@link RulePriorityActivationComparator} set up.
//     * 
//     * @param querySpecification
//     * @param engine
//     * @param priority
//     */
//    public <Matcher extends IncQueryMatcher<Match>> PriorityObservablePatternMatchList(IMatcherquerySpecification<Matcher> querySpecification, RuleEngine engine, int priority) {
//        this(querySpecification, engine, priority, null);
//    }
//
//    /**
//     * Priority support is only available with an existing engine that has a
//     * {@link RulePriorityActivationComparator} set up.
//     * 
//     * @param querySpecification
//     * @param engine
//     * @param priority
//     * @param filter
//     */
//    public <Matcher extends IncQueryMatcher<Match>> PriorityObservablePatternMatchList(IMatcherquerySpecification<Matcher> querySpecification, RuleEngine engine, int priority, Match filter) {
//        super(querySpecification);
//        ObservableCollectionHelper.addPrioritizedRuleSpecification(engine, getSpecification(), priority, filter);
//    }
//    
//    
//    
//    
//    
//}
