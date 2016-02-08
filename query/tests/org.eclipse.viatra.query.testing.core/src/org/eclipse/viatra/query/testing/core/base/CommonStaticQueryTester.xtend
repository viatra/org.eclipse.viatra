package org.eclipse.viatra.query.testing.core.base

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.IncQueryEngine
import org.eclipse.viatra.query.runtime.api.IncQueryMatcher
import org.eclipse.viatra.query.testing.core.ModelLoadHelper
import org.eclipse.viatra.query.testing.core.SnapshotHelper
import org.eclipse.viatra.query.testing.core.TestExecutor
import org.eclipse.viatra.query.testing.snapshot.IncQuerySnapshot

import static org.junit.Assert.*

/**
 * @deprecated use EIQTest api instead
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
		val IncQueryMatcher matcher = queryInput.initializeMatcherFromModel(engine, queryFQN)
		val results = matcher.compareResultSets(sns.getMatchSetRecordForPattern(queryFQN))
		assertArrayEquals(results.logDifference,newHashSet,results)
	}

	def testQuery(IQuerySpecification queryMF){
		val sns = snapshot
		val engine = getEngine(sns.EMFRootForSnapshot)
		testQuery(engine, sns, queryMF)
	}

	def testQuery(IncQueryEngine engine, IncQuerySnapshot sns, IQuerySpecification queryMF){
		val IncQueryMatcher matcher = engine.getMatcher(queryMF)
		val results = matcher.compareResultSets(sns.getMatchSetRecordForPattern(queryMF.fullyQualifiedName))
		assertArrayEquals(results.logDifference,newHashSet,results)
	}

	def getEngine(Notifier root) {
		return IncQueryEngine::on(root)
	}

	def snapshot() { // Creates new resource set
		return snapshotURI.loadExpectedResultsFromUri as IncQuerySnapshot
	}
	def queryInput() { // Creates new resource set
		return queryInputEIQURI.loadPatternModelFromUri(injector, queryInputDependencyURIs) as PatternModel
	}

	def String snapshotURI() // abstract
	def String queryInputEIQURI() // abstract
	def Iterable<String> queryInputDependencyURIs() // abstract
}