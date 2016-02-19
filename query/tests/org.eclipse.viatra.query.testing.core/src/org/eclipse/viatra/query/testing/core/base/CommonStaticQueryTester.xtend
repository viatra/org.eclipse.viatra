package org.eclipse.viatra.query.testing.core.base

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.eclipse.viatra.query.testing.core.SnapshotHelper
import org.eclipse.viatra.query.testing.core.TestExecutor
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot

import static org.junit.Assert.*
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest

/**
 * @deprecated use {@link ViatraQueryTest} API instead
 */
@Deprecated
abstract class CommonStaticQueryTester {
	@Inject extension ModelLoadHelper
	@Inject extension TestExecutor
	@Inject extension SnapshotHelper
	@Inject var Injector injector

	def testQuery(String queryFQN){
		val sns = snapshot
		val engine = getEngine(sns.EMFRootForSnapshot)
		val ViatraQueryMatcher matcher = queryInput.initializeMatcherFromModel(engine, queryFQN)
		val results = matcher.compareResultSets(sns.getMatchSetRecordForPattern(queryFQN))
		assertArrayEquals(results.logDifference,newHashSet,results)
	}

	def testQuery(IQuerySpecification queryMF){
		val sns = snapshot
		val engine = getEngine(sns.EMFRootForSnapshot)
		testQuery(engine, sns, queryMF)
	}

	def testQuery(ViatraQueryEngine engine, QuerySnapshot sns, IQuerySpecification queryMF){
		val ViatraQueryMatcher matcher = engine.getMatcher(queryMF)
		val results = matcher.compareResultSets(sns.getMatchSetRecordForPattern(queryMF.fullyQualifiedName))
		assertArrayEquals(results.logDifference,newHashSet,results)
	}

	def getEngine(Notifier root) {
		return ViatraQueryEngine::on(root)
	}

	def snapshot() { // Creates new resource set
		return snapshotURI.loadExpectedResultsFromUri as QuerySnapshot
	}
	def queryInput() { // Creates new resource set
		return queryInputVQLURI.loadPatternModelFromUri(injector, queryInputDependencyURIs) as PatternModel
	}

	def String snapshotURI() // abstract
	def String queryInputVQLURI() // abstract
	def Iterable<String> queryInputDependencyURIs() // abstract
}