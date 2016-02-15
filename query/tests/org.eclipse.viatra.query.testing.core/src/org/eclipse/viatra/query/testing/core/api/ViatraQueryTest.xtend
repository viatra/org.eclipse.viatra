/** 
 * Copyright (c) 2010-2015, Grill Bal�zs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Bal�zs - initial API and implementation
 */
package org.eclipse.viatra.query.testing.core.api

import java.util.LinkedList
import java.util.List
import org.eclipse.emf.common.util.URI
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQueryGroup
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.extensibility.QuerySpecificationRegistry
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.viatra.query.testing.core.PatternBasedMatchSetModelProvider
import org.eclipse.viatra.query.testing.core.SnapshotMatchSetModelProvider
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.testing.core.ViatraQueryTestCase

/**
 * This class defines an API to easily construct test cases. The base conception is to provide
 * a set of match set sources (from a snapshot or from the execution of a pattern) and make assertions
 * on them being equal.
 */
class ViatraQueryTest {

	val ViatraQueryTestCase testCase;

	/**
	 * Test the specified query
	 */
	static def <Match extends IPatternMatch> test(IQuerySpecification<? extends ViatraQueryMatcher<Match>> pattern) {
		new ViatraQueryTest().and(pattern)
	}

	static def test(IQueryGroup patterns) {
		new ViatraQueryTest().and(patterns)
	}

	static def test() {
		new ViatraQueryTest
	}

	/**
	 * Test the specified query
	 */
	static def <Match extends IPatternMatch> test(String pattern) {
		test().and(pattern)
	}

	def and(IQueryGroup patterns) {
		patterns.specifications.forEach [
			this.patterns += it as IQuerySpecification<ViatraQueryMatcher<IPatternMatch>>
		]
		this
	}

	def and(IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> pattern) {
		patterns.add(pattern)
		this
	}

	def and(String pattern) {
		and(
			QuerySpecificationRegistry.
				getQuerySpecification(pattern) as IQuerySpecification<ViatraQueryMatcher<IPatternMatch>>)
	}

	private val List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> patterns = new LinkedList;

	private new() {
		testCase = new ViatraQueryTestCase
	}

	private new(IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> pattern) {
		this()
		this.patterns.add(pattern);
	}

	/**
	 * Add match result set with a query initialized using the given hints
	 */
	def with(QueryEvaluationHint hint) {
		val modelProvider = new PatternBasedMatchSetModelProvider(hint);
		testCase.addMatchSetModelProvider(modelProvider)
		this
	}

	/**
	 * Add match result set with a query initialized using the given query backend
	 */
	def with(Class<? extends IQueryBackend> queryBackendClass) {
		val QueryEvaluationHint hint = new QueryEvaluationHint(queryBackendClass, emptyMap);
		with(hint)
	}

	/**
	 * Add match result set loaded from the given snapshot
	 */
	def with(URI snapshotURI) {
		testCase.addMatchSetModelProvider(new SnapshotMatchSetModelProvider(snapshotURI))
		this
	}

	/**
	 * Add match result set loaded from the given snapshot
	 */
	def with(String snapshotURI) {
		with(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, snapshotURI))
	}

	/**
	 * Load input model
	 */
	def on(URI inputURI) {
		testCase.loadModel(inputURI)
		this
	}

	/**
	 * Execute all queries and check that the result sets are equal
	 */
	def assertEquals() {
		patterns.forEach [
			testCase.assertMatchSetsEqual(it as IQuerySpecification<ViatraQueryMatcher<IPatternMatch>>)
		]
	}

}
