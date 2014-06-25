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
package org.eclipse.incquery.patternlanguage.emf.jvmmodel

import com.google.inject.Inject
import org.apache.log4j.Logger
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.incquery.patternlanguage.emf.specification.SpecificationBuilder
import org.eclipse.incquery.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.incquery.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.api.IQuerySpecification
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import org.eclipse.incquery.runtime.api.impl.BaseMatcher
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch
import org.eclipse.incquery.runtime.exception.IncQueryException
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociator

/**
 * <p>Infers a JVM model from the source model.</p>
 *
 * <p>The JVM model should contain all elements that would appear in the Java code
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>
 */
class EMFPatternLanguageJvmModelInferrer extends AbstractModelInferrer {

	private static final String JVM_MODEL_INFERRER_PREFIX = "org.eclipse.incquery.patternlanguage.emf.inferrer"
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
	@Inject extension PatternMatcherClassMethodInferrer
	@Inject extension PatternQuerySpecificationClassInferrer
	@Inject extension PatternMatchProcessorClassInferrer
	@Inject extension PatternGroupClassInferrer
	@Inject extension JavadocInferrer
	@Inject extension TypeReferences
	@Inject extension IJvmModelAssociator associator

	/**
	 * Is called for each Pattern instance in a resource.
	 *
	 * @param pattern - the model to create one or more JvmDeclaredTypes from.
	 * @param acceptor - each created JvmDeclaredType without a container should be passed to the acceptor in order get attached to the
	 *                   current resource.
	 * @param isPreLinkingPhase - whether the method is called in a pre linking phase, i.e. when the global index isn't fully updated. You
	 *        must not rely on linking using the index if iPrelinkingPhase is <code>true</code>
	 */
   	def void infer(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor, SpecificationBuilder builder,
		boolean isPrelinkingPhase) {
		val isPublic = !CorePatternLanguageHelper::isPrivate(pattern);
		try {
			if (!pattern.name.nullOrEmpty && isPublic) {
				pattern.inferPublic(acceptor, builder, isPrelinkingPhase)
			} else if (!pattern.name.nullOrEmpty) {
				pattern.inferPrivate(acceptor, builder, isPrelinkingPhase)
			}
		} catch (Exception e) {
				logger.error("Exception during Jvm Model Infer for: " + pattern, e)
		}
	}

	def void inferPublic(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor, SpecificationBuilder builder,
		boolean isPrelinkingPhase) {
		logger.debug("Inferring Jvm Model for pattern" + pattern.name);
		val packageName = pattern.getPackageName
		val utilPackageName = pattern.utilPackageName

		val matchClass = pattern.toClass(pattern.matchClassName) [
			it.packageName = packageName
			superTypes += pattern.newTypeRef(typeof(BasePatternMatch))
		]

		//pattern.inferMatchClass(isPrelinkingPhase, packageName)
		val matchClassRef = matchClass.createTypeRef
		val matcherClass = pattern.toClass(pattern.matcherClassName) [
			it.packageName = packageName
			superTypes += pattern.newTypeRef(typeof(BaseMatcher), cloneWithProxies(matchClassRef))
		]
		val matcherClassRef = matcherClass.createTypeRef
		val querySpecificationClass = pattern.inferQuerySpecificationClass(isPrelinkingPhase, utilPackageName,
			matcherClassRef)
		val querySpecificationClassRef = querySpecificationClass.createTypeRef
		val processorClass = pattern.inferProcessorClass(isPrelinkingPhase, utilPackageName, matchClassRef)

		acceptor.accept(matchClass).initializeLater [
			documentation = pattern.javadocMatchClass.toString
			abstract = true
			//it.superTypes += pattern.newTypeRef(typeof (IPatternMatch))
			inferMatchClassFields(pattern)
			inferMatchClassConstructors(pattern)
			inferMatchClassGetters(pattern)
			inferMatchClassSetters(pattern)
			inferMatchClassMethods(pattern, querySpecificationClassRef)
			inferMatchInnerClasses(pattern)
		]

		acceptor.accept(matcherClass).initializeLater [
			documentation = pattern.javadocMatcherClass.toString
			inferStaticMethods(pattern, matcherClass)
			inferFields(pattern)
			inferConstructors(pattern)
			inferMethods(pattern, matchClassRef)
		]

		// add querySpecification() field to Matcher class
		matcherClass.members += pattern.toMethod("querySpecification",
			pattern.newTypeRef(typeof(IQuerySpecification), cloneWithProxies(matcherClassRef))) [
			visibility = JvmVisibility::PUBLIC
			static = true
			documentation = pattern.javadocQuerySpecificationMethod.toString
			exceptions += pattern.newTypeRef(typeof(IncQueryException))
			body = [
				append(
					'''
					return ''')
				serialize(querySpecificationClassRef, pattern)
				append('''.instance();''')
			]
		]

		associator.associatePrimary(pattern, matcherClass)
		associator.associate(pattern, matcherClass)
		associator.associate(pattern, querySpecificationClass)
		associator.associate(pattern, processorClass)
		acceptor.accept(matcherClass)
		acceptor.accept(querySpecificationClass).initializeLater [
			initializePublicSpecification(pattern, matcherClassRef, matchClassRef, builder)
		]
		acceptor.accept(processorClass).initializeLater[
			processorClass.inferProcessorClassMethods(pattern, matchClassRef)
		]

	}


	def void inferPrivate(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor, SpecificationBuilder builder,
		boolean isPrelinkingPhase) {
		logger.debug("Inferring Jvm Model for private pattern " + pattern.name);
		val utilPackageName = pattern.utilPackageName


		val matcherClassRef = getTypeForName(typeof(IncQueryMatcher), pattern, getTypeForName(typeof(IPatternMatch), pattern))
		val querySpecificationClass = pattern.inferQuerySpecificationClass(isPrelinkingPhase, utilPackageName,
			matcherClassRef)
		associator.associatePrimary(pattern, querySpecificationClass)
		acceptor.accept(querySpecificationClass).initializeLater [
			initializePrivateSpecification(pattern, matcherClassRef, null /* no match class */, builder)
		]
	}
   	/**
	 * Is called for each Pattern instance in a resource.
	 *
	 * @param pattern - the model to create one or more JvmDeclaredTypes from.
	 * @param acceptor - each created JvmDeclaredType without a container should be passed to the acceptor in order get attached to the
	 *                   current resource.
	 * @param isPreLinkingPhase - whether the method is called in a pre linking phase, i.e. when the global index isn't fully updated. You
	 *        must not rely on linking using the index if iPrelinkingPhase is <code>true</code>
	 */
   	def dispatch void infer(PatternModel model, IJvmDeclaredTypeAcceptor acceptor, boolean isPrelinkingPhase) {
	   	try {
	   		val builder = new SpecificationBuilder
   			val groupClass = model.inferPatternGroupClass
   			for (pattern : model.patterns){
   				pattern.infer(acceptor, builder, isPrelinkingPhase)
   			}
   			acceptor.accept(groupClass).initializeLater[
   				initializePatternGroup(model)
   			]
	   		logger.debug("Inferring Jvm Model for Pattern model " + model.modelFileName);
   			model.associatePrimary(groupClass)
   		} catch (IllegalArgumentException e){
   			errorFeedback.reportErrorNoLocation(model, e.message, EMFPatternLanguageJvmModelInferrer::INVALID_PATTERN_MODEL_CODE, Severity::ERROR, IErrorFeedback::JVMINFERENCE_ERROR_TYPE)
   		} catch(Exception e) {
	   		logger.error("Exception during Jvm Model Infer for pattern model: " + model, e)
	   	}
   	}
}
