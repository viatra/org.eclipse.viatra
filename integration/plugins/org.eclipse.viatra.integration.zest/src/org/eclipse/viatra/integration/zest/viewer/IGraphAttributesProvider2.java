/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.integration.zest.viewer;

import java.util.Map;

import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.gef.zest.fx.jface.IGraphAttributesProvider;

/**
 * @author Zoltan Ujhelyi
 *
 */
public interface IGraphAttributesProvider2 extends IGraphAttributesProvider {

    /**
     * Determines the attributes that should be set on the edge with the
     * specified source and target content elements. If no attributes should be
     * set on the edge, either an empty map or <code>null</code> can be
     * returned.
     *
     * @see ZestProperties For an overview of the supported attributes.
     * @param edge
     *            A content element representing the edge,
     *            according to the
     *            {@link IGraphEntityRelationshipContentProvider#getEdges(Object, Object)} method.
     * @return A mapping from attribute names to values that should be set on
     *         the specified edge.
     */
    public Map<String, Object> getEdgeAttributes(Object edge);
}
