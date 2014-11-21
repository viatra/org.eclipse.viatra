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

import com.google.inject.Inject
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.incquery.patternlanguage.emf.util.EMFJvmTypesBuilder
import org.eclipse.incquery.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import org.eclipse.incquery.runtime.exception.IncQueryException
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmGenericType
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.TypeReferences
import org.apache.log4j.Logger
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil

/**
 * {@link IncQueryMatcher} implementation inferrer.
 *
 * @author Mark Czotter
 */
class PatternMatcherClassInferrer {

	@Inject extension EMFJvmTypesBuilder
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension JavadocInferrer
	@Inject extension TypeReferences types


   	/**
   	 * Infers fields for Matcher class based on the input 'pattern'.
   	 */
   	def inferFields(JvmDeclaredType matcherClass, Pattern pattern) {
   		for (Variable variable : pattern.parameters) {
   			matcherClass.members += matcherClass.toField(variable.positionConstant, pattern.newTypeRef(typeof (int)))[
	 			static = true
	 			final = true
   				initializer = [append('''«pattern.parameters.indexOf(variable)»''')]
   			]
   		}
   		matcherClass.members += matcherClass.toField("LOGGER", pattern.newTypeRef(typeof(Logger))) [
   			static = true
   			final = true
   			initializer = [
   				referClass(pattern, IncQueryLoggingUtil)
   				append('''.getLogger(«pattern.matcherClassName».class)''')
   			]
   		]
   	}

   	/**
   	 * Infers static methods for Matcher class based on the input 'pattern'.
   	 * NOTE: queryDefinition() will be inferred later, in EMFPatternLanguageJvmModelInferrer
   	 */
   	def inferStaticMethods(JvmGenericType type, Pattern pattern, JvmGenericType matcherClass) {
   		matcherClass.members += matcherClass.toMethod("on", types.createTypeRef(matcherClass)) [
   			static = true
			visibility = JvmVisibility::PUBLIC
			documentation = pattern.javadocMatcherStaticOnEngine.toString
			parameters += type.toParameter("engine", pattern.newTypeRef(typeof (IncQueryEngine)))
			exceptions += pattern.newTypeRef(typeof (IncQueryException))
			body = [append('''
				// check if matcher already exists
				«matcherClass.simpleName» matcher = engine.getExistingMatcher(querySpecification());
				if (matcher == null) {
					matcher = new «matcherClass.simpleName»(engine);
					// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
				}
				return matcher;''')
		    ]
   		]
   	}



	/**
   	 * Infers constructors for Matcher class based on the input 'pattern'.
   	 */
   	def inferConstructors(JvmDeclaredType matcherClass, Pattern pattern) {
   		matcherClass.members += matcherClass.toConstructor [
   			simpleName = pattern.matcherClassName
			addAnnotation(typeof (Deprecated))
			visibility = JvmVisibility::PUBLIC
			documentation = pattern.javadocMatcherConstructorNotifier.toString
			parameters += matcherClass.toParameter("emfRoot", pattern.newTypeRef(typeof (Notifier)))
			exceptions += pattern.newTypeRef(typeof (IncQueryException))
			body = [
				append('''this(''')
				referClass(pattern, typeof(IncQueryEngine))
				append('''.on(emfRoot));''')
			]
		]

		matcherClass.members += matcherClass.toConstructor [
			simpleName = pattern.matcherClassName
			addAnnotation(typeof (Deprecated))
			visibility = JvmVisibility::PUBLIC
			documentation = pattern.javadocMatcherConstructorEngine.toString
			parameters += matcherClass.toParameter("engine", pattern.newTypeRef(typeof (IncQueryEngine)))
			exceptions += pattern.newTypeRef(typeof (IncQueryException))
			body = [append('''super(engine, querySpecification());''')]
		]
   	}

}