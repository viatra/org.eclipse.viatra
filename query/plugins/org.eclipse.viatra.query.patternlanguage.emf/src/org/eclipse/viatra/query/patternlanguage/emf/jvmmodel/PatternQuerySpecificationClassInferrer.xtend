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

import com.google.common.collect.Sets
import com.google.inject.Inject
import java.util.Arrays
import java.util.List
import java.util.Set
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder
import org.eclipse.viatra.query.patternlanguage.emf.types.IEMFTypeProvider
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFPQuery
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFQuerySpecification
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.ParameterReference
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PProblem
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException
import org.eclipse.xtend2.lib.StringConcatenationClient
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.JvmUnknownTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.serializer.impl.Serializer
import org.eclipse.xtext.xbase.jvmmodel.JvmAnnotationReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException

/**
 * {@link IQuerySpecification} implementation inferrer.
 *
 * @author Mark Czotter
 */
class PatternQuerySpecificationClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil util
	@Inject extension JavadocInferrer
	@Inject extension IEMFTypeProvider
	@Inject var IErrorFeedback feedback
	@Inject var Serializer serializer
	@Extension private JvmTypeReferenceBuilder builder
	@Extension private JvmAnnotationReferenceBuilder annBuilder

	/**
	 * Infers the {@link IQuerySpecification} implementation class from {@link Pattern}.
	 */
	def JvmDeclaredType inferQuerySpecificationClass(Pattern pattern, boolean isPrelinkingPhase, String querySpecificationPackageName, JvmType matcherClass, JvmTypeReferenceBuilder builder, JvmAnnotationReferenceBuilder annBuilder) {
		this.builder = builder
		this.annBuilder = annBuilder
		
		val querySpecificationClass = pattern.toClass(pattern.querySpecificationClassName) [
  			packageName = querySpecificationPackageName
  			documentation = pattern.javadocQuerySpecificationClass.toString
  			final = true
  			if (pattern.isPublic) {
  				superTypes += typeRef(typeof (BaseGeneratedEMFQuerySpecification), typeRef(matcherClass))
  			} else {
	  			superTypes += typeRef(typeof (BaseGeneratedEMFQuerySpecification), typeRef(matcherClass, typeRef(typeof(IPatternMatch))))
  			}
  		]
  		return querySpecificationClass
  	}

  	def initializePublicSpecification(JvmDeclaredType querySpecificationClass, Pattern pattern, JvmType matcherClass, JvmType matchClass, SpecificationBuilder specBuilder) {
  		querySpecificationClass.inferQuerySpecificationMethods(pattern, matcherClass, matchClass, true)
  		querySpecificationClass.inferQuerySpecificationInnerClasses(pattern, true, specBuilder)
  		querySpecificationClass.inferExpressions(pattern)
  	}

  	def initializePrivateSpecification(JvmDeclaredType querySpecificationClass, Pattern pattern, JvmType matcherClass, JvmType matchClass, SpecificationBuilder specBuilder) {
  		querySpecificationClass.visibility = JvmVisibility::DEFAULT
  		querySpecificationClass.inferQuerySpecificationMethods(pattern, matcherClass, matchClass, false)
  		querySpecificationClass.inferQuerySpecificationInnerClasses(pattern, false, specBuilder)
  		querySpecificationClass.inferExpressions(pattern)
  	}

	/**
   	 * Infers methods for QuerySpecification class based on the input 'pattern'.
   	 */
  	def inferQuerySpecificationMethods(JvmDeclaredType querySpecificationClass, Pattern pattern, JvmType matcherClass, JvmType matchClass, boolean isPublic) {
  		querySpecificationClass.members += pattern.toConstructor [
			visibility = JvmVisibility::PRIVATE
			body = '''
				super(«pattern.querySpecificationPQueryClassName».INSTANCE);
			'''
		] 	
  		
   		querySpecificationClass.members += pattern.toMethod("instance", typeRef(querySpecificationClass)) [
			visibility = JvmVisibility::PUBLIC
			static = true
			exceptions += typeRef(typeof (ViatraQueryException))
			documentation = pattern.javadocQuerySpecificationInstanceMethod.toString
			body = '''
					try{
						return «pattern.querySpecificationHolderClassName».INSTANCE;
					} catch («ExceptionInInitializerError» err) {
						throw processInitializerError(err);
					}
			'''
		]

  		querySpecificationClass.members += pattern.toMethod("instantiate", typeRef(matcherClass)) [
			visibility = JvmVisibility::PROTECTED
			annotations += annotationRef(typeof (Override))
			parameters += pattern.toParameter("engine", typeRef(typeof (ViatraQueryEngine)))
			exceptions += typeRef(typeof (ViatraQueryException))
			body = if (isPublic) { 
				'''return «pattern.matcherClassName».on(engine);'''
			} else {
				'''throw new «UnsupportedOperationException»();'''
			}
		]
		querySpecificationClass.members += pattern.toMethod("newEmptyMatch",
			if (isPublic) typeRef(matchClass) else typeRef(typeof(IPatternMatch))) 
		[
			visibility = JvmVisibility::PUBLIC
			annotations += annotationRef(typeof(Override))
			body = if (isPublic) {
					'''return «pattern.matchClassName».newEmptyMatch();'''
				} else {
					'''throw new «UnsupportedOperationException»();'''
				}
			
		]
		querySpecificationClass.members += pattern.toMethod("newMatch",
			if (isPublic) typeRef(matchClass) else typeRef(typeof(IPatternMatch))) [
			visibility = JvmVisibility::PUBLIC
			annotations += annotationRef(typeof(Override))
			parameters += pattern.toParameter("parameters", typeRef(Object).addArrayTypeDimension)
			varArgs = true
			body = if (isPublic) {
					'''return «pattern.matchClassName».newMatch(«FOR p : pattern.parameters SEPARATOR ', '»(«p.calculateType.qualifiedName») parameters[«pattern.parameters.indexOf(p)»]«ENDFOR»);'''
				} else {
					'''throw new «UnsupportedOperationException»();'''
				}
		]
  	}

	def inferPQueryMembers(JvmDeclaredType pQueryClass, Pattern pattern, SpecificationBuilder specBuilder) {
		pQueryClass.members += pattern.toField("INSTANCE", typeRef(pQueryClass)/*pattern.newTypeRef("volatile " + querySpecificationClass.simpleName)*/) [
			final = true
			static = true
			initializer = '''new «pattern.querySpecificationPQueryClassName»()''';
		]
		
		pQueryClass.members += pattern.toMethod("getFullyQualifiedName", typeRef(typeof (String))) [
			visibility = JvmVisibility::PUBLIC
			annotations += annotationRef(typeof (Override))
			body = '''
				return "«CorePatternLanguageHelper::getFullyQualifiedName(pattern)»";
			'''
		]
		pQueryClass.members += pattern.toMethod("getParameterNames",
			typeRef(typeof(List), typeRef(typeof(String)))) [
			visibility = JvmVisibility::PUBLIC
			annotations += annotationRef(typeof(Override))
			body = '''return «Arrays».asList(«FOR param : pattern.parameters SEPARATOR ","»"«param.name»"«ENDFOR»);'''
		]
		pQueryClass.members += pattern.toMethod("getParameters",
			typeRef(typeof(List), typeRef(typeof(PParameter)))) [
			visibility = JvmVisibility::PUBLIC
			annotations += annotationRef(typeof(Override))
			body = '''return «Arrays».asList(«FOR param : pattern.parameters SEPARATOR ","»«param.parameterInstantiation»«ENDFOR»);'''
		]
		pQueryClass.members += pattern.toMethod("doGetContainedBodies",
			typeRef(typeof(Set), typeRef(typeof(PBody)))) [
			visibility = JvmVisibility::PUBLIC
			annotations += annotationRef(typeof(Override))
			exceptions += typeRef(typeof(QueryInitializationException))
			try {
				body = '''
					«Set»<«PBody»> bodies = «Sets».newLinkedHashSet();
					try {
						«inferBodies(pattern)»
						«inferAnnotations(pattern)»
						// to silence compiler error
						if (false) throw new ViatraQueryException("Never", "happens");
					} catch («ViatraQueryException» ex) {
						throw processDependencyException(ex);
					}
					return bodies;
				'''
			} catch (Exception e) {
				//If called with an inconsistent pattern, then no body will be built
				body = '''
					addError(new «PProblem»("Inconsistent pattern definition threw exception «e.class.simpleName»  with message: «e.getMessage.escapeToQuotedString»"));
					return bodies;
				'''
				//TODO smarter error reporting required, see bug 468992
				//Turned off logging as it is disturbing for users, while the cause of the error should be reported by validation 
				//logger.warn("Error while building PBodies", e)
			}
		]
		
	}

	def StringConcatenationClient inferBodies(Pattern pattern) throws IllegalStateException {
		'''«FOR body : pattern.bodies »
			{
				PBody body = new PBody(this);
				«new BodyCodeGenerator(pattern, body, util, feedback, serializer)»
				bodies.add(body);
			}
		«ENDFOR»'''
	}

 	/**
   	 * Infers inner class for QuerySpecification class based on the input 'pattern'.
   	 */
  	def inferQuerySpecificationInnerClasses(JvmDeclaredType querySpecificationClass, Pattern pattern, boolean isPublic, SpecificationBuilder specBuilder) {
   		querySpecificationClass.members += pattern.toClass(pattern.querySpecificationHolderClassName) [
			visibility = JvmVisibility::PRIVATE
			static = true
			documentation = '''
				Inner class allowing the singleton instance of {@link «pattern.querySpecificationClassName»} to be created 
					<b>not</b> at the class load time of the outer class, 
					but rather at the first call to {@link «pattern.querySpecificationClassName»#instance()}.
				
				<p> This workaround is required e.g. to support recursion.
			'''
			
			members += pattern.toField("INSTANCE", typeRef(querySpecificationClass)/*pattern.newTypeRef("volatile " + querySpecificationClass.simpleName)*/) [
				final = true
				static = true
				initializer = '''new «pattern.querySpecificationClassName»()''';
			]
			members += pattern.toField("STATIC_INITIALIZER", typeRef(Object)) [
				final = true
				static = true
				initializer = '''ensureInitialized()''';
				documentation = '''
					Statically initializes the query specification <b>after</b> the field {@link #INSTANCE} is assigned.
					This initialization order is required to support indirect recursion.
					
					<p> The static initializer is defined using a helper field to work around limitations of the code generator.
				'''
				
			]
			it.members += pattern.toMethod("ensureInitialized", typeRef(Object)) [
				visibility = JvmVisibility::PUBLIC
				static = true
				body = '''
					INSTANCE.ensureInitializedInternalSneaky();
					return null;					
				'''
			]
		]
		
   		querySpecificationClass.members += pattern.toClass(pattern.querySpecificationPQueryClassName) [
			visibility = JvmVisibility::PRIVATE
			static = true
			superTypes += typeRef(typeof (BaseGeneratedEMFPQuery))
			inferPQueryMembers(pattern, specBuilder)
		]
	}
	
    def inferExpressions(JvmDeclaredType querySpecificationClass, Pattern pattern) {
    	pattern.bodies.map[CorePatternLanguageHelper.getAllTopLevelXBaseExpressions(it)].flatten.forEach[ex |
    		querySpecificationClass.members += ex.toMethod(expressionMethodName(ex), inferredType(ex)) [
  				it.visibility = JvmVisibility::PRIVATE
  				it.static = true
				for (variable : variables(ex)){
					val parameter = variable.toParameter(variable.name, variable.calculateType)
					it.parameters += parameter
				}
				it.body = ex
			]
    	]
    }

    def parameterInstantiation(Variable variable) {
		val ref = getVariableType(variable);
        // bug 411866: JvmUnknownTypeReference.getType() returns null in Xtext 2.4
        val clazz = if (ref == null || ref instanceof JvmUnknownTypeReference) {
        	""
        } else {
			ref.getType().getQualifiedName()
		}
		'''new PParameter("«variable.name»", "«clazz»")'''
    }

    def StringConcatenationClient inferAnnotations(Pattern pattern) {
    	'''
    		«FOR annotation : pattern.annotations»
                {
        			«PAnnotation» annotation = new «PAnnotation»("«annotation.name»");
        			«FOR attribute : CorePatternLanguageHelper.evaluateAnnotationParameters(annotation).entrySet»
        				annotation.addAttribute("«attribute.key»", «outputAnnotationParameter(attribute.value)»);
        			«ENDFOR»
        			addAnnotation(annotation);
        		}
    		«ENDFOR»
    	'''
    }

    def StringConcatenationClient outputAnnotationParameter(Object value) {
        switch value {
            List<?>: {
                '''«Arrays».asList(new Object[] {
                «FOR item : value SEPARATOR ", "»
                    «outputAnnotationParameter(item)»
                «ENDFOR»
                })'''
            }
            ParameterReference: {
                '''new «ParameterReference»("«value.name»")'''
            }
            String: {
                '''"«value»"'''
            }
            default: {
                '''«value.toString»'''
            }
        }
    }
}