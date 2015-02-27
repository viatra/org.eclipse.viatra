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

import com.google.common.collect.Sets
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
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import org.eclipse.incquery.runtime.api.impl.BaseMatcher
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch
import org.eclipse.incquery.runtime.base.comprehension.WellbehavingDerivedFeatureRegistry
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry
import org.eclipse.incquery.runtime.matchers.context.surrogate.SurrogateQueryRegistry
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
			superTypes += typeRef(typeof(BasePatternMatch))
		]

		//pattern.inferMatchClass(isPrelinkingPhase, packageName)
		val matcherClass = pattern.toClass(pattern.matcherClassName) [
			it.packageName = packageName
			superTypes += typeRef(typeof(BaseMatcher), typeRef(matchClass))
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


	def void inferPrivate(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor, SpecificationBuilder builder,
		boolean isPrelinkingPhase) {
		logger.debug("Inferring Jvm Model for private pattern " + pattern.name);
		val utilPackageName = pattern.utilPackageName


		val matcherClass = typeRef(typeof(IncQueryMatcher), typeRef(typeof(IPatternMatch))).type 
		val querySpecificationClass = pattern.inferQuerySpecificationClass(isPrelinkingPhase, utilPackageName,
			matcherClass, _typeReferenceBuilder, _annotationTypesBuilder)
		associator.associatePrimary(pattern, querySpecificationClass)
		acceptor.accept(querySpecificationClass) [
			initializePrivateSpecification(querySpecificationClass, pattern, matcherClass, null /* no match class */, builder)
		]
	}
	
	def collectSurrogateQueries(PatternModel model) {
		val patterns = newHashSet
		val existingSpecifications = newHashSet
		SurrogateQueryRegistry.instance.allSurrogateQueryFQNMap.entrySet.forEach[
			val name = it.value	
			val pattern = model.patterns.findFirst[p |
				val fqn = CorePatternLanguageHelper.getFullyQualifiedName(p)
				fqn == name
			]
			if (pattern == null) {
				val specification = QuerySpecificationRegistry.getQuerySpecification(name)
					//XXX In case of no existing specification the EPMToPBody will fail to rewrite references; but there more specific issues can be provided
				if (specification != null) {
					existingSpecifications.add(specification)
				}
			} else {
				patterns.add(pattern)
			}
		]
		val builder = new SpecificationBuilder(existingSpecifications)
		patterns.forEach[
			builder.getOrCreateSpecification(it)			
		]
		builder
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
	   		val builder = model.collectSurrogateQueries
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
