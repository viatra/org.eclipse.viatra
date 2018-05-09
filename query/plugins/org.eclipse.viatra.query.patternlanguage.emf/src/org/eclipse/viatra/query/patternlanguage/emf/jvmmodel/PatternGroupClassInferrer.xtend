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
package org.eclipse.viatra.query.patternlanguage.emf.jvmmodel

import com.google.inject.Inject
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedPatternGroup
import org.eclipse.viatra.query.runtime.api.impl.BaseMatcher
import org.eclipse.xtext.common.types.JvmConstructor
import org.eclipse.xtext.common.types.JvmField
import org.eclipse.xtext.common.types.JvmGenericType
import org.eclipse.xtext.common.types.JvmOperation
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageGeneratorConfig
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageGeneratorConfig.MatcherGenerationStrategy

/**
 * Model Inferrer for Pattern grouping. Infers a Group class for every PatternModel.
 * @noreference
 */
class PatternGroupClassInferrer {

    @Inject extension EMFJvmTypesBuilder
    @Inject extension EMFPatternLanguageJvmModelInferrerUtil
    @Inject extension JavadocInferrer
    @Extension JvmTypeReferenceBuilder builder

    def inferPatternGroupClass(PatternModel model, JvmTypeReferenceBuilder builder,
        EMFPatternLanguageGeneratorConfig config, boolean includePrivate) {
        this.builder = builder
        model.toClass(model.groupClassName(includePrivate)) [
            packageName = model.groupPackageName(includePrivate)
            final = true
            superTypes += typeRef(BaseGeneratedPatternGroup)
            fileHeader = model.fileComment
        ]
    }

    def initializePatternGroup(JvmGenericType groupClass, PatternModel model, JvmTypeReferenceBuilder builder,
        EMFPatternLanguageGeneratorConfig config, boolean includePrivate) {
        this.builder = builder

        groupClass.documentation = javadocGroupClass(model, includePrivate).toString
        groupClass.members += model.inferInstanceMethod(groupClass)
        groupClass.members += model.inferInstanceField(groupClass)
        groupClass.members += model.inferConstructor(groupClass, includePrivate)
        if (!includePrivate && config.matcherGenerationStrategy !== MatcherGenerationStrategy::USE_GENERIC) {
            for (pattern : model.patterns.filter[public && !name.nullOrEmpty]) {
                groupClass.members += pattern.inferSpecificationGetter(groupClass, pattern.findInferredSpecification)
                groupClass.members += pattern.inferMatcherGetter(groupClass, pattern.findInferredClass(BaseMatcher))
            }
        }
    }

    private def String groupClassName(PatternModel model, boolean includePrivate) {
        val fileName = model.modelFileName.toFirstUpper
        return if(includePrivate) fileName + "All" else fileName
    }

    private def String groupPackageName(PatternModel model, boolean includePrivate) {
        val packageName = model.packageName
        return if(includePrivate) packageName + ".internal" else packageName
    }

    def JvmField inferInstanceField(PatternModel model, JvmType groupClass) {
        model.toField("INSTANCE", groupClass.typeRef) [
            visibility = JvmVisibility::PRIVATE
            static = true
        ]
    }

    def JvmOperation inferInstanceMethod(PatternModel model, JvmType groupClass) {
        model.toMethod("instance", groupClass.typeRef) [
            documentation = model.javadocGroupClassInstanceMethod.toString
            visibility = JvmVisibility::PUBLIC
            static = true
            body = '''
                if (INSTANCE == null) {
                    INSTANCE = new «groupClass»();
                }
                return INSTANCE;
            '''
        ]

    }

    def JvmConstructor inferConstructor(PatternModel model, JvmType groupClass, boolean includePrivate) {
        model.toConstructor [
            visibility = JvmVisibility::PRIVATE
            simpleName = groupClassName(model, includePrivate)
            body = '''
                «FOR matcherRef : model.patterns.filter[includePrivate || public].filterNull.map[findInferredSpecification?.typeRef].filterNull»
                    querySpecifications.add(«matcherRef».instance());
                «ENDFOR»
            '''
        ]
    }

    def JvmOperation inferSpecificationGetter(Pattern model, JvmType groupClass, JvmType specificationClass) {
        val classRef = if (specificationClass === null) {
                typeRef(Object)
            } else {
                specificationClass.typeRef
            }
        model.toMethod("get" + model.name.toFirstUpper, classRef) [
            visibility = JvmVisibility::PUBLIC
            body = '''return «classRef».instance();'''
        ]
    }

    def JvmOperation inferMatcherGetter(Pattern model, JvmType groupClass, JvmType matcherClass) {
        val classRef = if (matcherClass === null) {
                typeRef(Object)
            } else {
                matcherClass.typeRef
            }
        model.toMethod("get" + model.name.toFirstUpper, classRef) [
            visibility = JvmVisibility::PUBLIC
            parameters += model.toParameter("engine", typeRef(ViatraQueryEngine))
            body = '''return «classRef».on(engine);'''
        ]
    }
}
