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

package org.eclipse.incquery.patternlanguage.emf.jvmmodel

import com.google.common.base.Joiner
import com.google.common.base.Preconditions
import com.google.common.collect.Sets
import com.google.inject.Inject
import java.util.Arrays
import java.util.List
import java.util.Set
import org.apache.log4j.Logger
import org.eclipse.emf.common.util.Enumerator
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EEnumLiteral
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.incquery.patternlanguage.emf.specification.SpecificationBuilder
import org.eclipse.incquery.patternlanguage.emf.specification.XBaseEvaluator
import org.eclipse.incquery.patternlanguage.emf.types.IEMFTypeProvider
import org.eclipse.incquery.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.incquery.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.api.IQuerySpecification
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification
import org.eclipse.incquery.runtime.exception.IncQueryException
import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator
import org.eclipse.incquery.runtime.matchers.psystem.IValueProvider
import org.eclipse.incquery.runtime.matchers.psystem.PBody
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint
import org.eclipse.incquery.runtime.matchers.psystem.PVariable
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation
import org.eclipse.incquery.runtime.matchers.psystem.annotations.ParameterReference
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.PatternMatchCounter
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.ConstantValue
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter
import org.eclipse.incquery.runtime.matchers.psystem.queries.PProblem
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery.PQueryStatus
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple
import org.eclipse.incquery.runtime.matchers.tuple.Tuple
import org.eclipse.xtend2.lib.StringConcatenationClient
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.common.types.JvmUnknownTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.XFeatureCall
import org.eclipse.xtext.xbase.jvmmodel.JvmAnnotationReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.incquery.runtime.matchers.psystem.queries.BasePQuery
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFPQuery
import org.eclipse.incquery.patternlanguage.emf.specification.GenericEMFPatternPQuery

/**
 * {@link IQuerySpecification} implementation inferrer.
 *
 * @author Mark Czotter
 */
class PatternQuerySpecificationClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer
	@Inject extension IEMFTypeProvider
	@Inject var IErrorFeedback feedback
	@Inject var Logger logger
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
			exceptions += typeRef(typeof (IncQueryException))
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
			parameters += pattern.toParameter("engine", typeRef(typeof (IncQueryEngine)))
			exceptions += typeRef(typeof (IncQueryException))
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
				val IQuerySpecification<?> genericSpecification = specBuilder.getOrCreateSpecification(pattern, true)
				if (genericSpecification == null || genericSpecification.internalQueryRepresentation == null || genericSpecification.internalQueryRepresentation.status == PQueryStatus::ERROR) {
					feedback.reportError(pattern, "Error building generic query specification",
					EMFPatternLanguageJvmModelInferrer::SPECIFICATION_BUILDER_CODE, Severity::ERROR,
							IErrorFeedback::JVMINFERENCE_ERROR_TYPE)
				}
				if (genericSpecification == null || genericSpecification.internalQueryRepresentation == null) {
					body = '''
						addError(new «PProblem»("Could not initialize query specification from the pattern definition"));
						return null;
					'''
				} else {
					body = '''
						«Set»<«PBody»> bodies = «Sets».newLinkedHashSet();
						try {
							«inferBodies(pattern, genericSpecification)»
							«inferAnnotations(pattern, genericSpecification)»
							«FOR problem : genericSpecification.internalQueryRepresentation.PProblems»
								addError(new «PProblem»("«problem.shortMessage.escapeToQuotedString»"));
							«ENDFOR»
							// to silence compiler error
							if (false) throw new IncQueryException("Never", "happens");
						} catch («IncQueryException» ex) {
							throw processDependencyException(ex);
						}
						return bodies;
					'''
				}			
			} catch (Exception e) {
				//If called with an inconsistent pattern, then no body will be built
				body = '''
					addError(new «PProblem»("Inconsistent pattern definition threw exception «e.class.simpleName»  with message: «e.getMessage.escapeToQuotedString»"));
					return bodies;
				'''
				// 	TODO smarter error reporting required
				logger.warn("Error while building PBodies", e)
			}
		]
		
	}


	def StringConcatenationClient inferBodies(Pattern pattern, IQuerySpecification<?> genericSpecification) throws IllegalStateException {
		'''«FOR pBody : genericSpecification.internalQueryRepresentation.disjunctBodies.bodies »
			{
				PBody body = new PBody(this);
				«FOR variable : pBody.uniqueVariables »
					«PVariable» «variable.escapedName» = body.getOrCreateVariableByName("«variable.name»");
				«ENDFOR»
				body.setExportedParameters(«Arrays».<«ExportedParameter»>asList(
					«FOR parameter : pBody.symbolicParameters SEPARATOR ",\n"»
						«parameter.inferExportedParameterConstraint(pBody, pattern)»
					«ENDFOR»
				));
				«FOR constraint : pBody.constraints»
					«constraint.inferConstraint(pBody, pattern)»
				«ENDFOR»
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
			members += pattern.toField("INSTANCE", typeRef(querySpecificationClass)/*pattern.newTypeRef("volatile " + querySpecificationClass.simpleName)*/) [
				final = true
				static = true
				initializer = '''make()''';
			]
			it.members += pattern.toMethod("make", typeRef(querySpecificationClass)) [
				visibility = JvmVisibility::PUBLIC
				static = true
				body = '''
					return new «pattern.querySpecificationClassName»();					
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
	
	def escapedName(PVariable variable) {
		if (variable == null)
			"var_"
		else
		"var_" + variable.name.replaceAll("[\\.\\{\\}<>]","_")
	}

	def StringConcatenationClient inferExportedParameterConstraint(ExportedParameter constraint, PBody body, Pattern pattern) {
		'''new «ExportedParameter»(body, «constraint.parameterVariable.escapedName», "«constraint.parameterName»")'''
	}
	
	def StringConcatenationClient inferConstraint(PConstraint constraint, PBody body, Pattern pattern) {
		switch constraint {
			ExportedParameter : {
				// Exported parameters are already handled in inferExportedParameterConstraints - ignore here
				''''''
			}
			Equality : {
				'''new «Equality»(body, «constraint.who.escapedName», «constraint.withWhom.escapedName»);'''
			}
			Inequality: {
				'''new «Inequality»(body, «constraint.who.escapedName», «constraint.withWhom.escapedName»);'''
			}
			TypeUnary: {
				val literal = constraint.supplierKey as EClassifier
				val packageNsUri = literal.EPackage.nsURI
				'''new «TypeUnary»(body, «constraint.variablesTuple.output», getClassifierLiteral("«packageNsUri»", "«literal.name»"), "«constraint.typeString»");'''
			}
			TypeBinary: {
				val literal = constraint.supplierKey as EStructuralFeature
				val container = literal.EContainingClass
				val packageNsUri = container.EPackage.nsURI
				'''new «TypeBinary»(body, CONTEXT, «constraint.variablesTuple.output», getFeatureLiteral("«packageNsUri»", "«container.name»", "«literal.name»"), "«constraint.typeString»");'''
			}
			ConstantValue : {
				'''new «ConstantValue»(body, «constraint.variablesTuple.output», «constraint.supplierKey.outputConstant»);'''
			}
			PositivePatternCall : {
				'''new «PositivePatternCall»(body, new «FlatTuple»(«constraint.variablesTuple.output»), «referPQuery(constraint.referredQuery, pattern)»);'''
			}
			NegativePatternCall : {
				'''new «NegativePatternCall»(body, new «FlatTuple»(«constraint.actualParametersTuple.output»), «referPQuery(constraint.referredQuery, pattern)»);'''
			}
			BinaryTransitiveClosure: {
				'''new «BinaryTransitiveClosure»(body, new «FlatTuple»(«constraint.variablesTuple.output»), «referPQuery(constraint.supplierKey, pattern)»);'''
			}
			PatternMatchCounter: {
				'''new «PatternMatchCounter»(body, new «FlatTuple»(«constraint.actualParametersTuple.output»), «referPQuery(constraint.referredQuery, pattern)», «constraint.resultVariable.escapedName»);'''
			}
			ExpressionEvaluation : {
				'''
				«val evaluator = constraint.evaluator as XBaseEvaluator»
				«if (evaluator.inputParameterNames.empty) {
					feedback.reportError(evaluator.expression, "No parameters defined", EMFPatternLanguageJvmModelInferrer::SPECIFICATION_BUILDER_CODE, Severity::WARNING, IErrorFeedback::JVMINFERENCE_ERROR_TYPE)
				}»
				new «ExpressionEvaluation»(body, new «IExpressionEvaluator»() {
					
					@Override
					public String getShortDescription() {
						return "Expression evaluation from pattern «pattern.name»";
					}

					@Override
					public Iterable<String> getInputParameterNames() {
						return «Arrays».asList(«FOR name : evaluator.inputParameterNames SEPARATOR ", "»"«name»"«ENDFOR»);
					}

					@Override
					public Object evaluateExpression(«IValueProvider» provider) throws Exception {
							«val variables = variables(evaluator.expression)»
							«FOR variable : variables»
								«variable.calculateType.qualifiedName» «variable.name» = («variable.calculateType.qualifiedName») provider.getValue("«variable.name»");
							«ENDFOR»
							return «expressionMethodName(evaluator.expression)»(«FOR variable : variables SEPARATOR ', '»«variable.name»«ENDFOR»);
						}

				}, «IF constraint.outputVariable != null » «constraint.outputVariable.escapedName» «ELSE» null«ENDIF»); '''
			} default:
				'''
					//TODO error in code generation
					Unsupported constraint «constraint.class.simpleName»
				'''
		}

	}

	def output(Tuple tuple) {
		Joiner.on(", ").join(tuple.elements.map[(it as PVariable).escapedName])
	}

	def StringConcatenationClient outputConstant(Object constant) {
		switch constant {
			EEnumLiteral: {
				val enumeration = constant.EEnum
				val ePackage = enumeration.EPackage
				'''getEnumLiteral("«ePackage.nsURI»", "«enumeration.name»", "«constant.name»").getInstance()'''
			}
			Enumerator : {
				'''«constant.class.canonicalName».get("«constant.literal»")'''
			}
			String :
				'''"«constant»"'''
			default :
				'''«constant»'''
		}
	}

	def StringConcatenationClient referPQuery(PQuery referredQuery, Pattern callerPattern) {
		if ((referredQuery as GenericEMFPatternPQuery).getPattern == callerPattern) {
			'''this'''
		} else {
			'''«referredQuery.findGeneratedSpecification».instance().getInternalQueryRepresentation()'''
		}
		
	}

	def findGeneratedSpecification(PQuery query) {
		(query as GenericEMFPatternPQuery).getPattern.findInferredSpecification
	}

	def expressionMethodName(XExpression ex) {
		"evaluateExpression_" + getExpressionPostfix(ex)
	}

    def getExpressionPostfix(XExpression xExpression) {
    	val pattern = EcoreUtil2.getContainerOfType(xExpression, typeof(Pattern))
    	Preconditions.checkArgument(pattern != null, "Expression is not inside a pattern")
        var bodyNo = 0
        for (patternBody : pattern.getBodies()) {
            bodyNo = bodyNo + 1
            var exNo = 0
            for (xExpression2 : CorePatternLanguageHelper.getAllTopLevelXBaseExpressions(patternBody)) {
                    exNo = exNo + 1
                    if (xExpression.equals(xExpression2)) {
                        return bodyNo + "_" + exNo
                    }
			}
        }
        //Shall never be executed
        throw new RuntimeException("Expression not found in pattern")
    }

	def variables(XExpression ex) {
		val body = EcoreUtil2.getContainerOfType(ex, PatternBody)
   		val valNames = (ex.eAllContents + newImmutableList(ex).iterator).
			    filter(typeof(XFeatureCall)).map[concreteSyntaxFeatureName].
				toList
		body.variables.filter[valNames.contains(it.name)].sortBy[name]
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

    def StringConcatenationClient inferAnnotations(Pattern pattern, IQuerySpecification<?> genericSpecification) {
    	'''
    		«FOR ann : genericSpecification.allAnnotations»
    		{
    			«PAnnotation» annotation = new «PAnnotation»("«ann.name»");
    			«FOR attribute : ann.allValues»
    				annotation.addAttribute("«attribute.key»", «outputAnnotationParameter(pattern, attribute.value)»);
    			«ENDFOR»
    			addAnnotation(annotation);
    		}
    		«ENDFOR»
    	'''
    }

    def StringConcatenationClient outputAnnotationParameter(Pattern ctx, Object value) {
		switch value {
			List<?> : {
				'''«Arrays».asList(new Object[] {
					«FOR item : value SEPARATOR ", "»
						«outputAnnotationParameter(ctx, item)»
					«ENDFOR»
				})'''
			} ParameterReference : {
				'''new «ParameterReference»("«value.name»")'''
			} String : {
				'''"«value»"'''
			} default : {
				'''«value.toString»'''
			}
    	}
    }
}