package org.eclipse.viatra.cep.core.engine.compiler

import org.eclipse.incquery.runtime.emf.EMFScope
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.emf.runtime.modelmanipulation.IModelManipulations
import org.eclipse.viatra.emf.runtime.modelmanipulation.SimpleModelManipulations
import org.eclipse.viatra.emf.runtime.rules.TransformationRuleGroup
import org.eclipse.viatra.emf.runtime.rules.batch.BatchTransformationRuleFactory
import org.eclipse.viatra.emf.runtime.rules.batch.BatchTransformationStatements
import org.eclipse.viatra.emf.runtime.transformation.batch.BatchTransformation

class Minimize {
	extension BatchTransformationRuleFactory ruleFactory = new BatchTransformationRuleFactory
	extension BatchTransformation transformation
	extension BatchTransformationStatements statements
	extension IModelManipulations manipulation

	new(Automaton automaton) {
		transformation = BatchTransformation.forScope(new EMFScope(automaton))
		statements = new BatchTransformationStatements(transformation)
		manipulation = new SimpleModelManipulations(transformation.iqEngine)
	}

	def minimize() {
//		unifyNegativeTransitions.fireWhilePossible
	}

	def getMinimizationRules() {
		new TransformationRuleGroup(
//			unifyNegativeTransitions
		)
	}

//	val unifyNegativeTransitions = createRule(MinimalizableNegativeEdgesMatcher::querySpecification) [
//		transition1.guards.addAll(transition2.guards)
//		transition1.parameters.addAll(transition2.parameters)
//		transition2.remove
//	]
}