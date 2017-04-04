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
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra.query.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFIssueCodes
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ExecutionType
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Parameter
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ParameterDirection
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable
import org.eclipse.viatra.query.patternlanguage.typing.ITypeInferrer
import org.eclipse.viatra.query.patternlanguage.typing.ITypeSystem
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFPQuery
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFQuerySpecification
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackendFactory
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.ParameterReference
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameterDirection
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PProblem
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory
import org.eclipse.xtend2.lib.StringConcatenationClient
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.JvmUnknownTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.serializer.impl.Serializer
import org.eclipse.xtext.xbase.jvmmodel.JvmAnnotationReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory
import org.eclipse.viatra.query.runtime.api.GenericPatternMatch
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedPrivateEMFQuerySpecification

/**
 * {@link IQuerySpecification} implementation inferrer.
 *
 * @author Mark Czotter
 * @noreference
 */
class PatternQuerySpecificationClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil util
	@Inject extension JavadocInferrer
	@Inject extension ITypeInferrer
	@Inject var ITypeSystem typeSystem
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
    			superTypes += typeRef(typeof (BaseGeneratedPrivateEMFQuerySpecification))
  			}
  			fileHeader = pattern.fileComment
  		]
  		return querySpecificationClass
  	}

  	def initializeSpecification(JvmDeclaredType querySpecificationClass, Pattern pattern, JvmType matcherClass, JvmType matchClass, SpecificationBuilder specBuilder) {
  	    try {
  		    querySpecificationClass.inferQuerySpecificationMethods(pattern, matcherClass, matchClass, pattern.isPublic)
  		    querySpecificationClass.inferQuerySpecificationInnerClasses(pattern, pattern.isPublic, specBuilder)
  		    querySpecificationClass.inferExpressions(pattern)
        } catch (IllegalStateException ex) {
            feedback.reportError(pattern, ex.message, EMFIssueCodes.OTHER_ISSUE, Severity.ERROR,
                IErrorFeedback.JVMINFERENCE_ERROR_TYPE)
        }
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

  		if (isPublic) {
      		querySpecificationClass.members += pattern.toMethod("instantiate", typeRef(matcherClass)) [
    			visibility = JvmVisibility::PROTECTED
    			annotations += annotationRef(typeof (Override))
    			parameters += pattern.toParameter("engine", typeRef(typeof (ViatraQueryEngine)))
    			exceptions += typeRef(typeof (ViatraQueryException))
    			body = '''return «matcherClass».on(engine);'''
    		]
    		querySpecificationClass.members += pattern.toMethod("instantiate", typeRef(matcherClass)) [
                visibility = JvmVisibility::PUBLIC
                annotations += annotationRef(typeof (Override))
                exceptions += typeRef(typeof (ViatraQueryException))
                body = '''return «matcherClass».create();'''
            ]
    		querySpecificationClass.members += pattern.toMethod("newEmptyMatch", typeRef(matchClass)) [
    			visibility = JvmVisibility::PUBLIC
    			annotations += annotationRef(typeof(Override))
    			body = '''return «matchClass».newEmptyMatch();'''
    			
    		]
    		querySpecificationClass.members += pattern.toMethod("newMatch", typeRef(matchClass)) [
    			visibility = JvmVisibility::PUBLIC
    			annotations += annotationRef(typeof(Override))
    			parameters += pattern.toParameter("parameters", typeRef(Object).addArrayTypeDimension)
    			varArgs = true
    			body = '''return «pattern.matchClassName».newMatch(«FOR p : pattern.parameters SEPARATOR ', '»(«p.calculateType.qualifiedName») parameters[«pattern.parameters.indexOf(p)»]«ENDFOR»);'''
    		]
  		}
  	}

    def direction(Variable variable){
        if(variable instanceof Parameter){
            variable.direction
        }
        ParameterDirection.INOUT;
    }
    
    def StringConcatenationClient directionLiteral(Variable variable){
        '''«PParameterDirection».«variable.direction.name()»'''
    }

	def inferPQueryMembers(JvmDeclaredType pQueryClass, Pattern pattern, SpecificationBuilder specBuilder) {
		pQueryClass.members += pattern.toField("INSTANCE", typeRef(pQueryClass)/*pattern.newTypeRef("volatile " + querySpecificationClass.simpleName)*/) [
			final = true
			static = true
			initializer = '''new «pattern.querySpecificationPQueryClassName»()'''
		]
		for (parameter : pattern.parameters) {
		    pQueryClass.members += pattern.toField(parameter.PParameterName, typeRef(typeof(PParameter)))[
		        final = true
		        visibility = JvmVisibility::PRIVATE
		        initializer = parameter.parameterInstantiation
		    ]
		}
		pQueryClass.members += pattern.toField("parameters", typeRef(typeof(List), typeRef(typeof(PParameter))))[
	        final = true
		    visibility = JvmVisibility::PRIVATE
		    initializer = '''«Arrays».asList(«FOR param : pattern.parameters SEPARATOR ", "»«param.PParameterName»«ENDFOR»)'''
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
			body = '''return parameters;'''
		]
		pQueryClass.members += pattern.toMethod("doGetContainedBodies",
			typeRef(typeof(Set), typeRef(typeof(PBody)))) [
			visibility = JvmVisibility::PUBLIC
			annotations += annotationRef(typeof(Override))
			exceptions += typeRef(typeof(QueryInitializationException))
			try {
				body = '''
					setEvaluationHints(«inferQueryEvaluationHints(pattern)»);
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
    
    def ExecutionType getRequestedExecutionType(Pattern pattern){
        val modifier = pattern.modifiers
        if(modifier != null){
            modifier.execution
        } else{
            ExecutionType::UNSPECIFIED
        }
    }
    
    def StringConcatenationClient incrementalBackendFactory(){
        '''new «ReteBackendFactory»()'''
    }
    
    def StringConcatenationClient searchBackendFactory(){
        '''«LocalSearchBackendFactory».INSTANCE'''
    }
    def StringConcatenationClient defaultBackendFactory(){
        '''(«IQueryBackendFactory»)null''' 
    }
    
    def StringConcatenationClient inferQueryEvaluationHints(Pattern pattern) {
        '''new «QueryEvaluationHint»(null, «
        switch(getRequestedExecutionType(pattern)){
            case INCREMENTAL: {
                incrementalBackendFactory
            }
            case SEARCH: {
                searchBackendFactory
            }
            case UNSPECIFIED: {
               defaultBackendFactory 
            }
            
         }
            »)'''
    }
    

	def StringConcatenationClient inferBodies(Pattern pattern) throws IllegalStateException {
		'''«FOR body : pattern.bodies »
			{
				PBody body = new PBody(this);
				«new BodyCodeGenerator(pattern, body, util, feedback, serializer, builder)»
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

    def StringConcatenationClient parameterInstantiation(Variable variable) {
		val ref = getJvmType(variable, variable);
        // bug 411866: JvmUnknownTypeReference.getType() returns null in Xtext 2.4
        val clazz = if (ref == null || ref instanceof JvmUnknownTypeReference) {
        	""
        } else {
			ref.getType().getQualifiedName()
		}
		val type = variable.type
        if (type == null || !typeSystem.isValidType(type)) {
    		'''new PParameter("«variable.name»", "«clazz»", («IInputKey»)null, «variable.directionLiteral»)'''
        } else {
            val declaredInputKey = typeSystem.extractTypeDescriptor(type)
            '''new PParameter("«variable.name»", "«clazz»", «serializeInputKey(declaredInputKey, true)», «variable.directionLiteral»)'''
        }
    }

    def StringConcatenationClient inferAnnotations(Pattern pattern) {
    	'''
    		«FOR annotation : pattern.annotations»
        		{
        			«PAnnotation» annotation = new «PAnnotation»("«annotation.name»");
        			«FOR attribute : CorePatternLanguageHelper.evaluateAnnotationParametersWithMultiplicity(annotation).entries»
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