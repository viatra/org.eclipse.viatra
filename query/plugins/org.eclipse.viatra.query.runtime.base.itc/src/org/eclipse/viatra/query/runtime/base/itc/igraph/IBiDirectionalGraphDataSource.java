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

import java.util.Map;

/**
 * A bi-directional graph data source supports all operations that an {@link IGraphDataSource} does, but it 
 * also makes it possible to query the incoming edges of nodes, not only the outgoing edges. 
 * 
 * @author Tamas Szabo
 * 
 * @param <V> the type of the nodes in the graph
 */
public interface IBiDirectionalGraphDataSource<V> extends IGraphDataSource<V> {

    /**
     * Returns the source nodes for the given target node.  
     * The returned data structure is a map because of potential parallel edges in the graph data source.
     * The values in the returned map represent the count of the given (source, target) edge. 
     * 
     * The method must not return null.
     * 
     * @param target the target node
     * @return the source nodes with their count values
     * @since 1.6
     */
    public Map<V, Integer> getSourceNodes(V target);
    
}
