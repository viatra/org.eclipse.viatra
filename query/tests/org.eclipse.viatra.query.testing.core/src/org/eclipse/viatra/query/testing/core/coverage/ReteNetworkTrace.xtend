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
import java.util.Collection
import java.util.Collections
import java.util.HashMap
import java.util.Map
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory
import org.eclipse.viatra.query.runtime.rete.matcher.ReteEngine
import org.eclipse.viatra.query.runtime.rete.network.Node
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe
import org.eclipse.viatra.query.runtime.rete.traceability.CompiledSubPlan
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IPConstraintTraceProvider

/** 
 * This utility class can determine trace connection between Nodes in a Rete network and PConstraints in a PQuery model.
 * @since 1.6
 */
class ReteNetworkTrace {

    final Map<ReteNodeRecipe, Node> recipeToNodeMap = new HashMap()
    final val Multimap<PConstraint, ReteNodeRecipe> constraintToRecipeMap = Multimaps.newSetMultimap(newHashMap(), [newHashSet()])

    def private static <T> visit(T start, (T)=>Collection<? extends T> next, (T)=>void procedure){
        val queue = newLinkedList(start)
        while(!queue.empty){
            val current = queue.poll
            queue.addAll(next.apply(current))
            procedure.apply(current)
        }
    }

    new(ViatraQueryMatcher<?> matcher, IPConstraintTraceProvider trace) throws ViatraQueryException, QueryProcessingException {
        this(((matcher.getEngine() as AdvancedViatraQueryEngine)).getQueryBackend(
            new ReteBackendFactory()) as ReteEngine, trace)
    }

    new(ReteEngine reteEngine, IPConstraintTraceProvider trace) throws ViatraQueryException, QueryProcessingException {
        reteEngine.getReteNet().getRecipeTraces().forEach[recipeTrace |
            val Node node = recipeTrace.node
            val ReteNodeRecipe recipe = recipeTrace.recipe
            val ReteNodeRecipe shadowedRecipe = recipeTrace.shadowedRecipe
            if (node !== null){
                recipeToNodeMap.put(recipe, node)
                if (shadowedRecipe !== null) {
                    recipeToNodeMap.put(shadowedRecipe, node)
                }
            }
            
            if (recipeTrace instanceof CompiledSubPlan){
                recipeTrace.subPlan.visit([parentPlans], [plan |
                    plan.getDeltaEnforcedConstraints().forEach[derivedConstraint |
                        trace.getPConstraintTraces(derivedConstraint).forEach[pConstraint |
                            constraintToRecipeMap.put(pConstraint, recipe)
                            if (shadowedRecipe !== null){
                                constraintToRecipeMap.put(pConstraint, shadowedRecipe)
                            }
                        ]
                    ]
                ])
            }            
        ]
        
    }
    
    /**
     * Find all nodes in the Rete network which originate from the given constraint
     */
    def Iterable<Node> findNodes(PConstraint constraint){
        return if (constraintToRecipeMap.containsKey(constraint)){
            constraintToRecipeMap.get(constraint).map[recipeToNodeMap.get(it)]
        }else{
            Collections.emptyList
        }
    }
    
    /**
     * Extract the coverage of a PQuery based on a Rete coverage
     */
    def CoverageInfo<PConstraint> traceCoverage(PQuery pQuery, CoverageInfo<Node> reteCoverage){
        val coverage = new CoverageInfo<PConstraint>();
         
        pQuery.disjunctBodies.bodies.forEach[pBody |
            pBody.constraints.forEach[constraint |
                    coverage.put(constraint, constraint.findNodes.map[reteCoverage.get(it)].fold(CoverageState::NOT_REPRESENTED, [r, t | r.best(t)]))
            ]
        ]
        
        return coverage;
    }
    
    /**
     * Extract the coverage of a ViatraQueryMatcher based on a Rete coverage
     */
    def CoverageInfo<PConstraint> traceCoverage(ViatraQueryMatcher<?> matcher, CoverageInfo<Node> reteCoverage){
        traceCoverage(matcher.specification.internalQueryRepresentation, reteCoverage)
    }
    
}
