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
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageGeneratorConfig
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern
import org.eclipse.viatra.query.runtime.api.GenericPatternMatch
import org.eclipse.viatra.query.runtime.api.GenericPatternMatcher
import org.eclipse.viatra.query.runtime.api.impl.BaseMatcher
import org.eclipse.viatra.query.runtime.api.impl.BasePatternMatch
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.xbase.compiler.IGeneratorConfigProvider
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociator
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageGeneratorConfig.MatcherGenerationStrategy
import org.eclipse.emf.ecore.EObject

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
    @Inject
    private IGeneratorConfigProvider generatorConfigProvider
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
    def void inferPattern(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor, boolean isPrelinkingPhase) {
        val config = getConfiguration(pattern)

        if (!pattern.name.nullOrEmpty) {
            val isPublic = !PatternLanguageHelper::isPrivate(pattern);
            try {
                if (isPublic) {
                    switch (config.matcherGenerationStrategy) {
                        case MatcherGenerationStrategy::SEPARATE_CLASS,
                        case MatcherGenerationStrategy::NESTED_CLASS:
                            pattern.inferPublic(acceptor, config, isPrelinkingPhase)
                        case MatcherGenerationStrategy::USE_GENERIC:
                            pattern.inferPublicWithNoMatchers(acceptor, config, isPrelinkingPhase)
                    }
                } else {
                    pattern.inferPrivate(acceptor, config, isPrelinkingPhase)
                }
            } catch (Exception e) {
                logger.error("Exception during Jvm Model Infer for: " + pattern, e)
            }
        }
    }

    private def EMFPatternLanguageGeneratorConfig getConfiguration(EObject ctx) {
        val _config = generatorConfigProvider.get(ctx)
        val EMFPatternLanguageGeneratorConfig config = if (_config instanceof EMFPatternLanguageGeneratorConfig) {
                _config
            } else {
                val newConfig = new EMFPatternLanguageGeneratorConfig
                newConfig.copy(_config)
                newConfig
            }
        return config
    }

    private def void inferPublic(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor,
        EMFPatternLanguageGeneratorConfig config, boolean isPrelinkingPhase) {
        logger.debug("Inferring Jvm Model for pattern" + pattern.name);
        val generateMatchProcessors = config.generateMatchProcessors
        val nestedClasses = (config.matcherGenerationStrategy === MatcherGenerationStrategy::NESTED_CLASS)
        val packageName = pattern.getPackageName
        val utilPackageName = pattern.utilPackageName
        val specificationPackageName = if(nestedClasses) packageName else utilPackageName

        val matchClass = pattern.toClass(pattern.matchClassName(config.matcherGenerationStrategy)) [
            if (nestedClasses) {
                it.static = true
            } else {
                it.packageName = packageName
                fileHeader = pattern.fileComment
            }
            superTypes += typeRef(BasePatternMatch)
        ]

        val matcherClass = pattern.toClass(pattern.matcherClassName(config.matcherGenerationStrategy)) [
            if (nestedClasses) {
                it.static = true
            } else {
                it.packageName = packageName
                fileHeader = pattern.fileComment
            }
            superTypes += typeRef(BaseMatcher, typeRef(matchClass))
        ]
        val querySpecificationClass = pattern.inferQuerySpecificationClass(isPrelinkingPhase, specificationPackageName,
            matcherClass, _typeReferenceBuilder, _annotationTypesBuilder, config)

        if (nestedClasses) {
            querySpecificationClass.members += matchClass
            querySpecificationClass.members += matcherClass
        }

        acceptor.accept(querySpecificationClass) [
            initializeSpecification(pattern, matcherClass, matchClass, config)
        ]

        acceptor.accept(matchClass) [
            inferMatchClassElements(pattern, querySpecificationClass, _typeReferenceBuilder, _annotationTypesBuilder)
        ]

        acceptor.accept(matcherClass) [
            inferMatcherClassElements(pattern, querySpecificationClass, matchClass, _typeReferenceBuilder,
                _annotationTypesBuilder, config)
        ]

        associator.associatePrimary(pattern, querySpecificationClass)
        associator.associate(pattern, matcherClass)
        associator.associate(pattern, querySpecificationClass)

        if (generateMatchProcessors) {
            val processorClass = pattern.inferProcessorClass(isPrelinkingPhase, utilPackageName, matchClass,
                _typeReferenceBuilder, _annotationTypesBuilder, config)
            acceptor.accept(processorClass) [
                processorClass.inferProcessorClassMethods(pattern, matchClass)
            ]
            associator.associate(pattern, processorClass)
            if (nestedClasses) {
                querySpecificationClass.members += processorClass
            }
        }

    }

    private def void inferPublicWithNoMatchers(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor,
        EMFPatternLanguageGeneratorConfig config, boolean isPrelinkingPhase) {
        logger.debug("Inferring Jvm Model for private pattern " + pattern.name);
        val utilPackageName = pattern.packageName

        inferQuerySpecificationWithGeneric(pattern, acceptor, config, isPrelinkingPhase, utilPackageName)
    }

    private def void inferPrivate(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor,
        EMFPatternLanguageGeneratorConfig config, boolean isPrelinkingPhase) {
        logger.debug("Inferring Jvm Model for private pattern " + pattern.name);
        val utilPackageName = pattern.internalSpecificationPackage /* This package is not exported by design */

        inferQuerySpecificationWithGeneric(pattern, acceptor, config, isPrelinkingPhase, utilPackageName)
    }

    private def void inferQuerySpecificationWithGeneric(Pattern pattern, IJvmDeclaredTypeAcceptor acceptor,
        EMFPatternLanguageGeneratorConfig config, boolean isPrelinkingPhase, String packageName) {
        val matcherClass = typeRef(GenericPatternMatcher).type
        val matchClass = typeRef(GenericPatternMatch).type
        val querySpecificationClass = pattern.inferQuerySpecificationClass(isPrelinkingPhase, packageName, matcherClass,
            _typeReferenceBuilder, _annotationTypesBuilder, config)
        associator.associatePrimary(pattern, querySpecificationClass)
        acceptor.accept(querySpecificationClass) [
            initializeSpecification(querySpecificationClass, pattern, matcherClass, matchClass, config)
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
            val config = getConfiguration(model)
            for (pattern : model.patterns) {
                pattern.inferPattern(acceptor, isPrelinkingPhase)
            }
            logger.debug("Inferring Jvm Model for Pattern model " + model.modelFileName);
            if (!model.patterns.empty) {
                val groupClass = model.inferPatternGroupClass(_typeReferenceBuilder, config, false)
                acceptor.accept(groupClass) [
                    initializePatternGroup(model, _typeReferenceBuilder, config, false)
                ]
                if (model.patterns.exists[PatternLanguageHelper.isPrivate(it)]) {
                    val privateGroupClass = model.inferPatternGroupClass(_typeReferenceBuilder, config, true)
                    acceptor.accept(privateGroupClass) [
                        initializePatternGroup(model, _typeReferenceBuilder, config, true)
                    ]
                }
                model.associatePrimary(groupClass)
            }

        } catch (IllegalArgumentException e) {
            errorFeedback.reportErrorNoLocation(model, e.message,
                EMFPatternLanguageJvmModelInferrer::INVALID_PATTERN_MODEL_CODE, Severity::ERROR,
                IErrorFeedback::JVMINFERENCE_ERROR_TYPE)
            } catch (Exception e) {
                logger.error("Exception during Jvm Model Infer for pattern model: " + model, e)
            }
        }
    }
    