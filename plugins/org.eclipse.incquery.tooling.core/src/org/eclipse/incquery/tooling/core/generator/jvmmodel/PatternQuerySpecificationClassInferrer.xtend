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

package org.eclipse.incquery.tooling.core.generator.jvmmodel

import com.google.inject.Inject
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.exception.IncQueryException
import org.eclipse.incquery.tooling.core.generator.util.EMFJvmTypesBuilder
import org.eclipse.incquery.tooling.core.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification

/**
 * {@link IQuerySpecification} implementation inferrer.
 * 
 * @author Mark Czotter
 */
class PatternQuerySpecificationClassInferrer {
	
	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer
	@Inject extension TypeReferences types

	/**
	 * Infers the {@link IQuerySpecification} implementation class from {@link Pattern}.
	 */		
	def JvmDeclaredType inferQuerySpecificationClass(Pattern pattern, boolean isPrelinkingPhase, String querySpecificationPackageName, JvmTypeReference matchClassRef, JvmTypeReference matcherClassRef) {
		val querySpecificationClass = pattern.toClass(pattern.querySpecificationClassName) [
  			it.packageName = querySpecificationPackageName
  			it.documentation = pattern.javadocQuerySpecificationClass.toString
  			it.final = true
  			it.superTypes += pattern.newTypeRef(typeof (BaseGeneratedQuerySpecification), cloneWithProxies(matcherClassRef))
  		]
  		querySpecificationClass.inferQuerySpecificationMethods(pattern, matcherClassRef)
  		querySpecificationClass.inferQuerySpecificationConstructor(pattern)
  		querySpecificationClass.inferQuerySpecificationField(pattern)
  		querySpecificationClass.inferQuerySpecificationInnerClasses(pattern)
  		return querySpecificationClass
  	}
  	
	/**
   	 * Infers methods for QuerySpecification class based on the input 'pattern'.
   	 */
  	def inferQuerySpecificationMethods(JvmDeclaredType querySpecificationClass, Pattern pattern, JvmTypeReference matcherClassRef) {
   		querySpecificationClass.members += pattern.toMethod("instance", types.createTypeRef(querySpecificationClass)) [
			it.visibility = JvmVisibility::PUBLIC
			it.setStatic(true)
			it.exceptions += pattern.newTypeRef(typeof (IncQueryException))
			it.documentation = pattern.javadocQuerySpecificationInstanceMethod.toString
			it.setBody([append('''
				try {
					return «pattern.querySpecificationHolderClassName».INSTANCE;
				} catch (''') referClass(pattern, typeof(ExceptionInInitializerError)) append(" ") append(''' 
				err) {
					processInitializerError(err);
					throw err;
				}
			''')])
		]

  		querySpecificationClass.members += pattern.toMethod("instantiate", cloneWithProxies(matcherClassRef)) [
			it.visibility = JvmVisibility::PROTECTED
			it.annotations += pattern.toAnnotation(typeof (Override))
			it.parameters += pattern.toParameter("engine", pattern.newTypeRef(typeof (IncQueryEngine)))
			it.exceptions += pattern.newTypeRef(typeof (IncQueryException))
			it.setBody([append('''
				return «pattern.matcherClassName».on(engine);
			''')])
		]
//		querySpecificationClass.members += pattern.toMethod("patternString", pattern.newTypeRef(typeof (String))) [
//			it.visibility = JvmVisibility::PROTECTED
//			it.annotations += pattern.toAnnotation(typeof (Override))
//			it.body = ['''
//«««				Serialize the PatternModel
//«««				«pattern.eContainer.serializeToJava»
//«««				return patternString;  
//				throw new UnsupportedOperationException();
//			''']
//		]
		querySpecificationClass.members += pattern.toMethod("getBundleName", pattern.newTypeRef(typeof (String))) [
			it.visibility = JvmVisibility::PROTECTED
			it.annotations += pattern.toAnnotation(typeof (Override))
			it.setBody([append('''
				return "«pattern.bundleName»";
			''')])
		]
		querySpecificationClass.members += pattern.toMethod("patternName", pattern.newTypeRef(typeof (String))) [
			it.visibility = JvmVisibility::PROTECTED
			it.annotations += pattern.toAnnotation(typeof (Override))
			it.setBody([append('''
				return "«CorePatternLanguageHelper::getFullyQualifiedName(pattern)»";
			''')])
		]
  	}
  	
 	/**
   	 * Infers constructor for QuerySpecification class based on the input 'pattern'.
   	 */
  	def inferQuerySpecificationConstructor(JvmDeclaredType querySpecificationClass, Pattern pattern) {
  		querySpecificationClass.members += pattern.toConstructor [
  			it.simpleName = querySpecificationClass.simpleName
			it.visibility = JvmVisibility::PRIVATE
			it.exceptions += pattern.newTypeRef(typeof (IncQueryException))
			it.setBody([append('''super();''')])
		]
  	}
  	
 	/**
   	 * Infers field for QuerySpecification class based on the input 'pattern'.
   	 */
  	def inferQuerySpecificationField(JvmDeclaredType querySpecificationClass, Pattern pattern) {
  		// TODO volatile?
  		//querySpecificationClass.members += pattern.toField("INSTANCE", types.createTypeRef(querySpecificationClass)/*pattern.newTypeRef("volatile " + querySpecificationClass.simpleName)*/) [
		//	it.visibility = JvmVisibility::PRIVATE
		//	it.setStatic(true)
		//	it.setInitializer([append('''null''')]);
		//]
  	}
  	
 	/**
   	 * Infers inner class for QuerySpecification class based on the input 'pattern'.
   	 */
  	def inferQuerySpecificationInnerClasses(JvmDeclaredType querySpecificationClass, Pattern pattern) {
  		querySpecificationClass.members += pattern.toClass(pattern.querySpecificationProviderClassName) [
			it.visibility = JvmVisibility::PUBLIC
			it.setStatic(true)
			it.superTypes += pattern.newTypeRef(typeof(IQuerySpecificationProvider), types.createTypeRef(querySpecificationClass))
			
			it.members += pattern.toMethod("get", types.createTypeRef(querySpecificationClass)) [
				it.visibility = JvmVisibility::PUBLIC
				it.annotations += pattern.toAnnotation(typeof (Override))
				it.exceptions += pattern.newTypeRef(typeof (IncQueryException))
				it.setBody([append('''return instance();''')])			
			]		
		]
   		querySpecificationClass.members += pattern.toClass(pattern.querySpecificationHolderClassName) [
			it.visibility = JvmVisibility::PRIVATE
			it.setStatic(true)
			it.members += pattern.toField("INSTANCE", types.createTypeRef(querySpecificationClass)/*pattern.newTypeRef("volatile " + querySpecificationClass.simpleName)*/) [
				it.setFinal(true)
				it.setStatic(true)
				it.setInitializer([append('''make()''')]);
			]
			it.members += pattern.toMethod("make", types.createTypeRef(querySpecificationClass)) [
				it.visibility = JvmVisibility::PUBLIC
				it.setStatic(true)
				it.setBody([append('''
					try {
						return new «pattern.querySpecificationClassName»();
					} catch (''') referClass(pattern, typeof(IncQueryException)) append(" ") append(''' 
					ex) {
						throw new ''') referClass(pattern, typeof(RuntimeException)) append('''
						(ex);
					}
				''')])			
			]		
		]
 	}
	
}