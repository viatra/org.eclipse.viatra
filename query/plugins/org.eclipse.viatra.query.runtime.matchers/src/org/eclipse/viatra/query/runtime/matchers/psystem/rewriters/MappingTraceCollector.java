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
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.viatra.query.runtime.matchers.psystem.PTraceable;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
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
    private final Multimap<PTraceable, PTraceable> traces = HashMultimap.create();

    /**
     * Traces from original to derivative
     */
    private final Multimap<PTraceable, PTraceable> inverseTraces = HashMultimap.create();
    
    /**
     * Reasons for removing {@link PTraceable}s
     */
    private final Map<PTraceable, IDerivativeModificationReason> removals = new HashMap<>();
    
    /**
     * Decides whether {@link PTraceable} is removed
     */
    private final Predicate<PTraceable> removed = removals::containsKey;

    /**
     * @since 2.0
     */
    @Override
    public Stream<PTraceable> getCanonicalTraceables(PTraceable derivative) {
        return findTraceEnds(derivative, traces).stream();
    }

    /**
     * @since 2.0
     */
    @Override
    public Stream<PTraceable> getRewrittenTraceables(PTraceable source) {
        return findTraceEnds(source, inverseTraces).stream();
    }

    /**
     * Returns the end of trace chains starting from the given {@link PTraceable} along the given trace edges. 
     */
    private Set<PTraceable> findTraceEnds(PTraceable traceable, Multimap<PTraceable, PTraceable> traceRecords) {
        if (traceable instanceof PQuery) { // PQueries are preserved
            return ImmutableSet.of(traceable);
        }
        Set<PTraceable> visited = new HashSet<>();
        Set<PTraceable> result = new HashSet<>();
        Queue<PTraceable> queue = new LinkedList<>();
        queue.add(traceable);
        while(!queue.isEmpty()){
            PTraceable aDerivative = queue.poll();
            // Track visited elements to avoid infinite loop via directed cycles in traces
            visited.add(aDerivative);
            Collection<PTraceable> nextOrigins = traceRecords.get(aDerivative);
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
        inverseTraces.put(original, derivative);
        // Even if this element was marked as removed earlier, now we replace it with another constraint!
        removals.remove(original);
    }
    
    @Override
    public void derivativeRemoved(PTraceable derivative, IDerivativeModificationReason reason){
        Preconditions.checkState(!removals.containsKey(derivative), "Traceable %s removed multiple times", derivative);
        // XXX the derivative must not be removed from the trace chain, as some rewriters, e.g. the normalizer keeps trace links to deleted elements 
        if (!inverseTraces.containsKey(derivative)) {
            // If there already exists a trace link, this removal means an update
            removals.put(derivative, reason);
        }
    }

    @Override
    public boolean isRemoved(PTraceable traceable) {
        return getRewrittenTraceables(traceable).allMatch(removed);
    }

    /**
     * @since 2.0
     */
    @Override
    public Stream<IDerivativeModificationReason> getRemovalReasons(PTraceable traceable) {
        return getRewrittenTraceables(traceable).filter(removed).map(removals::get);
    }

}
