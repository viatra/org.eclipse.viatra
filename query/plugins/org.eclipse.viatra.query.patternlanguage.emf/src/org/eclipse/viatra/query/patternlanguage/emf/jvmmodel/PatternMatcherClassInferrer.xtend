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
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmGenericType
import org.eclipse.xtext.common.types.JvmVisibility
import org.apache.log4j.Logger
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import java.util.HashSet
import java.util.Set
import org.eclipse.viatra.query.runtime.api.IMatchProcessor
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple
import java.util.Collection
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.xtext.xbase.jvmmodel.JvmAnnotationReferenceBuilder
import org.eclipse.xtend2.lib.StringConcatenationClient
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFIssueCodes
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageGeneratorConfig

/**
 * {@link ViatraQueryMatcher} implementation inferrer.
 *
 * @author Mark Czotter
 * @noreference
 */
class PatternMatcherClassInferrer {

    @Inject extension EMFJvmTypesBuilder
    @Inject extension EMFPatternLanguageJvmModelInferrerUtil
    @Inject extension JavadocInferrer
    @Extension private JvmTypeReferenceBuilder builder
    @Extension private JvmAnnotationReferenceBuilder annBuilder
    @Inject private IErrorFeedback feedback

    def inferMatcherClassElements(JvmGenericType matcherClass, Pattern pattern, JvmDeclaredType specificationClass, JvmDeclaredType matchClass, JvmTypeReferenceBuilder builder, JvmAnnotationReferenceBuilder annBuilder, EMFPatternLanguageGeneratorConfig config) {
        try {
          val generateMatchProcessors = config.generateMatchProcessors
            
          this.builder = builder
          this.annBuilder = annBuilder
        
          matcherClass.documentation = pattern.javadocMatcherClass.toString
          matcherClass.inferStaticMethods(pattern)
          matcherClass.inferFields(pattern)
          matcherClass.inferConstructors(pattern)
          matcherClass.inferMethods(pattern, matchClass, generateMatchProcessors)
        
          // add querySpecification() field to Matcher class
          matcherClass.members += pattern.toMethod("querySpecification",
             typeRef(IQuerySpecification, typeRef(matcherClass))) [
             visibility = JvmVisibility::PUBLIC
             static = true
             documentation = pattern.javadocQuerySpecificationMethod.toString
             exceptions += typeRef(ViatraQueryException)
             body = '''
                    return «specificationClass.typeRef».instance();
             '''
          ]
        
        } catch (IllegalStateException ex) {
            feedback.reportError(pattern, ex.message, EMFIssueCodes.OTHER_ISSUE, Severity.ERROR,
                IErrorFeedback.JVMINFERENCE_ERROR_TYPE)
        }
    }

       /**
        * Infers fields for Matcher class based on the input 'pattern'.
        */
       def inferFields(JvmDeclaredType matcherClass, Pattern pattern) {
           for (Variable variable : pattern.parameters) {
               matcherClass.members += pattern.toField(variable.positionConstant, typeRef(int))[
                 static = true
                 final = true
                   initializer = '''«pattern.parameters.indexOf(variable)»'''
               ]
           }
           matcherClass.members += pattern.toField("LOGGER", typeRef(Logger)) [
               static = true
               final = true
               initializer = '''
                   «ViatraQueryLoggingUtil».getLogger(«matcherClass».class)
               '''
           ]
       }

       /**
        * Infers static methods for Matcher class based on the input 'pattern'.
        * NOTE: queryDefinition() will be inferred later, in EMFPatternLanguageJvmModelInferrer
        */
       def inferStaticMethods(JvmGenericType matcherClass, Pattern pattern) {
           matcherClass.members += pattern.toMethod("on", typeRef(matcherClass)) [
               static = true
            visibility = JvmVisibility::PUBLIC
            documentation = pattern.javadocMatcherStaticOnEngine.toString
            parameters += pattern.toParameter("engine", typeRef(ViatraQueryEngine))
            exceptions += typeRef(ViatraQueryException)
            body = '''
                // check if matcher already exists
                «matcherClass.simpleName» matcher = engine.getExistingMatcher(querySpecification());
                if (matcher == null) {
                    matcher = («matcherClass.simpleName»)engine.getMatcher(querySpecification());
                }
                return matcher;
            '''
           ]
           matcherClass.members += pattern.toMethod("create", typeRef(matcherClass)) [
            static = true
            visibility = JvmVisibility::PUBLIC
            documentation = pattern.javadocMatcherStaticCreate.toString
            exceptions += typeRef(ViatraQueryException)
            body = '''
                return new «matcherClass.simpleName»();
            '''
        ]
       }



    /**
        * Infers constructors for Matcher class based on the input 'pattern'.
        */
       def inferConstructors(JvmDeclaredType matcherClass, Pattern pattern) {

        matcherClass.members += pattern.toConstructor [
            visibility = JvmVisibility::PRIVATE
            documentation = pattern.javadocMatcherConstructorEngine.toString
            exceptions += typeRef(ViatraQueryException)
            body = '''super(querySpecification());'''
        ]
       }

    /**
        * Infers methods for Matcher class based on the input 'pattern'.
        */
       def inferMethods(JvmDeclaredType type, Pattern pattern, JvmType matchClass, boolean generateMatchProcessor) {
           this.builder = builder
           // Adding type-safe matcher calls
        // if the pattern not defines parameters, the Matcher class contains only the hasMatch method
        if (!pattern.parameters.isEmpty) {
             type.members += pattern.toMethod("getAllMatches", typeRef(Collection, typeRef(matchClass))) [
                   documentation = pattern.javadocGetAllMatchesMethod.toString
                   for (parameter : pattern.parameters){
                    parameters += parameter.toParameter(parameter.parameterName, parameter.calculateType)
                   }
                   body = '''
                       return rawGetAllMatches(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.parameterName»«ENDFOR»});
                   '''
               ]
               type.members += pattern.toMethod("getOneArbitraryMatch", typeRef(matchClass)) [
                   documentation = pattern.javadocGetOneArbitraryMatchMethod.toString
                   for (parameter : pattern.parameters){
                    parameters += parameter.toParameter(parameter.parameterName, parameter.calculateType)
                   }
                   body = '''
                       return rawGetOneArbitraryMatch(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.parameterName»«ENDFOR»});
                   '''
               ]
               type.members += pattern.toMethod("hasMatch", typeRef(boolean)) [
                   documentation = pattern.javadocHasMatchMethod.toString
                   for (parameter : pattern.parameters){
                    parameters += parameter.toParameter(parameter.parameterName, parameter.calculateType)
                   }
                   body = '''
                       return rawHasMatch(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.parameterName»«ENDFOR»});
                   '''
               ]
               type.members += pattern.toMethod("countMatches", typeRef(int)) [
                   documentation = pattern.javadocCountMatchesMethod.toString
                   for (parameter : pattern.parameters){
                    parameters += parameter.toParameter(parameter.parameterName, parameter.calculateType)
                   }
                   body = '''
                       return rawCountMatches(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.parameterName»«ENDFOR»});
                   '''
               ]
               if (generateMatchProcessor) {
                   type.members += pattern.toMethod("forEachMatch", null) [
                       returnType = typeRef(Void::TYPE)
                       documentation = pattern.javadocForEachMatchMethod.toString
                       for (parameter : pattern.parameters){
                        parameters += parameter.toParameter(parameter.parameterName, parameter.calculateType)
                       }
                    parameters += pattern.toParameter("processor", typeRef(IMatchProcessor, typeRef(matchClass).wildcardSuper))
                       body = '''
                           rawForEachMatch(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.parameterName»«ENDFOR»}, processor);
                       '''
                   ]
               }
               type.members += pattern.toMethod("forOneArbitraryMatch", typeRef(boolean)) [
                   documentation = pattern.javadocForOneArbitraryMatchMethod.toString
                   for (parameter : pattern.parameters){
                    parameters += parameter.toParameter(parameter.parameterName, parameter.calculateType)
                   }
                   parameters += pattern.toParameter("processor", typeRef(IMatchProcessor, typeRef(matchClass).wildcardSuper))
                   body = '''
                       return rawForOneArbitraryMatch(new Object[]{«FOR p : pattern.parameters SEPARATOR ', '»«p.parameterName»«ENDFOR»}, processor);
                   '''
               ]
               type.members += pattern.toMethod("newMatch", typeRef(matchClass)) [
                documentation = pattern.javadocNewMatchMethod.toString
                   for (parameter : pattern.parameters){
                    parameters += parameter.toParameter(parameter.parameterName, parameter.calculateType)
                   }
                   body = '''
                       return «typeRef(matchClass)».newMatch(«FOR p : pattern.parameters SEPARATOR ', '»«p.parameterName»«ENDFOR»);
                   '''
               ]
               for (variable : pattern.parameters){
                   val typeOfVariable = variable.calculateType
                   type.members += variable.toMethod("rawAccumulateAllValuesOf"+variable.name, typeRef(Set, typeOfVariable)) [
                       documentation = variable.javadocGetAllValuesOfMethod.toString
                       parameters += variable.toParameter("parameters", typeRef(Object).addArrayTypeDimension)
                       visibility = JvmVisibility::PROTECTED
                       body = '''
                           «Set»<«typeOfVariable»> results = new «HashSet»<«typeOfVariable»>();
                           rawAccumulateAllValues(«variable.positionConstant», parameters, results);
                           return results;
                       '''
                   ]
                   type.members += pattern.toMethod("getAllValuesOf"+variable.name, typeRef(Set, typeOfVariable)) [
                       documentation = variable.javadocGetAllValuesOfMethod.toString
                       body = '''
                           return rawAccumulateAllValuesOf«variable.name»(emptyArray());
                       '''
                   ]
                   if(pattern.parameters.size > 1){
                       type.members += variable.toMethod("getAllValuesOf"+variable.name, typeRef(Set, typeOfVariable)) [
                           documentation = variable.javadocGetAllValuesOfMethod.toString
                           parameters += pattern.toParameter("partialMatch", typeRef(matchClass))
                           body = '''
                               return rawAccumulateAllValuesOf«variable.name»(partialMatch.toArray());
                           '''
                       ]
                       type.members += variable.toMethod("getAllValuesOf"+variable.name, typeRef(Set, typeOfVariable)) [
                           documentation = variable.javadocGetAllValuesOfMethod.toString
                           for (parameter : pattern.parameters){
                               if(parameter != variable){
                                parameters += parameter.toParameter(parameter.parameterName, parameter.calculateType)
                               }
                           }
                           body = '''
                               return rawAccumulateAllValuesOf«variable.name»(new Object[]{
                               «FOR p : pattern.parameters SEPARATOR ', '»
                                   «if (p.parameterName == variable.parameterName) "null" else p.parameterName»
                                   «ENDFOR»});
                           '''
                       ]
                   }
               }
        } else {
            type.members += pattern.toMethod("hasMatch", typeRef(boolean)) [
                   documentation = pattern.javadocHasMatchMethodNoParameter.toString
                   body = '''return rawHasMatch(new Object[]{});'''
               ]
        }

        type.inferMatcherClassToMatchMethods(pattern, matchClass)
       }

       /**
        * Infers tupleToMatch, arrayToMatch methods for Matcher class based on the input 'pattern'.
        */
       def inferMatcherClassToMatchMethods(JvmDeclaredType matcherClass, Pattern pattern, JvmType matchClass) {
           val tupleToMatchMethod = pattern.toMethod("tupleToMatch", typeRef(matchClass)) [
               annotations += annotationRef(Override)
               visibility = JvmVisibility::PROTECTED
               parameters += pattern.toParameter("t", typeRef(Tuple))
           ]
           val arrayToMatchMethod = pattern.toMethod("arrayToMatch", typeRef(matchClass)) [
               annotations += annotationRef(Override)
               visibility = JvmVisibility::PROTECTED
               parameters += pattern.toParameter("match", typeRef(Object).addArrayTypeDimension)
           ]
           val arrayToMatchMutableMethod = pattern.toMethod("arrayToMatchMutable", typeRef(matchClass)) [
               annotations += annotationRef(Override)
               visibility = JvmVisibility::PROTECTED
               parameters += pattern.toParameter("match", typeRef(Object).addArrayTypeDimension)
           ]
           tupleToMatchMethod.body = '''«pattern.inferTupleToMatchMethodBody(matchClass)»'''
           arrayToMatchMethod.body = '''«pattern.inferArrayToMatchMethodBody(matchClass)»'''
           arrayToMatchMutableMethod.body = '''«pattern.inferArrayToMatchMutableMethodBody(matchClass)»'''
           matcherClass.members += tupleToMatchMethod
           matcherClass.members += arrayToMatchMethod
           matcherClass.members += arrayToMatchMutableMethod
       }

      /**
       * Infers the tupleToMatch method body.
       */
      def StringConcatenationClient inferTupleToMatchMethodBody(Pattern pattern, JvmType matchClass) {
           '''
            try {
                return «matchClass».newMatch(«FOR p : pattern.parameters SEPARATOR ', '»(«p.calculateType.type») t.get(«p.positionConstant»)«ENDFOR»);
            } catch(ClassCastException e) {
                «inferErrorLogging("Element(s) in tuple not properly typed!", "e")»
                return null;
            }
        '''
      }

      /**
       * Infers the arrayToMatch method body.
       */
      def StringConcatenationClient inferArrayToMatchMethodBody(Pattern pattern, JvmType matchClass) {
          '''
            try {
                return «matchClass».newMatch(«FOR p : pattern.parameters SEPARATOR ', '»(«p.calculateType.type») match[«p.positionConstant»]«ENDFOR»);
            } catch(ClassCastException e) {
                «inferErrorLogging("Element(s) in array not properly typed!", "e")»
                return null;
            }
           '''
      }
      /**
       * Infers the arrayToMatch method body.
       */
      def StringConcatenationClient inferArrayToMatchMutableMethodBody(Pattern pattern, JvmType matchClass) {
          '''
               try {
                   return «matchClass».newMutableMatch(«FOR p : pattern.parameters SEPARATOR ', '»(«p.calculateType.type») match[«p.positionConstant»]«ENDFOR»);
               } catch(ClassCastException e) {
                   «inferErrorLogging("Element(s) in array not properly typed!", "e")»
                   return null;
               }
           '''
      }

      /**
       * Infers the appropriate logging based on the parameters.
       *
       */
      def inferErrorLogging(String message, String exceptionName) {
          if(exceptionName === null){
              '''LOGGER.error("«message»");'''
          } else {
              '''LOGGER.error("«message»",«exceptionName»);'''
          }
    }
}