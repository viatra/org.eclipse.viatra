/*******************************************************************************
 * Copyright (c) 2014-2016 Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import com.google.common.collect.Sets
import java.util.Map
import java.util.Set
import org.eclipse.viatra.query.runtime.cps.tests.queries.FunctionalDependencies
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.emf.EMFQueryMetaContext
import org.eclipse.viatra.query.runtime.matchers.planning.helpers.FunctionalDependencyHelper
import org.eclipse.viatra.query.runtime.matchers.psystem.analysis.QueryAnalyzer
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class FunctionalDependencyAnalysisTest {
	val QueryAnalyzer analyzer = new QueryAnalyzer(EMFQueryMetaContext::DEFAULT)
	val extension FunctionalDependencies functionalDependenciesGroup = FunctionalDependencies::instance
	
	@Test
	@Ignore
	def prettyPrint() {
		val grp = FunctionalDependencies::instance
		grp.specifications.forEach[println(prettyPrintDependencies)]
	}
	
	@Test
	def toOneFeatureTest() {
		assertDependencies(toOneFeature, true, #{
			#{"obj"} -> #{"id"}	
		})
		assertDependencies(toOneFeature, false, #{
			#{"obj"} -> #{"id"}	
		})
	}
	
	@Test
	def toOneFeatureWithSoftDepTest() {
		assertDependencies(toOneFeatureWithSoftDep, true, #{
			#{"obj"} -> #{"id"}	
		})
		assertDependencies(toOneFeatureWithSoftDep, false, #{
			#{"obj"} -> #{"id"},
			#{"id"} -> #{"obj"}	
		})
	}
	
	@Test
	def hasCallTest() {
		assertDependencies(hasCall, true, #{
			#{"obj"} -> #{"id"}	
		})
		assertDependencies(hasCall, false, #{
			#{"obj"} -> #{"id"},
			#{"id"} -> #{"obj"}	
		})
	}
	
	@Test
	def compositeKeyTest() {
		assertDependencies(compositeKey, true, #{
			#{"obj1", "obj2"} -> #{"result"}	
		})
		assertDependencies(compositeKey, false, #{
			#{"obj1", "obj2"} -> #{"result"}	
		})
	}
	
	@Test
	def relativeIDTest() {
		assertDependencies(relativeID, true, #{
			#{"obj"} -> #{"id", "cps"}	
		})
		assertDependencies(relativeID, false, #{
			#{"obj"} -> #{"id", "cps"},	
			#{"id", "cps"} -> #{"obj"}	
		})
	}
	
	@Test
	def evaluationTest() {
		assertDependencies(evaluation, true, #{
			#{"obj"} -> #{"id"},
			#{"id"} -> #{"upper"}	
		})
		assertDependencies(evaluation, false, #{
			#{"obj"} -> #{"id"},
			#{"id"} -> #{"upper"}	
		})
	}
	
	
	@Test
	def transitiveDepTest() {
		assertDependencies(transitiveDep, true, #{
			#{"obj"} -> #{"upper"}	
		})
		assertDependencies(transitiveDep, false, #{
			#{"obj"} -> #{"upper"}	
		})
	}
	
	@Test
	def constantTest() {
		assertDependencies(constant, true, #{
			<String>newHashSet() -> #{"id"}	
		})
		assertDependencies(constant, false, #{
			<String>newHashSet() -> #{"id"}	
		})
	}
	
	@Test
	def constantWithSoftTest() {
		assertDependencies(constantWithSoft, true, #{
			<String>newHashSet() -> #{"id"}	
		})
		assertDependencies(constantWithSoft, false, #{
			<String>newHashSet() -> #{"id", "obj"}	
		})
	}
	
	@Test
	def disjunctiveDependenciesTest() {
		assertDependencies(disjunctiveDependencies, true, #{})
		assertDependencies(disjunctiveDependencies, false, #{
			#{"obj"} -> #{"id"},
			#{"id"} -> #{"obj"}	
		})
	}
	
	
	
	
	def toNames(Set<Integer> paramIndices, IQuerySpecification<?> query) {
		Sets.newHashSet(paramIndices.map[query.parameterNames.get(it)])
	}
	def assertDependencies(
		IQuerySpecification<?> query,
		boolean strict, 
		Map<Set<String>, Set<String>> expectedDeps) 
	{
		val pQuery = query.internalQueryRepresentation
		val params = Sets.newHashSet((0..(pQuery.parameters.size - 1)) as Iterable<Integer>)		
		val actualDeps = analyzer.getProjectedFunctionalDependencies(pQuery, strict)
		
		for (Set<Integer> leftIndices : Sets.powerSet(params).sortBy[size]) {
			val actualClosureIndices = FunctionalDependencyHelper::closureOf(leftIndices, actualDeps)
			val actualClosureNames = actualClosureIndices.toNames(query)
			val leftNames = leftIndices.toNames(query)
			val expectedClosureNames = FunctionalDependencyHelper::closureOf(leftNames, expectedDeps)
			
			Assert::assertEquals(
				'''Probing «query.fullyQualifiedName» (strict=«strict») on «leftNames.sort»''', 
				expectedClosureNames, actualClosureNames)
		}
	}
	
	
	def prettyPrintDependencies(IQuerySpecification<?> query) {
		val pQuery = query.internalQueryRepresentation
		
		val strictDeps = analyzer.getProjectedFunctionalDependencies(pQuery, true)
		val softDeps = analyzer.getProjectedFunctionalDependencies(pQuery, false)
		
		'''
			«query.fullyQualifiedName» 
				«IF	!strictDeps.empty»strict: «strictDeps.prettyPrintDependencies(query)»«ENDIF»
				«IF !strictDeps.equals(softDeps)»soft: «softDeps.prettyPrintDependencies(query)»«ENDIF»
		'''
	}
	def prettyPrintParamlist(Set<Integer> paramIndices, IQuerySpecification<?> query) {
		paramIndices.toNames(query).sort.join(', ')
	}
	def prettyPrintDependencies(Map<Set<Integer>, Set<Integer>> deps, IQuerySpecification<?> query) 
		'''[[
	«FOR dep : deps.entrySet»
		{«dep.key.prettyPrintParamlist(query)»} -> {«dep.value.prettyPrintParamlist(query)»}
	«ENDFOR»
	]]'''
	
	
	
	
}
