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
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.viatra.query.runtime.api.IMatchProcessor

/**
 * @noreference
 */
class JavadocInferrer {

    @Inject extension EMFPatternLanguageJvmModelInferrerUtil
    @Inject extension IQualifiedNameProvider

    /**
     * Infers javadoc for Match class based on the input 'pattern'.
     */
    def javadocMatchClass(Pattern pattern) '''
        Pattern-specific match representation of the «pattern.fullyQualifiedName» pattern,
        to be used in conjunction with {@link «pattern.findMatcherClass?.simpleName»}.
        
        <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
        Each instance is a (possibly partial) substitution of pattern parameters,
        usable to represent a match of the pattern in the result of a query,
        or to specify the bound (fixed) input parameters when issuing a query.
        
        @see «pattern.findMatcherClass?.simpleName»
        «IF pattern.findInferredClass(IMatchProcessor) !== null» @see «pattern.findInferredClass(IMatchProcessor)?.simpleName»«ENDIF»
    '''

    def javadocMatcherClass(Pattern pattern) '''
        Generated pattern matcher API of the «pattern.fullyQualifiedName» pattern,
        providing pattern-specific query methods.
        
        <p>Use the pattern matcher on a given model via {@link #on(ViatraQueryEngine)},
        e.g. in conjunction with {@link ViatraQueryEngine#on(Notifier)}.
        
        <p>Matches of the pattern will be represented as {@link «pattern.findMatchClass?.simpleName»}.
        
        <p>Original source:
        <code><pre>
        «pattern.serializeToJavadoc»
        </pre></code>
        
        @see «pattern.findMatchClass?.simpleName»
        «IF pattern.findInferredClass(IMatchProcessor) !== null» @see «pattern.findInferredClass(IMatchProcessor)?.simpleName»«ENDIF»
        @see «pattern.findInferredSpecification?.simpleName»
    '''

    def javadocQuerySpecificationClass(Pattern pattern) '''
        A pattern-specific query specification that can instantiate «pattern.findMatcherClass?.simpleName» in a type-safe way.
        
        @see «pattern.findMatcherClass?.simpleName»
        @see «pattern.findMatchClass?.simpleName»
    '''

    def javadocProcessorClass(Pattern pattern) '''
        A match processor tailored for the «pattern.fullyQualifiedName» pattern.
        
        Clients should derive an (anonymous) class that implements the abstract process().
    '''

    def javadocMatcherConstructorEngine(Pattern pattern) '''
        Initializes the pattern matcher within an existing VIATRA Query engine.
        If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
        The match set will be incrementally refreshed upon updates.
        @param engine the existing VIATRA Query engine in which this matcher will be created.
        @throws ViatraQueryException if an error occurs during pattern matcher creation
    '''

    def javadocMatcherStaticOnEngine(Pattern pattern) '''
        Initializes the pattern matcher within an existing VIATRA Query engine.
        If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
        The match set will be incrementally refreshed upon updates.
        @param engine the existing VIATRA Query engine in which this matcher will be created.
        @throws ViatraQueryException if an error occurs during pattern matcher creation
    '''

    def javadocMatcherStaticCreate(Pattern pattern) '''	
        @throws ViatraQueryException if an error occurs during pattern matcher creation
        @return an initialized matcher
        @noreference This method is for internal matcher initialization by the framework, do not call it manually.
    '''

    def javadocGetAllMatchesMethod(Pattern pattern) '''
        Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
        «FOR p : pattern.parameters»
            @param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
        «ENDFOR»
        @return matches represented as a «pattern.findMatchClass?.simpleName» object.
    '''

    def javadocGetOneArbitraryMatchMethod(Pattern pattern) '''
        Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
        Neither determinism nor randomness of selection is guaranteed.
        «FOR p : pattern.parameters»
            @param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
        «ENDFOR»
        @return a match represented as a «pattern.findMatchClass?.simpleName» object, or null if no match is found.
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

    def javadocNewMatchMethod(Pattern pattern) '''
        Returns a new (partial) match.
        This can be used e.g. to call the matcher with a partial match.
        <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
        «FOR p : pattern.parameters»
            @param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
        «ENDFOR»
        @return the (partial) match object.
    '''

    def javadocNewMutableMatchMethod(Pattern pattern) '''
        Returns a mutable (partial) match.
        Fields of the mutable match can be filled to create a partial match, usable as matcher input.
        
        «FOR p : pattern.parameters»
            @param «p.parameterName» the fixed value of pattern parameter «p.name», or null if not bound.
        «ENDFOR»
        @return the new, mutable (partial) match object.
    '''

    def javadocNewEmptyMatchMethod(Pattern pattern) '''
        Returns an empty, mutable match.
        Fields of the mutable match can be filled to create a partial match, usable as matcher input.
        
        @return the empty match.
    '''

    def javadocGetAllValuesOfMethod(Variable parameter) '''
        Retrieve the set of values that occur in matches for «parameter.name».
        @return the Set of all values or empty set if there are no matches
    '''

    def javadocQuerySpecificationMethod(Pattern pattern) '''
        @return the singleton instance of the query specification of this pattern
        @throws ViatraQueryException if the pattern definition could not be loaded
    '''

    def javadocQuerySpecificationInstanceMethod(Pattern pattern) '''
        @return the singleton instance of the query specification
        @throws ViatraQueryException if the pattern definition could not be loaded
    '''

    def javadocGroupClass(PatternModel model, boolean includePrivate) '''
        A pattern group formed of all«IF !includePrivate» public«ENDIF» patterns defined in «model.modelFileName».vql.
        
        «IF includePrivate»
            <p>A private group that includes private patterns as well. Only intended use case is for pattern testing.
        «ELSE»
            <p>Use the static instance as any {@link org.eclipse.viatra.query.runtime.api.IPatternGroup}, to conveniently prepare
            a VIATRA Query engine for matching all patterns originally defined in file «model.modelFileName».vql,
            in order to achieve better performance than one-by-one on-demand matcher initialization.
        «ENDIF»
        
        <p> From package «model.packageName», the group contains the definition of the following patterns: <ul>
        «FOR p : model.patterns.filter[includePrivate || !it.modifiers.isPrivate]»
            <li>«p.name»</li>
        «ENDFOR»
        </ul>
        
        @see IPatternGroup
    '''

    def javadocGroupClassInstanceMethod(PatternModel model) '''
        Access the pattern group.
        
        @return the singleton instance of the group
        @throws ViatraQueryException if there was an error loading the generated code of pattern specifications
    '''
}
