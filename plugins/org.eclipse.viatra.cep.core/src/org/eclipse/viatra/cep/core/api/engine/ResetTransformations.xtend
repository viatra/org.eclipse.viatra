package org.eclipse.viatra.cep.core.api.engine

import org.eclipse.incquery.runtime.emf.EMFScope
import org.eclipse.viatra.cep.core.engine.runtime.ResettableEventTokenMatcher
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
import org.eclipse.viatra.emf.runtime.modelmanipulation.IModelManipulations
import org.eclipse.viatra.emf.runtime.modelmanipulation.SimpleModelManipulations
import org.eclipse.viatra.emf.runtime.rules.batch.BatchTransformationRuleFactory
import org.eclipse.viatra.emf.runtime.rules.batch.BatchTransformationStatements
import org.eclipse.viatra.emf.runtime.transformation.batch.BatchTransformation

class ResetTransformations {
	extension BatchTransformationRuleFactory ruleFactory = new BatchTransformationRuleFactory
	extension BatchTransformation transformation
	extension BatchTransformationStatements statements
	extension IModelManipulations manipulation

	new(InternalModel internalModel) {
		transformation = BatchTransformation.forScope(new EMFScope(internalModel))
		statements = new BatchTransformationStatements(transformation)
		manipulation = new SimpleModelManipulations(transformation.iqEngine)
	}

	def resetAll() {
		deleteTokensDuringReset.fireWhilePossible
	}

	val deleteTokensDuringReset = createRule(ResettableEventTokenMatcher::querySpecification)[
			automaton.eventTokens.remove(eventToken)
		]
}