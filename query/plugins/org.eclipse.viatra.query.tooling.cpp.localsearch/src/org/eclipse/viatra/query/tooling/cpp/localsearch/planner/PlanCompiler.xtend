/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.cpp.localsearch.planner

import com.google.common.collect.ImmutableSet
import com.google.common.collect.Iterables
import java.util.Collections
import java.util.List
import java.util.Map
import java.util.Set
import java.util.concurrent.atomic.AtomicInteger
import org.apache.log4j.Logger
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHints
import org.eclipse.viatra.query.runtime.localsearch.planner.LocalSearchRuntimeBasedStrategy
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendHintProvider
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody
import org.eclipse.viatra.query.runtime.matchers.psystem.analysis.QueryAnalyzer
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.ParameterReference
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.DefaultFlattenCallPredicate
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PBodyNormalizer
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.PQueryFlattener
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.PatternDescriptor

/**
 * @author Robert Doczi
 */
class PlanCompiler implements IQueryBackendContext {
	
	val PQueryFlattener flattener
	val PBodyNormalizer normalizer
	val Map<PQuery, List<PBody>> compiledBodies
	val Set<MatcherReference> dependencies
	val MatchingFrameRegistry frameRegistry
    val LocalSearchHints configuration
    val runtimeContext = new CppQueryRuntimeContext
	
	extension val	LocalSearchRuntimeBasedStrategy strategy	
	extension val POperationCompiler compiler
    
	
	new () {
		val flattenCallPredicate = new DefaultFlattenCallPredicate
        this.flattener = new PQueryFlattener(flattenCallPredicate)
		this.normalizer = new PBodyNormalizer(null, false)
		this.compiledBodies = newHashMap
		this.dependencies = newHashSet
		this.frameRegistry = new MatchingFrameRegistry
		this.configuration = LocalSearchHints::defaultNoBase
		// this should not matter as flattening is done by the the compiler bellow, it's done for the sake of consistency
		this.configuration.flattenCallPredicate = flattenCallPredicate
				
		this.strategy = new LocalSearchRuntimeBasedStrategy()
		this.compiler = new POperationCompiler
	}
	
	def compilePlan(PQuery pQuery) {
		this.dependencies.clear		
		
		val normalizedBodies = pQuery.flattenAndNormalize
		
		val bindings = pQuery.allAnnotations.filter[name == "Bind"]
		val boundPatternStubs = bindings.map[ binding |
			val boundParameters = getBoundParameters(binding, pQuery)

			val bodies = normalizedBodies.compile(boundParameters, frameRegistry)
			return new PatternDescriptor(pQuery, bodies, boundParameters)
		]

		val bodies = normalizedBodies.compile(#{}, frameRegistry)
		val unboundPatternStub = new PatternDescriptor(pQuery, bodies)
		
		val dependentPatternStubs = dependencies.map[
			val dependentNormalizedBodies = it.referredQuery.flattenAndNormalize
			val dependentBodies = dependentNormalizedBodies.compile(it.adornment, frameRegistry)
			return new PatternDescriptor(it.referredQuery, dependentBodies, it.adornment)
		]
				
		// copy to prevent lazy evaluation
		return ImmutableSet::copyOf(Iterables::concat(#[unboundPatternStub], boundPatternStubs, dependentPatternStubs))
	}
	
	private def flattenAndNormalize(PQuery pQuery) {
		val cachedBody = compiledBodies.get(pQuery)
		if(cachedBody != null)
			return cachedBody
		val flatDisjunction = flattener.rewrite(pQuery.disjunctBodies)
		val normalizedDisjunction = normalizer.rewrite(flatDisjunction)
		
		return normalizedDisjunction.bodies.toList
	}
	
	private def getBoundParameters(PAnnotation binding, PQuery pQuery) {
		binding.getAllValues("parameters").map [
			switch (it) {
				ParameterReference: #[it]
				List<ParameterReference>: it
			}
		].flatten.map[
			pQuery.parameters.get(pQuery.getPositionOfParameter(it.name))
		].toSet
	}

	def compile(List<PBody> normalizedBodies, Iterable<PParameter> boundParameters, MatchingFrameRegistry frameRegistry) {

		// no need for atomic, required a simple counter with final reference in closure
		val AtomicInteger counter = new AtomicInteger(0)
		val patternBodyStubs = normalizedBodies.map[pBody |
			val boundPVariables = boundParameters.map[pBody.getVariableByNameChecked(name)]
												 .toSet

			val acceptor = new CPPSearchOperationAcceptor(counter.getAndIncrement, frameRegistry)
			pBody.plan(boundPVariables, this, configuration)
				 .compile(pBody, boundPVariables, acceptor)
			dependencies += acceptor.dependencies
			return acceptor.patternBodyStub
		].toSet
		
		return patternBodyStubs
	}

    override getHintProvider() {
        new IQueryBackendHintProvider(){
            
            override getQueryEvaluationHint(PQuery query) {
                return new QueryEvaluationHint(Collections.emptyMap(), null)
            }
            
        }
    }   

    override getLogger() {
        Logger::getLogger(PlanCompiler)
    }

    override getQueryAnalyzer() {
        new QueryAnalyzer(runtimeContext.metaContext)
    }

    override getQueryCacheContext() {
        null
    }

    override getResultProviderAccess() {
        null
    }

    override getRuntimeContext() {
        runtimeContext
    }

}