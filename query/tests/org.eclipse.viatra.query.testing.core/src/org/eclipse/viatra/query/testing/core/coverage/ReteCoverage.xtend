/*******************************************************************************
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.testing.core.coverage

import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory
import org.eclipse.viatra.query.runtime.rete.matcher.ReteEngine
import org.eclipse.viatra.query.runtime.rete.network.Node
import org.eclipse.viatra.query.runtime.rete.network.Supplier

/**
 * This utility can determine the coverage of nodes in a Rete network.
 * 
 * @since 1.6
 */
class ReteCoverage {
    
    val ReteEngine reteEngine;
    
    val EMFScope scope
    
    /**
     * Although this constructor requires a matcher, it extracts the underlying ReteEngine and
     * the computed coverage is engine-wide.
     * 
     * @throws ViatraQueryRuntimeException
     */
    new(ViatraQueryMatcher<?> matcher) {
        this.reteEngine = (matcher.getEngine() as AdvancedViatraQueryEngine).getQueryBackend(
            ReteBackendFactory.INSTANCE) as ReteEngine
        this.scope = matcher.engine.scope as EMFScope
    }
    
    def CoverageInfo<Node> reteCoverage(){
        val coverage = new CoverageInfo<Node>();
        
        reteEngine.getReteNet().containers.forEach[allNodes.forEach[
            coverage.put(CoverageContext.create(it, scope), it.computeCoverage)
        ]]
        
        return coverage;
    }
    
    private def CoverageState computeCoverage(Node node){
        if (node instanceof Supplier){
            return if (node.pulledContents.isEmpty) CoverageState::NOT_COVERED else CoverageState::COVERED;
        }
        return CoverageState::NOT_REPRESENTED_UNKNOWN_REASON
    }
    
}