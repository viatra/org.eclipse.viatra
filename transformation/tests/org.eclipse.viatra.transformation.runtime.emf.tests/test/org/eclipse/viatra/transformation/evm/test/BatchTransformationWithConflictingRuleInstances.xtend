/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
 package org.eclipse.viatra.transformation.evm.test

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.AllReachableStatesQuerySpecification
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRuleFactory
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformation
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformationStatements

class BatchTransformationWithConflictingRuleInstances {

    extension BatchTransformation transformation
    extension BatchTransformationStatements statements
    
    extension BatchTransformationRuleFactory = new BatchTransformationRuleFactory

    val exampleRule = createRule.name("ExampleRule")
        .precondition(AllReachableStatesQuerySpecification.instance)
        .action [
             s1.outgoingTransitions.clear
        ].build

    new(Resource resource) {
        val scope = new EMFScope(resource)
        val engine = ViatraQueryEngine.on(scope);
        
        transformation = BatchTransformation.forEngine(engine).build
        statements = transformation.transformationStatements
    }

    def executeAll() {
      exampleRule.fireAllCurrent
    }
    
    def executeOneByOne() {
      exampleRule.fireWhilePossible
    }

    def dispose() {
        transformation?.dispose
        transformation = null
    }
}
