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
package org.eclipse.incquery.patternlanguage.emf.jvmmodel

import com.google.inject.Inject
import org.eclipse.incquery.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.incquery.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternModel
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup
import org.eclipse.incquery.runtime.api.impl.BaseMatcher
import org.eclipse.incquery.runtime.exception.IncQueryException
import org.eclipse.xtext.common.types.JvmConstructor
import org.eclipse.xtext.common.types.JvmField
import org.eclipse.xtext.common.types.JvmGenericType
import org.eclipse.xtext.common.types.JvmOperation
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification

/**
 * Model Inferrer for Pattern grouping. Infers a Group class for every PatternModel.
 */
class PatternGroupClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension TypeReferences
	@Inject extension JavadocInferrer

	def inferPatternGroupClass(PatternModel model) {
		model.toClass(model.groupClassName) [
			packageName = model.packageName
			final = true
			superTypes += model.newTypeRef(typeof (BaseGeneratedPatternGroup))
		]
	}
		
	def initializePatternGroup(JvmGenericType groupClass, PatternModel model) {
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
		groupClass.toField("INSTANCE", groupClass.createTypeRef) [
			visibility = JvmVisibility::PRIVATE
			static = true
		]
	}

	def JvmOperation inferInstanceMethod(PatternModel model, JvmType groupClass) {
		val incQueryException = model.newTypeRef(typeof (IncQueryException))
		groupClass.toMethod("instance", groupClass.createTypeRef) [
			documentation = model.javadocGroupClassInstanceMethod.toString
			visibility = JvmVisibility::PUBLIC
			static = true
			exceptions += incQueryException
			body = [append('''
				if (INSTANCE == null) {
					INSTANCE = new ''')
					append(groupClass)
					append('''
				();
				}
				return INSTANCE;
			''')]
		]

	}

	def JvmConstructor inferConstructor(PatternModel model, JvmType groupClass) {
		val incQueryException = model.newTypeRef(typeof (IncQueryException))
		groupClass.toConstructor [
			visibility = JvmVisibility::PRIVATE
			simpleName = groupClassName(model)
			exceptions += incQueryException
			body = [
				for (matcherRef : model.patterns.filter[public].filterNull.map[findInferredSpecification.createTypeRef]) {
					append('''querySpecifications.add(''')
					serialize(matcherRef, model)
					append('''.instance());''')
					newLine
				}
			]
		]
	}

	

	def JvmOperation inferSpecificationGetter(Pattern model, JvmType groupClass, JvmType specificationClass) {
		val classRef = if (specificationClass == null) {
			getTypeForName(typeof(Object), model)
		} else {
			specificationClass.createTypeRef
		}
		val incQueryException = model.newTypeRef(typeof(IncQueryException))
		model.toMethod("get" + model.name.toFirstUpper, classRef) [
			visibility = JvmVisibility::PUBLIC
			exceptions += incQueryException
			body = [
				append('''return ''')
				serialize(classRef, model)
				append('''.instance();''')
			]
		]
	}
	
	def JvmOperation inferMatcherGetter(Pattern model, JvmType groupClass, JvmType matcherClass) {
		val classRef = if (matcherClass == null) {
			getTypeForName(typeof(Object), model)
		} else {
			matcherClass.createTypeRef
		}
		val incQueryException = model.newTypeRef(typeof(IncQueryException))
		model.toMethod("get" + model.name.toFirstUpper, classRef) [
			visibility = JvmVisibility::PUBLIC
			exceptions += incQueryException
			parameters += matcherClass.toParameter("engine", model.newTypeRef(typeof (IncQueryEngine)))
			body = [
				append('''return ''')
				serialize(classRef, model)
				append('''.on(engine);''')
			]
		]
	}
}
