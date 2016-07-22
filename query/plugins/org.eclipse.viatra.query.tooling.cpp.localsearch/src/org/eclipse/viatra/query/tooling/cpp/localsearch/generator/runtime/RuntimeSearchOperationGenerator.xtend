/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.cpp.localsearch.generator.runtime

import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.BaseGenerator
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.CheckInstanceOfDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.CheckMultiNavigationDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.CheckSingleNavigationDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.ExtendInstanceOfDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.ExtendMultiNavigationDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.ExtendSingleNavigationDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.ISearchOperationDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.NACOperationDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators.CppHelper
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * @author Robert Doczi
 */
class RuntimeSearchOperationGenerator extends BaseGenerator {

	val String queryName
	@Accessors(PUBLIC_GETTER) val ISearchOperationDescriptor operation;
	val MatchingFrameGenerator frameGenerator
	

	
	new(String queryName, ISearchOperationDescriptor operation, MatchingFrameGenerator frameGenerator) {
		this.queryName = queryName
		this.operation = operation
		this.frameGenerator = frameGenerator
	}

	override initialize() {
	}

	override compile(StringBuilder setupCode) {
		operation.compileOperation(setupCode)
	}

	private dispatch def compileOperation(CheckInstanceOfDescriptor operation, StringBuilder setupCode) {
		return '''create_«CheckInstanceOfDescriptor::NAME»(«operation.variable.toGetter», «operation.key.toTypeID»)'''
	}

	private dispatch def compileOperation(CheckSingleNavigationDescriptor operation, StringBuilder setupCode) {
		val sourceType = operation.matchingFrame.getVariableStrictType(operation.source) as EClass
		return '''create_«CheckSingleNavigationDescriptor::NAME»(«operation.source.toGetter», «operation.target.toGetter», «sourceType.toNavigator(operation.key.toRelationName)»)'''
	}
	
	private dispatch def compileOperation(CheckMultiNavigationDescriptor operation, StringBuilder setupCode) {
		val sourceType = operation.matchingFrame.getVariableStrictType(operation.source) as EClass
		return '''create_«CheckMultiNavigationDescriptor::NAME»(«operation.source.toGetter», «operation.target.toGetter», «sourceType.toNavigator(operation.key.toRelationName)»)'''
	}
	
	private dispatch def compileOperation(NACOperationDescriptor operation, StringBuilder setupCode) {
		val matcherName = '''matcher_«Math.abs(operation.hashCode)»'''
		setupCode.append('''«operation.matcher»<ModelRoot> «matcherName»(model,  «queryName.toFirstUpper»QueryGroup::instance()->context());''')
		return '''create_«NACOperationDescriptor::NAME»<«frameGenerator.frameName»>(«matcherName», «operation.bindings.map[toGetter].join(", ")»)'''
	}

	private dispatch def compileOperation(ExtendInstanceOfDescriptor operation, StringBuilder setupCode) {
		return '''create_«ExtendInstanceOfDescriptor::NAME»(«operation.variable.toSetter», «operation.key.toTypeID», model)'''
	}
	
	private dispatch def compileOperation(ExtendSingleNavigationDescriptor operation, StringBuilder setupCode) {
		val sourceType = operation.matchingFrame.getVariableStrictType(operation.source) as EClass
		return '''create_«ExtendSingleNavigationDescriptor::NAME»(«operation.source.toGetter», «operation.target.toSetter», «sourceType.toNavigator(operation.key.toRelationName)»)'''
	}
	
	private dispatch def compileOperation(ExtendMultiNavigationDescriptor operation, StringBuilder setupCode) {
		val sourceType = operation.matchingFrame.getVariableStrictType(operation.source) as EClass
		return '''create_«ExtendMultiNavigationDescriptor::NAME»(«operation.source.toGetter», «operation.target.toSetter», «sourceType.toNavigator(operation.key.toRelationName)»)'''
	}
	
	private dispatch def compileOperation(ISearchOperationDescriptor operation, StringBuilder setupCode) {
		return '''//NYI'''
	}

	private def toTypeID(EClassifier key) {
		'''«CppHelper::getTypeHelper(key).FQN»::type_id'''
	}
	
	private def toNavigator(EClass type, String name) {
		'''&«type.toCppName»::«name»'''		
	}
	
	private def toGetter(PVariable variable) {
		'''&«frameGenerator.frameName»::«frameGenerator.getVariableName(variable)»'''
	}
	
	private def toSetter(PVariable variable) {
		'''&«frameGenerator.frameName»::«frameGenerator.getVariableName(variable)»'''
	}

	private def toCppName(EClassifier type) {
		CppHelper::getTypeHelper(type).FQN
	}
	
	private def toRelationName(EStructuralFeature key) {
		key.name
	}
}
