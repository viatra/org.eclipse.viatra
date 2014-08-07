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
import org.eclipse.incquery.patternlanguage.emf.specification.GenericQuerySpecification
import org.eclipse.incquery.patternlanguage.emf.specification.SpecificationBuilder
import org.eclipse.incquery.patternlanguage.emf.specification.XBaseEvaluator
import org.eclipse.incquery.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.incquery.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.emf.util.IErrorFeedback
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable
import org.eclipse.incquery.runtime.api.IQuerySpecification
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification
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
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery.PQueryStatus
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple
import org.eclipse.incquery.runtime.matchers.tuple.Tuple
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmUnknownTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.XFeatureCall
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable
import org.eclipse.xtext.xbase.typing.ITypeProvider
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.matchers.psystem.queries.PProblemimport org.eclipse.incquery.runtime.emf.EMFPatternMatcherContext

/**
 * {@link IQuerySpecification} implementation inferrer.
 *
 * @author Mark Czotter
 */
class PatternQuerySpecificationClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer
	@Inject extension ITypeProvider
	@Inject extension TypeReferences types
	@Inject var IErrorFeedback feedback
	@Inject var Logger logger

	/**
	 * Infers the {@link IQuerySpecification} implementation class from {@link Pattern}.
	 */
	def JvmDeclaredType inferQuerySpecificationClass(Pattern pattern, boolean isPrelinkingPhase, String querySpecificationPackageName, JvmTypeReference matcherClassRef) {
		val querySpecificationClass = pattern.toClass(pattern.querySpecificationClassName) [
  			packageName = querySpecificationPackageName
  			documentation = pattern.javadocQuerySpecificationClass.toString
  			final = true
  			superTypes += pattern.newTypeRef(typeof (BaseGeneratedQuerySpecification), cloneWithProxies(matcherClassRef))
  		]
  		return querySpecificationClass
  	}

  	def initializePublicSpecification(JvmDeclaredType querySpecificationClass, Pattern pattern, JvmTypeReference matcherClassRef, JvmTypeReference matchClassRef, SpecificationBuilder builder) {
  		querySpecificationClass.inferQuerySpecificationMethods(pattern, matcherClassRef, matchClassRef, true, builder)
  		querySpecificationClass.inferQuerySpecificationInnerClasses(pattern, true)
  		querySpecificationClass.inferExpressions(pattern)
  	}

  	def initializePrivateSpecification(JvmDeclaredType querySpecificationClass, Pattern pattern, JvmTypeReference matcherClassRef, JvmTypeReference matchClassRef, SpecificationBuilder builder) {
  		querySpecificationClass.visibility = JvmVisibility::DEFAULT
  		querySpecificationClass.inferQuerySpecificationMethods(pattern, matcherClassRef, matchClassRef, false, builder)
  		querySpecificationClass.inferQuerySpecificationInnerClasses(pattern, false)
  		querySpecificationClass.inferExpressions(pattern)
  	}

	/**
   	 * Infers methods for QuerySpecification class based on the input 'pattern'.
   	 */
  	def inferQuerySpecificationMethods(JvmDeclaredType querySpecificationClass, Pattern pattern, JvmTypeReference matcherClassRef, JvmTypeReference matchClassRef, boolean isPublic, SpecificationBuilder builder) {
  		val context = new EMFPatternMatcherContext(logger)
   		querySpecificationClass.members += querySpecificationClass.toMethod("instance", types.createTypeRef(querySpecificationClass)) [
			visibility = JvmVisibility::PUBLIC
			static = true
			exceptions += pattern.newTypeRef(typeof (IncQueryException))
			documentation = pattern.javadocQuerySpecificationInstanceMethod.toString
			body = [append('''
					return «pattern.querySpecificationHolderClassName».INSTANCE;
			''')]
		]

  		querySpecificationClass.members += querySpecificationClass.toMethod("instantiate", cloneWithProxies(matcherClassRef)) [
			visibility = JvmVisibility::PROTECTED
			annotations += querySpecificationClass.toAnnotation(typeof (Override))
			parameters += querySpecificationClass.toParameter("engine", pattern.newTypeRef(typeof (IncQueryEngine)))
			exceptions += pattern.newTypeRef(typeof (IncQueryException))
			body = if (isPublic) [
				append('''return «pattern.matcherClassName».on(engine);''')
			] else [
				append('''throw new ''')
				referClass(pattern, typeof(UnsupportedOperationException))
				append('''();''')
			]
		]
		querySpecificationClass.members += querySpecificationClass.toMethod("getFullyQualifiedName", pattern.newTypeRef(typeof (String))) [
			visibility = JvmVisibility::PUBLIC
			annotations += querySpecificationClass.toAnnotation(typeof (Override))
			body = [append('''
				return "«CorePatternLanguageHelper::getFullyQualifiedName(pattern)»";
			''')]
		]
		querySpecificationClass.members += querySpecificationClass.toMethod("getParameterNames",
			pattern.newTypeRef(typeof(List), pattern.newTypeRef(typeof(String)))) [
			visibility = JvmVisibility::PUBLIC
			annotations += querySpecificationClass.toAnnotation(typeof(Override))
			body = [
				append('''return ''')
				referClass(pattern, typeof(Arrays))
				append(
					'''.asList(«FOR param : pattern.parameters SEPARATOR ","»"«param.name»"«ENDFOR»);''')
			]
		]
		querySpecificationClass.members += querySpecificationClass.toMethod("getParameters",
			pattern.newTypeRef(typeof(List), pattern.newTypeRef(typeof(PParameter)))) [
			visibility = JvmVisibility::PUBLIC
			annotations += querySpecificationClass.toAnnotation(typeof(Override))
			body = [
				append('''return ''')
				referClass(pattern, typeof(Arrays))
				append(
					'''.asList(«FOR param : pattern.parameters SEPARATOR ","»«param.parameterInstantiation»«ENDFOR»);''')
			]
		]
		querySpecificationClass.members += querySpecificationClass.toMethod("newEmptyMatch",
			if (isPublic) cloneWithProxies(matchClassRef) else pattern.newTypeRef(typeof(IPatternMatch))) 
		[
			visibility = JvmVisibility::PUBLIC
			annotations += querySpecificationClass.toAnnotation(typeof(Override))
			body = [
				if (isPublic) {
					append('''return «pattern.matchClassName».newEmptyMatch();''')
				} else {
					append('''throw new ''')
					referClass(pattern, UnsupportedOperationException)
					append('''();''')
				}
			]
		]
		querySpecificationClass.members += querySpecificationClass.toMethod("newMatch",
			if (isPublic) cloneWithProxies(matchClassRef) else pattern.newTypeRef(typeof(IPatternMatch))) 
		[
			visibility = JvmVisibility::PUBLIC
			annotations += querySpecificationClass.toAnnotation(typeof(Override))
			parameters += querySpecificationClass.toParameter("parameters", pattern.newTypeRef(Object).addArrayTypeDimension)
			varArgs = true
			body = [
				if (isPublic) {
					append('''return «pattern.matchClassName».newMatch(«FOR p : pattern.parameters SEPARATOR ', '»(«p.calculateType.qualifiedName») parameters[«pattern.parameters.indexOf(p)»]«ENDFOR»);''')
				} else {
					append('''throw new ''')
					referClass(pattern, UnsupportedOperationException)
					append('''();''')
				}
			]
		]
		querySpecificationClass.members += querySpecificationClass.toMethod("doGetContainedBodies",
			pattern.newTypeRef(typeof(Set), pattern.newTypeRef(typeof(PBody)))) [
			visibility = JvmVisibility::PUBLIC
			annotations += querySpecificationClass.toAnnotation(typeof(Override))
			exceptions += pattern.newTypeRef(typeof(IncQueryException))
			body = [ appender |
				var IQuerySpecification<?> genericSpecification
				try {
					genericSpecification = builder.getOrCreateSpecification(pattern, true)
					if (genericSpecification == null || genericSpecification.status == PQueryStatus::ERROR) {
						feedback.reportError(pattern, "Error building generic query specification",
							EMFPatternLanguageJvmModelInferrer::SPECIFICATION_BUILDER_CODE, Severity::ERROR,
							IErrorFeedback::JVMINFERENCE_ERROR_TYPE)
					}					
				} catch (Exception e) {
					//If called with an inconsistent pattern, then no body will be built
					appender.append('''addError(new ''')
					appender.referClass(pattern, PProblem)
					appender.append('''("Inconsistent pattern definition threw exception «e.class.simpleName»  with message: «e.getMessage.escapeToQuotedString»"));''')
					//appender.append('''Inconsistent pattern definition thrown exception «e.class.simpleName»  with message: «e.getMessage»''')
					// TODO smarter error reporting required
					logger.warn("Error while building PBodies", e)
					return
				}
				if (genericSpecification != null) {
					appender.inferBodies(pattern, genericSpecification, context)
					appender.inferAnnotations(pattern, genericSpecification)
					genericSpecification.PProblems.forEach[
						appender.append('''addError(new ''')
						appender.referClass(pattern, PProblem)
						appender.append('''("«shortMessage.escapeToQuotedString»"));''')
					]
				} else {
					//appender.append('''Cannot initialize PSystem from the pattern definition''')
					appender.append('''addError(new ''')
					appender.referClass(pattern, PProblem)
					appender.append('''("Could not initialize query specification from the pattern definition"));''')
				}
				appender.append('''return bodies;''')
			]
			]
  	}

	def inferBodies(ITreeAppendable appender, Pattern pattern, IQuerySpecification<?> genericSpecification, EMFPatternMatcherContext context) {
		appender.referClass(pattern, typeof(Set), pattern.newTypeRef(typeof(PBody)))
		appender.append(''' bodies = ''')
		appender.referClass(pattern, typeof(Sets))
		appender.append('''.newLinkedHashSet();''')
		appender.newLine
		for (pBody : genericSpecification.disjunctBodies.bodies) {
			appender.increaseIndentation
			appender.append("{")
			appender.newLine
			appender.append('''PBody body = new PBody(this);''')
			appender.newLine
			for (variable : pBody.uniqueVariables) {
				appender.referClass(pattern, typeof(PVariable))
				appender.append(''' «variable.escapedName» = body.getOrCreateVariableByName("«variable.name»");''')
				appender.newLine
			}
			appender.append('''body.setExportedParameters(''')
			appender.referClass(pattern,typeof(Arrays))
			appender.append('''.<''')
			appender.referClass(pattern, typeof(ExportedParameter))
			appender.append('''>asList(''')
			appender.increaseIndentation
			appender.newLine
			val exportIt = pBody.symbolicParameters.iterator
			while (exportIt.hasNext) {
				exportIt.next.inferExportedParameterConstraint(pBody, pattern, appender)
				if (exportIt.hasNext) {
					appender.append(''', ''')
					appender.newLine
				}
			}
			appender.decreaseIndentation
			appender.newLine			
			appender.append('''));''')
			appender.newLine
			pBody.constraints.forEach[inferConstraint(pBody, pattern,  appender)]
			appender.append('''bodies.add(body);''')
			appender.decreaseIndentation
			appender.newLine
			appender.append("}")
			appender.newLine
		}
	}

 	/**
   	 * Infers inner class for QuerySpecification class based on the input 'pattern'.
   	 */
  	def inferQuerySpecificationInnerClasses(JvmDeclaredType querySpecificationClass, Pattern pattern, boolean isPublic) {
   		querySpecificationClass.members += querySpecificationClass.toClass(pattern.querySpecificationHolderClassName) [
			visibility = JvmVisibility::PRIVATE
			static = true
			members += querySpecificationClass.toField("INSTANCE", types.createTypeRef(querySpecificationClass)/*pattern.newTypeRef("volatile " + querySpecificationClass.simpleName)*/) [
				final = true
				static = true
				initializer = [append('''make()''')];
			]
			it.members += querySpecificationClass.toMethod("make", types.createTypeRef(querySpecificationClass)) [
				visibility = JvmVisibility::PUBLIC
				static = true
				body = [append('''
					return new «pattern.querySpecificationClassName»();					
				''')]
			]
		]
 	}

	def escapedName(PVariable variable) {
		if (variable == null)
			"var_"
		else
		"var_" + variable.name.replaceAll("[\\.\\{\\}<>]","_")
	}

	def inferExportedParameterConstraint(ExportedParameter constraint, PBody body, Pattern pattern, ITreeAppendable appender) {
		appender.append('''new ''')
		appender.referClass(pattern, typeof(ExportedParameter))
		appender.append('''(body, «constraint.parameterVariable.escapedName», "«constraint.parameterName»")''')
	}
	
	def inferConstraint(PConstraint constraint, PBody body, Pattern pattern, ITreeAppendable appender) {
		switch constraint {
			ExportedParameter : {
				// Exported parameters are already handled in inferExportedParameterConstraints - ignore here
			}
			Equality : {
				appender.append('''new ''')
				appender.referClass(pattern, typeof(Equality))
				appender.append('''(body, «constraint.who.escapedName», «constraint.withWhom.escapedName»);''')
			}
			Inequality: {
				appender.append('''new ''')
				appender.referClass(pattern, typeof(Inequality))
				appender.append('''''')
				appender.append('''(body, «constraint.who.escapedName», «constraint.withWhom.escapedName»);''')
			}
			TypeUnary: {
				appender.append('''new ''')
				appender.referClass(pattern, typeof(TypeUnary))
				val literal = constraint.supplierKey as EClassifier
				val packageNsUri = literal.EPackage.nsURI
				appender.append('''(body, «constraint.variablesTuple.output», getClassifierLiteral("«packageNsUri»", "«literal.name»"), "«constraint.typeString»");''')
			}
			TypeBinary: {
				appender.append('''new ''')
				appender.referClass(pattern, typeof(TypeBinary))
				val literal = constraint.supplierKey as EStructuralFeature
				val container = literal.EContainingClass
				val packageNsUri = container.EPackage.nsURI
				appender.append('''(body, CONTEXT, «constraint.variablesTuple.output», getFeatureLiteral("«packageNsUri»", "«container.name»", "«literal.name»"), "«constraint.typeString»");''')
			}
			ConstantValue : {
				appender.append('''new ''')
				appender.referClass(pattern, typeof(ConstantValue))
				appender.append('''(body, «constraint.variablesTuple.output», «constraint.supplierKey.outputConstant»);''')
			}
			PositivePatternCall : {
				appender.append('''new ''')
				appender.referClass(pattern, typeof(PositivePatternCall))
				appender.append('''(body, new ''')
				appender.referClass(pattern, FlatTuple)
				appender.append('''(«constraint.variablesTuple.output»), ''')
				appender.referSpecification(constraint.referredQuery, pattern)
				appender.append(''');''')
			}
			NegativePatternCall : {
				appender.append('''new ''')
				appender.referClass(pattern, typeof(NegativePatternCall))
				appender.append('''(body, new ''')
				appender.referClass(pattern, FlatTuple)
				appender.append('''(«constraint.actualParametersTuple.output»), ''')
				appender.referSpecification(constraint.referredQuery, pattern)
				appender.append('''.instance());''')
			}
			BinaryTransitiveClosure: {
				appender.append('''new ''')
				appender.referClass(pattern, typeof(BinaryTransitiveClosure))
				appender.append('''(body, new ''')
				appender.referClass(pattern, FlatTuple)
				appender.append('''(«constraint.variablesTuple.output»), ''')
				appender.referSpecification(constraint.supplierKey, pattern)
				appender.append('''.instance());''')
			}
			PatternMatchCounter: {
				appender.append('''new ''')
				appender.referClass(pattern, typeof(PatternMatchCounter))
				appender.append('''(body, new ''')
				appender.referClass(pattern, FlatTuple)
				appender.append('''(«constraint.actualParametersTuple.output»), ''')
				appender.referSpecification(constraint.referredQuery, pattern)
				appender.append('''.instance(), «constraint.resultVariable.escapedName»);''')
			}
			ExpressionEvaluation : {
				val evaluator = constraint.evaluator as XBaseEvaluator
				if (evaluator.inputParameterNames.empty) {
					feedback.reportError(evaluator.expression, "No parameters defined", EMFPatternLanguageJvmModelInferrer::SPECIFICATION_BUILDER_CODE, Severity::WARNING, IErrorFeedback::JVMINFERENCE_ERROR_TYPE)
				}
				appender.append('''new ''')
				appender.referClass(pattern, typeof(ExpressionEvaluation))
				appender.append('''(body, new ''')
				appender.referClass(pattern, typeof(IExpressionEvaluator))
				appender.append('''() {''')
				appender.increaseIndentation
				appender.append('''

					@Override
					public String getShortDescription() {
						return "Expression evaluation from pattern «pattern.name»";
					}

					@Override
					public Iterable<String> getInputParameterNames() {
						return ''')
				appender.referClass(pattern, typeof(Arrays))
				appender.append('''.asList(«FOR name : evaluator.inputParameterNames SEPARATOR ", "»"«name»"«ENDFOR»);''')
				appender.append('''

						}

						@Override
						public Object evaluateExpression(''')
				appender.referClass(pattern, typeof(IValueProvider))
				appender.append(" ")
				appender.append('''
						provider) throws Exception {
							«val variables = variables(evaluator.expression)»
							«FOR variable : variables»
								«variable.calculateType.qualifiedName» «variable.name» = («variable.calculateType.qualifiedName») provider.getValue("«variable.name»");
							«ENDFOR»
							return «expressionMethodName(evaluator.expression)»(«FOR variable : variables SEPARATOR ', '»«variable.name»«ENDFOR»);
						}

						''')
				appender.decreaseIndentation
				appender.append('''}, «IF constraint.outputVariable != null » «constraint.outputVariable.escapedName» «ELSE» null«ENDIF»); ''')
			}
			default:
				appender.append('''
					//TODO error in code generation
					Unsupported constraint «constraint.class.simpleName»''')
		}

		appender.newLine
	}

	def output(Tuple tuple) {
		Joiner.on(", ").join(tuple.elements.map[(it as PVariable).escapedName])
	}

	def outputConstant(Object constant) {
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
				constant.toString
		}
	}

	def referSpecification(ITreeAppendable appender, PQuery referredQuery, Pattern callerPattern) {
		if ((referredQuery as GenericQuerySpecification).getPattern == callerPattern) {
			appender.append('''this''')
		} else {
			appender.referClass(callerPattern, referredQuery.findGeneratedSpecification)
			appender.append('''.instance()''')
		}
	}

	def findGeneratedSpecification(PQuery query) {
		(query as GenericQuerySpecification).pattern.findInferredSpecification
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
				for (variable : variables(ex)){
					val parameter = variable.toParameter(variable.name, variable.calculateType)
					it.parameters += parameter
				}
				it.body = ex
		]
    	]
    }

    def parameterInstantiation(Variable variable) {
		val ref = getTypeForIdentifiable(variable);
        // bug 411866: JvmUnknownTypeReference.getType() returns null in Xtext 2.4
        val clazz = if (ref == null || ref instanceof JvmUnknownTypeReference) {
        	""
        } else {
			ref.getType().getQualifiedName()
		}
		'''new PParameter("«variable.name»", "«clazz»")'''
    }

    def inferAnnotations(ITreeAppendable appender, Pattern pattern, IQuerySpecification<?> genericSpecification) {
    	genericSpecification.allAnnotations.forEach[
    		appender.append('''{''')
    		appender.increaseIndentation
    		appender.newLine
    		appender.append('''PAnnotation annotation = new ''')
    		appender.referClass(pattern, typeof(PAnnotation))
    		appender.append('''("«it.name»");''')
    		appender.newLine
    		allValues.forEach[
    			appender.append('''annotation.addAttribute("«key»",''')
    			appender.outputAnnotationParameter(pattern, value)
    			appender.append(''');''')
    			appender.newLine
    		]
    		appender.append('''addAnnotation(annotation);''')
    		appender.decreaseIndentation
    		appender.newLine
    		appender.append('''}''')
    		appender.newLine
    	]
    }

    def void outputAnnotationParameter(ITreeAppendable appender, Pattern ctx, Object value) {
		switch value {
			List<?> : {
				appender.referClass(ctx, typeof(Arrays))
				appender.append('''.asList(new Object[] {''')
				val iterator = value.iterator
				while (iterator.hasNext) {
					appender.outputAnnotationParameter(ctx, iterator.next)
					if (iterator.hasNext) {
						appender.append(''', ''')
					}					
				}
				appender.append('''})''')
			} ParameterReference : {
				appender.append('''new ''')
				appender.referClass(ctx, typeof(ParameterReference))
				appender.append('''("«value.name»")''')
			} String : {
				appender.append('''"«value»"''')
			} default : {
				appender.append(value.toString)
			}
    	}
    }
}