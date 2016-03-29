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

package org.eclipse.viatra.query.patternlanguage.emf.jvmmodel

import com.google.inject.Inject
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern
import org.eclipse.viatra.query.runtime.api.IMatchProcessor
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmAnnotationReferenceBuilder
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFIssueCodes
import org.eclipse.xtext.diagnostics.Severity

/**
 * {@link IMatchProcessor} implementation inferrer.
 *
 * @author Mark Czotter
 */
class PatternMatchProcessorClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer
	@Inject private IErrorFeedback feedback
	@Extension private JvmTypeReferenceBuilder builder
	@Extension private JvmAnnotationReferenceBuilder annBuilder

	/**
	 * Infers the {@link IMatchProcessor} implementation class from a {@link Pattern}.
	 */
	def JvmDeclaredType inferProcessorClass(Pattern pattern, boolean isPrelinkingPhase, String processorPackageName, JvmType matchClass, JvmTypeReferenceBuilder builder, JvmAnnotationReferenceBuilder annBuilder) {
		this.builder = builder
		this.annBuilder = annBuilder
		
		val processorClass = pattern.toClass(pattern.processorClassName) [
  			packageName = processorPackageName
  			documentation = pattern.javadocProcessorClass.toString
  			abstract = true
  			superTypes += typeRef(typeof(IMatchProcessor), typeRef(matchClass))
  		]
  		return processorClass
  	}

	/**
   	 * Infers methods for Processor class based on the input 'pattern'.
   	 */
  	def inferProcessorClassMethods(JvmDeclaredType processorClass, Pattern pattern, JvmType matchClassRef) {
  		try {
  		    processorClass.members += pattern.toMethod("process", null) [
  			   returnType = typeRef(Void::TYPE)
			 documentation = pattern.javadocProcessMethod.toString
			 abstract = true
			 for (parameter : pattern.parameters){
			 	   it.parameters += parameter.toParameter(parameter.parameterName, parameter.calculateType)
			 }
		  ]
		  processorClass.members += pattern.toMethod("process", null) [
			 returnType = typeRef(Void::TYPE)
			 annotations += annotationRef(typeof (Override))
			 parameters += pattern.toParameter("match", typeRef(matchClassRef))
			 body = '''
				    process(«FOR p : pattern.parameters SEPARATOR ', '»match.«p.getterMethodName»()«ENDFOR»);
			 '''
		  ]
		} catch (IllegalStateException ex) {
            feedback.reportError(pattern, ex.message, EMFIssueCodes.OTHER_ISSUE, Severity.ERROR,
                IErrorFeedback.JVMINFERENCE_ERROR_TYPE)
        }
  	}

}