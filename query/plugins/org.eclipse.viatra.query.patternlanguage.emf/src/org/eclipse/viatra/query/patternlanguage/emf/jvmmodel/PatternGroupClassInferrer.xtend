/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *   Andras Okros - minor changes
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.jvmmodel

import com.google.inject.Inject
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternModel
import org.eclipse.viatra.query.runtime.api.IncQueryEngine
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFQuerySpecification
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedPatternGroup
import org.eclipse.viatra.query.runtime.api.impl.BaseMatcher
import org.eclipse.viatra.query.runtime.exception.IncQueryException
import org.eclipse.xtext.common.types.JvmConstructor
import org.eclipse.xtext.common.types.JvmField
import org.eclipse.xtext.common.types.JvmGenericType
import org.eclipse.xtext.common.types.JvmOperation
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder

/**
 * Model Inferrer for Pattern grouping. Infers a Group class for every PatternModel.
 */
class PatternGroupClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer
	@Extension JvmTypeReferenceBuilder builder

	def inferPatternGroupClass(PatternModel model, JvmTypeReferenceBuilder builder) {
		this.builder = builder
		model.toClass(model.groupClassName) [
			packageName = model.packageName
			final = true
			superTypes += typeRef(typeof (BaseGeneratedPatternGroup))
		]
	}
		
	def initializePatternGroup(JvmGenericType groupClass, PatternModel model, JvmTypeReferenceBuilder builder) {
		this.builder = builder
		
		groupClass.documentation = model.javadocGroupClass.toString
		groupClass.members += model.inferInstanceMethod(groupClass)
		groupClass.members += model.inferInstanceField(groupClass)
		groupClass.members += model.inferConstructor(groupClass)
		for (pattern : model.patterns.filter[public && !name.nullOrEmpty]) {
			groupClass.members += pattern.inferSpecificationGetter(groupClass, pattern.findInferredClass(typeof(BaseGeneratedEMFQuerySpecification)))
			groupClass.members += pattern.inferMatcherGetter(groupClass, pattern.findInferredClass(typeof(BaseMatcher)))
		}
		groupClass
	}

	def String groupClassName(PatternModel model) {
		val fileName = model.modelFileName
		return fileName.toFirstUpper
	}

	def JvmField inferInstanceField(PatternModel model, JvmType groupClass) {
		model.toField("INSTANCE", groupClass.typeRef) [
			visibility = JvmVisibility::PRIVATE
			static = true
		]
	}

	def JvmOperation inferInstanceMethod(PatternModel model, JvmType groupClass) {
		val incQueryException = typeRef(typeof (IncQueryException))
		model.toMethod("instance", groupClass.typeRef) [
			documentation = model.javadocGroupClassInstanceMethod.toString
			visibility = JvmVisibility::PUBLIC
			static = true
			exceptions += incQueryException
			body = '''
				if (INSTANCE == null) {
					INSTANCE = new «groupClass»();
				}
				return INSTANCE;
			'''
		]

	}

	def JvmConstructor inferConstructor(PatternModel model, JvmType groupClass) {
		val incQueryException = typeRef(typeof (IncQueryException))
		model.toConstructor [
			visibility = JvmVisibility::PRIVATE
			simpleName = groupClassName(model)
			exceptions += incQueryException
			body = '''
				«FOR matcherRef : model.patterns.filter[public].filterNull.map[findInferredSpecification.typeRef]»
					querySpecifications.add(«matcherRef».instance());
				«ENDFOR»
			'''
		]
	}

	

	def JvmOperation inferSpecificationGetter(Pattern model, JvmType groupClass, JvmType specificationClass) {
		val classRef = if (specificationClass == null) {
			typeRef(typeof(Object))
		} else {
			specificationClass.typeRef
		}
		val incQueryException = typeRef(typeof(IncQueryException))
		model.toMethod("get" + model.name.toFirstUpper, classRef) [
			visibility = JvmVisibility::PUBLIC
			exceptions += incQueryException
			body = '''return «classRef».instance();'''
		]
	}
	
	def JvmOperation inferMatcherGetter(Pattern model, JvmType groupClass, JvmType matcherClass) {
		val classRef = if (matcherClass == null) {
			typeRef(typeof(Object))
		} else {
			matcherClass.typeRef
		}
		val incQueryException = typeRef(typeof(IncQueryException))
		model.toMethod("get" + model.name.toFirstUpper, classRef) [
			visibility = JvmVisibility::PUBLIC
			exceptions += incQueryException
			parameters += model.toParameter("engine", typeRef(typeof (IncQueryEngine)))
			body = '''return «classRef».on(engine);'''
		]
	}
}
