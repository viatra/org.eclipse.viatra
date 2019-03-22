/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.jvmmodel

import com.google.inject.Inject
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmAnnotationReferenceBuilder
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageGeneratorConfig
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageGeneratorConfig.MatcherGenerationStrategy
import java.util.function.Consumer

/**
 * Generated match processor implementation inferrer.
 * 
 * @author Mark Czotter
 * @noreference
 */
class PatternMatchProcessorClassInferrer {

    @Inject extension EMFJvmTypesBuilder
    @Inject extension EMFPatternLanguageJvmModelInferrerUtil
    @Inject extension JavadocInferrer
    @Inject IErrorFeedback feedback
    @Extension JvmTypeReferenceBuilder builder
    @Extension JvmAnnotationReferenceBuilder annBuilder

    /**
     * Infers the {@link Consumer} implementation class from a {@link Pattern}.
     */
    def JvmDeclaredType inferProcessorClass(Pattern pattern, boolean isPrelinkingPhase, String processorPackageName,
        JvmType matchClass, JvmTypeReferenceBuilder builder, JvmAnnotationReferenceBuilder annBuilder,
        EMFPatternLanguageGeneratorConfig config) {
        this.builder = builder
        this.annBuilder = annBuilder

        val processorClass = pattern.toClass(pattern.processorClassName(config.matcherGenerationStrategy)) [
            static = config.matcherGenerationStrategy == MatcherGenerationStrategy.NESTED_CLASS
            packageName = processorPackageName
            documentation = pattern.javadocProcessorClass.toString
            abstract = true
            superTypes += typeRef(Consumer, typeRef(matchClass))
            fileHeader = pattern.fileComment
        ]
        return processorClass
    }

    /**
     * Infers methods for Processor class based on the input 'pattern'.
     */
    def inferProcessorClassMethods(JvmDeclaredType processorClass, Pattern pattern, JvmType matchClassRef) {
        try {
            processorClass.members += pattern.toMethod("accept", null) [
                returnType = typeRef(Void::TYPE)
                documentation = pattern.javadocProcessMethod.toString
                abstract = true
                for (parameter : pattern.parameters) {
                    it.parameters += parameter.toParameter(parameter.parameterName, parameter.calculateType)
                }
            ]
            processorClass.members += pattern.toMethod("accept", null) [
                returnType = typeRef(Void::TYPE)
                annotations += annotationRef(Override)
                parameters += pattern.toParameter("match", typeRef(matchClassRef))
                body = '''
                    accept(«FOR p : pattern.parameters SEPARATOR ', '»match.«p.getterMethodName»()«ENDFOR»);
                '''
            ]
        } catch (IllegalStateException ex) {
            feedback.reportError(pattern, ex.message, IssueCodes.OTHER_ISSUE, Severity.ERROR,
                IErrorFeedback.JVMINFERENCE_ERROR_TYPE)
        }
    }

}
