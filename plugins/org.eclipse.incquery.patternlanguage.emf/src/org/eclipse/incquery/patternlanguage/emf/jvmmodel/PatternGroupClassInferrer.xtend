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
import java.util.HashSet
import org.eclipse.incquery.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.incquery.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternModel
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup
import org.eclipse.incquery.runtime.exception.IncQueryException
import org.eclipse.xtext.common.types.JvmConstructor
import org.eclipse.xtext.common.types.JvmField
import org.eclipse.xtext.common.types.JvmGenericType
import org.eclipse.xtext.common.types.JvmOperation
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations

/**
 * Model Inferrer for Pattern grouping. Infers a Group class for every PatternModel.
 */
class PatternGroupClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension IJvmModelAssociations
	@Inject extension TypeReferences types
	@Inject extension JavadocInferrer

	def inferPatternGroup(PatternModel model) {
		if (model.patterns.size == 0) {
			return null
		}
		val groupClass = model.toClass(model.groupClassName) [
			packageName = model.packageName
			final = true
			superTypes += model.newTypeRef(typeof (BaseGeneratedPatternGroup))
		]
		groupClass.documentation = model.javadocGroupClass.toString
		groupClass.members += model.inferInstanceMethod(groupClass)
		groupClass.members += model.inferInstanceField(groupClass)
		groupClass.members += model.inferConstructor
		groupClass
	}





	def String groupClassName(PatternModel model) {
		val fileName = model.modelFileName
		return fileName.toFirstUpper
	}

	def JvmField inferInstanceField(PatternModel model, JvmGenericType groupClass) {
		model.toField("INSTANCE", types.createTypeRef(groupClass)) [
			visibility = JvmVisibility::PRIVATE
			static = true
		]
	}

	def JvmOperation inferInstanceMethod(PatternModel model, JvmGenericType groupClass) {
		val incQueryException = model.newTypeRef(typeof (IncQueryException))
		model.toMethod("instance", types.createTypeRef(groupClass)) [
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

	def JvmConstructor inferConstructor(PatternModel model) {
		val incQueryException = model.newTypeRef(typeof (IncQueryException))
		val matcherReferences = gatherMatchers(model)
		model.toConstructor [
			visibility = JvmVisibility::PRIVATE
			simpleName = groupClassName(model)
			exceptions += incQueryException
			body = [
				for (matcherRef : matcherReferences) {
					append('''querySpecifications.add(''')
					serialize(matcherRef, model)
					append('''.querySpecification());''')
					newLine
				}
			]
		]
	}

	def gatherMatchers(PatternModel model) {
		val result = new HashSet<JvmTypeReference>()
		for (pattern : model.patterns) {
			if (!CorePatternLanguageHelper::isPrivate(pattern)) {
				val jvmElements = pattern.jvmElements
				val matcherClass = jvmElements.findFirst([e | e instanceof JvmGenericType])
				if (matcherClass instanceof JvmGenericType) {
					val sourceElementRef = types.createTypeRef(matcherClass as JvmGenericType)
					result.add(sourceElementRef)
				}
			}
		}
		result
	}

}