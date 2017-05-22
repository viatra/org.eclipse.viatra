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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.psystem.PTraceable;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
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
    private Multimap<PTraceable, PTraceable> traces = HashMultimap.create();
    private Map<PTraceable, IDerivativeModificationReason> removals = new HashMap<>();

    @Override
    public Iterable<PTraceable> getPTraceableTraces(PTraceable derivative) {
        if (derivative instanceof PQuery) { // PQueries are preserved
            return ImmutableList.of(derivative);
        }
        Set<PTraceable> visited = new HashSet<>();
        Set<PTraceable> result = new HashSet<>();
        Queue<PTraceable> queue = new LinkedList<>();
        queue.add(derivative);
        while(!queue.isEmpty()){
            PTraceable aDerivative = queue.poll();
            // Track visited elements to avoid infinite loop via directed cycles in traces
            visited.add(aDerivative);
            Collection<PTraceable> nextOrigins = traces.get(aDerivative);
            if (nextOrigins.isEmpty()){
                // End of trace chain
                result.add(aDerivative);
            } else {
                // Follow traces
                for(PTraceable nextOrigin : nextOrigins){
                    if (!visited.contains(nextOrigin)){
                        queue.add(nextOrigin);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void addTrace(PTraceable original, PTraceable derivative){
        traces.put(derivative, original);
    }
    
    @Override
    public void derivativeRemoved(PTraceable derivative, IDerivativeModificationReason reason){
        Preconditions.checkState(!removals.containsKey(derivative), "Traceable %s removed multiple times", derivative);
        removals.put(derivative, reason);
        // XXX the derivative must not be removed from the trace chain, as some rewriters, e.g. the normalizer keeps trace links to deleted elements 
    }
    
    @Override
    public Iterable<PTraceable> getKnownDerivatives() {
        return traces.keySet();
    }

}
