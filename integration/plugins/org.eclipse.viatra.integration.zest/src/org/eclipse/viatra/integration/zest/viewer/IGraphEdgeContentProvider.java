/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.integration.zest.viewer;

import org.eclipse.zest.core.viewers.IGraphContentProvider;

/**
 * @author Zoltan Ujhelyi
 *
 */
public interface IGraphEdgeContentProvider extends IGraphContentProvider {

    /**
     * Returns all the nodes in the graph for the given input.
     * 
     * @input the input model object.
     * @return all the relationships in the graph for the given input.
     */
    public Object[] getElements(Object input);
    
    /**
     * Returns all content elements which represent edges.
     *
     * @param node
     *            A model object representing a graph edge.
     */
    public Object[] getRelationships(Object input);
    
}
