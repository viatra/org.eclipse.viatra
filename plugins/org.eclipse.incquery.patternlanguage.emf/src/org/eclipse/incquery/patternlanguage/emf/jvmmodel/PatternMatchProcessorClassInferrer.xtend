/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.patternlanguage.emf.jvmmodel

import com.google.inject.Inject
import org.eclipse.incquery.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.incquery.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.runtime.api.IMatchProcessor
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference

/**
 * {@link IMatchProcessor} implementation inferer.
 *
 * @author Mark Czotter
 */
class PatternMatchProcessorClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer

	/**
	 * Infers the {@link IMatchProcessor} implementation class from a {@link Pattern}.
	 */
	def JvmDeclaredType inferProcessorClass(Pattern pattern, boolean isPrelinkingPhase, String processorPackageName, JvmTypeReference matchClassRef) {
		val processorClass = pattern.toClass(pattern.processorClassName) [
  			packageName = processorPackageName
  			documentation = pattern.javadocProcessorClass.toString
  			abstract = true
  			superTypes += pattern.newTypeRef(typeof(IMatchProcessor), cloneWithProxies(matchClassRef))
  		]
  		return processorClass
  	}

	/**
   	 * Infers methods for Processor class based on the input 'pattern'.
   	 */
  	def inferProcessorClassMethods(JvmDeclaredType processorClass, Pattern pattern, JvmTypeReference matchClassRef) {
  		processorClass.members += processorClass.toMethod("process", null) [
  			returnType = pattern.newTypeRef(Void::TYPE)
			documentation = pattern.javadocProcessMethod.toString
			abstract = true
			for (parameter : pattern.parameters){
				it.parameters += parameter.toParameter(parameter.parameterName, parameter.calculateType)
			}
		]
		processorClass.members += processorClass.toMethod("process", null) [
			returnType = pattern.newTypeRef(Void::TYPE)
			addAnnotation(typeof (Override))
			parameters += processorClass.toParameter("match", cloneWithProxies(matchClassRef))
			body = [it.append('''
				process(«FOR p : pattern.parameters SEPARATOR ', '»match.«p.getterMethodName»()«ENDFOR»);
			''')]
		]
  	}

}