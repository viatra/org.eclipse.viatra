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

import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MemoryType;
import org.eclipse.viatra.query.runtime.matchers.util.IMemoryView;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiLookup;

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

    private IGraphDataSource<V> wrappedDataSource;
    // target -> source -> count
    private IMultiLookup<V, V> incomingEdges;

    public IBiDirectionalWrapper(IGraphDataSource<V> gds) {
        this.wrappedDataSource = gds;

        this.incomingEdges = CollectionsFactory.createMultiLookup(
                Object.class, MemoryType.MULTISETS, Object.class);

        if (gds.getAllNodes() != null) {
            for (V source : gds.getAllNodes()) {
                IMemoryView<V> targets = gds.getTargetNodes(source);
                for (V target : targets.distinctValues()) {
                    int count = targets.getCount(target);
                    for (int i = 0; i < count; i++) {
                        edgeInserted(source, target);
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
    public IMemoryView<V> getTargetNodes(V source) {
        return wrappedDataSource.getTargetNodes(source);
    }

    @Override
    public IMemoryView<V> getSourceNodes(V target) {
        return incomingEdges.lookupOrEmpty(target);
    }

    @Override
    public void edgeInserted(V source, V target) {
        incomingEdges.addPair(target, source);
    }

    @Override
    public void edgeDeleted(V source, V target) {
        incomingEdges.removePair(target, source);
    }

    @Override
    public void nodeInserted(V n) {

    }

    @Override
    public void nodeDeleted(V node) {

    }

    @Override
    public String toString() {
        return wrappedDataSource.toString();
    }
}
