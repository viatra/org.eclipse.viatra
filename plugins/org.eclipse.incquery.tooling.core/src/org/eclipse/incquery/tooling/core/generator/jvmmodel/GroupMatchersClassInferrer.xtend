/*******************************************************************************
 * Copyright (c) 2010-2013, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.core.generator.jvmmodel

import com.google.inject.Inject
import java.util.HashSet
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.exception.IncQueryException
import org.eclipse.incquery.tooling.core.generator.util.EMFJvmTypesBuilder
import org.eclipse.incquery.tooling.core.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.xtext.common.types.JvmConstructor
import org.eclipse.xtext.common.types.JvmGenericType
import org.eclipse.xtext.common.types.JvmOperation
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations

/**
 * @author Bergmann Gabor
 *
 * TODO: add Javadoc to generated class and methods.
 */
class GroupMatchersClassInferrer {
	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension IJvmModelAssociations
	@Inject extension TypeReferences types
	@Inject extension JavadocInferrer
	
	def inferGroupMatchers(PatternModel model) {
		if (model.patterns.size == 0) {
			return null
		}
		val matchersClass = model.toClass(model.groupMatchersClassName) [
			it.packageName = model.packageName
			it.final = true
			// it.superTypes += model.newTypeRef(typeof (BaseGeneratedPatternGroup))
		]
		//matchersClass.documentation = model.javadocMatchersClass.toString
		matchersClass.members += model.toField("engine", model.newTypeRef(typeof (IncQueryEngine)))
		matchersClass.members += model.inferConstructor
		for (matcherRef : model.gatherMatchers) {
			matchersClass.members += model.inferMatcherGetter(matcherRef)
		}
		
		
		matchersClass
	}
	
	def JvmOperation inferMatcherGetter(PatternModel model, JvmGenericType matcherClass) {
		val incQueryException = model.newTypeRef(typeof (IncQueryException)) 
		model.toMethod("get" + matcherClass.simpleName, types.createTypeRef(matcherClass)) [
			it.visibility = JvmVisibility::PUBLIC
			it.exceptions += incQueryException
			it.setBody([
				append('''return ''')
				serialize(types.createTypeRef(matcherClass), model)				
				append('''.on(engine);''')
			])
		]
	}
	
	def JvmConstructor inferConstructor(PatternModel model) {
		model.toConstructor [
			it.visibility = JvmVisibility::PUBLIC
			it.simpleName = model.groupMatchersClassName
			it.parameters += model.toParameter("engine", model.newTypeRef(typeof (IncQueryEngine)))
			it.setBody([append(
				'''
					this.engine = engine;
				'''
			)])
		]
	}
	// TODO generalize with PatternGroupInferrer
	def gatherMatchers(PatternModel model) {
		val result = new HashSet<JvmGenericType>()
		for (pattern : model.patterns) {
			if (!CorePatternLanguageHelper::isPrivate(pattern)) {
				val jvmElements = pattern.jvmElements
				val matcherClass = jvmElements.findFirst([e | e instanceof JvmGenericType])	
				if (matcherClass instanceof JvmGenericType) {
					result.add(matcherClass as JvmGenericType)
					//val sourceElementRef = types.createTypeRef(matcherClass as JvmGenericType)
				}
			}
		}
		result
	}
	
}