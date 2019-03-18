/*******************************************************************************
 * Copyright (c) 2010-2019, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.test

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRuleFactory
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformation
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformationStatements
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstanceQuerySpecification
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule
import org.eclipse.viatra.query.runtime.cps.tests.queries.HostInstanceMatcher
import org.eclipse.viatra.query.runtime.cps.tests.queries.HostInstanceMatch
import org.eclipse.viatra.transformation.evm.api.event.EventFilter

class BatchTransformationWithDefaultFilter {

    extension BatchTransformation transformation
    extension BatchTransformationStatements statements
    
    extension BatchTransformationRuleFactory = new BatchTransformationRuleFactory
    var counter = 0
    val BatchTransformationRule<HostInstanceMatch, HostInstanceMatcher> filteredRule
    val BatchTransformationRule<HostInstanceMatch, HostInstanceMatcher> unfilteredRule

    new(Resource resource, EventFilter<HostInstanceMatch> filter) {
        val scope = new EMFScope(resource)
        val engine = ViatraQueryEngine.on(scope);
        
        filteredRule = createRule(HostInstanceQuerySpecification.instance).name("CounterRule")
            .filter(filter)
            .action [
                 counter ++
            ].build
        unfilteredRule = createRule(HostInstanceQuerySpecification.instance).name("CounterRule")
            .action [
                 counter ++
            ].build
        
        transformation = BatchTransformation.forEngine(engine).build
        statements = transformation.transformationStatements
    }

    def BatchTransformationRule<HostInstanceMatch, HostInstanceMatcher> getFilteredRule() {
        filteredRule
    }
    
    def BatchTransformationRule<HostInstanceMatch, HostInstanceMatcher> getUnfilteredRule() {
        unfilteredRule
    }

    def int countMatchesNoFilter() {
      counter = 0
      unfilteredRule.fireAllCurrent()
      return counter
    }
    
    def int countMatchesDefaultFilter() {
      counter = 0
      filteredRule.fireAllCurrent()
      return counter
    }
    
    def int countMatchesOverriddenDefaultFilter(EventFilter<HostInstanceMatch> filter) {
      counter = 0
      filteredRule.fireAllCurrent(filter)
      return counter
    }
    
    def int countMatchesOverriddenEmptyFilter(EventFilter<HostInstanceMatch> filter) {
      counter = 0
      unfilteredRule.fireAllCurrent(filter)
      return counter
    }

    def dispose() {
        transformation?.dispose
        transformation = null
    }
}
