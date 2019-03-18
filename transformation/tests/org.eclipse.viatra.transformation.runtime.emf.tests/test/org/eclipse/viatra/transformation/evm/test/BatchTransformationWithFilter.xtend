/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.test

import static extension org.eclipse.viatra.transformation.runtime.emf.transformation.TransformationExtensions.*

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRuleFactory
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformation
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformationStatements
import org.eclipse.viatra.query.runtime.cps.tests.queries.util.HostInstanceQuerySpecification
import org.eclipse.viatra.examples.cps.cyberPhysicalSystem.HostInstance
import org.eclipse.viatra.transformation.runtime.emf.filters.MatchParameterFilter

class BatchTransformationWithFilter {

    extension BatchTransformation transformation
    extension BatchTransformationStatements statements
    
    extension BatchTransformationRuleFactory = new BatchTransformationRuleFactory
    var counter = 0
    val exampleRule = createRule.name("CounterRule")
        .precondition(HostInstanceQuerySpecification.instance)
        .action [
             counter ++
        ].build

    new(Resource resource) {
        val scope = new EMFScope(resource)
        val engine = ViatraQueryEngine.on(scope);
        
        transformation = BatchTransformation.forEngine(engine).build
        statements = transformation.transformationStatements
    }

    def int countMatches(HostInstance instance) {
      counter = 0
      exampleRule.fireAllCurrent(new MatchParameterFilter("host" -> instance))
      return counter
    }

    def dispose() {
        transformation?.dispose
        transformation = null
    }
}
