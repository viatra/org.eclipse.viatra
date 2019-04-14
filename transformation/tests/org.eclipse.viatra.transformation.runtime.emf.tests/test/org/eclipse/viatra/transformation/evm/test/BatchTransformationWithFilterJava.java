/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.test;

import java.util.Collections;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule;
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRuleFactory;
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformation;
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformationStatements;
import org.eclipse.viatra.query.runtime.cps.tests.queries.HostInstanceMatch;
import org.eclipse.viatra.query.runtime.cps.tests.queries.HostInstanceMatcher;
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstanceQuerySpecification;
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance;
import org.eclipse.viatra.transformation.runtime.emf.filters.MatchParameterFilter;
import org.eclipse.viatra.transformation.runtime.emf.filters.MatchParameterPredicateFilter;

public class BatchTransformationWithFilterJava {

    private BatchTransformation transformation;
    private BatchTransformationStatements statements;
    
    private BatchTransformationRuleFactory factory = new BatchTransformationRuleFactory();
    
    private int counter = 0;
    
    private BatchTransformationRule<HostInstanceMatch, HostInstanceMatcher> typeInferredRule = factory
            .createRule(HostInstanceQuerySpecification.instance())
            .name("CounterRule")
            .action(m -> counter++)
            .build();
    
    @SuppressWarnings("deprecation")
    /* This factory ensures the deprecated methods are also tested */
    private BatchTransformationRule<HostInstanceMatch, HostInstanceMatcher> castTypedRule = factory
            .<HostInstanceMatch, HostInstanceMatcher>createRule()
            .name("CounterRule")
            .precondition(HostInstanceQuerySpecification.instance())
            .action(m -> counter++)
            .build();

    public BatchTransformationWithFilterJava(Resource resource) {
        EMFScope scope = new EMFScope(resource);
        ViatraQueryEngine engine = ViatraQueryEngine.on(scope);
        
        transformation = BatchTransformation.forEngine(engine).build();
        statements = transformation.getTransformationStatements();
    }

    public int callTypeInferredRule(HostInstance instance) {
      counter = 0;
      final MatchParameterFilter filter = new MatchParameterFilter(Collections.singletonMap("host", instance));
      statements.fireAllCurrent(typeInferredRule, filter);
      return counter;
    }
    
    public int callTypeInferredWithPredicateFilterRule(Set<HostInstance> instances) {
        counter = 0;
        final MatchParameterPredicateFilter filter = new MatchParameterPredicateFilter("host", instances::contains);
        statements.fireAllCurrent(typeInferredRule, filter);
        return counter;
    }
    
    public int callCastTypeRule(HostInstance instance) {
        counter = 0;
        final MatchParameterFilter filter = new MatchParameterFilter(Collections.singletonMap("host", instance));
        statements.fireAllCurrent(castTypedRule, filter);
        return counter;
    }
    
    public int callCastTypeWithPredicateFilterRule(Set<HostInstance> instances) {
        counter = 0;
        final MatchParameterPredicateFilter filter = new MatchParameterPredicateFilter("host", instances::contains);
        statements.fireAllCurrent(castTypedRule, filter);
        return counter;
    }

}
