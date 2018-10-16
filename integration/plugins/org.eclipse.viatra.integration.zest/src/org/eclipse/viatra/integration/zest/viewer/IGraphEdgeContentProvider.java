/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.zest.viewer;

import org.eclipse.jface.viewers.IContentProvider;

/**
 * @author Zoltan Ujhelyi
 *
 */
public interface IGraphEdgeContentProvider extends IContentProvider, INestedGraphContentProvider {

    /**
     * Returns all content elements which represent nodes on the first level of
     * the graph. If no nodes exist, either an empty array or <code>null</code>
     * is returned.
     *
     * @return All content elements which represent nodes on the first level of
     *         the graph.
     */
    public Object[] getNodes();
    
    /**
     * Returns all content elements which represent edges.
     *
     * @param node
     *            A model object representing a graph edge.
     */
    public Object[] getEdges();
    
    public Object getSource(Object edge);
    
    public Object getTarget(Object edge);
}
