/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.engine.compiler

import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.viatra.cep.core.engine.compiler.rules.AtomicMappingRules
import org.eclipse.viatra.cep.core.engine.compiler.rules.ComplexMappingRules
import org.eclipse.viatra.cep.core.engine.compiler.rules.OptimizationRules
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
import org.eclipse.viatra.cep.core.metamodels.events.EventModel
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.IModelManipulations
import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.SimpleModelManipulations
import org.eclipse.viatra.transformation.runtime.emf.rules.BatchTransformationRuleGroup
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformation
import org.eclipse.viatra.transformation.runtime.emf.transformation.batch.BatchTransformationStatements

class Pattern2AutomatonMapping {

    extension AtomicMappingRules atomicMappingRules
    extension ComplexMappingRules complexMappingRules
    extension OptimizationRules optimizationRules

    extension BatchTransformation transformation
    extension BatchTransformationStatements statements
    extension IModelManipulations manipulation

    private InternalModel internalModel
    private EventModel eventModel
    private TraceModel traceModel

    new(ResourceSet resourceSet) {
        internalModel = (
            resourceSet.getResource(TransformationBasedCompiler.AUTOMATON_MODEL_URI, true).contents.head as InternalModel
        )
        eventModel = (
            resourceSet.getResource(TransformationBasedCompiler.EVENT_MODEL_URI, true).contents.head as EventModel
        )
        traceModel = (
            resourceSet.getResource(TransformationBasedCompiler.TRACE_MODEL_URI, true).contents.head as TraceModel
        )

        atomicMappingRules = new AtomicMappingRules(internalModel, traceModel)
        complexMappingRules = new ComplexMappingRules(internalModel, traceModel)
        optimizationRules = new OptimizationRules(internalModel, traceModel)

        transformation = BatchTransformation.forScope(new EMFScope(resourceSet)).build
        statements = transformation.transformationStatements
        manipulation = new SimpleModelManipulations(transformation.queryEngine)
    }

    def mapPatterns() {
        val ruleGroup = new BatchTransformationRuleGroup()
        ruleGroup.addAll(atomicMappingRules.allRules)
        ruleGroup.addAll(complexMappingRules.allRules)
        ruleGroup.addAll(optimizationRules.allRules)
        ruleGroup.fireWhilePossible
    }
}