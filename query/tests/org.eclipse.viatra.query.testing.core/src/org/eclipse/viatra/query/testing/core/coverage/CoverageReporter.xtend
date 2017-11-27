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
import java.util.Set
import org.eclipse.emf.common.util.URI
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody
import org.eclipse.viatra.query.runtime.matchers.psystem.PTraceable
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PVisibility

/**
 * Utility methods to report the results of a {@link CoverageAnalyzer}.
 * 
 * @since 1.6
 */
class CoverageReporter {

    /**
     * Prints coverage report to the standard output.
     */
    static def void reportConsole(CoverageAnalyzer it) {
	    println('''
	    «FOR query : coverage.elementCoverage.keySet.filter(PQuery).sortBy[fullyQualifiedName]»
	    	pattern «query.fullyQualifiedName» («coverage.elementCoverage.get(query)») «coverage.getCoveragePercent(query).formatPercent»
	    	«FOR body : query.disjunctBodies.bodies SEPARATOR " or "»
	    		{ («coverage.elementCoverage.get(body)»)
	    			«FOR constraint : body.constraints»
	    				«constraint» («coverage.elementCoverage.get(constraint)»)
	    			«ENDFOR»
	    		}
	    	«ENDFOR»
	    «ENDFOR»
	    ''')
    }

    private static def formatPercent(double percent) {
        '''«String.format("%1$.2f", percent)»%'''
    }

    /**
     * Saves coverage report in HTML format to the given {@link File}.
     */
	static def void reportHtml(CoverageAnalyzer it, File file) {
		val content = '''
		<html><body>
		<h1>Pattern coverage report</h1>
		<h2>Pattern & body coverage</h2>
		<h3>Overall: «getBodyCoverage(coverage, coverage.elementCoverage.keySet.filter(PBody))»</h3>
		<ul>
		«FOR query : coverage.elementCoverage.keySet.filter(PQuery).sortBy[fullyQualifiedName]»
            «val bodies = query.disjunctBodies.bodies»
		    <li>
		      <a href="#«query.fullyQualifiedName»"><span «getAttributes(coverage, query, bodies)»>
		      «IF query.visibility == PVisibility.PRIVATE»private«ENDIF»
		      pattern «query.fullyQualifiedName»</span></a> «getBodyCoverage(coverage, bodies)»
		    </li>
        «ENDFOR»
		</ul>
		<h2>Detailed coverage</h2>
		«FOR query : coverage.elementCoverage.keySet.filter(PQuery).sortBy[fullyQualifiedName]»
			<h3 id="«query.fullyQualifiedName»"/>pattern «query.fullyQualifiedName»</h3>
			«FOR context : coverage.keySet.filter[element == query]»
			 <p>Model: «context.scope»</p>
			 <p>
			 «var bodyIndex = 0»
             «FOR body: query.disjunctBodies.bodies SEPARATOR " or "»
                  «val constraints = body.constraints.filter[!(it instanceof ExportedParameter)]»
                 <div><span «getAttributes(coverage, body, constraints, context.scope)»>Body #«bodyIndex++» {</span>
                 «FOR constraint: constraints»
                     <div «getAttributes(coverage, constraint, #[], context.scope)»">
                     «constraint»
                     </div>
                 «ENDFOR»
                 }</div>
             «ENDFOR»
			«ENDFOR»
			</p>
		«ENDFOR»
		<h2>Legend</h2>
		<p>
			«getLegend(CoverageState.COVERED, true)»
			«getLegend(CoverageState.COVERED, false)»
			«getLegend(CoverageState.NOT_COVERED, false)»
			«getLegend(CoverageState.NOT_REPRESENTED, true)»
			«getLegend(CoverageState.NOT_REPRESENTED_UNKNOWN_REASON, true)»
		</p>
		</body></html>'''
		Files.write(content, file, Charsets.UTF_8)
	}

    private static def CharSequence getBodyCoverage(CoverageInfo<?> coverage, Iterable<PBody> bodies) {
        '''«coverage.getCoveragePercent(bodies).formatPercent»
        («bodies.filter[body | coverage.elementCoverage.get(body) == CoverageState.COVERED].size»/«bodies.size» bodies covered)'''
    }

	private static def String getAttributes(CoverageInfo<?> coverage, PTraceable traceable, Iterable<? extends PTraceable> children) {
	    val state = coverage.elementCoverage.get(traceable)?:CoverageState.NOT_REPRESENTED_UNKNOWN_REASON
	    val childrenCovered = children.forall[coverage.elementCoverage.get(it) != CoverageState.NOT_COVERED]
		'''style="«getStyle(state, childrenCovered)»" title="«getTitle(state, childrenCovered)»"'''
	}
	
	private static def String getAttributes(CoverageInfo<?> coverage, PTraceable traceable, Iterable<? extends PTraceable> children, Set<URI> scope) {
        val state = coverage.get(new CoverageContext(traceable, scope))?:CoverageState.NOT_REPRESENTED_UNKNOWN_REASON
        val childrenCovered = children.forall[coverage.get(new CoverageContext(it, scope)) != CoverageState.NOT_COVERED]
        '''style="«getStyle(state, childrenCovered)»" title="«getTitle(state, childrenCovered)»"'''
    }
	
	private static def String getLegend(CoverageState state, boolean childrenCovered) {
	   '''<span style="«getStyle(state, childrenCovered)»">«getTitle(state, childrenCovered)»</span><br/>'''
    }

    private static def String getTitle(CoverageState state, boolean childrenCovered) {
        switch (state) {
            case COVERED: if (childrenCovered) "Covered" else "Partially covered"
            case NOT_COVERED: "Not covered"
            case NOT_REPRESENTED: "Not represented"
            case NOT_REPRESENTED_UNKNOWN_REASON: "Not represented by error - please report"
        }
    }

    private static def String getStyle(CoverageState state, boolean childrenCovered) {
        switch (state) {
            case COVERED: if (childrenCovered) "background:springgreen" else "background:yellow"
            case NOT_COVERED: "background:tomato"
            case NOT_REPRESENTED: "color:lightgray"
            case NOT_REPRESENTED_UNKNOWN_REASON: "color:red"
        }
    }
    
}