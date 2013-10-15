package org.eclipse.incquery.viewers.runtime.model;

import java.util.Collection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

public class EdgeDelayer{
    
    private Multimap<Item, Edge> delayedEdgesForNonExistingSource = HashMultimap.create();
    private Multimap<Item, Edge> delayedEdgesForNonExistingTarget = HashMultimap.create();
    
    protected void delayEdgeForNonExistingSource(Edge edge) {
        delayedEdgesForNonExistingSource.put(edge.getSource(), edge);
    }

    protected void delayEdgeForNonExistingTarget(Edge edge) {
        delayedEdgesForNonExistingTarget.put(edge.getTarget(), edge);
    }

    protected boolean removeDelayedEdgeForNonExistingSource(Edge edge) {
        return delayedEdgesForNonExistingSource.remove(edge.getSource(), edge);
    }

    protected boolean removeDelayedEdgeForNonExistingTarget(Edge edge) {
        return delayedEdgesForNonExistingTarget.remove(edge.getTarget(), edge);
    }

    protected Collection<Edge> removeDelayedEdgesForItem(Item item) {
        Collection<Edge> delayedEdgesForSource = delayedEdgesForNonExistingSource.removeAll(item);
        Collection<Edge> delayedEdgesForTarget = delayedEdgesForNonExistingTarget.removeAll(item);
        ImmutableSet<Edge> allEdges = ImmutableSet.<Edge> builder().addAll(delayedEdgesForSource)
                .addAll(delayedEdgesForTarget).build();
        return allEdges;
    }
}