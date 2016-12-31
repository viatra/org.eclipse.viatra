/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.base.itc.igraph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class can be used to wrap an {@link IGraphDataSource} into an {@link IBiDirectionalGraphDataSource}. This class
 * provides support for the retrieval of source nodes for a given target which is not supported by standard
 * {@link IGraphDataSource} implementations.
 * 
 * @author Tamas Szabo
 *
 * @param <V>
 *            the type parameter of the nodes in the graph data source
 */
public class IBiDirectionalWrapper<V> implements IBiDirectionalGraphDataSource<V>, IGraphObserver<V> {

    private static final long serialVersionUID = -5771114630390029106L;
    private IGraphDataSource<V> wrappedDataSource;
    // target -> source -> count
    private Map<V, Map<V, Integer>> incomingEdges;

    public IBiDirectionalWrapper(IGraphDataSource<V> gds) {
        this.wrappedDataSource = gds;

        this.incomingEdges = new HashMap<V, Map<V, Integer>>();

        if (gds.getAllNodes() != null) {
            for (V source : gds.getAllNodes()) {
                Map<V, Integer> targets = gds.getTargetNodes(source);
                for (Entry<V, Integer> entry : targets.entrySet()) {
                    for (int i = 0; i < entry.getValue(); i++) {
                        edgeInserted(source, entry.getKey());
                    }
                }
            }
        }

        gds.attachAsFirstObserver(this);
    }

    @Override
    public void attachObserver(IGraphObserver<V> observer) {
        wrappedDataSource.attachObserver(observer);
    }

    @Override
    public void attachAsFirstObserver(IGraphObserver<V> observer) {
        wrappedDataSource.attachAsFirstObserver(observer);
    }

    @Override
    public void detachObserver(IGraphObserver<V> observer) {
        wrappedDataSource.detachObserver(observer);
    }

    @Override
    public Set<V> getAllNodes() {
        return wrappedDataSource.getAllNodes();
    }

    @Override
    public Map<V, Integer> getTargetNodes(V source) {
        return wrappedDataSource.getTargetNodes(source);
    }

    @Override
    public Map<V, Integer> getSourceNodes(V target) {
        Map<V, Integer> result = incomingEdges.get(target);
        if (result == null) {
            return Collections.emptyMap();
        } else {
            return result;
        }
    }

    @Override
    public void edgeInserted(V source, V target) {
        Map<V, Integer> incoming = incomingEdges.get(target);
        if (incoming == null) {
            incoming = new HashMap<V, Integer>();
            incomingEdges.put(target, incoming);
        }
        Integer count = incoming.get(incoming);
        if (count == null) {
            count = 0;
        }
        count++;
        incoming.put(source, count);
    }

    @Override
    public void edgeDeleted(V source, V target) {
        Map<V, Integer> incoming = incomingEdges.get(target);
        if (incoming != null) {
            Integer count = incoming.get(source);
            if (count != null) {
                count--;

                if (count == 0) {
                    incoming.remove(source);
                } else {
                    incoming.put(source, count);
                }
            }
        }
    }

    @Override
    public void nodeInserted(V n) {

    }

    @Override
    public void nodeDeleted(V node) {
        Map<V, Integer> outgoing = wrappedDataSource.getTargetNodes(node);
        if (outgoing != null) {
            for (V target : outgoing.keySet()) {
                Map<V, Integer> incoming = incomingEdges.get(target);
                assert incoming != null;
                incoming.remove(node);
            }
        }
        incomingEdges.remove(node);
    }

    @Override
    public String toString() {
        return wrappedDataSource.toString();
    }
}
