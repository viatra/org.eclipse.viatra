/*******************************************************************************
 * Copyright (c) 2010-2017, Dénes Harmath, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Dénes Harmath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.testing.core.coverage

import com.google.common.base.Charsets
import com.google.common.io.Files
import java.io.File
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedPrivateEMFQuerySpecification

/**
 * Utility methods to report the results of a {@link CoverageAnalyzer}.
 * 
 * @since 1.6
 */
class CoverageReporter {

	private static def formatPercent(double percent) {
		'''«String.format("%1$.2f", percent)»%'''
	}

    /**
     * Prints coverage report to the standard output.
     */
    static def void reportConsole(CoverageAnalyzer it) {
	    println('''
	    «FOR query : coverage.queryCoverage.keySet.sortBy[fullyQualifiedName]»
	    	pattern «query.fullyQualifiedName» («coverage.queryCoverage.get(query)») «coverage.getCoveragePercent(query).formatPercent»
	    	«FOR body : query.disjunctBodies.bodies SEPARATOR " or "»
	    		{ («coverage.bodyCoverage.get(body)»)
	    			«FOR constraint : body.constraints»
	    				«constraint» («coverage.constraintCoverage.get(constraint)»)
	    			«ENDFOR»
	    		}
	    	«ENDFOR»
	    «ENDFOR»
	    ''')
    }

    /**
     * Saves coverage report in HTML format to the given {@link File}.
     */
	static def void reportHtml(CoverageAnalyzer it, File file) {
		val content = '''
		<html><body>
		<h1>Pattern coverage report</h1>
		<h2>Overall coverage: «coverage.aggregatedCoveragePercent.formatPercent»</h2>
		<h2>Detailed coverage</h2>
		«FOR query : coverage.queryCoverage.keySet.sortBy[fullyQualifiedName]»
			<details>
				<summary>
					«val queryState = coverage.queryCoverage.get(query)?:CoverageState.UNDEFINED»
					<span style="«queryState.style»" title="«queryState.info»">
						«IF !query.publishedAs.filter(BaseGeneratedPrivateEMFQuerySpecification).empty /* XXX */»private«ENDIF»
						pattern «query.fullyQualifiedName»</span>
					«coverage.getCoveragePercent(query).formatPercent»
				</summary>
				«FOR body: query.disjunctBodies.bodies SEPARATOR " or "»
					«val bodyState = coverage.bodyCoverage.get(body)?:CoverageState.UNDEFINED»
					<div><span style="«bodyState.style»" title="«bodyState.info»">{</span>
					«FOR constraint: body.constraints»
						«val constraintState = coverage.constraintCoverage.get(constraint)?:CoverageState.UNDEFINED»
						<div style="«constraintState.style»" title="«constraintState.info»">
						«constraint»
						</div>
					«ENDFOR»
					}</div>
				«ENDFOR»
			</details>
		«ENDFOR»
		<h2>Legend</h2>
		<p>
			<span style="«CoverageState.COVERED.style»">Covered</span><br/>
			<span style="«CoverageState.NOT_COVERED.style»">Not covered</span><br/>
			<span style="«CoverageState.NOT_REPRESENTED.style»">Not represented (optimized out)</span><br/>
			<span style="«CoverageState.UNDEFINED.style»">Coverage cannot be determined</span><br/>
		</p>
		</body></html>'''
		Files.write(content, file, Charsets.UTF_8)
	}
	
	private static def String getStyle(CoverageState cs){
		switch (cs) {
		case COVERED: "background:springgreen"
		case NOT_COVERED: "background:tomato"
		case NOT_REPRESENTED: "color:lightgray"
		case UNDEFINED: "color:red"
		}
	}
	
	private static def String getInfo(CoverageState cs){
		switch (cs) {
		case COVERED: "Covered"
		case NOT_COVERED: "Not covered"
		case NOT_REPRESENTED: "Not represented"
		case UNDEFINED: "N/A"
		}
	}

}