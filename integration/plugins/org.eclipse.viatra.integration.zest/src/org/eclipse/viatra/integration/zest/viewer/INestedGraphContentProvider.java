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
public interface INestedGraphContentProvider extends IContentProvider {

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
     * Returns the content elements representing the nodes within the graph that
     * is nested inside the node represented by the given content element. If
     * the node does not contain a nested graph, either an empty array or
     * <code>null</code> is returned.
     *
     * @param node
     *            A content element that represents a node.
     * @return The content elements representing the nodes within the graph that
     *         is nested inside the node represented by the given content
     *         element.
     */
    Object[] getNestedGraphNodes(Object node);

    /**
     * Determines whether the node represented by the given content element has
     * nested children.
     *
     * @param node
     *            A content element that represents a node, according to the
     *            {@link #getNodes()} method.
     * @return <code>true</code> when the node represented by the given content
     *         element has nested children, otherwise <code>false</code>.
     */
    boolean hasNestedGraph(Object node);

}