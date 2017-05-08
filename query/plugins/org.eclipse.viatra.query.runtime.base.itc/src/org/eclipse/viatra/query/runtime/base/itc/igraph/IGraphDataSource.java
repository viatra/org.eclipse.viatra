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

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * The interface prescribes the set of operations that a graph data source must support. 
 * <p> Note that the old version of the interface is broken at version 1.6; 
 *  MultiSets are now presented as Maps instead of Lists.
 * 
 * @author Tamas Szabo
 * 
 * @param <V>
 *            the type of the nodes in the graph
 */
public interface IGraphDataSource<V> extends Serializable {

    /**
     * Attaches a new graph observer to this graph data source. Observers will be notified in the order they have been registered.   
     *
     * @param observer the graph observer
     */
    public void attachObserver(IGraphObserver<V> observer);
    
    /**
     * Attaches a new graph observer to this graph data source as the first one. 
     * In the notification order this observer will be the first one as long as another call to this method happens.
     *
     * @param observer the graph observer
     * @since 1.6
     */
    public void attachAsFirstObserver(IGraphObserver<V> observer);

    /**
     * Detaches an already registered graph observer from this graph data source. 
     * 
     * @param observer the graph observer
     */
    public void detachObserver(IGraphObserver<V> observer);

    /**
     * Returns the complete set of nodes in the graph data source. 
     * 
     * @return the set of all nodes
     */
    public Set<V> getAllNodes();

    /**
     * Returns the target nodes for the given source node.  
     * The returned data structure is a map (essentially a MultiSet) because of potential parallel edges in the graph data source.
     * The values in the returned map represent the count of the given (source, target) edge. 
     * 
     * The method must not return null.
     * 
     * @param source the source node
     * @return the target nodes with their count values
     * @since 1.6
     */
    public Map<V, Integer> getTargetNodes(V source);
}
