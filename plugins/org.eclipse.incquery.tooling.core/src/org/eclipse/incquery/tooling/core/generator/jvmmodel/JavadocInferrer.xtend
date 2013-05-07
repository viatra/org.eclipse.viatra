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
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.incquery.tooling.core.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable

class JavadocInferrer {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension IQualifiedNameProvider
	
	/**
   	 * Infers javadoc for Match class based on the input 'pattern'.
   	 */
   	def javadocMatchClass(Pattern pattern) '''
		Pattern-specific match representation of the «pattern.fullyQualifiedName» pattern, 
		to be used in conjunction with {@link «pattern.matcherClassName»}.
		
		<p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
		Each instance is a (possibly partial) substitution of pattern parameters, 
		usable to represent a match of the pattern in the result of a query, 
		or to specify the bound (fixed) input parameters when issuing a query.
		
		@see «pattern.matcherClassName»
		@see «pattern.processorClassName»
   	'''
	
	def javadocMatcherClass(Pattern pattern) '''		
		Generated pattern matcher API of the «pattern.fullyQualifiedName» pattern, 
		providing pattern-specific query methods.
		
		Use the pattern matcher on a given model via {@link #on(IncQueryEngine)}, 
		e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
		
		<p>Matches of the pattern will be represented as {@link «pattern.matchClassName»}.

		<p>Original source:
		<code><pre>
		«pattern.serializeToJavadoc»
		</pre></code>
		
		@see «pattern.matchClassName»
		@see «pattern.processorClassName»
		@see «pattern.querySpecificationClassName»
   	'''
   	
   	def javadocQuerySpecificationClass(Pattern pattern) '''
	 	A pattern-specific query specification that can instantiate «pattern.matcherClassName» in a type-safe way.
	 	
	 	@see «pattern.matcherClassName»
	 	@see «pattern.matchClassName»
   	'''
   	
   	def javadocProcessorClass(Pattern pattern) '''
		A match processor tailored for the «pattern.fullyQualifiedName» pattern.
		
		Clients should derive an (anonymous) class that implements the abstract process().
	'''
	
	def javadocEvaluatorClass(Pattern pattern) '''
		A xbase xexpression evaluator tailored for the «pattern.fullyQualifiedName» pattern.'''
		
	def javadocEvaluatorClassGeneratedMethod(Pattern pattern) '''
		The raw java code generated from the xbase xexpression by xtext.'''
		
	def javadocEvaluatorClassWrapperMethod(Pattern pattern) '''
		A wrapper method for calling the generated java method with the correct attributes.'''
   	
   	def javadocMatcherConstructorNotifier(Pattern pattern) '''
		Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). 
		If a pattern matcher is already constructed with the same root, only a light-weight reference is returned.
		The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
		The match set will be incrementally refreshed upon updates from this scope.
		<p>The matcher will be created within the managed {@link IncQueryEngine} belonging to the EMF model root, so 
		multiple matchers will reuse the same engine and benefit from increased performance and reduced memory footprint.
		@param emfRoot the root of the EMF containment hierarchy where the pattern matcher will operate. Recommended: Resource or ResourceSet.
		@throws IncQueryException if an error occurs during pattern matcher creation
		@deprecated use {@link #on(IncQueryEngine)} instead, e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}
	'''
	
	def javadocMatcherConstructorEngine(Pattern pattern) '''
		Initializes the pattern matcher within an existing EMF-IncQuery engine. 
		If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
		The match set will be incrementally refreshed upon updates.
		@param engine the existing EMF-IncQuery engine in which this matcher will be created.
		@throws IncQueryException if an error occurs during pattern matcher creation
		@deprecated use {@link #on(IncQueryEngine)} instead
	'''
	
	def javadocMatcherStaticOnEngine(Pattern pattern) '''
		Initializes the pattern matcher within an existing EMF-IncQuery engine. 
		If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
		The match set will be incrementally refreshed upon updates.
		@param engine the existing EMF-IncQuery engine in which this matcher will be created.
		@throws IncQueryException if an error occurs during pattern matcher creation
	'''

	def javadocGetAllMatchesMethod(Pattern pattern) '''
		Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return matches represented as a «pattern.matchClassName» object.
	'''
	
	def javadocGetOneArbitraryMatchMethod(Pattern pattern) '''
		Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
		Neither determinism nor randomness of selection is guaranteed.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return a match represented as a «pattern.matchClassName» object, or null if no match is found.
	'''
	
	def javadocHasMatchMethod(Pattern pattern) '''
		Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
		under any possible substitution of the unspecified parameters (if any).
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return true if the input is a valid (partial) match of the pattern.
	'''
	
	def javadocHasMatchMethodNoParameter(Pattern pattern) '''
		Indicates whether the (parameterless) pattern matches or not. 
		@return true if the pattern has a valid match.
	'''
	
	def javadocCountMatchesMethod(Pattern pattern) '''
		Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return the number of pattern matches found.
	'''
	
	def javadocForEachMatchMethod(Pattern pattern) '''
		Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@param processor the action that will process each pattern match.
	'''
	
	def javadocForOneArbitraryMatchMethod(Pattern pattern) '''
		Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  
		Neither determinism nor randomness of selection is guaranteed.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@param processor the action that will process the selected match. 
		@return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
	'''
	
	def javadocProcessMethod(Pattern pattern) '''
		Defines the action that is to be executed on each match.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the value of pattern parameter «p.name» in the currently processed match 
		«ENDFOR»
	'''
	
	def javadocNewFilteredDeltaMonitorMethod(Pattern pattern) '''
		Registers a new filtered delta monitor on this pattern matcher.
		The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters. 
		It can also be reset to track changes from a later point in time, 
		and changes can even be acknowledged on an individual basis. 
		See {@link DeltaMonitor} for details.
		@param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return the delta monitor.
	'''
	
	def javadocNewMatchMethod(Pattern pattern) '''
		Returns a new (partial) Match object for the matcher. 
		This can be used e.g. to call the matcher with a partial match. 
		<p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
		«FOR p : pattern.parameters»
		@param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
		«ENDFOR»
		@return the (partial) match object.
	'''
	
	def javadocGetAllValuesOfMethod(Variable parameter) '''
		Retrieve the set of values that occur in matches for «parameter.name».
		@return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
	'''
	
	def javadocQuerySpecificationMethod(Pattern pattern) '''
		@return the singleton instance of the query specification of this pattern
		@throws IncQueryException if the pattern definition could not be loaded
	'''
	
	def javadocQuerySpecificationInstanceMethod(Pattern pattern) '''
		@return the singleton instance of the query specification
		@throws IncQueryException if the pattern definition could not be loaded
	'''
}
