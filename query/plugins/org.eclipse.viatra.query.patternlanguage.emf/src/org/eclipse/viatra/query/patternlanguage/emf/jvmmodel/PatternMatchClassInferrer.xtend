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
import java.util.Arrays
import java.util.List
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociator
import org.eclipse.xtext.xbase.jvmmodel.JvmAnnotationReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.xtext.diagnostics.Severity
import java.util.Objects

/**
 * {@link IPatternMatch} implementation inferer.
 * 
 * @author Mark Czotter
 * @noreference
 */
class PatternMatchClassInferrer {

    @Inject extension EMFJvmTypesBuilder
    @Inject extension IQualifiedNameProvider
    @Inject extension EMFPatternLanguageJvmModelInferrerUtil
    @Inject TypeReferences typeReference
    @Inject IErrorFeedback feedback
    @Extension JvmTypeReferenceBuilder builder
    @Extension JvmAnnotationReferenceBuilder annBuilder
    @Inject extension IJvmModelAssociator associator
    @Inject extension JavadocInferrer

    def inferMatchClassElements(JvmDeclaredType it, Pattern pattern, JvmType querySpecificationClass,
        JvmTypeReferenceBuilder builder, JvmAnnotationReferenceBuilder annBuilder) {
        try {

            this.builder = builder
            this.annBuilder = annBuilder

            documentation = pattern.javadocMatchClass.toString
            abstract = true
            inferMatchClassFields(pattern)
            inferMatchClassConstructors(pattern)
            inferMatchClassGetters(pattern)
            inferMatchClassSetters(pattern)
            inferMatchClassMethods(pattern, typeRef(querySpecificationClass))
            inferMatchInnerClasses(pattern)
        } catch (IllegalStateException ex) {
            feedback.reportError(pattern, ex.message, IssueCodes.OTHER_ISSUE, Severity.ERROR,
                IErrorFeedback.JVMINFERENCE_ERROR_TYPE)
        }
    }

    /**
     * Infers fields for Match class based on the input 'pattern'.
     */
    def inferMatchClassFields(JvmDeclaredType matchClass, Pattern pattern) {
        for (Variable variable : pattern.parameters) {
            matchClass.members += variable.toField(variable.fieldName, variable.calculateType)
        }
        matchClass.members += pattern.toField("parameterNames", builder.typeRef(List, builder.typeRef(String))) [
            static = true
            initializer = '''makeImmutableList(«FOR variable : pattern.parameters SEPARATOR ', '»"«variable.name»"«ENDFOR»)'''
        ]
    }

    /**
     * Infers constructors for Match class based on the input 'pattern'.
     */
    def inferMatchClassConstructors(JvmDeclaredType matchClass, Pattern pattern) {
        matchClass.members += pattern.toConstructor() [
            visibility = JvmVisibility::PRIVATE // DEFAULT
            for (Variable variable : pattern.parameters) {
                val javaType = variable.calculateType
                parameters += variable.toParameter(variable.parameterName, javaType)
            }
            body = '''
                «FOR variable : pattern.parameters»
                    this.«variable.fieldName» = «variable.parameterName»;
                «ENDFOR»
            '''
        ]
    }

    /**
     * Infers getters for Match class based on the input 'pattern'.
     */
    def inferMatchClassGetters(JvmDeclaredType matchClass, Pattern pattern) {
        matchClass.members += pattern.toMethod("get", typeRef(Object)) [
            annotations += annotationRef(Override)
            parameters += pattern.toParameter("parameterName", typeRef(String))
            body = '''
                «FOR variable : pattern.parameters»
                    if ("«variable.name»".equals(parameterName)) return this.«variable.fieldName»;
                «ENDFOR»
                return null;
            '''
        ]
        for (Variable variable : pattern.parameters) {
            val getter = variable.toMethod(variable.getterMethodName, variable.calculateType) [
                body = '''
                    return this.«variable.fieldName»;
                '''
            ]
            matchClass.members += getter
            associator.associatePrimary(variable, getter)
        }
    }

    /**
     * Infers setters for Match class based on the input 'pattern'.
     */
    def inferMatchClassSetters(JvmDeclaredType matchClass, Pattern pattern) {
        matchClass.members += pattern.toMethod("set", typeRef(boolean)) [
            returnType = typeRef(Boolean::TYPE)
            annotations += annotationRef(Override)
            parameters += pattern.toParameter("parameterName", typeRef(String))
            parameters += pattern.toParameter("newValue", typeRef(Object))
            body = '''
                if (!isMutable()) throw new java.lang.UnsupportedOperationException();
                «FOR variable : pattern.parameters»
                    «val type = variable.calculateType»
                    if ("«variable.name»".equals(parameterName) «IF typeReference.is(type, Object)»&& newValue instanceof «type»«ENDIF») {
                        this.«variable.fieldName» = («type») newValue;
                        return true;
                    }
                «ENDFOR»
                return false;
            '''
        ]
        for (Variable variable : pattern.parameters) {
            matchClass.members += pattern.toMethod(variable.setterMethodName, null) [
                returnType = typeRef(Void::TYPE)
                parameters += variable.toParameter(variable.parameterName, variable.calculateType)
                body = '''
                    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
                    this.«variable.fieldName» = «variable.parameterName»;
                '''
            ]
        }
    }

    /**
     * Infers methods for Match class based on the input 'pattern'.
     */
    def inferMatchClassMethods(JvmDeclaredType matchClass, Pattern pattern,
        JvmTypeReference querySpecificationClassRef) {
        matchClass.members += pattern.toMethod("patternName", typeRef(String)) [
            annotations += annotationRef(Override)
            body = '''
                return "«pattern.fullyQualifiedName»";
            '''
        ]
        // add extra methods like equals, hashcode, toArray, parameterNames
        matchClass.members += pattern.toMethod("parameterNames", typeRef(List, builder.typeRef(String))) [
            annotations += annotationRef(Override)
            body = '''
                return «matchClass».parameterNames;
            '''
        ]
        matchClass.members += pattern.toMethod("toArray", typeRef(Object).addArrayTypeDimension) [
            annotations += annotationRef(Override)
            body = '''
                return new Object[]{«FOR variable : pattern.parameters SEPARATOR ', '»«variable.fieldName»«ENDFOR»};
            '''
        ]
        matchClass.members += pattern.toMethod("toImmutable", typeRef(matchClass)) [
            annotations += annotationRef(Override)
            body = '''
                return isMutable() ? newMatch(«FOR variable : pattern.parameters SEPARATOR ', '»«variable.fieldName»«ENDFOR») : this;
            '''
        ]
        matchClass.members += pattern.toMethod("prettyPrint", typeRef(String)) [
            annotations += annotationRef(Override)
            setBody = '''
                «IF pattern.parameters.empty»
                    return "[]";
                «ELSE»
                    «StringBuilder» result = new «StringBuilder»();
                    «FOR variable : pattern.parameters SEPARATOR " + \", \");\n" AFTER ");\n"»result.append("\"«variable.name»\"=" + prettyPrintValue(«variable.fieldName»)«ENDFOR»
                    return result.toString();
                «ENDIF»
            '''
        ]
        matchClass.members += pattern.toMethod("hashCode", typeRef(int)) [
            annotations += annotationRef(Override)
            body = '''
                return «Objects».hash(«FOR variable : pattern.parameters SEPARATOR ", " »«variable.fieldName»«ENDFOR»);
            '''
        ]
        matchClass.members += pattern.toMethod("equals", typeRef(boolean)) [
            annotations += annotationRef(Override)
            parameters += pattern.toParameter("obj", typeRef(Object))
            body = '''
                if (this == obj)
                    return true;
                if (obj == null) {
                    return false;
                }
                if ((obj instanceof «matchClass»)) {
                    «IF !pattern.parameters.isEmpty»
                        «matchClass» other = («matchClass») obj;
                        «FOR variable : pattern.parameters BEFORE "return " SEPARATOR " && " AFTER ";"»«Objects».equals(«variable.fieldName», other.«variable.fieldName»)«ENDFOR»
                    «ELSE»
                        return true;
                    «ENDIF»
                } else {
                    // this should be infrequent
                    if (!(obj instanceof «IPatternMatch»)) {
                        return false;
                    }
                    «IPatternMatch» otherSig  = («IPatternMatch») obj;
                    return «Objects».equals(specification(), otherSig.specification()) && «Arrays».deepEquals(toArray(), otherSig.toArray());
                }
            '''
        ]
        matchClass.members += pattern.toMethod("specification", querySpecificationClassRef) [
            annotations += annotationRef(Override)
            body = '''
                return «querySpecificationClassRef.type.simpleName».instance();
            '''
        ]
        matchClass.members += pattern.toMethod("newEmptyMatch", typeRef(matchClass)) [
            static = true
            documentation = pattern.javadocNewEmptyMatchMethod.toString
            body = '''
                return new Mutable(«FOR p : pattern.parameters SEPARATOR ", "»null«ENDFOR»);
            '''
        ]
        matchClass.members += pattern.toMethod("newMutableMatch", typeRef(matchClass)) [
            static = true
            parameters += pattern.parameters.map[it.toParameter(parameterName, calculateType)]
            documentation = pattern.javadocNewMutableMatchMethod.toString
            body = '''
                return new Mutable(«FOR p : pattern.parameters SEPARATOR ", "»«p.parameterName»«ENDFOR»);
            '''
        ]
        matchClass.members += pattern.toMethod("newMatch", typeRef(matchClass)) [
            static = true
            parameters += pattern.parameters.map[it.toParameter(parameterName, calculateType)]
            documentation = pattern.javadocNewMatchMethod.toString
            body = '''
                return new Immutable(«FOR p : pattern.parameters SEPARATOR ", "»«p.parameterName»«ENDFOR»);
            '''
        ]
    }

    /**
     * Infers inner classes for Match class based on the input 'pattern'.
     */
    def inferMatchInnerClasses(JvmDeclaredType matchClass, Pattern pattern) {
        matchClass.members += matchClass.makeMatchInnerClass(pattern, pattern.matchMutableInnerClassName, true);
        matchClass.members += matchClass.makeMatchInnerClass(pattern, pattern.matchImmutableInnerClassName, false);
    }

    /**
     * Infers a single inner class for Match class
     */
    def makeMatchInnerClass(JvmDeclaredType matchClass, Pattern pattern, String innerClassName, boolean isMutable) {
        pattern.toClass(innerClassName) [
            visibility = JvmVisibility::PRIVATE
            static = true
            final = true
            superTypes += typeRef(matchClass)

            members += pattern.toConstructor() [
                simpleName = innerClassName
                visibility = JvmVisibility::DEFAULT
                for (Variable variable : pattern.parameters) {
                    val javaType = variable.calculateType
                    parameters += variable.toParameter(variable.parameterName, javaType)
                }
                body = '''
                    super(«FOR variable : pattern.parameters SEPARATOR ", "»«variable.parameterName»«ENDFOR»);
                '''
            ]
            members += pattern.toMethod("isMutable", typeRef(boolean)) [
                visibility = JvmVisibility::PUBLIC
                annotations += annotationRef(Override)
                body = '''return «isMutable»;'''
            ]
        ]
    }

}
