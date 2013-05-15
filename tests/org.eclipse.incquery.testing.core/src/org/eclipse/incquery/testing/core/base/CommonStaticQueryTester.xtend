package org.eclipse.incquery.testing.core.base

import org.eclipse.incquery.testing.core.ModelLoadHelper
import com.google.inject.Inject
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.incquery.snapshot.EIQSnapshot.IncQuerySnapshot
import org.eclipse.incquery.testing.core.SnapshotHelper
import org.eclipse.incquery.testing.core.TestExecutor
import org.eclipse.incquery.runtime.api.IQuerySpecification

import static org.junit.Assert.*
import org.eclipse.incquery.runtime.api.IncQueryEngine
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx$Snapshot

abstract class CommonStaticQueryTester {
	@Inject extension ModelLoadHelper
	@Inject extension TestExecutor
	@Inject extension SnapshotHelper
		
	def testQuery(String queryFQN){
		val sns = snapshot
		val engine = getEngine(sns.EMFRootForSnapshot)
		val IncQueryMatcher matcher = queryInputXMI.initializeMatcherFromModel(engine, queryFQN)
		val results = matcher.compareResultSets(sns.getMatchSetRecordForPattern(queryFQN))
		assertArrayEquals(results.logDifference,newHashSet,results)
	}
	
	def testQuery(IQuerySpecification queryMF){
		val sns = snapshot
		val engine = getEngine(sns.EMFRootForSnapshot)
		testQuery(engine, sns, queryMF)
	}

	def testQuery(IncQueryEngine engine, IncQuerySnapshot sns, IQuerySpecification queryMF){
		val IncQueryMatcher matcher = engine.getMatcher(queryMF)//queryInputXMI.initializeMatcherFromModel(sns.EMFRootForSnapshot, queryFQN)
		val results = matcher.compareResultSets(sns.getMatchSetRecordForPattern(queryMF.patternFullyQualifiedName))
		assertArrayEquals(results.logDifference,newHashSet,results)
	}
	
	def getEngine(Notifier root) { 
		return IncQueryEngine::on(root)
	}
		
	def snapshot() { // Creates new resource set
		return snapshotURI.loadExpectedResultsFromUri as IncQuerySnapshot
	}
	def queryInputXMI() { // Creates new resource set
		return queryInputXMIURI.loadPatternModelFromUri as PatternModel
	}
	
	def String snapshotURI() // abstract
	def String queryInputXMIURI() // abstract
}