package org.eclipse.incquery.testing.core

import com.google.common.base.Stopwatch
import com.google.common.collect.Maps
import java.util.Map
import java.util.concurrent.TimeUnit
import org.apache.log4j.Logger
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.api.IQueryGroup
import org.eclipse.incquery.runtime.api.IQuerySpecification
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import org.eclipse.incquery.runtime.api.scope.IncQueryScope
import org.eclipse.incquery.runtime.extensibility.QueryBackendRegistry
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackend
import org.eclipse.incquery.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil
import org.junit.Test

/**
 * This abstract test class can be used to measure the steady-state memory requirements of the base index and
 * Rete networks of individual queries on a given IncQueryScope and with a given query group.
 * 
 * <p/>
 * This test case prepares an IncQueryEngine on the given scope and with the provided query group.
 * After the initial preparation is done, the engine is wiped (deletes the Rete network but keeps the base index).
 * Next, the following is performed for each query in the group:
 * <p/>
 * <ol>
 *   <li> Wipe the engine </li>
 *   <li> Create the matcher and count matches </li>
 *   <li> Wipe the engine </li>
 * </ol>  
 * 
 * After each step, the used, total and free heap space is logged in MBytes after 5 GC calls and 1 second of waiting.
 * Note that even this does not always provide an absolute steady state or a precise result, but can be useful for 
 * finding problematic queries.
 */
abstract class QueryPerformanceTest {
	
	protected static extension Logger logger = IncQueryLoggingUtil.getLogger(QueryPerformanceTest)
	
	AdvancedIncQueryEngine incQueryEngine
	IQueryGroup queryGroup
	Map<String, Long> results = Maps.newTreeMap()
	
	/**
	 * This method shall return a scope that identifies the input artifact used for performance testing the queries.
	 */
	def IncQueryScope getScope()
	
	/**
	 * This method shall return the query group that contains the set of queries to evaluate.
	 */
	def IQueryGroup getQueryGroup()
	
	/**
	 * This method shall return the query backend class that will be used for evaluation.
	 * The backend must be already registered in the {@link QueryBackendRegistry}.
	 * 
	 * Default implementation returns the registered default backend class.
	 */
	def Class<? extends IQueryBackend> getQueryBackend() {
	    QueryBackendRegistry.getInstance.defaultBackendClass
	}
	
	protected def prepare() {
		info("Preparing query performance test")
		
		val preparedScope = scope
		logMemoryProperties("Scope prepared")
		
		incQueryEngine = AdvancedIncQueryEngine.createUnmanagedEngine(preparedScope)
		queryGroup.prepare(incQueryEngine)
		logMemoryProperties("Base index created")
		incQueryEngine.wipe()
		logMemoryProperties("IncQuery engine wiped")
		info("Prepared query performance test")
	}
	
	/**
	 * This test case executes the performance evaluation on the given scope and with the provided query group.
	 */
	@Test
	public def queryPerformance() {
		prepare()
		
		info("Starting query performance test")
		
		for (IQuerySpecification<?> _specification : queryGroup.getSpecifications) {
			
			val specification = _specification as IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>
			debug("Measuring query " + specification.getFullyQualifiedName)
			incQueryEngine.wipe
			val usedHeapBefore = logMemoryProperties("Wiped engine before building")
			
			debug("Building Rete")
			val watch = Stopwatch.createStarted
			val matcher = incQueryEngine.getMatcher(specification, new QueryEvaluationHint(queryBackend, newHashMap))
			watch.stop()
			val countMatches = matcher.countMatches
			val usedHeapAfter = logMemoryProperties("Matcher created")
			
			val usedHeap = usedHeapAfter - usedHeapBefore
			results.put(specification.getFullyQualifiedName, usedHeap)
			info("Query " + specification.fullyQualifiedName + "( " + countMatches + " matches, used " + usedHeap +
					" kByte heap, took " + watch.elapsed(TimeUnit.MILLISECONDS) + " ms)")
			
			incQueryEngine.wipe
			logMemoryProperties("Wiped engine after building\n-------------------------------------------\n")
		}
		
		info("Finished query performance test")
		
		printResults()
	}
	
	protected def printResults() {
		
		val resultSB = new StringBuilder("\n\nPerformance test results:\n")
		results.entrySet.forEach[entry |
			resultSB.append("  " + entry.key + "," + entry.value + "\n")
		]
		info(resultSB)
		
	}
	
	/**
	 * Calls garbage collector 5 times, sleeps 1 second and logs the used, total and free heap sizes in MByte.
	 * 
	 * @param logger
	 * @return The amount of used heap memory in kBytes
	 */
	protected def static logMemoryProperties(String status) {
		(0..4).forEach[Runtime.getRuntime().gc()]
		
		try {
			Thread.sleep(1000)
		} catch (InterruptedException e) {
			trace("Sleep after GC interrupted")
		}
		
		val totalHeapKB = Runtime.getRuntime().totalMemory() / 1024;
		val freeHeapKB = Runtime.getRuntime().freeMemory() / 1024;
		val usedHeapKB = totalHeapKB - freeHeapKB;
		debug(status + ": Used Heap size: " + usedHeapKB / 1024 + " MByte (Total: " + totalHeapKB / 1024 + " MByte, Free: " + freeHeapKB / 1024 + " MByte)")
		
		usedHeapKB
	}
}