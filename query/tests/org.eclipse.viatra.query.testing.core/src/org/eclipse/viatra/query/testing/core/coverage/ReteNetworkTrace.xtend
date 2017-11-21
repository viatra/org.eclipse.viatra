/** 
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Grill Balázs - initial API and implementation
 */
package org.eclipse.viatra.query.testing.core.coverage

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException
import org.eclipse.viatra.query.runtime.matchers.psystem.PTraceable
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQueries
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IPTraceableTraceProvider
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory
import org.eclipse.viatra.query.runtime.rete.matcher.ReteEngine
import org.eclipse.viatra.query.runtime.rete.network.Node
import org.eclipse.viatra.query.runtime.rete.traceability.CompiledQuery
import org.eclipse.viatra.query.runtime.rete.traceability.CompiledSubPlan
import org.eclipse.viatra.query.runtime.emf.EMFScope

/** 
 * This utility class can determine trace connection between Nodes in a Rete network and elements in a PQuery model.
 * @since 1.6
 */
class ReteNetworkTrace {

	final ReteEngine reteEngine
	val EMFScope scope
    final val Multimap<PTraceable, Node> traceableToNodeMap = Multimaps.newSetMultimap(newHashMap(), [newHashSet()])
    final IPTraceableTraceProvider traceProvider
    
    private def void updateMapWithCanonicalTraceable(PTraceable derivedTraceable, Node node, IPTraceableTraceProvider traceProvider, Multimap<PTraceable, Node> traceableToNodeMap) {
        traceProvider.getCanonicalTraceables(derivedTraceable).forEach [ traceable |
            traceableToNodeMap.put(traceable, node)
        ]
     }

    new(ViatraQueryMatcher<?> matcher, IPTraceableTraceProvider traceProvider) throws ViatraQueryException, QueryProcessingException {
    	this.reteEngine = (matcher.getEngine() as AdvancedViatraQueryEngine).getQueryBackend(
            new ReteBackendFactory()) as ReteEngine
        this.scope = matcher.engine.scope as EMFScope
    	this.traceProvider = traceProvider
        reteEngine.getReteNet().getRecipeTraces().forEach[recipeTrace |
            val Node node = recipeTrace.node
            
            switch recipeTrace {
                CompiledSubPlan: {
                    // XXX allEnforcedConstraints is not entirely correct, should rather be deltaEnforcedConstraints
                    // Required for functionally correct handling of join nodes
                    recipeTrace.subPlan.allEnforcedConstraints.forEach[updateMapWithCanonicalTraceable(it, node, traceProvider, traceableToNodeMap)]
                }
                CompiledQuery: {
                    updateMapWithCanonicalTraceable(recipeTrace.query, node, traceProvider, traceableToNodeMap)
                    recipeTrace.parentRecipeTracesPerBody.forEach [ derivedBody, parentRecipeTrace |
                        updateMapWithCanonicalTraceable(derivedBody, parentRecipeTrace.node, traceProvider, traceableToNodeMap)
                    ]
                }             
            }
        ]
        
    }
    
    /**
     * Find all nodes in the Rete network which originate from the given {@link PTraceable}
     */
    def Iterable<Node> findNodes(PTraceable traceable){
        traceableToNodeMap.get(traceable)
    }

    /**
     * Extract the coverage of a PQuery based on a Rete coverage
     */
    def CoverageInfo<PTraceable> traceCoverage(PQuery pQuery, CoverageInfo<Node> reteCoverage){
        val coverage = new CoverageInfo<PTraceable>();
        PQueries.getTraceables(pQuery).forEach[traceable |
            val coverageBasedOnTracedReteNodes = traceable.findNodes.map[reteCoverage.get(CoverageContext.create(it, scope))].fold(CoverageState::NOT_REPRESENTED_UNKNOWN_REASON, [r, t | r.best(t)])
            val amendedCoverage =
                if ((coverageBasedOnTracedReteNodes == CoverageState.NOT_REPRESENTED_UNKNOWN_REASON) && hasRemovalReason(traceable)) {
                    CoverageState.NOT_REPRESENTED
                } else coverageBasedOnTracedReteNodes
            coverage.put(CoverageContext.create(traceable, scope), amendedCoverage)
        ]
        coverage
    }
    
    private def boolean hasRemovalReason(PTraceable canonical) {
        traceProvider.isRemoved(canonical)
    }
    
    /**
     * Extract the coverage of a ViatraQueryMatcher based on a Rete coverage
     */
    def CoverageInfo<PTraceable> traceCoverage(ViatraQueryMatcher<?> matcher, CoverageInfo<Node> reteCoverage){
        traceCoverage(matcher.specification.internalQueryRepresentation, reteCoverage)
    }
    
}
