/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi, Mark Czotter - initial API and implementation
 *    Andras Okros - minor changes
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.jvmmodel

import com.google.inject.Inject
import org.apache.log4j.Logger
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.impl.BaseMatcher
import org.eclipse.viatra.query.runtime.api.impl.BasePatternMatch
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociator
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher

/**
 * <p>Infers a JVM model from the source model.</p>
 *
 * <p>The JVM model should contain all elements that would appear in the Java code
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>
 * @noreference
 */
class EMFPatternLanguageJvmModelInferrer extends AbstractModelInferrer {

	private static final String JVM_MODEL_INFERRER_PREFIX = "org.eclipse.viatra.query.patternlanguage.emf.inferrer"
	public static final String INVALID_PATTERN_MODEL_CODE = JVM_MODEL_INFERRER_PREFIX + ".invalid.patternmodel";
    public static final String INVALID_TYPEREF_CODE = JVM_MODEL_INFERRER_PREFIX + ".invalid.typeref";
    public static final String SPECIFICATION_BUILDER_CODE = JVM_MODEL_INFERRER_PREFIX + ".specification.builder";

	@Inject
	private Logger logger;
	@Inject
	private IErrorFeedback errorFeedback;
    /**
     * convenience API to build and initialize JvmTypes and their members.
     */
	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension PatternMatchClassInferrer
	@Inject extension PatternMatcherClassInferrer
	@Inject extension PatternQuerySpecificationClassInferrer
	@Inject extension PatternMatchProcessorClassInferrer
	@Inject extension PatternGroupClassInferrer
	@Inject extension IJvmModelAssociator associator

	/**
	 * Is called for each Pattern instance in a resource.
	 *
	 * @param pattern - the model to create one or more JvmDeclaredTypes from.
	 * @param acceptor - each created JvmDeclaredType without a container should be passed to the acceptor in order get attached to the
	 *                   current resource.
	 * @param isPreLinkingPhase - whether the method is called in a pre linking phase, i.e. when the global index isn't fully updated. You
	 *        must not rely on linking using the index if this is <code>true</code>
	 */
   	def void infer(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor, SpecificationBuilder builder,
		boolean isPrelinkingPhase) {
		if (!pattern.name.nullOrEmpty) {
    		val isPublic = !CorePatternLanguageHelper::isPrivate(pattern);
    		try {
    			if (isPublic) {
    				pattern.inferPublic(acceptor, builder, isPrelinkingPhase)
    			} else {
    				pattern.inferPrivate(acceptor, builder, isPrelinkingPhase)
    			}
    		} catch (Exception e) {
    				logger.error("Exception during Jvm Model Infer for: " + pattern, e)
    		}
		}
	}

	private def void inferPublic(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor, SpecificationBuilder builder,
		boolean isPrelinkingPhase) {
		logger.debug("Inferring Jvm Model for pattern" + pattern.name);
		val packageName = pattern.getPackageName
		val utilPackageName = pattern.utilPackageName

		val matchClass = pattern.toClass(pattern.matchClassName) [
			it.packageName = packageName
			superTypes += typeRef(typeof(BasePatternMatch))
			fileHeader = pattern.fileComment
		]

		val matcherClass = pattern.toClass(pattern.matcherClassName) [
			it.packageName = packageName
			superTypes += typeRef(typeof(BaseMatcher), typeRef(matchClass))
			fileHeader = pattern.fileComment
		]
		val querySpecificationClass = pattern.inferQuerySpecificationClass(isPrelinkingPhase, utilPackageName,
			matcherClass, _typeReferenceBuilder, _annotationTypesBuilder)
		val processorClass = pattern.inferProcessorClass(isPrelinkingPhase, utilPackageName, matchClass, _typeReferenceBuilder, _annotationTypesBuilder)

		acceptor.accept(querySpecificationClass) [
			initializePublicSpecification(pattern, matcherClass, matchClass, builder)
		]
		acceptor.accept(processorClass) [
			processorClass.inferProcessorClassMethods(pattern, matchClass)
		]

		acceptor.accept(matchClass) [
			inferMatchClassElements(pattern, querySpecificationClass, _typeReferenceBuilder, _annotationTypesBuilder)
		]

		acceptor.accept(matcherClass) [
			inferMatcherClassElements(pattern, querySpecificationClass, matchClass, _typeReferenceBuilder, _annotationTypesBuilder)
		]

		associator.associatePrimary(pattern, matcherClass)
		associator.associate(pattern, matcherClass)
		associator.associate(pattern, querySpecificationClass)
		associator.associate(pattern, processorClass)

	}


	private def void inferPrivate(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor, SpecificationBuilder builder,
		boolean isPrelinkingPhase) {
		logger.debug("Inferring Jvm Model for private pattern " + pattern.name);
		val utilPackageName = pattern.utilPackageName


		val matcherClass = typeRef(typeof(ViatraQueryMatcher), typeRef(typeof(IPatternMatch))).type 
		val querySpecificationClass = pattern.inferQuerySpecificationClass(isPrelinkingPhase, utilPackageName,
			matcherClass, _typeReferenceBuilder, _annotationTypesBuilder)
		associator.associatePrimary(pattern, querySpecificationClass)
		acceptor.accept(querySpecificationClass) [
			initializePrivateSpecification(querySpecificationClass, pattern, matcherClass, null /* no match class */, builder)
		]
	}
		
   	/**
	 * Is called for each PatternModel instance in a resource.
	 *
	 * @param model - the model to create one or more JvmDeclaredTypes from.
	 * @param acceptor - each created JvmDeclaredType without a container should be passed to the acceptor in order get attached to the
	 *                   current resource.
	 * @param isPreLinkingPhase - whether the method is called in a pre linking phase, i.e. when the global index isn't fully updated. You
	 *        must not rely on linking using the index if this is <code>true</code>
	 */
   	def dispatch void infer(PatternModel model, IJvmDeclaredTypeAcceptor acceptor, boolean isPrelinkingPhase) {
	   	try {
	   		val builder = new SpecificationBuilder()
   			for (pattern : model.patterns){
   				pattern.infer(acceptor, builder, isPrelinkingPhase)
   			}
	   		logger.debug("Inferring Jvm Model for Pattern model " + model.modelFileName);
   			if (!model.patterns.empty) {
	   			val groupClass = model.inferPatternGroupClass(_typeReferenceBuilder)
   				acceptor.accept(groupClass) [
   					initializePatternGroup(model, _typeReferenceBuilder)
   				]
   				model.associatePrimary(groupClass)
   			}
   				
   		} catch (IllegalArgumentException e){
   			errorFeedback.reportErrorNoLocation(model, e.message, EMFPatternLanguageJvmModelInferrer::INVALID_PATTERN_MODEL_CODE, Severity::ERROR, IErrorFeedback::JVMINFERENCE_ERROR_TYPE)
   		} catch(Exception e) {
	   		logger.error("Exception during Jvm Model Infer for pattern model: " + model, e)
	   	}
   	}
}
