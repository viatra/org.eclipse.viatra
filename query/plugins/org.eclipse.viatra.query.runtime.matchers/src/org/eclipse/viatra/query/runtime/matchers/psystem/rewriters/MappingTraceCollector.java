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
package org.eclipse.viatra.query.runtime.matchers.psystem.rewriters;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Multimap-based implementation to contain and query traces
 * 
 * @since 1.6
 *
 */
public class MappingTraceCollector implements IRewriterTraceCollector {

    /**
     * Traces from derivative to original
     */
    private Multimap<PConstraint, PConstraint> traces = HashMultimap.create();

    @Override
    public Iterable<PConstraint> getPConstraintTraces(PConstraint derivative) {
        Set<PConstraint> visited = new HashSet<>();
        Set<PConstraint> result = new HashSet<>();
        Queue<PConstraint> queue = new LinkedList<>();
        queue.add(derivative);
        while(!queue.isEmpty()){
            PConstraint aDerivative = queue.poll();
            // Track visited elements to avoid infinite loop via directed cycles in traces
            visited.add(aDerivative);
            Collection<PConstraint> nextOrigins = traces.get(aDerivative);
            if (nextOrigins.isEmpty()){
                // End of trace chain
                result.add(aDerivative);
            } else {
                // Follow traces
                for(PConstraint nextOrigin : nextOrigins){
                    if (!visited.contains(nextOrigin)){
                        queue.add(nextOrigin);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void addTrace(PConstraint original, PConstraint derivative){
        traces.put(derivative, original);
    }
    
    @Override
    public void derivativeRemoved(PConstraint derivative, IDerivativeModificationReason reason){
        // This implementation ignores the provided reason
        traces.removeAll(derivative);
    }
    
    @Override
    public Iterable<PConstraint> getKnownDerivatives() {
        return traces.keySet();
    }

}
