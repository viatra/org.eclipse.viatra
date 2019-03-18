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
import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.IModelManipulations
import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.SimpleModelManipulations

class BatchTransformationWithFiringStatus {

    extension BatchTransformation transformation
    extension BatchTransformationStatements statements
    extension IModelManipulations manipulations
    
    extension BatchTransformationRuleFactory = new BatchTransformationRuleFactory
    val BatchTransformationRule<HostInstanceMatch, HostInstanceMatcher> filteredRule
    val BatchTransformationRule<HostInstanceMatch, HostInstanceMatcher> unfilteredRule

    new(Resource resource, EventFilter<HostInstanceMatch> filter) {
        val scope = new EMFScope(resource)
        val engine = ViatraQueryEngine.on(scope);
        
        filteredRule = createRule(HostInstanceQuerySpecification.instance).name("CounterRule")
            .filter(filter)
            .action [
                 host.remove
            ].build
        unfilteredRule = createRule(HostInstanceQuerySpecification.instance).name("CounterRule")
            .action [
                 host.remove
            ].build
        
        transformation = BatchTransformation
            .forEngine(engine)
            .addRule(filteredRule)
            .addRule(unfilteredRule)
            .build
        statements = transformation.transformationStatements
        manipulations = new SimpleModelManipulations(engine)
    }

    def BatchTransformationRule<HostInstanceMatch, HostInstanceMatcher> getFilteredRule() {
        filteredRule
    }
    
    def BatchTransformationRule<HostInstanceMatch, HostInstanceMatcher> getUnfilteredRule() {
        unfilteredRule
    }

    def boolean canTransformationExecute() {
      return statements.hasCurrent()
    }
    
    def boolean canFireFiltered() {
      return statements.hasCurrent(filteredRule)
 
    }
    def boolean canFireUnfiltered() {
      return statements.hasCurrent(unfilteredRule)
    }
    def boolean canFireFiltered(EventFilter<HostInstanceMatch> filter) {
      return statements.hasCurrent(filteredRule, filter)
 
    }
    def boolean canFireUnfiltered(EventFilter<HostInstanceMatch> filter) {
      return statements.hasCurrent(unfilteredRule, filter)
    }
    
    def void fireFiltered() {
      filteredRule.fireAllCurrent()
    }
    
    def void fireUnfiltered() {
      unfilteredRule.fireAllCurrent()
    }

    def dispose() {
        transformation?.dispose
        transformation = null
    }
}
