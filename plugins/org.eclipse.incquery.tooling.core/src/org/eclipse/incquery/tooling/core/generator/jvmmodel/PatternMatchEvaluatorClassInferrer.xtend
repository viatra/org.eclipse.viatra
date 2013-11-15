/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.core.generator.jvmmodel

import com.google.inject.Inject
import java.util.ArrayList
import java.util.List
import java.util.Map
import org.eclipse.incquery.tooling.core.generator.util.EMFJvmTypesBuilder
import org.eclipse.incquery.tooling.core.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.runtime.rete.tuple.Tuple
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.incquery.patternlanguage.patternLanguage.CheckConstraint
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.util.Primitives
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.typing.ITypeProvider
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.incquery.runtime.extensibility.IMatchChecker
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody
import org.eclipse.xtext.xbase.XFeatureCall
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable
import org.eclipse.emf.ecore.EObject
import org.eclipse.incquery.runtime.rete.construction.psystem.IValueProvider
import com.google.common.collect.ImmutableList

/**
 * {@link IMatchChecker} implementation inferer.
 */
class PatternMatchEvaluatorClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer
	@Inject extension ITypeProvider
	@Inject extension Primitives

	/**
	 * Infers the {@link IMatchChecker} implementation class from a {@link Pattern}.
	 */
	def List<JvmDeclaredType> inferEvaluatorClass(Pattern pattern, String checkerPackageName) {
		val List<JvmDeclaredType> result = new ArrayList<JvmDeclaredType>()
		var int patternBodyNumber = 0
		for (patternBody: pattern.bodies) {
			patternBodyNumber = patternBodyNumber + 1
			var int expressionNumber = 0
  			for (xExpression: CorePatternLanguageHelper::getAllTopLevelXBaseExpressions(patternBody)) {
				expressionNumber = expressionNumber + 1
				val String postFix = patternBodyNumber + "_" + expressionNumber
				val checkerClass = pattern.toClass(pattern.evaluatorClassName + postFix ) [
					it.packageName = checkerPackageName
					it.documentation = pattern.javadocEvaluatorClass.toString
					it.superTypes += pattern.newTypeRef(typeof(IMatchChecker))
				]
				
				//Forcing a boolean return type for check expression
				//Results in less misleading error messages
				val returnType = 
					if (xExpression.eContainer instanceof CheckConstraint)
						pattern.newTypeRef(typeof(Boolean))
					else
						pattern.newTypeRef(typeof(Object))
						
				checkerClass.inferEvaluatorClassMethods(pattern, patternBody, xExpression, returnType)
				result.add(checkerClass)
  			}
  		}
  		return result
  	}
  	
	/**
   	 * Infers methods for checker class based on the input 'pattern'.
   	 */  	
  	def inferEvaluatorClassMethods(JvmDeclaredType checkerClass, Pattern pattern, PatternBody body, XExpression xExpression) {
  		val type = getType(xExpression)
  		if (xExpression != null) {
  			inferEvaluatorClassMethods(checkerClass, pattern, body, xExpression, type)
  		}
  	}
  	
	/**
   	 * Infers methods for checker class based on the input 'pattern'.
   	 */  	
  	def inferEvaluatorClassMethods(JvmDeclaredType checkerClass, Pattern pattern, PatternBody body, XExpression xExpression, JvmTypeReference type) {
  		val List<Variable> variables = if (xExpression == null) {
				emptyList
			} else {
				val valNames = (xExpression.eAllContents + newImmutableList(xExpression).iterator).
				    filter(typeof(XFeatureCall)).map[concreteSyntaxFeatureName].
					toList
				body.variables.filter[valNames.contains(it.name)].sortBy[name]
			}
		checkerClass.members += xExpression.toField("parameterNames", xExpression.newTypeRef(typeof(ImmutableList), xExpression.newTypeRef(typeof(String)))) [
			it.setInitializer[append('''
				ImmutableList.of(
				«FOR variable : variables SEPARATOR ', '»
					"«variable.name»"	
				«ENDFOR»
				);
			''')]
		]
  		checkerClass.members += xExpression.toMethod("evaluateGeneratedExpression", asWrapperTypeIfPrimitive(type)) [
  			it.visibility = JvmVisibility::PRIVATE
			for (variable : variables){
				val parameter = variable.toParameter(variable.name, variable.calculateType)
				it.parameters += parameter
			}
			it.documentation = pattern.javadocEvaluatorClassGeneratedMethod.toString
			it.setBody(xExpression)
		]

		checkerClass.members += xExpression.toMethod("evaluateXExpression", asWrapperTypeIfPrimitive(type)) [
			it.annotations += pattern.toAnnotation(typeof(Override))
			it.annotations += pattern.toAnnotation(typeof(Deprecated))
			it.parameters += pattern.toParameter("tuple", pattern.newTypeRef(typeof (Tuple)))
			it.parameters += pattern.toParameter("tupleNameMap", pattern.newTypeRef(typeof (Map), pattern.newTypeRef(typeof (String)), pattern.newTypeRef(typeof (Integer))))
			it.documentation = pattern.javadocEvaluatorClassWrapperMethod.toString
			it.setBody([append('''
				«FOR variable : variables»int «variable.name»Position = tupleNameMap.get("«variable.name»");
				«variable.calculateType.qualifiedName» «variable.name» = («variable.calculateType.qualifiedName») tuple.get(«variable.name»Position);
				«ENDFOR»
				return evaluateGeneratedExpression(«FOR variable : variables SEPARATOR ', '»«variable.name»«ENDFOR»);''')
	   		])
		]
		
		checkerClass.members += xExpression.toMethod("evaluateExpression", asWrapperTypeIfPrimitive(type)) [
			it.annotations += pattern.toAnnotation(typeof(Override))
			it.parameters += pattern.toParameter("provider", pattern.newTypeRef(typeof (IValueProvider)))
			it.documentation = pattern.javadocEvaluatorClassWrapperMethod.toString
			it.setBody([append('''
				«FOR variable : variables»
				«variable.calculateType.qualifiedName» «variable.name» = («variable.calculateType.qualifiedName») provider.getValue("«variable.name»");
				«ENDFOR»
				return evaluateGeneratedExpression(«FOR variable : variables SEPARATOR ', '»«variable.name»«ENDFOR»);
			''')
	   		])
		]
		checkerClass.members += xExpression.toMethod("getInputParameterNames", pattern.newTypeRef(typeof(Iterable), pattern.newTypeRef(typeof(String)))) [
			it.annotations += pattern.toAnnotation(typeof(Override))
			it.documentation = pattern.javadocEvaluatorClassWrapperMethod.toString
			it.setBody([append('''
				return parameterNames;
			''')
	   		])
		]
		checkerClass.members += xExpression.toMethod("getShortDescription", pattern.newTypeRef(typeof(String))) [
			it.annotations += pattern.toAnnotation(typeof(Override))
			it.documentation = pattern.javadocEvaluatorClassWrapperMethod.toString
			
			val identifier = checkerClass.identifier
			
			it.setBody([append('''
				return "XExpression «identifier.substring(identifier.length-3, identifier.length)» from Pattern «pattern.name»";
			''')
			])
		]
  	}
	
}