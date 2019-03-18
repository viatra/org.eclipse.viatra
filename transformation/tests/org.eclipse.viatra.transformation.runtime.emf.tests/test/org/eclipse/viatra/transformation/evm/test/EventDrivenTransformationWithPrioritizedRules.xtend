/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.test

import org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven.EventDrivenTransformation
import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRuleFactory
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRule
import org.eclipse.viatra.query.runtime.cps.tests.queries.ApplicationInstancesMatcher
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum
import org.eclipse.viatra.transformation.evm.specific.resolver.InvertedDisappearancePriorityConflictResolver
import org.eclipse.viatra.query.runtime.cps.tests.queries.HostInstancesMatcher
import org.eclipse.viatra.query.runtime.cps.tests.queries.ApplicationTypesMatcher

class EventDrivenTransformationWithPrioritizedRules {

    extension EventDrivenTransformation transformation

    extension EventDrivenTransformationRuleFactory = new EventDrivenTransformationRuleFactory

    val EventDrivenTransformationRule<?, ?> atRule
    val EventDrivenTransformationRule<?, ?> aiRule
    val EventDrivenTransformationRule<?, ?> hiRule
    
    val result = new StringBuilder

    new(Resource resource) {
        this(ViatraQueryEngine.on(new EMFScope(resource)))
    }
    
    new(ViatraQueryEngine engine) {
        atRule = createRule(ApplicationTypesMatcher.querySpecification).action(
            CRUDActivationStateEnum.CREATED) [
            result.append(AT.identifier)
        ].build
        aiRule = createRule(ApplicationInstancesMatcher.querySpecification).action(
            CRUDActivationStateEnum.CREATED) [
            result.append(AI.identifier)
        ].build
        hiRule = createRule(HostInstancesMatcher.querySpecification).action(
            CRUDActivationStateEnum.CREATED) [
            result.append(hi.identifier)
        ].build
        
        val cr = new InvertedDisappearancePriorityConflictResolver
        cr.setPriority(atRule.ruleSpecification, 1)
        cr.setPriority(hiRule.ruleSpecification, 2)
        cr.setPriority(aiRule.ruleSpecification, 3)
        
        
        transformation = EventDrivenTransformation.forEngine(engine)
            .addRule(atRule)
            .addRule(hiRule)
            .addRule(aiRule)
            .setConflictResolver(cr)
            .build
    }

    def execute() {
        transformation.executionSchema.startUnscheduledExecution
        return result.toString
    }

    def dispose() {
        if (transformation !== null) {
            transformation.dispose
        }
        transformation = null
    }
}
