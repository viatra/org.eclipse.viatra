/** 
 * Copyright (c) 2010-2015, Grill Balázs, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Balázs - initial API and implementation
 */
package org.eclipse.viatra.query.testing.core

import java.util.LinkedList
import java.util.List
import junit.framework.AssertionFailedError
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQueryGroup
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.testing.core.internal.MatchSetRecordDiff

/** 
 * @author Grill Balázs
 */
class ViatraQueryTestCase {

	public static val String UNEXPECTED_MATCH = "Unexpected match"
	public static val String EXPECTED_NOT_FOUND = "Expected match not found"

	final ResourceSet resourceSet

	final List<IMatchSetModelProvider> modelProviders
	extension SnapshotHelper = new SnapshotHelper

	new() {
		this.resourceSet = new ResourceSetImpl
		this.modelProviders = new LinkedList
	}

	def loadModel(URI uri) {
		resourceSet.getResource(uri, true)
	}

	def modifyModel(URI uri, (Resource)=>void operation){
		operation.apply(resourceSet.getResource(uri, false))
	}

	def addMatchSetModelProvider(IMatchSetModelProvider matchSetModelProvider) {
		modelProviders.add(matchSetModelProvider);
	}

	def <Match extends IPatternMatch> assertMatchSetsEqual(
		IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification) {
		if (modelProviders.size < 2) {
			throw new IllegalArgumentException("At least two model providers shall be set")
		}

		val reference = modelProviders.head

		modelProviders.tail.forEach [
			val diff = compareMatchSets(querySpecification, reference, it)
			if (!diff.empty) {
				throw new AssertionFailedError(diff.toString)
			}
		]
	}

	def assertMatchSetsEqual(IQueryGroup queryGroup) {
		queryGroup.specifications.forEach [
			assertMatchSetsEqual(it as IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>)
		]
	}

	def <Match extends IPatternMatch> getMatchSetDiff(
		IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification,
		IMatchSetModelProvider expectedProvider, IMatchSetModelProvider actualProvider){

		var Match filter = null;

		var expected = expectedProvider.getMatchSetRecord(resourceSet, querySpecification, filter)
		if (expected.filter != null) {
			filter = createMatchForMatchRecord(querySpecification, expected.filter)
		}

		val actual = actualProvider.getMatchSetRecord(resourceSet, querySpecification, filter)
		if (actual.filter != null) {
			if (filter != null) {
				throw new IllegalArgumentException(
					"Filter is provided by more than one sources: " + expectedProvider + ", " + actualProvider)
			} else {
				filter = createMatchForMatchRecord(querySpecification, actual.filter)
				// Reexecute expected to have the same filtering
				expected = expectedProvider.getMatchSetRecord(resourceSet, querySpecification, filter)
			}
		}

		MatchSetRecordDiff::compute(expected, actual)	
	}

	private def <Match extends IPatternMatch> compareMatchSets(
		IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification,
		IMatchSetModelProvider expectedProvider, IMatchSetModelProvider actualProvider) {
		
		val matchdiff = getMatchSetDiff(querySpecification, expectedProvider, actualProvider)
		val diff = newHashSet
		diff.addAll(matchdiff.additions.map[UNEXPECTED_MATCH + " (" + it.prettyPrint + ")"])
		diff.addAll(matchdiff.removals.map[EXPECTED_NOT_FOUND + " (" + it.prettyPrint + ")"])

		return diff
	}
	
}
